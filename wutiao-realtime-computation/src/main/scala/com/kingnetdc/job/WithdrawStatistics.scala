package com.kingnetdc.job

import com.kingnetdc.model.{DurationConfiguration, Event, WithdrawStatus, WithdrawType}
import com.kingnetdc.model.WindowDisplayMode.{EVENT_TIME, FLUSH_TIME}
import com.kingnetdc.model.EventTypeEnum.WITHDRAW
import com.kingnetdc.sink.MySqlSink
import com.kingnetdc.sql.{And, ComputationRule, Equal}
import com.kingnetdc.utils.{JsonUtils, KafkaOffsetUtil}
import com.kingnetdc.watermelon.input.KingnetInputDStream
import com.kingnetdc.sql.StatisticsFunction.{COUNT_DISTINCT, SUM}
import com.kingnetdc.watermelon.utils.AppConstants._
import com.kingnetdc.watermelon.utils._
import com.kingnetdc.watermelon.utils.ConfigurationKeys._
import org.apache.spark.rdd.RDD
import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}
import org.apache.spark.sql.types.{DoubleType, StringType}
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import org.apache.spark.streaming.{Duration, StreamingContext, Time}
import com.kingnetdc.utils.StatisticsUtils._

import scala.collection.JavaConversions._

//scalastyle:off
object WithdrawStatistics extends EventStatistics {

    private val OUID = "ouid"

    private val RMB = "rmb"

    private val status = "status"

    private val withdrawType = "type"

    // 5 min
    private val fiveMin: Long = 1000 * 60 * 5
    // 1 hour
    private val oneHour: Long = 1000 * 3600
    // 1 day
    private val oneDay: Long = 1000 * 3600 * 24

    // 定义计算规则
    override protected val computationRules: List[ComputationRule] = {
        ComputationRule(
            RMB, DoubleType, s"rmb_total_${WithdrawStatus.APPLIED}", SUM.toString,
            Some(Equal(status, WithdrawStatus.APPLIED.id.toString, StringType))
        ) ::
        ComputationRule(
            RMB, DoubleType, s"rmb_total_${WithdrawStatus.SUCCESS}", SUM.toString,
            Some(Equal(status, WithdrawStatus.SUCCESS.id.toString, StringType))
        ) ::
        ComputationRule(
            RMB, DoubleType, s"rmb_total_${WithdrawStatus.FAILED}", SUM.toString,
            Some(Equal(status, WithdrawStatus.FAILED.id.toString, StringType))
        ) ::
        ComputationRule(
            OUID, StringType, s"ouid_total_${WithdrawStatus.APPLIED}", COUNT_DISTINCT.toString,
            Some(Equal(status, WithdrawStatus.APPLIED.id.toString, StringType))
        ) ::
        ComputationRule(
            OUID, StringType, s"ouid_total_${WithdrawStatus.SUCCESS}", COUNT_DISTINCT.toString,
            Some(Equal(status, WithdrawStatus.SUCCESS.id.toString, StringType))
        ) ::
        ComputationRule(
            OUID, StringType, s"ouid_total_${WithdrawStatus.FAILED}", COUNT_DISTINCT.toString,
            Some(Equal(status, WithdrawStatus.FAILED.id.toString, StringType))
        ) ::
        ComputationRule(
            RMB, DoubleType, s"rmb_total_${WithdrawType.COIN}", SUM.toString,
            Some(And(List(Equal(status, WithdrawStatus.APPLIED.id.toString, StringType), Equal(withdrawType, WithdrawType.COIN.id.toString, StringType))))
        ) ::
        ComputationRule(
            RMB, DoubleType, s"rmb_total_${WithdrawType.HONGBAO}", SUM.toString,
            Some(And(List(Equal(status, WithdrawStatus.APPLIED.id.toString, StringType), Equal(withdrawType, WithdrawType.HONGBAO.id.toString, StringType))))
        ) ::
        ComputationRule(
            OUID, StringType, s"ouid_total_${WithdrawType.COIN}", COUNT_DISTINCT.toString,
            Some(And(List(Equal(status, WithdrawStatus.APPLIED.id.toString, StringType), Equal(withdrawType, WithdrawType.COIN.id.toString, StringType))))
        ) ::
        ComputationRule(
            OUID, StringType, s"ouid_total_${WithdrawType.HONGBAO}", COUNT_DISTINCT.toString,
            Some(And(List(Equal(status, WithdrawStatus.APPLIED.id.toString, StringType), Equal(withdrawType, WithdrawType.HONGBAO.id.toString, StringType))))
        ) ::
        Nil
    }

    protected val durationConfigurations: List[DurationConfiguration] = {
        List(
            DurationConfiguration(fiveMin, fiveMin, EVENT_TIME.toString),
            DurationConfiguration(oneHour, fiveMin, EVENT_TIME.toString),
            DurationConfiguration(oneDay, fiveMin, FLUSH_TIME.toString)
        )
    }

    override protected val dimensions: List[String] = List("appver", "channel", "os")

    def parse(line: String, config: Map[String, String]): Option[(Long, String, String, String, String, String, String, Double)] = {
        try {
            val jsonNode = JsonUtils.getJsonNode(line)

            val ouid =
                Option(jsonNode.at("/ouid"))
                    .map(_.asText().toLowerCase)
                    .filter(StringUtils.nonEmpty)
                    .get

            val event = jsonNode.at("/event").asText()

            if (WITHDRAW.toString == event) {
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

                val rmb =
                    Option(jsonNode.at("/properties/rmb"))
                            .map(_.asDouble())
                            .getOrElse(0: Double)

                val status =
                    Option(jsonNode.at("/properties/status"))
                            .map(_.asText())
                            .getOrElse(ABNORMAL_VALUE)

                val withdrawType =
                    Option(jsonNode.at("/properties/type"))
                            .map(_.asText())
                            .getOrElse(ABNORMAL_VALUE)

                val logTimestampStartOpt = config.get("log.timestamp.start").map(_.toLong)

                val logTimestamp = jsonNode.at("/timestamp").asLong()

                if (
                    validLogtime(logTimestamp, logTimestampStartOpt) &&
                    validLogtime(logReceiveTime, logReceiveStartOpt)
                ) {
                    Some((logReceiveTime, ouid, appVersion, appChannel, os, status, withdrawType, rmb))
                } else {
                    None
                }
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

    def convertToEvent(
        withdrawLogIter: Iterator[(Long, String, String, String, String, String, String, Double)]
    ): Iterator[Event] = {
        withdrawLogIter.map {
            case (eventTime, ouid, appVersion, appChannel, os, status, withdrawType, rmb) =>

            new Event(
                WITHDRAW.toString, eventTime,
                buildDimensionValues(appVersion, appChannel, os),
                Map(OUID -> ouid, RMB -> rmb)
            ).setFilterFieldValueMap(
                Map("status" -> status, "type" -> withdrawType)
            )
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
        val kafkaSourceTopic = config("source.topic")
        val kafkaTopicGroup = config("kafka.topic.group")

        val sparkConf = new SparkConf()
        sparkConfCheck(sparkConf)

        val sparkContext = new SparkContext(sparkConf)
        val streamingContext = new StreamingContext(
            sparkContext, Duration(sparkConf.get(SPARK_STREAMING_DURATION).toLong)
        )
        streamingContext.checkpoint(sparkConf.get(SPARK_CP_DIR))

        var startTime = 0L
        var offsetRanges = Array[OffsetRange]()

        val withdrawLogStream =
            KingnetInputDStream.createDirectKafkaStream[String, String](
                streamingContext, getKafkaParams(bootstrapServers, kafkaSourceTopic, kafkaTopicGroup)
            ).transform((rdd, time) => {
                if (startTime == 0L) {
                    startTime = time.milliseconds
                }

                offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
                rdd
            }).map(_.value).flatMap { log =>
                parse(log, config)
            }

        val eventStream =
            withdrawLogStream.transform { (userBehaviorLogRDD, time) =>
                userBehaviorLogRDD.mapPartitions { userBehaviorLogIter =>
                    convertToEvent(userBehaviorLogIter)
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
//scalastyle:on