package com.kingnetdc.job

import com.kingnetdc.metrics.StateEnum
import com.kingnetdc.model.EventTypeEnum.{ACTIVE, ENTERFRONT, LOGIN, OPEN_CLIENT}
import com.kingnetdc.model.WindowDisplayMode.{EVENT_TIME, FLUSH_TIME}
import com.kingnetdc.model._
import com.kingnetdc.sink.MySqlSink
import com.kingnetdc.sql.StatisticsFunction.COUNT_DISTINCT
import com.kingnetdc.sql.{ComputationRule, Equal}
import com.kingnetdc.utils.StatisticsUtils._
import com.kingnetdc.utils.{AbstractOptions, JsonUtils, KafkaOffsetUtil, StatisticsUtils}
import com.kingnetdc.watermelon.input.KingnetInputDStream
import com.kingnetdc.watermelon.utils.AppConstants.ABNORMAL_VALUE
import com.kingnetdc.watermelon.utils.ConfigurationKeys._
import com.kingnetdc.watermelon.utils.{ConfigUtils, SparkUtils, StringUtils}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{ByteType, StringType}
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import org.apache.spark.streaming.{Duration, StreamingContext}
import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}

import scala.collection.JavaConversions._

object ActiveStatistics extends EventStatistics {

    class Options(className: String, args: Seq[String]) extends AbstractOptions(className, args) {}

    //一些必须的变量
    //判新的key
    private val IS_NEW_USER = "is_new_user"
    private val IS_OLD_USER = "is_old_user"
    private val OUID = "ouid"
    private val DID = "did"
    private val BYTE_ONE: Byte = 1
    private val BYTE_ZERO: Byte = 0

    // 时间维度
    private val fiveMin: Long = 1000 * 60 // 1min
    private val oneHour: Long = 1000 * 3600 // 1hour
    private val oneDay: Long = 1000 * 3600 * 24 // 1day

    //定义计算规则
    override protected val computationRules: List[ComputationRule] = {
        ComputationRule(
            OUID, StringType, StateEnum.ACTIVE.toString, COUNT_DISTINCT.toString, None
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
    override protected val dimensions: List[String] = List("appver", "channel")


    private def parse(line: String) = {
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

            Some(
                (eventTime, appVersion, appChannel, ouid, event)
            )
        } catch {
            case e: Exception =>
                logger.error(s"Failed to parse ${line}", e)
                None
        }
    }

    def convertToEvent(
            userBehaviorLogIter: Iterator[(Long, String, String, String, String)],
            kdcCacheConfig: String
    ): Iterator[Event] = {
        val userBehaviorLogs = userBehaviorLogIter.toList

        val ouidMinEventTime = userBehaviorLogs.map { userBehaviorLog =>
            (userBehaviorLog._4, userBehaviorLog._1, userBehaviorLog._3)
        }.groupBy(_._1).mapValues { uidTimePair =>
            val min = uidTimePair.minBy(_._2)
            (min._2, min._3)
        }

        val userFirstActiveTime =
            StatisticsUtils.getOrSetUserFirstActiveTime(kdcCacheConfig, ouidMinEventTime)

        userBehaviorLogs.map {
            case (eventTime, appVersion, appChannel, ouid, event) =>
                val userStatusInCalculationDuration: Map[Long, Boolean] =
                    StatisticsUtils.statusCheck(
                        ouid, eventTime,
                        userFirstActiveTime, List(fiveMin, oneHour, oneDay)
                    )

                new Event(
                    event, eventTime,
                    List(
                        List(appVersion, appChannel),
                        List(appVersion, all),
                        List(all, appChannel),
                        List(all, all)
                    ),
                    Map(OUID -> ouid)
                ).setFilterFieldValueMap(
                    StatisticsUtils.getUserStatusFilterFieldMap(userStatusInCalculationDuration,
                        List(fiveMin, oneHour, oneDay)).toMap
                )
        }.toIterator
    }

    private[job] def getUserStatus(userStatus: Map[Long, Boolean]): List[(String, Byte)] = {
        val userStatusInFiveMin = userStatus.get(fiveMin).getOrElse(false)
        val userStatusInOneHour = userStatus.get(oneHour).getOrElse(false)
        val userStatusInOneDay = userStatus.get(oneDay).getOrElse(false)

        List(
            StateEnum.ACTIVE.toString -> BYTE_ONE,

            s"${StateEnum.NEW}_${fiveMin}" -> userStatusInFiveMin,
            s"${StateEnum.OLD}_${fiveMin}" -> !userStatusInFiveMin,

            s"${StateEnum.NEW}_${oneHour}" -> userStatusInOneHour,
            s"${StateEnum.OLD}_${oneHour}" -> !userStatusInOneHour,

            s"${StateEnum.NEW}_${oneDay}" -> userStatusInOneDay,
            s"${StateEnum.OLD}_${oneDay}" -> !userStatusInOneDay
        )
    }

    private def distributeOuid(
            behaviorLog: RDD[(Long, String, String, String, String)]
    ) = {
        val partitionNum = behaviorLog.sparkContext.getConf.getInt("spark.did.partition", 100)
        behaviorLog.map { log =>
            log._4 -> (log._1, log._2, log._3, log._5)
        }.partitionBy(new HashPartitioner(partitionNum)).map { pair =>
            val (ouid, (eventTime, appVersion, appChannel, event)) = pair
            (eventTime, appVersion, appChannel, ouid, event)
        }
    }

    private def completeDimensions(line: KPIRecord): List[KPIRecord] = {
        List(
            line,
            line.changeDimensionsToAll(Set("appver")),
            line.changeDimensionsToAll(Set("channel")),
            line.changeDimensionsToAll(Set("appver", "channel"))
        )
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
        val bootstrapServers = config("bootstrap.servers")
        val kafkaTopic = config("source.topic")
        val kafkaTopicGroup = config("kafka.topic.group")
        val kdcCacheConfig = config("cache.config")
        val outputTable = config("output.table")
        val influxDBConfig = getInfluxDBConfig(config)
        val mysqlConfig = getMysqlConfig(config)

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
            }).map(_.value).flatMap(parse).filter(log => {
                List(LOGIN, ACTIVE, OPEN_CLIENT, ENTERFRONT).exists(_.toString.equals(log._5))
            }).transform(rdd => distributeOuid(rdd))


        val eventStream =
            userBehaviorLogStream.transform { (userBehaviorLogRDD, time) =>
                userBehaviorLogRDD.mapPartitions { userBehaviorLogIter =>
                    convertToEvent(userBehaviorLogIter, kdcCacheConfig)
                }
            }
        val mapWithStateStream = getMapWithStateStream(eventStream)

        mapWithStateStream.flatMap(_.toList).foreachRDD((rdd, batchTime) => {

//            val allRDD = rdd.flatMap(completeDimensions).map(kpi => {
//                kpi.getDimensionValueString -> kpi
//            }).reduceByKey(_ + _).map(_._2)

            val outputRDD =
                rdd.repartition(rdd.sparkContext.getConf.getInt(SPARK_OUTPUT_PARTITION, DEFAULT_OUTPUT_PARTITION))
            //输出计算结果
            // TODO 使用新的KPIRecord
            // MySqlSink.saveMetricsByColumn(batchTime, outputRDD, mysqlConfig, outputTable, dimensions)
            //提交offset
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
        })

        //控制存储snapshot
        mapWithStateStream.stateSnapshots().foreachRDD { (rdd, time) =>
            SparkUtils.periodicRDDCheckpoint(rdd, startTime, time.milliseconds)
        }

        streamingContext.start()
        streamingContext.awaitTermination()
    }

}
