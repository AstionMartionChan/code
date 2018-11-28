package com.kingnetdc.job


import com.kingnetdc.sql.StatisticsFunction.SUM
import com.kingnetdc.model._
import com.kingnetdc.metrics.StateEnum
import com.kingnetdc.model.WindowDisplayMode.{EVENT_TIME, FLUSH_TIME}
import com.kingnetdc.model.EventTypeEnum.CONTRIBUTIONINC
import com.kingnetdc.sink.{InfluxDBSink, MySqlSink}
import com.kingnetdc.sql.{ComputationRule, Equal}
import com.kingnetdc.utils.{JsonUtils, KafkaOffsetUtil, StatisticsUtils}
import com.kingnetdc.watermelon.input.KingnetInputDStream
import com.kingnetdc.watermelon.utils._
import com.kingnetdc.watermelon.utils.ConfigurationKeys._
import org.apache.spark.rdd.RDD
import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}
import org.apache.spark.sql.types.{ByteType, DoubleType}
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import org.apache.spark.streaming.{Duration, StreamingContext}
import com.kingnetdc.utils.StatisticsUtils._

import scala.collection.JavaConversions._
import com.kingnetdc.watermelon.utils.AppConstants.ABNORMAL_VALUE

// "暂时下线"
@Deprecated
object ContributionStatistics extends EventStatistics {

    val increfs = Set(1, 2, 3, 4, 5)

    //一些必须的变量
    //判新的key
    private val IS_NEW_USER = "is_new_user"
    private val IS_OLD_USER = "is_old_user"
    private val OUID = "ouid"
    private val CONTRIBUTIONPOINT = "contributionpoint"
    private val BYTE_ONE: Byte = 1
    private val BYTE_ZERO: Byte = 0

    // 时间维度
    private val fiveMin: Long = 1000 * 60 * 5 // 5min
    private val oneHour: Long = 1000 * 3600 // 1hour
    private val oneDay: Long = 1000 * 3600 * 24 // 1day

    //定义计算规则
    override protected val computationRules: List[ComputationRule] = {
        ComputationRule(
            CONTRIBUTIONPOINT, DoubleType, StateEnum.ACTIVE.toString, SUM.toString,
            None
        ) ::
        ComputationRule(
            CONTRIBUTIONPOINT, DoubleType, s"${StateEnum.NEW.toString}_${fiveMin}", SUM.toString,
            Some(Equal(s"${IS_NEW_USER}_${fiveMin}", 1, ByteType))
        ) ::
        ComputationRule(
            CONTRIBUTIONPOINT, DoubleType, s"${StateEnum.OLD.toString}_${fiveMin}", SUM.toString,
            Some(Equal(s"${IS_OLD_USER}_${fiveMin}", 1, ByteType))
        ) ::
        ComputationRule(
            CONTRIBUTIONPOINT, DoubleType, s"${StateEnum.NEW.toString}_${oneHour}", SUM.toString,
            Some(Equal(s"${IS_NEW_USER}_${oneHour}", 1, ByteType))
        ) ::
        ComputationRule(
            CONTRIBUTIONPOINT, DoubleType, s"${StateEnum.OLD.toString}_${oneHour}", SUM.toString,
            Some(Equal(s"${IS_OLD_USER}_${oneHour}", 1, ByteType))
        ) ::
        ComputationRule(
            CONTRIBUTIONPOINT, DoubleType, s"${StateEnum.NEW.toString}_${oneDay}", SUM.toString,
            Some(Equal(s"${IS_NEW_USER}_${oneDay}", 1, ByteType))
        ) ::
        ComputationRule(
            CONTRIBUTIONPOINT, DoubleType, s"${StateEnum.OLD.toString}_${oneDay}", SUM.toString,
            Some(Equal(s"${IS_OLD_USER}_${oneDay}", 1, ByteType))
        ) ::
        Nil
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
    override protected val dimensions: List[String] = List("incref", "item_category", "media_type")

    private def parse(line: String) = {
        // TODO 将日志转化为Event类
        try {
            val jsonNode = JsonUtils.getJsonNode(line)
            val event = jsonNode.at("/event").asText()
            val eventTime = jsonNode.at("/timestamp").asLong()
            // just let the error throw
            val ouid = Option(jsonNode.at("/ouid")).map(_.asText()).filter(StringUtils.nonEmpty).get

            val contributionpoint =
                Option(jsonNode.at("/properties/contributionpoint")).map(_.asDouble()).getOrElse(0: Double)

            val incref =
                Option(jsonNode.at("/properties/incref")).map(_.asInt()).getOrElse(-1)

            val sortid =
                Option(jsonNode.at("/properties/sortid")).map(_.asText())
                  .filter(StringUtils.nonEmpty).getOrElse(ABNORMAL_VALUE)

            if (increfs.contains(incref)) {
                Some(
                    (eventTime, ouid, event, contributionpoint, incref, sortid)
                )
            } else {
                None
            }
        } catch {
            case e: Exception =>
                logger.error(s"Failed to parse ${line}", e)
                None
        }
    }

    private def distributeOuid(
            behaviorLog: RDD[(Long, String, String, Double, Int, String)]
    ) = {
        val partitionNum = behaviorLog.sparkContext.getConf.getInt("spark.did.partition", 100)
        behaviorLog.map { log =>
            log._2 -> (log._1, log._3, log._4, log._5, log._6)
        }.partitionBy(new HashPartitioner(partitionNum)).map { pair =>
            val (ouid, (eventTime, event, contributionpoint, incref, sortId)) = pair
            (eventTime, ouid, event, contributionpoint, incref, sortId)
        }
    }

    def convertToEvent(
            userBehaviorLogIter: Iterator[(Long, String, String, Double, Int, String)],
            kdcCacheConfig: String, mediaCacheConfig: String
    ): Iterator[Event] = {
        val userBehaviorLogs = userBehaviorLogIter.toList

        val ouids = userBehaviorLogs.map(_._2).distinct

        val userFirstActiveTime =
            StatisticsUtils.getFirstTime(kdcCacheConfig, ouids)

        val mediaInfo = StatisticsUtils.getMediaInfo(mediaCacheConfig, ouids)

        userBehaviorLogs.map {
            case (eventTime, ouid, event, contributionpoint, incref, sortId) =>
                val userStatusInCalculationDuration: Map[Long, Boolean] =
                    StatisticsUtils.statusCheck(
                        ouid, eventTime,
                        userFirstActiveTime, List(fiveMin, oneHour, oneDay)
                    )
                val mediaType = mediaInfo.get(ouid) match {
                    case Some(info) => {
                        val infos = info.split(",")
                        if (infos.size > 1) {
                            if (infos(0).equals("1")) {
                                infos(1)
                            } else {
                                ABNORMAL_VALUE
                            }
                        } else {
                            ABNORMAL_VALUE
                        }
                    }
                    case None => ABNORMAL_VALUE
                }


                new Event(
                    event, eventTime,
                    List(
                        List(incref.toString, sortId.toString, mediaType),
                        List(incref.toString, sortId.toString, all),
                        List(incref.toString, all, mediaType),
                        List(incref.toString, all, all)
                    ),
                    Map(OUID -> ouid, CONTRIBUTIONPOINT -> contributionpoint)
                ).setFilterFieldValueMap(
                    StatisticsUtils.getUserStatusFilterFieldMap(userStatusInCalculationDuration,
                        List(fiveMin, oneHour, oneDay)).toMap
                )
        }.toIterator
    }

    private def sparkConfCheck(sparkConf: SparkConf) = {
        // TODO 检查spark配置
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
        val mediaCacheConfig = config("cache.media.config")
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
                List(CONTRIBUTIONINC).exists(_.toString.equals(log._3.toString))
            }).transform(rdd => distributeOuid(rdd))

        val eventStream =
            userBehaviorLogStream.transform { (userBehaviorLogRDD, time) =>
                userBehaviorLogRDD.mapPartitions { userBehaviorLogIter =>
                    convertToEvent(userBehaviorLogIter, kdcCacheConfig, mediaCacheConfig)
                }
            }

        val mapWithStateStream = getMapWithStateStream(eventStream)

        mapWithStateStream.flatMap(_.toList).foreachRDD((rdd, batchTime) => {
            val outputRDD = rdd.repartition(
                rdd.sparkContext.getConf.getInt(SPARK_OUTPUT_PARTITION, DEFAULT_OUTPUT_PARTITION)
            )
            //输出计算结果
            MySqlSink.save(
                batchTime, outputRDD, mysqlConfig, outputTable, dimensions
            )
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
