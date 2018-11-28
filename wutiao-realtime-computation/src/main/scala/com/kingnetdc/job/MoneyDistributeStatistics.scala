package com.kingnetdc.job

import com.kingnetdc.metrics.StateEnum
import com.kingnetdc.model.WindowDisplayMode._
import com.kingnetdc.model.{MoneySourceTypeEnum, DurationConfiguration, Event}
import com.kingnetdc.sink.MySqlSink
import com.kingnetdc.sql.{Equal, ComputationRule}
import com.kingnetdc.model.MoneySourceTypeEnum._
import com.kingnetdc.sql.StatisticsFunction._
import com.kingnetdc.utils.{KafkaOffsetUtil, StatisticsUtils, JsonUtils}
import com.kingnetdc.utils.StatisticsUtils._
import com.kingnetdc.watermelon.input.KingnetInputDStream
import com.kingnetdc.watermelon.utils.{StringUtils, SparkUtils, ConfigUtils}
import com.kingnetdc.watermelon.utils.ConfigurationKeys._
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import org.apache.spark.streaming.{Duration, StreamingContext}
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.sql.types.{ByteType, DoubleType}
import scala.collection.JavaConversions._
import com.kingnetdc.watermelon.utils.AppConstants._
import scala.util.{Failure, Success, Try}


// "暂时下线"
@Deprecated
/**
 * 统计当日的分币各个渠道(内容提供者, 内容发现者, 分享, 评论, 点赞, 阅读等)
 * Created by zhouml on 11/06/2018.
 */
object MoneyDistributeStatistics extends EventStatistics {

    private val MONEY_DISTRIBUTE = "money_distribute"

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

    override protected val dimensions: List[String] =
        List("action_type", "item_type", "media_type", "item_category", "channel")

    override protected val computationRules: List[ComputationRule] = {
        ComputationRule(
            MONEY_DISTRIBUTE, DoubleType, StateEnum.ACTIVE.toString, SUM.toString,
            None
        ) ::
        ComputationRule(
            MONEY_DISTRIBUTE, DoubleType,
            s"${StateEnum.NEW.toString}_${fiveMin}", SUM.toString,
            Some(Equal(s"${IS_NEW_USER}_${fiveMin}", 1, ByteType))
        ) ::
        ComputationRule(
            MONEY_DISTRIBUTE, DoubleType, s"${StateEnum.OLD.toString}_${fiveMin}", SUM.toString,
            Some(Equal(s"${IS_OLD_USER}_${fiveMin}", 1, ByteType))
        ) ::
        ComputationRule(
            MONEY_DISTRIBUTE, DoubleType, s"${StateEnum.NEW.toString}_${oneHour}", SUM.toString,
            Some(Equal(s"${IS_NEW_USER}_${oneHour}", 1, ByteType))
        ) ::
        ComputationRule(
            MONEY_DISTRIBUTE, DoubleType, s"${StateEnum.OLD.toString}_${oneHour}", SUM.toString,
            Some(Equal(s"${IS_OLD_USER}_${oneHour}", 1, ByteType))
        ) ::
        ComputationRule(
            MONEY_DISTRIBUTE, DoubleType, s"${StateEnum.NEW.toString}_${oneDay}", SUM.toString,
            Some(Equal(s"${IS_NEW_USER}_${oneDay}", 1, ByteType))
        ) ::
        ComputationRule(
            MONEY_DISTRIBUTE, DoubleType, s"${StateEnum.OLD.toString}_${oneDay}", SUM.toString,
            Some(Equal(s"${IS_OLD_USER}_${oneDay}", 1, ByteType))
        ) ::
        Nil
    }

    def parse(line: String): Option[(Long, String, String, String, String, Double)] = {
        try {
            val jsonNode = JsonUtils.getJsonNode(line)
            val uid = jsonNode.at("/uid").asText()
            val eventTime = jsonNode.at("/time").asLong() * 1000
            val itemId = jsonNode.at("/itemId").asText()
            val itemType = jsonNode.at("/itemType").asText()
            val value = jsonNode.at("/value").asText().toDouble
            val actionType = jsonNode.at("/actionType").asText()

            if (
                Option(uid).filter(StringUtils.nonEmpty).exists(uid => uid != ABNORMAL_UID) &&
                Option(itemId).nonEmpty &&
                ITEM_TYPE.exists(_ == itemType) &&
                MoneySourceTypeEnum.values.exists(_.id.toString == actionType)
            ) {
                Some((eventTime, actionType, uid, itemId, itemType, value))
            } else {
                None
            }
        } catch {
            case ex: Exception =>
                logger.error(s"Failed to parse ${line}", ex)
                None
        }
    }


    /**
     * @param actionType 分币来源类型
     * @param itemType 资讯 | 视频
     * @param mediaType 自媒体类型
     * @param category 垂直类目
     * @param channel 注册渠道
     *
     * @return 根据不同actionType构建的维度值
     */
    def buildDimensionValues(
        actionType: String, itemType: String, mediaType: String, category: String, channel: String
    ) = {
        val allDimensionValues: List[(String, String, String, String)] =
            if (register.id.toString == actionType) {
                List(
                    (all, all, all, channel),
                    (all, all, all, all)
                )
            } else if (List(invite, invited).exists(_.id.toString == actionType)) {
                List((all, all, all, all))
            } else if (List(issue, discover, share, comment, vote, read, report).exists(_.id.toString == actionType)) {
                List(
                    (itemType, mediaType, category),
                    (itemType, all, all),
                    (itemType, mediaType, all),
                    (itemType, all, category),

                    (all, mediaType, category),
                    (all, all, all),
                    (all, mediaType, all),
                    (all, all, category)
                ).map {
                    case (itemType, mediaType, category) => (itemType, mediaType, category, all)
                }
            } else {
                Nil
            }

        allDimensionValues.flatMap {
            case (itemType, mediaType, category, channel) =>
                List(
                    List(actionType, itemType, mediaType, category, channel),
                    List(all, itemType, mediaType, category, channel)
                )
        }
    }

    def convertToEvent(
        moneyDistributeDetailIterator: Iterator[(Long, String, String, String, String, Double)],
        userStatusKdcCacheConfig: String, userMediaTypeKdcCacheConfig: String, itemKdcCacheConfig: String
    ) = {
        val moneyDistributeDetail = moneyDistributeDetailIterator.toList

        val uids = moneyDistributeDetail.map(_._3).distinct
        val itemIds = moneyDistributeDetail.map(_._4).distinct

        // 用户注册 | 渠道信息
        val userInfo: Map[String, String] = StatisticsUtils.getUserInfo(userStatusKdcCacheConfig, uids)

        val userFirstActiveTime = userInfo.flatMap {
            case (userId, value) =>
                Try {
                    userId -> value.split(COMMA)(0).toLong
                } match {
                    case Success(pair) => Some(pair)
                    case Failure(ex) =>
                        logger.error(s"Failed to get first active time for ${userId} from value ${value}")
                        None
                }
        }

        // 用户自媒体类型
        val userMediaType = StatisticsUtils.getMediaInfo(userMediaTypeKdcCacheConfig, uids)

        // 垂直类目
        val itemCategory = StatisticsUtils.getItemInfo(itemKdcCacheConfig, itemIds)

        moneyDistributeDetail.map {
            case (eventTime, actionType, uid, itemId, itemType, value) =>
                val userStatusInCalculationDuration: Map[Long, Boolean] =
                    StatisticsUtils.statusCheck(
                        uid, eventTime,
                        userFirstActiveTime,
                        List(fiveMin, oneHour, oneDay)
                    )

                val registerChannel =
                    userInfo.get(uid).filter(value => Option(value).nonEmpty) match {
                        case Some(info) =>
                            Try {
                                info.split(COMMA)(1)
                            }.filter(StringUtils.nonEmpty) match {
                                case Success(value) => value
                                case Failure(ex) =>
                                    logger.error(s"Failed to get user info for ${uid} from value ${info}")
                                    ABNORMAL_VALUE
                            }
                        case None => ABNORMAL_VALUE
                    }

                val mediaType =
                    userMediaType.get(uid).filter(value => Option(value).nonEmpty).map { value =>
                        Try {
                            val statusAndMediaType = value.split(COMMA)
                            if (statusAndMediaType(0) == "1") {
                                statusAndMediaType(1)
                            } else {
                                ABNORMAL_VALUE
                            }
                        }.filter(StringUtils.nonEmpty) match {
                            case Success(value) => value
                            case Failure(ex) =>
                                logger.error(s"Failed to get user media type for ${uid} from value ${value}")
                                ABNORMAL_VALUE
                        }

                    }.getOrElse(ABNORMAL_VALUE)

                val category =
                    itemCategory.get(itemId).filter(value => Option(value).nonEmpty).map { value =>
                        Try {
                            val itemInfo = value.split(COMMA)

                            if (itemInfo(0) == "0") {
                                itemInfo(2)
                            } else {
                                ABNORMAL_VALUE
                            }
                        }.filter(StringUtils.nonEmpty) match {
                            case Success(value) => value
                            case Failure(ex) =>
                                logger.error(s"Failed to get item category for ${itemId} from value ${value}")
                                ABNORMAL_VALUE
                        }
                    }.getOrElse(ABNORMAL_VALUE)

                new Event(
                    MONEY_DISTRIBUTE, eventTime,
                    buildDimensionValues(actionType, itemType, mediaType, category, registerChannel),
                    Map(MONEY_DISTRIBUTE -> value)
                ).setFilterFieldValueMap(
                    getUserStatusFilterFieldMap(
                        userStatusInCalculationDuration, List(fiveMin, oneHour, oneDay)
                    )
                )
        }.iterator
    }

    def main(args: Array[String]) = {
        if (args.length < 1) {
            throw new IllegalArgumentException("configuration path is missing")
        }

        val config = ConfigUtils.loadFromFile(args(0)).toMap
        val bootstrapServers = config("bootstrap.servers")
        val kafkaTopic = config("source.topic")
        val kafkaTopicGroup = config("kafka.topic.group")
        val userStatusKdcCacheConfig = config("user.cache.config")
        val userMediaTypeKdcCacheConfig = config("user.media.type.cache.config")
        val itemKdcCacheConfig = config("item.category.cache.config")
        val outputTable = config("output.table")

        val mysqlConfig = getMysqlConfig(config)

        val sparkConf = new SparkConf()
        sparkConfCheck(sparkConf)

        val sparkContext = new SparkContext(sparkConf)
        val streamingContext = new StreamingContext(
            sparkContext, Duration(sparkConf.get(SPARK_STREAMING_DURATION).toLong)
        )
        streamingContext.checkpoint(sparkConf.get(SPARK_CP_DIR))

        var startTime = 0L
        var offsetRanges = Array[OffsetRange]()

        val moneyDistributeLogStream =
            KingnetInputDStream.createDirectKafkaStream[String, String](
                streamingContext,
                getKafkaParams(bootstrapServers, kafkaTopic, kafkaTopicGroup)
            ).transform((rdd, time) => {
                if (startTime == 0L) {
                    startTime = time.milliseconds
                }
                offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
                rdd
            }).map(_.value).flatMap(parse)


        val eventStream =
            moneyDistributeLogStream.transform { (moneyDistributeLogRDD, time) =>
                moneyDistributeLogRDD.mapPartitions { deviceBehaviorLogIter =>
                    convertToEvent(
                        deviceBehaviorLogIter, userStatusKdcCacheConfig,
                        userMediaTypeKdcCacheConfig, itemKdcCacheConfig
                    )
                }
            }

        val mapWithStateStream = getMapWithStateStream(eventStream)

        mapWithStateStream.flatMap(_.toList).foreachRDD { (rdd, batchTime) =>
            val outputRDD = rdd.repartition(
                rdd.sparkContext.getConf.getInt(SPARK_OUTPUT_PARTITION, DEFAULT_OUTPUT_PARTITION)
            )

            MySqlSink.save(batchTime, outputRDD, mysqlConfig, outputTable, dimensions)

            KafkaOffsetUtil.saveOffsetToMysql(
                sparkConf.get(SPARK_OFFSET_MYSQL_TABLE),
                kafkaTopicGroup,
                Map(
                    MYSQL_URL -> sparkConf.get(SPARK_OFFSET_MYSQL_URL),
                    MYSQL_USER -> sparkConf.get(SPARK_OFFSET_MYSQL_USER),
                    MYSQL_PASSWORD -> sparkConf.get(SPARK_OFFSET_MYSQL_PW)
                ),
                batchTime.milliseconds,
                offsetRanges
            )
        }

        mapWithStateStream.stateSnapshots().foreachRDD { (rdd, time) =>
            SparkUtils.periodicRDDCheckpoint(rdd, startTime, time.milliseconds)
        }

        streamingContext.start()
        streamingContext.awaitTermination()
    }

}
