package com.kingnetdc.job

import com.kingnetdc.sql.StatisticsFunction.COUNT
import com.kingnetdc.model.{DurationConfiguration, Event, EventTypeEnum}
import com.kingnetdc.model.WindowDisplayMode.{EVENT_TIME, FLUSH_TIME}
import com.kingnetdc.model.EventTypeEnum._
import com.kingnetdc.sql.{ComputationRule, Equal}
import com.kingnetdc.utils.{JsonUtils, KafkaOffsetUtil, StatisticsUtils}
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

/*
    设备(count distinct),  设备(count)

    + 阅读
    + 播放
    + 评论(算评论和回复) 即统计对于资讯或者视频的评论 以及 对于评论的 评论
    + 不感兴趣
    + 举报
    + 收藏
    + 点赞(like) 点赞的类型 type=1 && status=1 (发现者在发现页面投票不算)
*/
object InteractStatistics extends EventStatistics {

    private val METRICS_NAME = "time_cnt"

    private val DID = "did"

    private val ACTION = "action"

    // 5min
    private val fiveMin: Long = 1000 * 60 * 5
    // 1hour
    private val oneHour: Long = 1000 * 3600
    // 1day
    private val oneDay: Long = 1000 * 3600 * 24

    override protected val computationRules: List[ComputationRule] = {
        ComputationRule(
            DID, StringType, s"${ACTION}_${EventTypeEnum.READ.toString}", COUNT.toString,
            Some(Equal(ACTION, EventTypeEnum.READ.toString, StringType))
        ) ::
        ComputationRule(
            DID, StringType, s"${ACTION}_${EventTypeEnum.PLAY.toString}", COUNT.toString,
            Some(Equal(ACTION, EventTypeEnum.PLAY.toString, StringType))
        ) ::
            ComputationRule(
                DID, StringType, s"${ACTION}_${EventTypeEnum.LIKE.toString}", COUNT.toString,
                Some(Equal(ACTION, EventTypeEnum.LIKE.toString, StringType))
            ) ::
            ComputationRule(
                DID, StringType, s"${ACTION}_${EventTypeEnum.COMMENT.toString}", COUNT.toString,
                Some(Equal(ACTION, EventTypeEnum.COMMENT.toString, StringType))
            ) ::
            ComputationRule(
                DID, StringType, s"${ACTION}_${EventTypeEnum.NOINTEREST.toString}", COUNT.toString,
                Some(Equal(ACTION, EventTypeEnum.NOINTEREST.toString, StringType))
            ) ::
            ComputationRule(
                DID, StringType, s"${ACTION}_${EventTypeEnum.REPORT.toString}", COUNT.toString,
                Some(Equal(ACTION, EventTypeEnum.REPORT.toString, StringType))
            ) ::
            ComputationRule(
                DID, StringType, s"${ACTION}_${EventTypeEnum.FAVOUR.toString}", COUNT.toString,
                Some(Equal(ACTION, EventTypeEnum.FAVOUR.toString, StringType))
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

    override protected val dimensions: List[String] =
        List("device_type", "metrics_name", "item_type", "os", "item_category")

    def parse(line: String, config: Map[String, String]) = {
        try {
            val jsonNode = JsonUtils.getJsonNode(line)
            val event = jsonNode.at("/event").asText()
            val logReceiveTime = jsonNode.at("/properties/_sst").asLong()
            val logReceiveStartOpt = config.get("log.receive-time.start").map(_.toLong)

            val logTimestampStartOpt = config.get("log.timestamp.start").map(_.toLong)
            val logTimestamp = jsonNode.at("/timestamp").asLong()
            val did =
                Option(jsonNode.at("/did"))
                    .map(_.asText())
                    .map(_.toLowerCase)
                    .filter(StringUtils.nonEmpty)
                    .filterNot(_.equals("-1"))
                    .getOrElse(ABNORMAL_VALUE)

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

            var itemtype =
                Option(jsonNode.at("/properties/itemtype"))
                    .map(_.asInt())
                    .getOrElse(ABNORMAL_VALUE.toInt)

            var ptype = ABNORMAL_VALUE

            var itemid = ABNORMAL_VALUE

            val status = Option(jsonNode.at("/properties/status")).map(_.asInt()).getOrElse(-1)

            if (event.equals(FAVOUR.toString)) {  // 收藏
                val itemlist =
                    Option(jsonNode.at("/properties/itemlist"))
                            .map(_.asText())
                            .filter(StringUtils.nonEmpty)
                            .getOrElse(ABNORMAL_VALUE)


                if (status == 1 && StringUtils.nonEmpty(itemlist) && !itemlist.equals(ABNORMAL_VALUE)) {
                    val props = itemlist.split("@")
                    if (props.length >= 3) {
                        itemid = props(0)
                        if (props(2).equals("article")) {
                            itemtype = 1
                        } else {
                            itemtype = 2
                        }
                    } else {
                        itemid = ABNORMAL_VALUE
                        itemtype = -1
                    }
                } else {
                    itemid = ABNORMAL_VALUE
                    itemtype = -1
                }
            } else {
                ptype =
                    Option(jsonNode.at("/properties/type"))
                            .map(_.asText())
                            .filter(StringUtils.nonEmpty)
                            .getOrElse(ABNORMAL_VALUE)

                itemid =
                    Option(jsonNode.at("/properties/itemid"))
                            .map(_.asText())
                            .filter(StringUtils.nonEmpty)
                            .getOrElse(ABNORMAL_VALUE)
            }

            if (
                validLogtime(logTimestamp, logTimestampStartOpt) &&
                validLogtime(logReceiveTime, logReceiveStartOpt)
            ) {
                Some(
                    (logTimestamp, did, event, appVersion, appChannel, os, itemtype, ptype.toInt, itemid, status)
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

    private def checkAction(line: (Long, String, String, String, String, String, Int, Int, String, Int)): Boolean = {
        val event = line._3
        event match {
            case "read" => line._7 == 1

            case "play" => line._8 == 1

            case "like" => line._10 == 1 && line._8 == 1

            case "favour" => line._10 == 1

            case "comment" => line._10 == 1

            case _ => true
        }
    }

    private def distributeDid(
        behaviorLog: RDD[(Long, String, String, String, String, String, Int, Int, String, Int)]
    ) = {
        val partitionNum = behaviorLog.sparkContext.getConf.getInt("spark.did.partition", 100)
        behaviorLog.map { log =>
            log._2 -> (log._1, log._3, log._4, log._5, log._6, log._7, log._8, log._9)
        }.partitionBy(new HashPartitioner(partitionNum)).map { pair =>
            val (did, (eventTime, event, appVersion, appChannel, os, itemtype, ptype, itemid)) = pair
            (eventTime, did, event, appVersion, appChannel, os, itemtype, ptype, itemid)
        }
    }

    def convertToEvent(
        deviceBehaviorLogIter: Iterator[(Long, String, String, String, String, String, Int, Int, String)],
        kdcCacheConfig: String, itemCacheConfig: String
    ): Iterator[Event] = {
        val deviceBehaviorLogs = deviceBehaviorLogIter.toList

        val dids = deviceBehaviorLogs.map(_._2).distinct
        val didFirstActiveTime = StatisticsUtils.getFirstTime(kdcCacheConfig, dids)

        val itemIds = deviceBehaviorLogs.map(_._9).distinct
        val itemInfo = StatisticsUtils.getItemInfo(itemCacheConfig, itemIds)

        deviceBehaviorLogs.map {
            case (eventTime, did, event, appVersion, appChannel, os, itemType, ptype, itemid) =>
                val didStatusInCalculationDuration: Map[Long, Boolean] =
                    StatisticsUtils.statusCheck(
                        did, eventTime,
                        didFirstActiveTime, List(fiveMin, oneHour, oneDay)
                    )

                val sortId = itemInfo.get(itemid) match {
                    case Some(info) => {
                        val infos = info.split(",")
                        if (infos.size > 2) {
                            if (infos(0).equals("0")) {
                                val details = infos(2).split(" ")
                                if (details.size > 0) {
                                    Option(
                                        if (details(0).isEmpty) details(1)
                                        else details(0)
                                    ).filter(StringUtils.nonEmpty).getOrElse(ABNORMAL_VALUE)
                                } else {
                                    ABNORMAL_VALUE
                                }
                            } else {
                                ABNORMAL_VALUE
                            }
                        } else {
                            ABNORMAL_VALUE
                        }
                    }
                    case None => ABNORMAL_VALUE
                }

                val device_type = didStatusInCalculationDuration.get(oneDay).getOrElse(false).toInt.toString

                new Event(
                    event, eventTime,
                    buildDimensionValues(device_type, itemType.toString, os, sortId),
                    Map(DID -> did)
                ).setFilterFieldValueMap(
                    Map(
                        ACTION -> event
                    )
                )
        }.toIterator
    }

    private def buildDimensionValues(device_type: String, itemType: String,
        os: String, sortId: String) = {
        List(
            List(device_type, METRICS_NAME, itemType.toString, os, sortId),
            List(device_type, METRICS_NAME, itemType.toString, os, "allsortid"),
            List(device_type, METRICS_NAME, itemType.toString, "allos", sortId),
            List(device_type, METRICS_NAME, "allitemtype", os, sortId),
            List(device_type, METRICS_NAME, itemType.toString, "allos", "allsortid"),
            List(device_type, METRICS_NAME, "allitemtype", os, "allsortid"),
            List(device_type, METRICS_NAME, "allitemtype", "allos", sortId),
            List(device_type, METRICS_NAME, "allitemtype", "allos", "allsortid"),
            List("alldidflag", METRICS_NAME, itemType.toString, os, sortId),
            List("alldidflag", METRICS_NAME, itemType.toString, os, "allsortid"),
            List("alldidflag", METRICS_NAME, itemType.toString, "allos", sortId),
            List("alldidflag", METRICS_NAME, "allitemtype", os, sortId),
            List("alldidflag", METRICS_NAME, itemType.toString, "allos", "allsortid"),
            List("alldidflag", METRICS_NAME, "allitemtype", os, "allsortid"),
            List("alldidflag", METRICS_NAME, "allitemtype", "allos", sortId),
            List("alldidflag", METRICS_NAME, "allitemtype", "allos", "allsortid")
        )
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
        val kafkaSourceTopic = config("source.topic")
        val kafkaTopicGroup = config("kafka.topic.group")
        val kdcCacheConfig = config("cache.config")
        val itemCacheConfig = config("cache.item.config")

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
                streamingContext, getKafkaParams(bootstrapServers, kafkaSourceTopic, kafkaTopicGroup)
            ).transform((rdd, time) => {
                if (startTime == 0L) {
                    startTime = time.milliseconds
                }

                offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges

                rdd
            }).map(_.value).flatMap(log => parse(log, config)).filter { log =>
                List(
                    READ, PLAY, COMMENT, NOINTEREST, REPORT, FAVOUR, LIKE
                ).exists(_.toString.equals(log._3.toString))
            }.filter(checkAction).map( log => {
                if (log._3.equals(PLAY.toString)) {
                    (log._1, log._2, READ.toString, log._4, log._5, log._6, log._7, log._8, log._9, log._10)
                } else {
                    log
                }
            }).transform(rdd => {
                distributeDid(rdd)
            })

        val eventStream =
            userBehaviorLogStream.transform { (userBehaviorLogRDD, time) =>
                userBehaviorLogRDD.mapPartitions { userBehaviorLogIter =>
                    convertToEvent(userBehaviorLogIter, kdcCacheConfig, itemCacheConfig)
                }
            }

        getUpdateWithStateStream(eventStream, config).foreachRDD { (stateRDD, batchTime) =>
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