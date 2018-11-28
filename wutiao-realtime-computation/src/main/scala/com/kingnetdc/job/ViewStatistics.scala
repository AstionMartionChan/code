package com.kingnetdc.job

import com.kingnetdc.sql.StatisticsFunction.{COUNT, COUNT_DISTINCT}
import com.kingnetdc.model._
import com.kingnetdc.model.WindowDisplayMode.{EVENT_TIME, FLUSH_TIME}
import com.kingnetdc.model.EventTypeEnum.{ACTIVE, ENTERFRONT, LOGIN, OPEN_CLIENT}
import com.kingnetdc.sink.{InfluxDBSink, MySqlSink}
import com.kingnetdc.sql.ComputationRule
import com.kingnetdc.utils.{JsonUtils, KafkaOffsetUtil}
import com.kingnetdc.watermelon.input.KingnetInputDStream
import com.kingnetdc.watermelon.utils.AppConstants.ABNORMAL_VALUE
import com.kingnetdc.watermelon.utils._
import com.kingnetdc.watermelon.utils.ConfigurationKeys._
import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}
import org.apache.spark.sql.types.StringType
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import org.apache.spark.streaming.{Duration, StreamingContext}

import scala.collection.JavaConversions._
import com.kingnetdc.utils.StatisticsUtils._
import org.apache.spark.rdd.RDD

// "暂时下线"
@Deprecated
object ViewStatistics extends EventStatistics {

    //一些必须的变量
    //判新的key
    private val VISITOR = "visitor"
    private val REGISTED = "registed"
    private val OUID = "ouid"
    private val DID = "did"
    private val BYTE_ONE: Byte = 1
    private val BYTE_ZERO: Byte = 0

    // 时间维度
    private val fiveMin: Long = 1000 * 60 * 5 // 5min
    private val oneHour: Long = 1000 * 3600 // 1hour
    private val oneDay: Long = 1000 * 3600 * 24 // 1day

    //定义计算规则
    override protected val computationRules: List[ComputationRule] = {
        ComputationRule(
            OUID, StringType, "pv", COUNT.toString,
            None
        ) ::
                ComputationRule(
                    DID, StringType, "uv", COUNT_DISTINCT.toString,
                    None
                ) :: Nil
    }

    // 计算间隔和刷新间隔的设置
    protected val durationConfigurations: List[DurationConfiguration] = {
        List(
            DurationConfiguration(fiveMin, fiveMin, EVENT_TIME.toString),
            DurationConfiguration(oneHour, fiveMin, EVENT_TIME.toString),
            DurationConfiguration(oneDay, fiveMin, FLUSH_TIME.toString)
        )
    }

    // 聚合的维度
    override protected val dimensions: List[String] = List("channel", "isvisitor")

    private def parse(line: String) = {
        // TODO 将日志转化为Event类
        try {
            val jsonNode = JsonUtils.getJsonNode(line)
            val event = jsonNode.at("/event").asText()
            val eventTime = jsonNode.at("/timestamp").asLong()

            val ouidTemp = Option(jsonNode.at("/ouid")).map(_.asText()).filter(StringUtils.nonEmpty)
            // just let the error throw
            val did = Option(jsonNode.at("/did")).map(_.asText()).filter(StringUtils.nonEmpty).get

            val channel = Option(jsonNode.at("/properties/_channel")).map(_.asText()).getOrElse(ABNORMAL_VALUE)

            ouidTemp match {
                case Some(ouid) => {
                    val isvisitor = false
                    Some(
                        (eventTime, ouid, did, event, channel, isvisitor)
                    )
                }
                case None => {
                    val isvisitor = true
                    Some(
                        (eventTime, ABNORMAL_VALUE, did, event, channel, isvisitor)
                    )
                }
            }
        } catch {
            case e: Exception =>
                logger.error(s"Failed to parse ${line}", e)
                None
        }
    }

    private def distributeOuid(
            behaviorLog: RDD[(Long, String, String, String, String, Boolean)]
    ) = {
        val partitionNum = behaviorLog.sparkContext.getConf.getInt("spark.did.partition", 100)
        behaviorLog.map { log =>
            log._3 -> (log._1, log._2, log._4, log._5, log._6)
        }.partitionBy(new HashPartitioner(partitionNum)).map { pair =>
            val (did, (eventTime, ouid, event, channel, isvisitor)) = pair
            (eventTime, ouid, did, event, channel, isvisitor)
        }
    }

    def convertToEvent(
            userBehaviorLogIter: Iterator[(Long, String, String, String, String, Boolean)],
            kdcCacheConfig: String
    ): Iterator[Event] = {
        val userBehaviorLogs = userBehaviorLogIter.toList

        val ouids = userBehaviorLogs.map(_._2).distinct

        userBehaviorLogs.map {
            case (eventTime, ouid, did, event, channel, isvisitor) =>
                new Event(
                    event, eventTime,
                    List(
                        List(channel, if (isvisitor) VISITOR else REGISTED),
                        List(all, if (isvisitor) VISITOR else REGISTED),
                        List(channel, all),
                        List(all, all)
                    ),
                    Map(OUID -> ouid, DID -> did)
                )
        }.toIterator
    }

    private def sparkConfCheck(sparkConf: SparkConf) = {
        // 检查spark配置
        require(sparkConf.contains(SPARK_CP_DIR), s"${SPARK_CP_DIR} is missing")
        require(sparkConf.contains(SPARK_STREAMING_DURATION), s"${SPARK_STREAMING_DURATION} is missing")
    }

    def main(args: Array[String]) = {
        if (args.length < 1) {
            throw new IllegalArgumentException("configuration path is missing")
        }

        val config = ConfigUtils.loadFromFile(args(0)).toMap
        // kafka properties
        val bootstrapServers = config("bootstrap.servers")
        val kafkaZkQuoram = config("kafka.zookeeper.connect")
        val kafkaSourceTopic = config("source.topic")
        val kafkaSourceTopicGroup = config("kafka.topic.group")
        val kdcCacheConfig = config("cache.config")
        // influxdb properties
        val influxDBConfig = getInfluxDBConfig(config)
        val mysqlConfig = getMysqlConfig(config)
        // output table
        val outputTable = config("output.table")

        val sparkConf = new SparkConf()
        sparkConfCheck(sparkConf)

        val sparkContext = new SparkContext(sparkConf)
        val streamingContext = new StreamingContext(
            sparkContext, Duration(sparkConf.get(SPARK_STREAMING_DURATION).toLong)
        )
        streamingContext.checkpoint(sparkConf.get(SPARK_CP_DIR))

        var startTime = 0L
        var offsetRanges = Array[OffsetRange]()
        val userBehaviorLogStream =
            KingnetInputDStream.createDirectKafkaStream[String, String](
                streamingContext, getKafkaParams(bootstrapServers, kafkaSourceTopic, kafkaSourceTopicGroup)
            ).transform((rdd, time) => {
                if (startTime == 0L) {
                    startTime = time.milliseconds
                }
                offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
                rdd
            }).map(_.value).flatMap(parse).filter(log => {
                List(OPEN_CLIENT, ENTERFRONT, ACTIVE, LOGIN).exists(_.toString.equals(log._4.toString))
            }).transform(rdd => distributeOuid(rdd))

        val eventStream =
            userBehaviorLogStream.transform { (userBehaviorLogRDD, time) =>
                userBehaviorLogRDD.mapPartitions { userBehaviorLogIter =>
                    convertToEvent(userBehaviorLogIter, kdcCacheConfig)
                }
            }


        val mapWithStateStream = getMapWithStateStream(eventStream)

        mapWithStateStream.flatMap(_.toList).foreachRDD((rdd, batchTime) => {
            val outputRDD = rdd.repartition(
                rdd.sparkContext.getConf.getInt(SPARK_OUTPUT_PARTITION, DEFAULT_OUTPUT_PARTITION)
            )
            //输出计算结果
            MySqlSink.save(batchTime, outputRDD, mysqlConfig, outputTable, dimensions)
            //提交offset
            KafkaOffsetUtil.saveOffsetToMysql(
                sparkConf.get(SPARK_OFFSET_MYSQL_TABLE),
                kafkaSourceTopicGroup,
                Map(
                    MYSQL_URL -> sparkConf.get(SPARK_OFFSET_MYSQL_URL),
                    MYSQL_USER -> sparkConf.get(SPARK_OFFSET_MYSQL_USER),
                    MYSQL_PASSWORD -> sparkConf.get(SPARK_OFFSET_MYSQL_PW)
                ),
                batchTime.milliseconds,
                offsetRanges
            )
        })


        //控制存储snapshot
        mapWithStateStream.stateSnapshots().foreachRDD { (rdd, time) =>
            SparkUtils.periodicRDDCheckpoint(rdd, startTime, time.milliseconds)
        }


        streamingContext.start()
        streamingContext.awaitTermination()
    }
}
