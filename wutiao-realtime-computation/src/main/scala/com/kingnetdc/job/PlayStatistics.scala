package com.kingnetdc.job

import com.kingnetdc.metrics.StateEnum
import com.kingnetdc.sql.StatisticsFunction.COUNT
import com.kingnetdc.model._
import com.kingnetdc.model.WindowDisplayMode.{EVENT_TIME, FLUSH_TIME}
import com.kingnetdc.model.EventTypeEnum.{PLAY, READ}
import com.kingnetdc.sink.{InfluxDBSink, MySqlSink}
import com.kingnetdc.sql.{ComputationRule, Equal}
import com.kingnetdc.utils.{AbstractOptions, JsonUtils, KafkaOffsetUtil, StatisticsUtils}
import com.kingnetdc.watermelon.input.KingnetInputDStream
import com.kingnetdc.watermelon.utils.AppConstants.ABNORMAL_VALUE
import com.kingnetdc.watermelon.utils.{CommonUtils, SparkUtils, StringUtils}
import com.kingnetdc.watermelon.utils.ConfigurationKeys._
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.types.{ByteType, StringType}
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import org.apache.spark.streaming.{Duration, StreamingContext}
import com.kingnetdc.utils.StatisticsUtils._

import scala.collection.JavaConversions._
import scala.util.{Failure, Success}

object PlayStatistics extends EventStatistics {

    class Options(className: String, args: Seq[String]) extends AbstractOptions(className, args) {}

    //一些必须的变量
    //判新的key
    private val IS_NEW_USER = "is_new_user"
    private val IS_OLD_USER = "is_old_user"
    private val OUID = "ouid"
    private val BYTE_ONE: Byte = 1
    private val BYTE_ZERO: Byte = 0

    // 时间维度
    private val fiveMin: Long = 1000 * 60 * 5 // 1min
    private val oneHour: Long = 1000 * 3600 // 1hour
    private val oneDay: Long = 1000 * 3600 * 24 // 1day

    //定义计算规则
    override protected val computationRules: List[ComputationRule] = {
        ComputationRule(
            OUID, StringType, StateEnum.ACTIVE.toString, COUNT.toString, None
        ) ::
                ComputationRule(
                    OUID, StringType, s"${StateEnum.NEW.toString}_${fiveMin}", COUNT.toString,
                    Some(Equal(s"${IS_NEW_USER}_${fiveMin}", 1, ByteType))
                ) ::
                ComputationRule(
                    OUID, StringType, s"${StateEnum.OLD.toString}_${fiveMin}", COUNT.toString,
                    Some(Equal(s"${IS_OLD_USER}_${fiveMin}", 0, ByteType))
                ) ::
                ComputationRule(
                    OUID, StringType, s"${StateEnum.NEW.toString}_${oneHour}", COUNT.toString,
                    Some(Equal(s"${IS_NEW_USER}_${oneHour}", 1, ByteType))
                ) ::
                ComputationRule(
                    OUID, StringType, s"${StateEnum.OLD.toString}_${oneHour}", COUNT.toString,
                    Some(Equal(s"${IS_OLD_USER}_${oneHour}", 0, ByteType))
                ) ::
                ComputationRule(
                    OUID, StringType, s"${StateEnum.NEW.toString}_${oneDay}", COUNT.toString,
                    Some(Equal(s"${IS_NEW_USER}_${oneDay}", 1, ByteType))
                ) ::
                ComputationRule(
                    OUID, StringType, s"${StateEnum.OLD.toString}_${oneDay}", COUNT.toString,
                    Some(Equal(s"${IS_OLD_USER}_${oneDay}", 0, ByteType))
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
    override protected val dimensions: List[String] = List("appver", "channel", "sortid")

    private def getKafkaParams(applicationConfig: ApplicationConfig, options: Options): Map[String, Object] = {
        // TODO 返回kafka配置
        applicationConfig.getKafkaConfig + (
                TOPIC -> options.topic,
                "group.id" -> options.group,
                "key.deserializer" -> classOf[StringDeserializer],
                "value.deserializer" -> classOf[StringDeserializer],
                "enable.auto.commit" -> (false: java.lang.Boolean)
        )
    }

    private def parseAsPlayEvent(line: String): Option[Event] = {
        // TODO 将日志转化为Event类
        try {
            val jsonNode = JsonUtils.getJsonNode(line)
            val event = jsonNode.at("/event").asText()
            val eventTime = jsonNode.at("/timestamp").asLong()
            // just let the error throw
            val ouid = Option(jsonNode.at("/ouid")).map(_.asText()).filter(StringUtils.nonEmpty).get

            val appVersion =
                Option(jsonNode.at("/properties/_appver")).map(_.asText()).getOrElse(ABNORMAL_VALUE)

            val appChannel =
                Option(jsonNode.at("/properties/_channel")).map(_.asText()).getOrElse(ABNORMAL_VALUE)

            val sortId =
                Option(jsonNode.at("/properties/sortid")).map(_.asText()).getOrElse(ABNORMAL_VALUE)

            val playType =
                Option(jsonNode.at("/properties/type")).map(_.asInt()).getOrElse(-1)

            playType match {
                case 1 => {
                    Some(
                        new Event(
                            event, eventTime,
                            List(List(appVersion, appChannel, sortId)), Map(OUID -> ouid)
                        )
                    )
                }
                case _ => None
            }

        } catch {
            case e: Exception =>
                logger.error(s"Failed to parse ${line}", e)
                None
        }
    }

    private def sparkConfCheck(sparkConf: SparkConf) = {
        // TODO 检查spark配置
        require(sparkConf.contains(SPARK_CP_DIR), s"${SPARK_CP_DIR} is missing")
        require(sparkConf.contains(SPARK_STREAMING_DURATION), s"${SPARK_STREAMING_DURATION} is missing")
    }

    def main(args: Array[String]) = {
        val options = new Options(simpleClassName, args)
        options.verify

        val applicationConfig = ApplicationConfigParser.loadFromResources(s"application-${options.env}.yml")
        val cacheConfigPath = s"cache-${options.env}.yml"

        val sparkConf = new SparkConf()
        sparkConfCheck(sparkConf)

        val sparkContext = new SparkContext(sparkConf)
        val streamingContext =
            new StreamingContext(sparkContext, Duration(sparkConf.get(SPARK_STREAMING_DURATION).toLong))
        streamingContext.checkpoint(sparkConf.get(SPARK_CP_DIR))

        var offsetRanges = Array[OffsetRange]()
        var startTime = 0L
        val eventStream =
            KingnetInputDStream.createDirectKafkaStream[String, String](
                streamingContext, getKafkaParams(applicationConfig, options)
            ).transform((rdd, time) => {
                if (startTime == 0L) {
                    startTime = time.milliseconds
                }
                offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
                rdd
            }).map(_.value).flatMap(parseAsPlayEvent).filter(event => {
                List(PLAY).exists(_.toString.equals(event.name))
            })

        val withNewCheckedDStream =
            eventStream.transform((eventRDD, time) => {
                eventRDD.mapPartitions(eventIter => {
                    val list = eventIter.toList
                    val ouids = list.groupBy(_.fieldValueMap.get(OUID).get.toString).flatMap(x => {
                        Map(x._1 -> x._2.reduce((a, b) => if (a.time < b.time) a else b))
                    }).map(x => {
                        x._1 -> x._2.time
                    })
                    val userKeyValue = StatisticsUtils.getUserPairs(cacheConfigPath, ouids, false)
                    list.toIterator.map(event => {
                        val isNewUser = StatisticsUtils.checkNew(
                            userKeyValue, event.fieldValueMap.get(OUID).get.toString,
                            event.time, time.milliseconds,
                            List(fiveMin, oneHour, oneDay)
                        )
                        event.setFilterFieldValueMap(
                            StatisticsUtils.getUserStatusFilterFieldMap(isNewUser, List(fiveMin, oneHour, oneDay))
                        )
                    })
                })
            })

        val mapWithStateStream = getMapWithStateStream(withNewCheckedDStream)

        // TODO offset提交修复
        val table = options.output
        val group = options.group

        mapWithStateStream.flatMap(_.toList).foreachRDD((rdd, batchTime) => {
            val outputRDD = rdd.repartition(
                rdd.sparkContext.getConf.getInt(SPARK_OUTPUT_PARTITION, DEFAULT_OUTPUT_PARTITION)
            )
            //输出计算结果
            InfluxDBSink.save(batchTime, outputRDD, applicationConfig.getInfluxDBConfig,
                table, dimensions, StatisticsType.STATISTICS_READ)
            //提交offset
            KafkaOffsetUtil.saveOffsetToMysql(
                sparkConf.get(SPARK_OFFSET_MYSQL_TABLE),
                group,
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
