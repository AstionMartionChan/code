package com.kingnetdc.watermelon.input

import java.sql.{Connection, DriverManager, PreparedStatement, Statement}
import com.google.common.annotations.VisibleForTesting
import com.kingnetdc.blueberry.cache.RedisClusterCache
import com.kingnetdc.blueberry.cache.base.Constants.{REDIS_CLUSTER_CONNECT, REDIS_PASSWORD}
import com.kingnetdc.watermelon.utils.{DateUtils, KafkaOffsetManager, KafkaOffsetStorage, Logging}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.SparkConf
import org.apache.spark.streaming.StreamingContext
import com.kingnetdc.watermelon.utils.ConfigurationKeys._
import com.kingnetdc.watermelon.utils.AppConstants._
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils}
import org.apache.spark.streaming.kafka010.LocationStrategies._
import scala.util.Try
import scala.collection.JavaConversions._

/**
* Created by zhouml on 16/05/2018.
*/
//scalastyle:off
object KingnetInputDStream extends Logging {

    private def kafkaParamsCheck(kafkaParams: Map[String, Object]) = {
        require(kafkaParams.contains(TOPIC), s"${TOPIC} is missing")
        require(kafkaParams.contains(KAFKA_GROUP), s"${KAFKA_GROUP} is missing")
    }

    /**
      * 判断存储Kafka offset的mysql表是否存在
 *
      * @param conn
      * @param tableName
      * @param url
      * @param user
      * @param pw
     *
     * @return 是否存在
      */
    private def checkMysqlTable(
        conn: Connection, tableName: String, url: String, user: String, pw: String
    ): Boolean = {
        val selectClause = s"select * from ${tableName} where 1 = 0"
        var stmt: PreparedStatement = null
        try {
            stmt = conn.prepareStatement(selectClause)
            stmt.executeQuery()
            true
        } catch {
            case ex: Exception => {
                logger.error("table not exist.", ex)
                false
            }
        } finally {
            if (stmt != null) {
                try {
                    stmt.close()
                } catch {
                    case ex: Exception => logger.error("Fail to close statement.", ex)
                }
            }
        }
    }

    //如果mysql表不存在，则按照格式立即创建
    private def createTableIfNotExist(tableName: String, url: String, user: String, pw: String) = {
        val conn = getConnection(url, user, pw)
        if (!checkMysqlTable(conn, tableName, url, user, pw)) {
            val createSql =
              s"""create table if not exists ${tableName} (
                 |  `time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '存储的时间',
                 |  `group` VARCHAR(255) NOT NULL COMMENT '消费组',
                 |  `topic` VARCHAR(255) NOT NULL COMMENT '话题',
                 |  `partition` int NOT NULL COMMENT '分区',
                 |  `offset` bigint NOT NULL COMMENT '偏置',
                 |  `last_update` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
                 |  PRIMARY KEY `time_topic_partition` (`time`, `group`, `topic`, `partition`)
                 |);
               """.stripMargin
            logger.info("create table sql: " + createSql)
            var stmt: Statement = null
            try {
                stmt = conn.createStatement()
                stmt.executeUpdate(createSql)
            } finally {
                if (stmt != null) {
                    try {
                        stmt.close()
                    } catch {
                        case e: Exception => logger.error("Failed to close statement.", e)
                    }
                }
            }
        }
    }

    //获取最新的批次时间
    def getLatestTime(tableName: String, url: String, user: String, pw: String, topic: String, group: String): String = {
        val conn = getConnection(url, user, pw)
        val sql = s"""select time from ${tableName} where topic='${topic}' and `group`='${group}' order by time desc limit 1"""
        logger.info("get latest time, sql is: " + sql)
        var stmt: Statement = null
        var time = ""
        try {
            stmt = conn.createStatement()
            val result = stmt.executeQuery(sql)
            if (result.next()) {
                time = result.getTimestamp("time").toString.stripSuffix(".0")
            }
        } finally {
            if (stmt != null) {
                try {
                    stmt.close()
                } catch {
                    case e: Exception => logger.error("Failed to close statement.", e)
                }
            }
        }
        time
    }


    private def getConnection(url: String, user: String, pw: String): Connection = {
        Class.forName(MYSQL_JDBC_DRIVER)
        DriverManager.getConnection(url, user, pw)
    }

    @VisibleForTesting
    private[watermelon] def getSavedTopicOffsets(
        sparkConf: SparkConf, kafkaParams: Map[String, Object]
    ) = {
        val topics = kafkaParams.get(TOPIC).map(_.toString).get.split(COMMA).toSet
        val consumerGroupId = kafkaParams.get(KAFKA_GROUP).map(_.toString).get

        val offSetStorage = sparkConf.get(SPARK_KAFKA_OFFSET_STORAGE, DEFAULT_SPARK_KAFKA_OFFSET_STORAGE)
        val availableStorage = KafkaOffsetStorage.values.map(_.toString)

        if (!availableStorage.contains(offSetStorage)) {
            throw new IllegalArgumentException(
                s"Currently we only support save kafka offset to ${availableStorage.mkString(COMMA)}"
            )
        }

        if (KafkaOffsetStorage.ZK.toString == offSetStorage) {
            require(sparkConf.contains(SPARK_KAFKA_OFFSET_ZK_CONNECT), s"${SPARK_KAFKA_OFFSET_ZK_CONNECT} is missing")
            val zkConnect = sparkConf.get(SPARK_KAFKA_OFFSET_ZK_CONNECT)

            (
                topics,
                topics.flatMap { topic =>
                    KafkaOffsetManager.getFromZookeeper(zkConnect, consumerGroupId, topic)
                }.toMap
            )
        } else {
            require(sparkConf.contains(SPARK_OFFSET_MYSQL_URL), s"${SPARK_OFFSET_MYSQL_URL} is missing")
            require(sparkConf.contains(SPARK_OFFSET_MYSQL_USER), s"${SPARK_OFFSET_MYSQL_USER} is missing")
            require(sparkConf.contains(SPARK_OFFSET_MYSQL_PW), s"${SPARK_OFFSET_MYSQL_PW} is missing")
            require(sparkConf.contains(SPARK_OFFSET_MYSQL_TABLE), s"${SPARK_OFFSET_MYSQL_TABLE} is missing")

            createTableIfNotExist(sparkConf.get(SPARK_OFFSET_MYSQL_TABLE), sparkConf.get(SPARK_OFFSET_MYSQL_URL),
                sparkConf.get(SPARK_OFFSET_MYSQL_USER), sparkConf.get(SPARK_OFFSET_MYSQL_PW))

            val restartFromOpt = sparkConf.getOption(SPARK_KAFKA_OFFSET_MYSQL_RESTARTFROM)
            val dateOpt = Try(DateUtils.getYMDHMS.parse(restartFromOpt.get)).toOption
            val mysqlConfig = Map(
                MYSQL_URL -> sparkConf.get(SPARK_OFFSET_MYSQL_URL),
                MYSQL_USER -> sparkConf.get(SPARK_OFFSET_MYSQL_USER),
                MYSQL_PASSWORD -> sparkConf.get(SPARK_OFFSET_MYSQL_PW)
            )
            // 为空的时候取最近的一个时间
            if (restartFromOpt.isEmpty || dateOpt.isEmpty) {
                val latestTime = getLatestTime(sparkConf.get(SPARK_OFFSET_MYSQL_TABLE),
                    sparkConf.get(SPARK_OFFSET_MYSQL_URL),
                    sparkConf.get(SPARK_OFFSET_MYSQL_USER),
                    sparkConf.get(SPARK_OFFSET_MYSQL_PW),
                    topics.head, consumerGroupId)
                (
                  topics,
                  KafkaOffsetManager.getFromMySQL(
                      sparkConf.get(SPARK_OFFSET_MYSQL_TABLE),
                      mysqlConfig, latestTime,
                      consumerGroupId
                  ).filter {
                      case (topicPartition, offset) => topics.contains(topicPartition.topic)
                  }
                )
            } else {
                (
                  topics,
                  KafkaOffsetManager.getFromMySQL(
                      sparkConf.get(SPARK_OFFSET_MYSQL_TABLE),
                      mysqlConfig, restartFromOpt.get, consumerGroupId
                  ).filter {
                      case (topicPartition, offset) => topics.contains(topicPartition.topic)
                  }
                )
            }
        }
    }


    def createDirectKafkaStream[K, V](
        ssc: StreamingContext, kafkaParams: Map[String, Object]
    ): InputDStream[ConsumerRecord[K, V]] = {
        val sparkConf = ssc.sparkContext.getConf
        kafkaParamsCheck(kafkaParams)

        // TODO topicPartitionOffsets 要么全部为空, 要么全部都有
        val (topics, topicPartitionOffsets) = getSavedTopicOffsets(sparkConf, kafkaParams)
        val directKafkaInputDStream =
            if (topicPartitionOffsets.nonEmpty) {
                logger.info(s"Create direct stream with from offset: ${topicPartitionOffsets}")
                KafkaUtils.createDirectStream[K, V](ssc,
                    PreferConsistent, ConsumerStrategies.Assign[K, V](
                        topicPartitionOffsets.keys.toList, kafkaParams, topicPartitionOffsets
                    )
                )
            } else {
                logger.info(s"Create direct stream with topics: ${topics}")
                KafkaUtils.createDirectStream[K, V](ssc,
                    PreferConsistent,
                    ConsumerStrategies.Subscribe[K, V](topics, kafkaParams)
                )
            }

        directKafkaInputDStream
    }

    def start(ssc: StreamingContext) = {
        ssc.start()

        val sparkConf = ssc.sparkContext.getConf
        val checkIntervalOption = sparkConf.getOption(SPARK_STREAMING_STATE_CHECK_INTERVAL).map(_.toLong)

        checkIntervalOption match {
            case Some(checkInterval) =>
                val streamingDuration = sparkConf.get(SPARK_STREAMING_DURATION).toLong

                require(
                    checkInterval <= streamingDuration,
                    "Spark streaming state check interval should be less than or equal to streaming duration"
                )

                var stopped = false

                while (!stopped) {
                    stopped = ssc.awaitTerminationOrTimeout(checkInterval)

                    if (stopped) {
                        logger.info(s"${ssc.sparkContext.appName} has stopped")
                    } else {
                        val stopSignal = sparkConf.get(SPARK_STREAMING_STOP_SIGNAL)

                        var redisClusterCache: RedisClusterCache = null
                        try {
                            val connect = sparkConf.get(SPARK_STREAMING_STATE_CHECK_REDIS_CONNECT)
                            val passwordOpt = sparkConf.getOption(SPARK_STREAMING_STATE_CHECK_REDIS_PASSWORD)

                            val redisCofig =
                                Map(
                                    REDIS_CLUSTER_CONNECT -> connect
                                ) ++ passwordOpt.map { password =>
                                    Map(REDIS_PASSWORD -> password)
                                }.getOrElse(Map.empty[String, String])

                            redisClusterCache = new RedisClusterCache(redisCofig)
                            if (redisClusterCache.exists(stopSignal)) {
                                logger.info(s"Trying to gracefully shutdown ${ssc.sparkContext.appName}")
                                try {
                                    ssc.stop(true, true)
                                } finally {
                                    stopped = true
                                }
                            }
                        } catch {
                            case ex: Exception =>
                                logger.error(s"Error during state check", ex)
                        } finally {
                            Option(redisClusterCache).foreach { cache =>
                                logger.info("Stopping streaming state check redis cluster cache!!!")
                                cache.close()
                            }
                        }
                    }
                }
            case None =>
                // 如果没有检测到check interval, 则执行默认的
                ssc.awaitTermination()
        }
    }

}
//scalastyle:on
