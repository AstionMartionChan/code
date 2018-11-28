package com.kingnetdc.job

import java.util.concurrent.TimeUnit

import com.kingnetdc.metrics.StateEnum
import com.kingnetdc.sql.StatisticsFunction.SUM
import com.kingnetdc.model._
import com.kingnetdc.model.WindowDisplayMode.{EVENT_TIME, FLUSH_TIME}
import com.kingnetdc.model.EventTypeEnum.READ
import com.kingnetdc.sink.{InfluxDBSink, MySqlSink}
import com.kingnetdc.sql.{ComputationRule, Equal}
import com.kingnetdc.utils.{AbstractOptions, JsonUtils, KafkaOffsetUtil, StatisticsUtils}
import com.kingnetdc.watermelon.input.KingnetInputDStream
import com.kingnetdc.watermelon.utils.AppConstants.ABNORMAL_VALUE
import com.kingnetdc.watermelon.utils._
import com.kingnetdc.watermelon.utils.ConfigurationKeys._
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}
import org.apache.spark.sql.types.{ByteType, DoubleType, StringType}
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import org.apache.spark.streaming.{Duration, StreamingContext}
import com.kingnetdc.model.MoneySourceTypeEnum._
import scala.collection.JavaConversions._
import com.kingnetdc.utils.StatisticsUtils._
import org.apache.spark.rdd.RDD

import scala.collection.mutable
import scala.util.{Failure, Success}

// "暂时下线"
@Deprecated
object MoneyGainStatistics extends EventStatistics {

    //一些必须的变量
    //判新的key
    private val IS_NEW_USER = "is_new_user"
    private val IS_OLD_USER = "is_old_user"
    private val OUID = "ouid"
    private val VALUE = "value"
    private val BYTE_ONE: Byte = 1
    private val BYTE_ZERO: Byte = 0

    // 时间维度
    private val fiveMin: Long = 1000 * 60 * 5 // 5min
    private val oneHour: Long = 1000 * 3600 // 1hour
    private val oneDay: Long = 1000 * 3600 * 24 // 1day

    //定义计算规则
    override protected val computationRules: List[ComputationRule] = {
        ComputationRule(
            VALUE, DoubleType, StateEnum.ACTIVE.toString, SUM.toString,
            None
        ) ::
        ComputationRule(
            VALUE, DoubleType, s"${StateEnum.NEW.toString}_${fiveMin}", SUM.toString,
            Some(Equal(s"${IS_NEW_USER}_${fiveMin}", 1, ByteType))
        ) ::
                ComputationRule(
                    VALUE, DoubleType, s"${StateEnum.OLD.toString}_${fiveMin}", SUM.toString,
                    Some(Equal(s"${IS_OLD_USER}_${fiveMin}", 1, ByteType))
                ) ::
                ComputationRule(
                    VALUE, DoubleType, s"${StateEnum.NEW.toString}_${oneHour}", SUM.toString,
                    Some(Equal(s"${IS_NEW_USER}_${oneHour}", 1, ByteType))
                ) ::
                ComputationRule(
                    VALUE, DoubleType, s"${StateEnum.OLD.toString}_${oneHour}", SUM.toString,
                    Some(Equal(s"${IS_OLD_USER}_${oneHour}", 1, ByteType))
                ) ::
                ComputationRule(
                    VALUE, DoubleType, s"${StateEnum.NEW.toString}_${oneDay}", SUM.toString,
                    Some(Equal(s"${IS_NEW_USER}_${oneDay}", 1, ByteType))
                ) ::
                ComputationRule(
                    VALUE, DoubleType, s"${StateEnum.OLD.toString}_${oneDay}", SUM.toString,
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
    override protected val dimensions: List[String] = List("action_type", "item_category", "media_type", "channel")

    private def parse(line: String) = {
        // TODO 将日志转化为Event类
        try {
            val jsonNode = JsonUtils.getJsonNode(line)
            val eventTime = jsonNode.at("/time").asLong() * 1000
            // just let the error throw
            val ouid = Option(jsonNode.at("/uid")).map(_.asText()).filter(StringUtils.nonEmpty).get

            val actionType = Option(jsonNode.at("/actionType")).map(_.asInt()).getOrElse(-1)

            val value = Option(jsonNode.at("/value")).map(_.asDouble()).getOrElse(0: Double)

            val itemId = Option(jsonNode.at("/itemId")).map(_.asText()).get

            if (MoneySourceTypeEnum.values.exists(_.id == actionType)) {
                Some(
                    (eventTime, ouid, actionType, value, itemId)
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
            behaviorLog: RDD[(Long, String, Int, Double, String)]
    ) = {
        val partitionNum = behaviorLog.sparkContext.getConf.getInt("spark.did.partition", 100)
        behaviorLog.map { log =>
            log._2 -> (log._1, log._3, log._4, log._5)
        }.partitionBy(new HashPartitioner(partitionNum)).map { pair =>
            val (ouid, (eventTime, actionType, value, itemId)) = pair
            (eventTime, ouid, actionType, value, itemId)
        }
    }

    def convertToEvent(
        userBehaviorLogIter: Iterator[(Long, String, Int, Double, String)],
        kdcCacheConfig: String, mediaCacheConfig: String, itemCacheConfig: String
    ): Iterator[Event] = {
        val userBehaviorLogs = userBehaviorLogIter.toList

        val ouids = userBehaviorLogs.map(_._2).distinct

        val itemIds = userBehaviorLogs.map(_._5).distinct

        val userInfo =
            StatisticsUtils.getUserInfo(kdcCacheConfig, ouids)
        val userFirstTime = userInfo.map(info => {
            info._1 -> info._2.split(",")(0).toLong
        })

        val mediaInfo =
            StatisticsUtils.getMediaInfo(mediaCacheConfig, ouids)

        val itemInfo =
            StatisticsUtils.getItemInfo(itemCacheConfig, itemIds)

        userBehaviorLogs.map {
            case (eventTime, ouid, actionType, value, itemId) =>
                val userStatusInCalculationDuration: Map[Long, Boolean] =
                    StatisticsUtils.statusCheck(
                        ouid, eventTime,
                        userFirstTime, List(fiveMin, oneHour, oneDay)
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
                val sortId = itemInfo.get(itemId) match {
                    case Some(info) => {
                        val infos = info.split(",")
                        if (infos.size > 2) {
                            if (infos(0).equals("0")) {
                                infos(2)
                            } else {
                                ABNORMAL_VALUE
                            }
                        } else {
                            ABNORMAL_VALUE
                        }
                    }
                    case None => ABNORMAL_VALUE
                }
                val channel = userInfo.get(ouid) match {
                    case Some(info) => {
                        val infos = info.split(",")
                        if (infos.size > 1) {
                            if (StringUtils.nonEmpty(infos(1))) infos(1)
                            else ABNORMAL_VALUE
                        } else {
                            ABNORMAL_VALUE
                        }
                    }
                    case None => ABNORMAL_VALUE
                }
                val dimensionList = actionType match {
                    case 1 => {
                        List(
                            List(actionType.toString, all, all, channel),
                            List(actionType.toString, all, all, all),
                            List(all, all, all, channel),
                            List(all, all, all, all)
                        )
                    }
                    case 2 => {
                        List(
                            List(actionType.toString, all, all, all),
                            List(all, all, all, all)
                        )
                    }
                    case 3 => {
                        List(
                            List(actionType.toString, all, all, all),
                            List(all, all, all, all)
                        )
                    }
                    case _ => {
                        List(
                            List(actionType.toString, sortId, mediaType, all),
                            List(actionType.toString, all, mediaType, all),
                            List(actionType.toString, sortId, all, all),
                            List(actionType.toString, all, all, all),
                            List(all, sortId, mediaType, all),
                            List(all, all, mediaType, all),
                            List(all, sortId, all, all),
                            List(all, all, all, all)
                        )
                    }
                }


                new Event(
                    "moneygain", eventTime,
                    dimensionList,
                    Map(OUID -> ouid, VALUE -> value)
                ).setFilterFieldValueMap(
                    getUserStatusFilterFieldMap(userStatusInCalculationDuration, List(fiveMin, oneHour, oneDay)).toMap
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
        val itemCacheConfig = config("cache.item.config")
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
        val wbBehaviorLogStream =
            KingnetInputDStream.createDirectKafkaStream[String, String](
                streamingContext, getKafkaParams(bootstrapServers, kafkaSourceTopic, kafkaSourceTopicGroup)
            ).transform((rdd, time) => {
                if (startTime == 0L) {
                    startTime = time.milliseconds
                }
                offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
                rdd
            }).map(_.value).flatMap(parse).transform(rdd => distributeOuid(rdd))

        val eventStream =
            wbBehaviorLogStream.transform { (wbBehaviorLogRDD, time) =>
                wbBehaviorLogRDD.mapPartitions { userBehaviorLogIter =>
                    convertToEvent(userBehaviorLogIter, kdcCacheConfig, mediaCacheConfig, itemCacheConfig)
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
