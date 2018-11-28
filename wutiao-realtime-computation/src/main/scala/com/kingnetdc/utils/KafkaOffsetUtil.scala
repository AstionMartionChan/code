package com.kingnetdc.utils

import com.kingnetdc.watermelon.clients.TopicPartitionOffset
import com.kingnetdc.watermelon.utils.ConfigurationKeys._
import com.kingnetdc.watermelon.utils.{Logging, DateUtils, KafkaOffsetManager}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.OffsetRange
import scala.util.{Failure, Success}

object KafkaOffsetUtil extends Logging {

    def saveOffsetToZookeeper(zkConnect: String, group: String, offsetRanges: Array[OffsetRange]) = {
        val topicPartitionOffsets =
            offsetRanges.toList.map { offsetRange =>
                TopicPartitionOffset(offsetRange.topic, offsetRange.partition, offsetRange.untilOffset)
            }

        KafkaOffsetManager.saveToZookeeper(
            zkConnect, group, topicPartitionOffsets
        ) match {
            case Success(_) =>
            case Failure(e) => logger.error(s"Failed to save offset", e)
        }
    }

    def periodicSaveOffsetToMysql(
        sparkConf: SparkConf, topicGroup: String,
        startTime: Long, batchTime: Long, offsetRanges: Array[OffsetRange]
    ) = {
        val table = sparkConf.get(SPARK_OFFSET_MYSQL_TABLE)

        val mysqlConfig = Map(
            MYSQL_URL -> sparkConf.get(SPARK_OFFSET_MYSQL_URL),
            MYSQL_USER -> sparkConf.get(SPARK_OFFSET_MYSQL_USER),
            MYSQL_PASSWORD -> sparkConf.get(SPARK_OFFSET_MYSQL_PW)
        )

        val saveInterval = sparkConf.getLong(SPARK_KAFKA_OFFSET_INTERVAL, DEFAULT_SPARK_KAFKA_OFFSET_INTERVAL)

        if (batchTime != startTime && (batchTime - startTime) % saveInterval == 0) {
            val topicPartitionOffsets =
                offsetRanges.toList.map { offsetRange =>
                    TopicPartitionOffset(offsetRange.topic, offsetRange.partition, offsetRange.untilOffset)
                }

            KafkaOffsetManager.saveToMySQL(
                table,
                mysqlConfig,
                DateUtils.getYMDHMS.format(batchTime),
                topicGroup,
                topicPartitionOffsets
            ) match {
                case Success(_) =>
                case Failure(ex) =>
                    logger.error(s"Failed to save offsets for ${topicGroup}", ex)
            }
        }
    }

    def saveOffsetToMysql(
        table: String,  topicGroup: String, config: Map[String, String],
        batchTime: Long, offsetRanges: Array[OffsetRange]
    ) = {
        val topicPartitionOffsets =
            offsetRanges.toList.map { offsetRange =>
                TopicPartitionOffset(offsetRange.topic, offsetRange.partition, offsetRange.untilOffset)
            }

        KafkaOffsetManager.saveToMySQL(
            table,
            config, DateUtils.getYMDHMS.format(batchTime), topicGroup,
            topicPartitionOffsets
        ) match {
            case Success(_) =>
            case Failure(ex) =>
                logger.error(s"Failed to save offsets for ${topicGroup}", ex)
        }
    }

}