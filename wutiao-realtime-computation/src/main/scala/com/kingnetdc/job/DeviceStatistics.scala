package com.kingnetdc.job

import com.kingnetdc.metrics.{Aggregator, StateEnum}
import com.kingnetdc.sql.StatisticsFunction._
import com.kingnetdc.model.{AggregatorMergeStrategy, DurationConfiguration}
import com.kingnetdc.sql.{ComputationRule, Equal}
import com.kingnetdc.utils.StatisticsUtils._
import com.kingnetdc.utils.{KafkaOffsetUtil, JsonUtils, StatisticsUtils}
import com.kingnetdc.watermelon.input.KingnetInputDStream
import com.kingnetdc.watermelon.utils.{SparkUtils, ConfigUtils, StringUtils}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{ByteType, StringType}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkContext, HashPartitioner, SparkConf}
import org.apache.spark.streaming.{Duration, StreamingContext}
import com.kingnetdc.model.WindowDisplayMode.{EVENT_TIME, FLUSH_TIME}
import scala.collection.JavaConversions._
import com.kingnetdc.watermelon.utils.AppConstants._
import com.kingnetdc.watermelon.utils.ConfigurationKeys._
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}


object DeviceStatistics extends EventStatistics {

    private val DID = "did"

    private val fiveMin: Long = 1000 * 60 * 5

    private val oneHour: Long = 1000 * 3600

    private val oneDay: Long = 1000 * 3600 * 24

    protected val durationConfigurations: List[DurationConfiguration] = {
        List(
            DurationConfiguration(fiveMin, fiveMin, EVENT_TIME.toString),
            DurationConfiguration(oneHour, fiveMin, EVENT_TIME.toString),
            DurationConfiguration(oneDay, fiveMin, FLUSH_TIME.toString)
        )
    }

    override protected val computationRules: List[ComputationRule] = {
        ComputationRule(
            DID, StringType, StateEnum.ACTIVE.toString, COUNT_DISTINCT.toString,
            Some(Equal(StateEnum.ACTIVE.toString, 1, ByteType))
        ) ::
        ComputationRule(
            DID, StringType, s"${StateEnum.NEW}_${fiveMin}", COUNT_DISTINCT.toString,
            Some(Equal(s"${StateEnum.NEW}_${fiveMin}", 1, ByteType))
        ) ::
        ComputationRule(
            DID, StringType, s"${StateEnum.OLD}_${fiveMin}", COUNT_DISTINCT.toString,
            Some(Equal(s"${StateEnum.OLD}_${fiveMin}", 1, ByteType))
        ) ::
        ComputationRule(
            DID, StringType, s"${StateEnum.NEW}_${oneHour}", COUNT_DISTINCT.toString,
            Some(Equal(s"${StateEnum.NEW}_${oneHour}", 1, ByteType))
        ) ::
        ComputationRule(
            DID, StringType, s"${StateEnum.OLD}_${oneHour}", COUNT_DISTINCT.toString,
            Some(Equal(s"${StateEnum.OLD}_${oneHour}", 1, ByteType))
        ) ::
        ComputationRule(
            DID, StringType, s"${StateEnum.NEW}_${oneDay}", COUNT_DISTINCT.toString,
            Some(Equal(s"${StateEnum.NEW}_${oneDay}", 1, ByteType))
        ) ::
        ComputationRule(
            DID, StringType, s"${StateEnum.OLD}_${oneDay}", COUNT_DISTINCT.toString,
            Some(Equal(s"${StateEnum.OLD}_${oneDay}", 1, ByteType))
        ) ::
        Nil
    }

    override protected val dimensions: List[String] = List("appver", "channel", "os")

    /**
     *  _sst 是日志的接收, 不使用timestamp是因为, 有可能传上来的时间异常(比如说手机端上传了未来的时间)
     *  log.receive-time.start --> 用于过滤_sst >= 某个阈值的时间, 一般用于日志重刷
     *  log.timestamp.start ---> 用于timestamp >= 某个阈值的时间
     * @param line
     * @param config
     * @return
     */
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
                val did =
                    Option(jsonNode.at("/did"))
                        .map(_.asText())
                        .map(_.toLowerCase)
                        .filter(StringUtils.nonEmpty)
                        .filterNot(_.equals(ABNORMAL_VALUE))
                        .get

                Some((logReceiveTime, appVersion, appChannel, os, did, event))
            } else {
                None
            }
        } catch {
            case ex: Exception =>
                logger.error(s"Failed to parse ${line}", ex)
                None
        }
    }

    def buildDimensionValues(appVersion: String, appChannel: String, os: String) = {
        List(
            List(appVersion, appChannel, os),
            List(appVersion, appChannel, "allos"),
            List(appVersion, "allchannel",  os),
            List(appVersion, "allchannel", "allos"),
            List("allappver", appChannel, os),
            List("allappver", appChannel, "allos"),
            List("allappver", "allchannel",  os),
            List("allappver", "allchannel", "allos")
        )
    }

    def getDidMinEventTimeAndDimension(deviceBehaviorLogs: List[(Long, String, String, String, String, String)]) = {
        deviceBehaviorLogs.map { deviceBehaviorLog =>
            (
                deviceBehaviorLog._5,
                (deviceBehaviorLog._1, deviceBehaviorLog._2, deviceBehaviorLog._3, deviceBehaviorLog._4)
            )
        }.groupBy(_._1).mapValues { didPair =>
            didPair.minBy(_._2._1)._2
        }
    }

    // 对于指定维度的数据进行删除
    def dimensionKeyToRemoveDid(eventTime: Long, appVersion: String, appChannel: String, os: String, did: String) = {
        val dimensionKey =
            (
                appVersion :: appChannel :: os :: windowStartByDuration(eventTime, fiveMin).toString :: Nil
            ).mkString(COMMA)

        val didHashCode = StringUtils.hashString(did).asInt

        (
            (dimensionKey, AggregatorMergeStrategy.Decrease.toString),
            (didHashCode, List(BYTE_ONE, BYTE_ZERO, BYTE_ZERO, BYTE_ZERO, BYTE_ONE, BYTE_ZERO, BYTE_ONE))
        )
    }

    def convertToKeyValue(
        deviceBehaviorLogIter: Iterator[(Long, String, String, String, String, String)],
        kdcDeviceCacheConfig: String, kdcDeviceDimensionConfig: String, kdcDeviceDimensionPrefix: String
    ): Iterator[((String, String), ValueType)] = {
        val deviceBehaviorLogs = deviceBehaviorLogIter.toList

        // (eventTime, appVersion, appChannel, os, did, event)
        val didMinEventTimeAndDimension = getDidMinEventTimeAndDimension(deviceBehaviorLogs)
        val deviceFirstDimension =
            StatisticsUtils.getOrSetFirstDimension(
                kdcDeviceDimensionConfig, didMinEventTimeAndDimension, kdcDeviceDimensionPrefix
            )

        val didMinEventTime = didMinEventTimeAndDimension.mapValues(_._1)
        val deviceFirstActiveTime = StatisticsUtils.getOrSetFirstActiveTime(kdcDeviceCacheConfig, didMinEventTime)

        deviceBehaviorLogs.flatMap {
            case (eventTime, appVersionInLog, appChannelInLog, osInLog, did, event) =>
                val (appVersion, appChannel, os, requireFixDimension) = deviceFirstDimension.get(did).getOrElse {
                    logger.warn(s"DeviceFirstDimension is empty for ${did}")
                    (appVersionInLog, appChannelInLog, osInLog, Nil)
                }

                val deviceStatusInCalculationDuration: Map[Long, Boolean] = {
                    val calculationAndStatus = StatisticsUtils.statusCheckFive(
                        did, eventTime,
                        deviceFirstActiveTime, List(fiveMin, oneHour, oneDay)
                    )

                    if (newEventSet.contains(event)) {
                        calculationAndStatus
                    } else {
                        calculationAndStatus.map {
                            case (calculation, status) =>
                                if (calculation == fiveMin) calculation -> false
                                else calculation -> status
                        }
                    }
                }

                val generatedDimension =
                    requireFixDimension.distinct.map {
                        case (appVersion, appChannel, os) =>
                            dimensionKeyToRemoveDid(eventTime, appVersion, appChannel, os, did)
                    }

                generatedDimension ::: (buildDimensionValues(appVersion, appChannel, os).map { dimensions =>
                    val rowKey =
                        (dimensions ::: windowStartByDuration(eventTime, fiveMin).toString :: Nil).mkString(COMMA)
                    val didHashCode = StringUtils.hashString(did).asInt
                    (
                        (rowKey, AggregatorMergeStrategy.Increase.toString),
                        (didHashCode, getDeviceStatus(deviceStatusInCalculationDuration).map(_._2))
                    )
                })
        }.iterator
    }


    /**
     *  对于5分钟,  新对应5分钟的新; 旧与一天内是否是新用户相反
     *  对于1小时,  新对应1小时的新; 旧与一天内是否是新用户相反
     *  对于1天,  新对应5分钟; 旧与一天内是否是新用户相反
     *
     * @param deviceStatus 在每一个统计区间 是新还是旧
     *
     * @return
     */
    private[job] def getDeviceStatus(deviceStatus: Map[Long, Boolean]): List[(String, Byte)] = {
        val newInFiveMin = deviceStatus.get(fiveMin).getOrElse(false)
        val newInOneHour = deviceStatus.get(oneHour).getOrElse(false)
        val newInOneDay = deviceStatus.get(oneDay).getOrElse(false)

        List(
            StateEnum.ACTIVE.toString -> BYTE_ONE,

            s"${StateEnum.NEW}_${fiveMin}" -> newInFiveMin,
            s"${StateEnum.OLD}_${fiveMin}" -> !newInOneDay,

            s"${StateEnum.NEW}_${oneHour}" -> newInOneHour,
            s"${StateEnum.OLD}_${oneHour}" -> !newInOneDay,

            s"${StateEnum.NEW}_${oneDay}" -> newInFiveMin,
            s"${StateEnum.OLD}_${oneDay}" -> !newInOneDay
        )
    }


    /**
     *  按照did分区是为了, 确保判新逻辑的一致性
     *
     * @param behaviorLog
     *
     * @return
     */
    def distributeDid(
        behaviorLog: RDD[(Long, String, String, String, String, String)]
    ) = {
        val partitionNum = behaviorLog.sparkContext.getConf.getInt("spark.did.partition", 50)
        behaviorLog.map { log =>
            log._5 -> (log._1, log._2, log._3, log._4, log._6)
        }.partitionBy(new HashPartitioner(partitionNum)).map { pair =>
            val (did, (eventTime, appVersion, appChannel, os, event)) = pair
            (eventTime, appVersion, appChannel, os, did, event)
        }
    }

    def preAggregate(
        deviceBehaviorLogRDD: RDD[(Long, String, String, String, String, String)],
        kdcCacheConfig: String, kdcDeviceDimensionConfig: String,
        kdcDeviceDimensionPrefix: String
    ): RDD[(String, Aggregator)] = {
        val keyValueRDD =
            deviceBehaviorLogRDD.mapPartitions { deviceBehaviorLogIter =>
                if (deviceBehaviorLogIter.isEmpty) {
                    Iterator.empty
                } else {
                    convertToKeyValue(
                        deviceBehaviorLogIter, kdcCacheConfig,
                        kdcDeviceDimensionConfig, kdcDeviceDimensionPrefix
                    )
                }
            }.persist(StorageLevel.MEMORY_AND_DISK_SER)

        convertKeyValueRDDToAggregator(keyValueRDD)
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
        val kdcDeviceDimensionConfig = config("device.dim.config")
        val kdcDeviceDimensionPrefix: String = config("device.dim.prefix")

        val sparkConf = new SparkConf()
        sparkConfCheck(sparkConf)

        val sparkContext = new SparkContext(sparkConf)
        val streamingContext = new StreamingContext(
            sparkContext, Duration(sparkConf.get(SPARK_STREAMING_DURATION).toLong)
        )
        streamingContext.checkpoint(sparkConf.get(SPARK_CP_DIR))

        var startTime = 0L
        var offsetRanges = Array[OffsetRange]()

        val deviceBehaviorLogStream =
            KingnetInputDStream.createDirectKafkaStream[String, String](
                streamingContext,
                getKafkaParams(bootstrapServers, kafkaTopic, kafkaTopicGroup)
            ).transform((rdd, time) => {
                if (startTime == 0L) {
                    startTime = time.milliseconds
                }
                offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
                rdd
            }).map(_.value)
            .flatMap { log =>
                parse(log, config)
            }.transform { rdd => distributeDid(rdd) }

        updateWithAggregator(
            deviceBehaviorLogStream.transform { (deviceBehaviorLogRDD, time) =>
                if (deviceBehaviorLogRDD.isEmpty) {
                    deviceBehaviorLogRDD.sparkContext.emptyRDD
                } else {
                    preAggregate(
                        deviceBehaviorLogRDD, kdcCacheConfig,
                        kdcDeviceDimensionConfig, kdcDeviceDimensionPrefix
                    )
                }
            }, config
        ).foreachRDD { (stateRDD, batchTime) =>
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
