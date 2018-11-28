package com.kingnetdc.job

import com.kingnetdc.metrics.StateEnum
import com.kingnetdc.model._
import com.kingnetdc.sql.{ComputationRule, Equal}
import com.kingnetdc.watermelon.utils.AppConstants.{ABNORMAL_VALUE, COMMA}
import com.kingnetdc.watermelon.utils.ConfigurationKeys._
import com.kingnetdc.utils._
import com.kingnetdc.watermelon.utils.{ConfigUtils, SparkUtils, StringUtils}
import com.kingnetdc.model.EventTypeEnum._
import org.apache.spark.sql.types.{ByteType, StringType}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}
import org.apache.spark.streaming.{Duration, StreamingContext}
import com.kingnetdc.model.WindowDisplayMode.{EVENT_TIME, FLUSH_TIME}
import com.kingnetdc.sql.StatisticsFunction.COUNT_DISTINCT
import com.kingnetdc.watermelon.input.KingnetInputDStream
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import com.kingnetdc.utils.StatisticsUtils._
import scala.collection.JavaConversions._


object UserStatistics extends EventStatistics {

    private val IS_NEW_USER = "is_new_user"

    private val IS_OLD_USER = "is_old_user"

    private val OUID = "ouid"

    private val BYTE_ONE: Byte = 1

    // 5min
    private val fiveMin: Long = 1000 * 60 * 5
    // 1hour
    private val oneHour: Long = 1000 * 3600
    // 1day
    private val oneDay: Long = 1000 * 3600 * 24

    //定义计算规则
    override protected val computationRules: List[ComputationRule] = {
        ComputationRule(
            OUID, StringType, StateEnum.ACTIVE.toString, COUNT_DISTINCT.toString,
            Some(Equal(StateEnum.ACTIVE.toString, 1, ByteType))
        ) ::
        ComputationRule(
            OUID, StringType, s"${StateEnum.NEW.toString}_${fiveMin}", COUNT_DISTINCT.toString,
            Some(Equal(s"${IS_NEW_USER}_${fiveMin}", 1, ByteType))
        ) ::
        ComputationRule(
            OUID, StringType, s"${StateEnum.OLD.toString}_${fiveMin}", COUNT_DISTINCT.toString,
            Some(Equal(s"${IS_OLD_USER}_${fiveMin}", 1, ByteType))
        ) ::
        ComputationRule(
            OUID, StringType, s"${StateEnum.NEW.toString}_${oneHour}", COUNT_DISTINCT.toString,
            Some(Equal(s"${IS_NEW_USER}_${oneHour}", 1, ByteType))
        ) ::
        ComputationRule(
            OUID, StringType, s"${StateEnum.OLD.toString}_${oneHour}", COUNT_DISTINCT.toString,
            Some(Equal(s"${IS_OLD_USER}_${oneHour}", 1, ByteType))
        ) ::
        ComputationRule(
            OUID, StringType, s"${StateEnum.NEW.toString}_${oneDay}", COUNT_DISTINCT.toString,
            Some(Equal(s"${IS_NEW_USER}_${oneDay}", 1, ByteType))
        ) ::
        ComputationRule(
            OUID, StringType, s"${StateEnum.OLD.toString}_${oneDay}", COUNT_DISTINCT.toString,
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
    override protected val dimensions: List[String] = List("appver", "channel", "os")

    def parse(line: String, config: Map[String, String]): Option[(Long, String, String, String, String, String)] = {
        try {
            val jsonNode = JsonUtils.getJsonNode(line)
            val event = jsonNode.at("/event").asText()
            val logReceiveTime = jsonNode.at("/properties/_sst").asLong()
            val logReceiveStartOpt = config.get("log.receive-time.start").map(_.toLong)

            val appVersion =
                Option(jsonNode.at("/properties/_appver"))
                        .map(_.asText())
                        .map(_.toLowerCase)
                        .filter(StringUtils.nonEmpty)
                        .getOrElse(ABNORMAL_VALUE)

            val appChannel =
                Option(jsonNode.at("/properties/_channel"))
                        .map(_.asText())
                        .map(_.toLowerCase)
                        .filter(StringUtils.nonEmpty)
                        .getOrElse(ABNORMAL_VALUE)

            val os =
                Option(jsonNode.at("/properties/_os"))
                        .map(_.asText())
                        .map(_.toLowerCase)
                        .filter(StringUtils.nonEmpty)
                        .getOrElse(ABNORMAL_VALUE)

            val logTimestampStartOpt = config.get("log.timestamp.start").map(_.toLong)
            val logTimestamp = jsonNode.at("/timestamp").asLong()

            if (
                validLogtime(logTimestamp, logTimestampStartOpt) &&
                validLogtime(logReceiveTime, logReceiveStartOpt) &&
                activeEventSet(event)
            ) {
                val ouid =
                    Option(jsonNode.at("/ouid"))
                        .map(_.asText())
                        .filter(StringUtils.nonEmpty)
                        .filterNot(_.equals(ABNORMAL_VALUE))
                        .get

                Some((logReceiveTime, appVersion, appChannel, os, ouid, event))
            } else {
                None
            }
        } catch {
            case e: Exception =>
                logger.error(s"Failed to parse ${line}", e)
                None
        }
    }

    def buildDimensionValues(appVersion: String, appChannel: String, os: String) = {
        List(
            List(appVersion, appChannel, os),
            List(appVersion, appChannel, "allos"),
            List(appVersion, "allchannel", os),
            List(appVersion, "allchannel", "allos"),
            List("allappver", appChannel, os),
            List("allappver", appChannel, "allos"),
            List("allappver", "allchannel", os),
            List("allappver", "allchannel", "allos")
        )
    }

    // 对于指定维度的数据进行删除
    def dimensionKeyToRemoveOuid(eventTime: Long, appVersion: String, appChannel: String, os: String, ouid: String) = {
        val dimensionKey =
            (
                appVersion :: appChannel :: os :: windowStartByDuration(eventTime, fiveMin).toString :: Nil
            ).mkString(COMMA)

        val ouidHashCode = StringUtils.hashString(ouid).asInt

        (
            (dimensionKey, AggregatorMergeStrategy.Decrease.toString),
            (ouidHashCode, List(BYTE_ONE, BYTE_ZERO, BYTE_ZERO, BYTE_ZERO, BYTE_ONE, BYTE_ZERO, BYTE_ONE))
        )
    }


    def convertToKeyValue(
        userBehaviorLogIter: Iterator[(Long, String, String, String, String, String)],
        kdcCacheConfig: String, kdcUserDimensionConfig: String, kdcUserDimensionPrefix: String
    ): Iterator[((String, String), ValueType)] = {
        val userBehaviorLogs = userBehaviorLogIter.toList

        val ouidMinEventTimeAndDimension = userBehaviorLogs.map { userBehaviorLog =>
            (userBehaviorLog._5, (userBehaviorLog._1, userBehaviorLog._2, userBehaviorLog._3, userBehaviorLog._4))
        }.groupBy(_._1).mapValues { uidTimePair =>
            uidTimePair.minBy(_._2._1)._2
        }

        val userMinEventTime = ouidMinEventTimeAndDimension.mapValues(value => (value._1, value._3))
        val userFirstActiveTime =
            StatisticsUtils.getOrSetUserFirstActiveTime(kdcCacheConfig, userMinEventTime)
        val userFirstDimension =
            StatisticsUtils.getOrSetFirstDimension(
                kdcUserDimensionConfig, ouidMinEventTimeAndDimension, kdcUserDimensionPrefix
            )

        userBehaviorLogs.flatMap {
            case (eventTime, appVersionInLog, appChannelInLog, osInLog, ouid, event) =>
                val (appVersion, appChannel, os, requireFixDimension) =
                    userFirstDimension.get(ouid).getOrElse {
                        logger.warn(s"UserFirstDimension is empty for ${ouid}")
                        (appVersionInLog, appChannelInLog, osInLog, Nil)
                    }

                val userStatusInCalculationDuration: Map[Long, Boolean] = {
                    val calculationAndStatus = StatisticsUtils.statusCheckFive(
                        ouid, eventTime,
                        userFirstActiveTime, List(fiveMin, oneHour, oneDay)
                    )
                    if (newEventSet.contains(event)) {
                        calculationAndStatus
                    } else {
                        calculationAndStatus.map(kv => {
                            if (kv._1 == fiveMin) kv._1 -> false
                            else kv._1 -> kv._2
                        })
                    }
                }

                val generatedDimension =
                    requireFixDimension.distinct.map {
                        case (appVersion, appChannel, os) =>
                            dimensionKeyToRemoveOuid(eventTime, appVersion, appChannel, os, ouid)
                    }

                generatedDimension ::: (
                    buildDimensionValues(appVersion, appChannel, os).map(dimensions => {
                        val rowKey = (dimensions ::: windowStartByDuration(eventTime, fiveMin).toString :: Nil)
                            .mkString(COMMA)
                        val ouidHashCode = StringUtils.hashString(ouid).asInt

                        (
                            (rowKey, AggregatorMergeStrategy.Increase.toString),
                            (ouidHashCode, getUserStatus(userStatusInCalculationDuration).map(_._2))
                        )
                    })
                )
        }.iterator
    }

    def preAggregate(
        userBehaviorLogRDD: RDD[(Long, String, String, String, String, String)], kdcCacheConfig: String,
        kdcUserDimensionConfig: String, kdcUserDimensionPrefix: String
    ) = {
        val keyValueRDD =
            userBehaviorLogRDD.mapPartitions { userBehaviorLogIter =>
                if (userBehaviorLogIter.isEmpty) {
                    Iterator.empty
                } else {
                    convertToKeyValue(
                        userBehaviorLogIter, kdcCacheConfig,
                        kdcUserDimensionConfig, kdcUserDimensionPrefix
                    )
                }
            }.persist(StorageLevel.MEMORY_AND_DISK_SER)

        convertKeyValueRDDToAggregator(keyValueRDD)
    }

    private[job] def getUserStatus(userStatus: Map[Long, Boolean]): List[(String, Byte)] = {
        val userStatusInFiveMin = userStatus.get(fiveMin).getOrElse(false)
        val userStatusInOneHour = userStatus.get(oneHour).getOrElse(false)
        val userStatusInOneDay = userStatus.get(oneDay).getOrElse(false)

        List(
            StateEnum.ACTIVE.toString -> BYTE_ONE,

            s"${IS_NEW_USER}_${fiveMin}" -> userStatusInFiveMin,
            s"${IS_OLD_USER}_${fiveMin}" -> !userStatusInOneDay,

            s"${IS_NEW_USER}_${oneHour}" -> userStatusInOneHour,
            s"${IS_OLD_USER}_${oneHour}" -> !userStatusInOneDay,

            s"${IS_NEW_USER}_${oneDay}" -> userStatusInFiveMin,
            s"${IS_OLD_USER}_${oneDay}" -> !userStatusInOneDay
        )
    }

    private def distributeOuid(
        behaviorLog: RDD[(Long, String, String, String, String, String)]
    ) = {
        val partitionNum = behaviorLog.sparkContext.getConf.getInt("spark.did.partition", 50)
        behaviorLog.map { log =>
            log._5 ->(log._1, log._2, log._3, log._4, log._6)
        }.partitionBy(new HashPartitioner(partitionNum)).map { pair =>
            val (ouid, (eventTime, appVersion, appChannel, os, event)) = pair
            (eventTime, appVersion, appChannel, os, ouid, event)
        }
    }

    private def sparkConfCheck(sparkConf: SparkConf) = {
        require(sparkConf.contains(SPARK_CP_DIR), s"${SPARK_CP_DIR} is missing")
        require(sparkConf.contains(SPARK_STREAMING_DURATION), s"${SPARK_STREAMING_DURATION} is missing")
    }

    def main(args: Array[String]) = {
        if (args.length < 1) {
            throw new IllegalArgumentException("configuration path is missing")
        }

        val config = ConfigUtils.loadFromFile(args(0)).toMap
        val bootstrapServers = config("bootstrap.servers")
        val kafkaTopic = config("source.topic")
        val kafkaTopicGroup = config("kafka.topic.group")
        val kdcCacheConfig = config("cache.config")
        val kdcUserDimensionConfig = config("user.dim.config")
        val kdcUserDimensionPrefix: String = config("user.dim.prefix")

        val sparkConf = new SparkConf()
        sparkConfCheck(sparkConf)

        val sparkContext = new SparkContext(sparkConf)
        val streamingContext = new StreamingContext(sparkContext,
            Duration(sparkConf.get(SPARK_STREAMING_DURATION).toLong))
        streamingContext.checkpoint(sparkConf.get(SPARK_CP_DIR))

        var startTime = 0L
        var offsetRanges = Array[OffsetRange]()

        val userBehaviorLogStream =
            KingnetInputDStream.createDirectKafkaStream[String, String](
                streamingContext, getKafkaParams(bootstrapServers, kafkaTopic, kafkaTopicGroup)
            ).transform((rdd, time) => {
                if (startTime == 0L) {
                    startTime = time.milliseconds
                }
                offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
                rdd
            }).map(_.value).flatMap { log =>
                parse(log, config)
            }.transform(rdd => distributeOuid(rdd))

        updateWithAggregator(
            userBehaviorLogStream.transform{ (userBehaviorLogRDD, time) =>
                preAggregate(userBehaviorLogRDD, kdcCacheConfig, kdcUserDimensionConfig, kdcUserDimensionPrefix)
            }, config
        ).foreachRDD{ (stateRDD, batchTime) =>
            if (!stateRDD.isEmpty) {
                SparkUtils.periodicRDDCheckpoint(stateRDD, startTime, batchTime.milliseconds)

                KafkaOffsetUtil.periodicSaveOffsetToMysql(
                    sparkConf, kafkaTopicGroup, startTime,
                    batchTime.milliseconds, offsetRanges
                )
            }
        }

        streamingContext.start()
        streamingContext.awaitTermination()
    }

}
