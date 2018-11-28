package com.kingnetdc.offline.job

import com.kingnetdc.blueberry.cache.KdcCache
import com.kingnetdc.offline.model.NotificationChannelEnum
import com.kingnetdc.offline.utils.SQLBuilder._
import com.kingnetdc.watermelon.output.KafkaSink
import com.kingnetdc.watermelon.utils.AppConstants._
import com.kingnetdc.watermelon.utils.ConfigurationKeys._
import com.kingnetdc.watermelon.utils.SparkUtils._
import com.kingnetdc.watermelon.utils.{MessageAlert, Logging}
import org.apache.kafka.clients.producer.{ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql._
import org.joda.time.DateTime
import scala.collection.JavaConversions._
import scala.util.control.NonFatal

/**
* Created by zhouml on 27/05/2018.
*/
trait BaseStatistics extends Serializable with Logging {

    protected val VALID_ITEM_STATUS = "0"

    protected val DEFAULT_RECEIVERS = "weixin:zhouml@kingnet.com,zhoujiongyu@kingnet.com,chenfy@wutiao.com"

    protected val RECEIVERS_KEY = "notification.receivers"

    protected def getKafkaParams(config: Map[String, String]): Map[String, Object] = {
        val bootstrapServers: String = config("bootstrap.servers")
        // 5m
        val maxRequestSize = config.get(ProducerConfig.MAX_REQUEST_SIZE_CONFIG).map(_.toInt).getOrElse(5242880)
        val retryTimes = config.get(ProducerConfig.RETRIES_CONFIG).map(_.toInt).getOrElse(3)

        Map(
            "bootstrap.servers" -> bootstrapServers,
            "key.serializer" -> classOf[StringSerializer],
            "value.serializer" -> classOf[StringSerializer],
            ProducerConfig.MAX_REQUEST_SIZE_CONFIG -> Integer.valueOf(maxRequestSize),
            ProducerConfig.RETRIES_CONFIG -> Integer.valueOf(retryTimes)
        )
    }


    def buildSuccessPath(sparkContext: SparkContext, successMarkPath: String, time: DateTime) = {
        val timeFormat = "yyyy-MM-dd-HH"
        s"${successMarkPath}/${time.toString(timeFormat)}_${sparkContext.appName}"
    }

    def touchSuccessMark(sparkContext: SparkContext, successMarkPath: String, time: DateTime) = {
        val path = buildSuccessPath(sparkContext, successMarkPath, time)
        touchFile(sparkContext, path)
    }

    /**
    * 将各种中间表输出到Hive中
      *
      * @param resultDataFrame 中间表对应的DataFrame
    * @param tableName 输出表名
      *
      * @return
    */
    def saveResultDataFrameToHive(resultDataFrame: DataFrame, tableName: String) {
        val tempView = "tbl_temp"

        val conf = resultDataFrame.sqlContext.sparkContext.getConf
        val partitionNum = conf.getInt(SPARK_OUTPUT_PARTITION, 50)

        resultDataFrame.repartition(partitionNum).createOrReplaceTempView(tempView)
        val columnName = resultDataFrame.schema.map(_.name).mkString(",")

        val insertSql =
            s"""
               insert overwrite table ${tableName} partition(${dayPartitionName})
               select ${columnName}, from_unixtime(unix_timestamp(), '${YMD}') as ${dayPartitionName}
               from ${tempView}
            """

        resultDataFrame.sparkSession.sql(insertSql)
    }

    /**
      * 将top N结果发送到Kafka
      *
      * @param resultDataFrame 结果表
      * @param rowIterToString 将分区转换成对应的JSON String
      * @param identifier json的标识, 如 wallet_daily_rank、wallet_total_rank、discover_daily_rank、discover_total_rank
      * @param producerBroadCast 生产者广播
      * @param topic 话题
      */
    def saveTopNToKafka(
        resultDataFrame: DataFrame,
        rowIterToString: (Iterator[Row], String) => String, identifier: String,
        producerBroadCast: Broadcast[KafkaSink[String, String]],
        topic: String
    ) = {
        val resultRow = resultDataFrame.collect()

        if (resultRow.nonEmpty) {
            val kafkaSink = producerBroadCast.value
            val recordValue = rowIterToString(resultRow.iterator, identifier)
            val record = new ProducerRecord[String, String](topic, recordValue)

            // Fail fast
            kafkaSink.send(record).get()
        } else {
            logger.warn(s"No data for ${identifier}")
        }
    }

    def getItemStatusAndCreateTime(
        itemIds: List[String], keyPrefix: String, kdcCache: KdcCache
    ): Map[String, (String, Long)] = {
        val prefixedItemIds = itemIds.map { itemId => s"${keyPrefix}${itemId}"}
        val itemStatusMap = kdcCache.multiGet(prefixedItemIds)

        itemStatusMap.toMap.flatMap {
            case (prefixedItemId, info) =>
                Option(info).map { value =>
                    val status = value.split(COMMA)(0)
                    val ts = value.split(COMMA)(1)
                    prefixedItemId.stripPrefix(keyPrefix) -> (status, ts.toLong)
                }
        }
    }

    def filterOutDeleted(
        itemRecommend: List[(String, Double)],
        itemInfo: Map[String, (String, Long)]
    ): List[(String, Double)] = {
        itemRecommend.flatMap {
            case (itemId, score) =>
                itemInfo.get(itemId).flatMap {
                    case (status, createdAt) =>
                        if (status == VALID_ITEM_STATUS) {
                            Some((itemId, score, createdAt))
                        } else None
                }
        }.sortWith {
            case (pair1, pair2) => pair1._3 > pair2._3
        }.map { pair =>
            pair._1 -> pair._2
        }
    }


    def alertByChannel(message: String, config: Map[String, String]) = {
        val notificationReceivers =
            config.get(RECEIVERS_KEY).getOrElse(DEFAULT_RECEIVERS)

        try {
            val channel :: receivers :: Nil = notificationReceivers.split(COLON).toList

            if (channel == NotificationChannelEnum.Phone.toString) {
                MessageAlert.sendToPhone(message, receivers)
            } else {
                MessageAlert.sendToWeixin(message, receivers)
            }
        } catch {
            case NonFatal(t) =>
                logger.error(s"Failed to send ${message} to ${notificationReceivers}", t)
        }
    }

}
