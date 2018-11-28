package com.kingnetdc.job

import com.google.common.annotations.VisibleForTesting
import com.kingnetdc.model.{WBRevenue, WBRevenueLog, ApplicationConfigParser, ApplicationConfig}
import com.kingnetdc.watermelon.utils.ConfigurationKeys._
import com.kingnetdc.utils.{Tuple2Ordering, Tuple2Partitioner, AbstractOptions}
import com.kingnetdc.watermelon.clients.TopicPartitionOffset
import com.kingnetdc.watermelon.input.KingnetInputDStream
import com.kingnetdc.watermelon.output.MysqlSink
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import org.apache.spark.streaming.{Duration, StreamingContext}
import org.apache.spark.{Partitioner, SparkContext, SparkConf}
import com.kingnetdc.watermelon.utils.{Logging, StringUtils, SparkUtils, CommonUtils}
import com.kingnetdc.watermelon.utils.{DateUtils, KafkaOffsetManager, ConfigurationKeys}
import org.apache.spark.streaming.{State, StateSpec, Time}
import org.rogach.scallop.Scallop
import scala.collection.mutable.ListBuffer
import scala.util.{Success, Failure}

@Deprecated
class WBRevenueWrapper(val itemType: Int) extends Serializable with Logging {

    private var window: Long = 0L

    def setWindow(window: Long) = {
        this.window = window
        this
    }

    private val revenueBuffer: ListBuffer[WBRevenue] = ListBuffer[WBRevenue]()

    def checkTimeoutAndFlush(currentWindow: Long): List[WBRevenue] = {
        var list: List[WBRevenue] = Nil
        if (currentWindow > this.window) {
            list = List(revenueBuffer: _*)
            revenueBuffer.clear()
            if (logger.isDebugEnabled) {
                logger.debug(
                    s"Window updated: ${DateUtils.getYMDHMS.format(currentWindow)}," +
                            s" old window: ${DateUtils.getYMDHMS.format(window)}"
                )
            }
            this.window = currentWindow
        }
        list
    }

    def add(wbRevenue: WBRevenue): Unit = {
        revenueBuffer += wbRevenue
    }

}


object WBRevenueStatistics extends Logging {


    class Options(className: String, args: Seq[String]) extends AbstractOptions(className, args) {}


    private def getKafkaParams(applicationConfig: ApplicationConfig, options: Options): Map[String, Object] = {
        applicationConfig.getKafkaConfig +
                (
                        ConfigurationKeys.TOPIC -> options.topic,
                        ConfigurationKeys.KAFKA_GROUP -> options.group,
                        "key.deserializer" -> classOf[StringDeserializer],
                        "value.deserializer" -> classOf[StringDeserializer],
                        "enable.auto.commit" -> (false: java.lang.Boolean)
                        )
    }

    private def mappingFunction(
        batchTime: Time, itemType: Int,
        wbRevenueOpt: Option[WBRevenue],
        wbRevenueInDurationState: State[WBRevenueWrapper]
    ): Option[ListBuffer[WBRevenue]] = {
        val mappedDataOut: ListBuffer[WBRevenue] = ListBuffer[WBRevenue]()

        wbRevenueOpt match {
            case Some(revenue) =>
                val wbRevenueWrapper =
                    wbRevenueInDurationState.getOption().getOrElse {
                        if (logger.isDebugEnabled) {
                            logger.debug(s"Start window for revenue calculation: ${DateUtils.getYMDHMS.format(revenue.eventTime)}")
                        }
                        new WBRevenueWrapper(itemType).setWindow(revenue.eventTime)
                    }

                if (logger.isDebugEnabled) {
                    logger.debug(s"ItemType: ${itemType}, Revenue eventTime: ${DateUtils.getYMDHMS.format(revenue.eventTime)}")
                }
                mappedDataOut ++= wbRevenueWrapper.checkTimeoutAndFlush(revenue.eventTime)
                wbRevenueWrapper.add(revenue)
                wbRevenueInDurationState.update(wbRevenueWrapper)
            case _ =>
        }

        Some(mappedDataOut)
    }

    @VisibleForTesting
    private[job] def calculateRevenueInGroup(wbRevenueRDD: RDD[WBRevenue]) = {
        val sparkSession = SparkSession.builder().config(wbRevenueRDD.sparkContext.getConf).getOrCreate()

        import sparkSession.implicits._

        val revenueDataFrame =
            wbRevenueRDD.map { wbRevenue =>
                (
                        wbRevenue.itemType, wbRevenue.eventTime, wbRevenue.itemId,
                        wbRevenue.revenuePercentageByType, wbRevenue.scoreBasedOnOperation,
                        wbRevenue.coin, wbRevenue.uniqueIdentifier
                        )
            }.toDF("item_type", "event_time", "item_id", "Y", "A", "coin", "unique_identifier")
                    .dropDuplicates("unique_identifier")
                    .drop("unique_identifier")
                    .persist(StorageLevel.MEMORY_AND_DISK_SER)

        if (logger.isDebugEnabled()) {
            revenueDataFrame.printSchema()
            revenueDataFrame.show()
        }

        /*
          root
           |-- item_type: integer (nullable = false)
           |-- event_time: long (nullable = false)
           |-- item_id: string (nullable = true)
           |-- Y: double (nullable = false)
           |-- A: double (nullable = false)
           |-- coin: double (nullable = false)
           |-- unique_identifier: string (nullable = true)
           |-- total_A: double (nullable = true)
           |-- revenue: double (nullable = true)
        */

        val resultDataFrame =
            revenueDataFrame
                    .withColumn("total_A", sum("A") over Window.partitionBy("item_type", "event_time"))
                    .withColumn("event_time", from_unixtime(col("event_time") / 1000))
                    .withColumn(
                        "revenue",
                        when($"total_A" === lit(0) || $"A" === lit(0), lit(0)).otherwise(
                            (col("Y") * col("A")).divide(col("total_A"))
                        )
                    )

        if (logger.isDebugEnabled()) {
            resultDataFrame.printSchema()
            resultDataFrame.explain()
            resultDataFrame.show()
        }

        resultDataFrame
    }

    private def saveOffsetToMySQL(
        batchTime: Time, offsetRanges: Array[OffsetRange],
        sparkConf: SparkConf, consumerGroup: String
    ) = {
        import com.kingnetdc.watermelon.utils.ConfigurationKeys._

        val mysqlConfig = Map(
            MYSQL_URL -> sparkConf.get(SPARK_OFFSET_MYSQL_URL),
            MYSQL_USER -> sparkConf.get(SPARK_OFFSET_MYSQL_USER),
            MYSQL_PASSWORD -> sparkConf.get(SPARK_OFFSET_MYSQL_PW)
        )
        val tableName = sparkConf.get(SPARK_OFFSET_MYSQL_TABLE)

        val topicPartitionOffsets =
            offsetRanges.toList.map { offsetRange =>
                TopicPartitionOffset(offsetRange.topic, offsetRange.partition, offsetRange.untilOffset)
            }

        val insertTry =
            KafkaOffsetManager.saveToMySQL(
                tableName, mysqlConfig, DateUtils.getYMDHMS.format(batchTime.milliseconds), consumerGroup, topicPartitionOffsets
            )

        insertTry match {
            case Success(_) =>
            case Failure(e) =>
                logger.error(
                    s"Failed to save offset for batch: ${DateUtils.getYMDHMS.format(batchTime.milliseconds)}", e
                )
        }
    }

    private def saveRevenueDataFrameToMySQL(
        batchTime: Time, revenueDataFrame: DataFrame,
        mysqlConfig: Map[String, String], table: String
    ) = {
        val columns = revenueDataFrame.schema.map(_.name).toList
        val sparkConf = revenueDataFrame.sqlContext.sparkContext.getConf
        val outputPartition = sparkConf.getInt(SPARK_OUTPUT_PARTITION, DEFAULT_OUTPUT_PARTITION)

        revenueDataFrame.repartition(outputPartition).foreachPartition { rowIter =>
            if (rowIter.nonEmpty) {
                val mysqlSink = new MysqlSink(mysqlConfig)
                val insertTry =
                    CommonUtils.safeRelease(mysqlSink)(mysqlSink => {
                        // 注意顺序
                        val rowValueIter = rowIter.map { row =>
                            columns.map(row.getAs[AnyRef])
                        }
                        // 不需要更新, 所以传Nil
                        mysqlSink.insertOrUpdate(table, columns, rowValueIter, Nil)
                    })()

                insertTry match {
                    case Success(_) =>
                    case Failure(e) =>
                        logger.error(
                            s"Failed to insert revenue records for batch: ${DateUtils.getYMDHMS.format(batchTime.milliseconds)}", e
                        )
                }
            }
        }
    }

    private def sparkConfCheck(sparkConf: SparkConf) = {
        require(sparkConf.contains(ConfigurationKeys.SPARK_CP_DIR), s"${ConfigurationKeys.SPARK_CP_DIR} is missing")
        require(sparkConf.contains(SPARK_STREAMING_DURATION), s"${SPARK_STREAMING_DURATION} is missing")
        require(
            sparkConf.contains(ConfigurationKeys.SPARK_RDD_CP_PATH),
            s"${ConfigurationKeys.SPARK_RDD_CP_PATH} is missing"
        )
    }

    private def getInitialRDD(sparkContext: SparkContext): RDD[(Int, WBRevenueWrapper)] = {
        val sparkConf = sparkContext.getConf
        SparkUtils.getInitialRDD(
            sparkContext,
            sparkConf.get(ConfigurationKeys.SPARK_RDD_CP_PATH),
            sparkConf.getOption(ConfigurationKeys.SPARK_RDD_CP_RESTORE_FROM)
        )
    }

    def main(args: Array[String]) {
        val options = new Options(simpleClassName, args)
        options.verify

        val applicationConfig = ApplicationConfigParser.loadFromResources(s"application-${options.env}.yml")

        val sparkConf = new SparkConf()
        sparkConfCheck(sparkConf)

        val sparkContext = new SparkContext(sparkConf)
        val streamingContext = new StreamingContext(
            sparkContext, Duration(sparkConf.get(SPARK_STREAMING_DURATION).toLong)
        )
        streamingContext.checkpoint(sparkConf.get(ConfigurationKeys.SPARK_CP_DIR))

        var offsetRanges = Array[OffsetRange]()
        var startTime: Long = 0L

        val table = ""
        val group = options.group

        val partitioner: Partitioner = new Tuple2Partitioner(25)
        implicit val tupleOrdering = new Tuple2Ordering[String, Long]

        // 解析
        val wbRevenueStream =
            KingnetInputDStream.createDirectKafkaStream[String, String](
                streamingContext,
                getKafkaParams(applicationConfig, options)
            ).transform { (rdd, time) =>
                offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges

                if (startTime == 0L) {
                    startTime = time.milliseconds
                }

                rdd
            }.flatMap { consumerRecord =>
                WBRevenueLog.convert(consumerRecord)
            }.map { wbRevenue =>
                (wbRevenue.itemType, wbRevenue.eventTime) -> wbRevenue
            }.transform { rdd =>
                rdd.repartitionAndSortWithinPartitions(partitioner).mapPartitions { iter =>
                    iter.map {
                        case ((itemtype, _), wbRevenue) => (itemtype, wbRevenue)
                    }
                }
            }

        val mapWithStateStream = wbRevenueStream.mapWithState(
            StateSpec
                    .function(mappingFunction _)
                    .initialState(getInitialRDD(sparkContext))
        )

        mapWithStateStream.flatMap(identity).foreachRDD { (wbRevenueRDD, time) =>
            if (!wbRevenueRDD.isEmpty()) {
                val revenueDataFrame = calculateRevenueInGroup(wbRevenueRDD)

                // save to storage
                saveRevenueDataFrameToMySQL(time, revenueDataFrame, applicationConfig.getMySqlConfig, table)
                // save offset
                saveOffsetToMySQL(time, offsetRanges, sparkConf, group)
            }
        }

        mapWithStateStream.stateSnapshots().foreachRDD { (rdd, time) =>
            SparkUtils.periodicRDDCheckpoint(rdd, startTime, time.milliseconds)
        }

        streamingContext.start()
        streamingContext.awaitTermination()
    }

}
