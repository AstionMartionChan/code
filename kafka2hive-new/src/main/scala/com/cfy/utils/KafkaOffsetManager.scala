package com.cfy.utils

import org.apache.kafka.common.TopicPartition
import org.apache.spark.SparkConf
import com.cfy.constants.ConfigurationKey._
import com.cfy.log.Logging
import org.apache.spark.streaming.kafka010.OffsetRange

object KafkaOffsetManager extends Logging{

  def getOffsets(sparkConf: SparkConf, kafkaParams: Map[String, AnyRef]) = {
    check(sparkConf, kafkaParams)
    val topics = kafkaParams.get(KAFKA_TOPICS).get.toString.split(COMMA).toList
    val group = kafkaParams.get(GROUP).get.toString
    val offsetStorage = sparkConf.get(KAFKA_OFFSET_STORAGE).toString

    offsetStorage match {
      case MYSQL => {
        require(sparkConf.contains(KAFKA_OFFSET_MYSQL_TABLE), s"config ${KAFKA_OFFSET_MYSQL_TABLE} is missing")
        val tableName = sparkConf.get(KAFKA_OFFSET_MYSQL_TABLE).toString
        topics.map(topic => {
          val sql = getSelectSql(tableName, topic, group)

          if (logger.isDebugEnabled){
            logger.info(s"sql: ${sql}")
          }

          val topicPartitionOffsets = JdbcHandler.select(sql) { rs =>
            (rs.string(1), rs.int(2), rs.long(3))
          }.map(t => (new TopicPartition(t._1, t._2) -> t._3)).toMap

          (topic -> topicPartitionOffsets)
        }).toMap
      }
      case ZK => {
        require(sparkConf.contains(ZK_CONNECT), s"config ${ZK_CONNECT} is missing")
        topics.map(topic => {
          val client = ZkClient.getInstance(sparkConf.get(ZK_CONNECT))
          val topicPartitionOffsets = CommonUtil.safeRelease(client)(_.getOffsets(topic, group))

          (topic -> topicPartitionOffsets)
        }).toMap
      }
      case _ => throw new UnsupportedOperationException(s"${offsetStorage.toString}")
    }
  }


  def saveOffsets(sparkConf: SparkConf,
                        offsetRanges: Array[OffsetRange],
                        batchTime: String,
                        group: String) = {

    val offsetStorage = sparkConf.get(KAFKA_OFFSET_STORAGE).toString

    offsetStorage match {
      case MYSQL => {
        val tableName = sparkConf.get(KAFKA_OFFSET_MYSQL_TABLE).toString
        val batchParams = offsetRanges.toList.map(o => Seq(group, o.topic, o.partition, o.untilOffset, batchTime)).toSeq
        val sql = getInsertSql(tableName)

        JdbcHandler.insertOrUpdate(sql, batchParams)
      }
      case ZK => {
        require(sparkConf.contains(ZK_CONNECT), s"config ${ZK_CONNECT} is missing")
        offsetRanges.toList.map(offset => {
          val client = ZkClient.getInstance(sparkConf.get(ZK_CONNECT))
          CommonUtil.safeRelease(client)(_.commitOffsets(offset.topic, group, offset.partition, offset.untilOffset))
        })
      }
      case _ => throw new UnsupportedOperationException(s"${offsetStorage.toString}")
    }
  }


  private def check(sparkConf: SparkConf, kafkaParams: Map[String, AnyRef] ): Unit = {
    require(kafkaParams.contains(KAFKA_TOPICS), s"${KAFKA_TOPICS} is missing")
    require(kafkaParams.contains(GROUP), s"${GROUP} is missing")
    require(sparkConf.contains(KAFKA_OFFSET_STORAGE), s"${KAFKA_OFFSET_STORAGE} is missing")
  }

  private def getInsertSql(tableName: String) = {
    s"""
     INSERT INTO ${tableName} (
        `group`,
        `topic`,
        `partition`,
        `offset`,
        `batch_time`
     ) VALUES (
        ?,
        ?,
        ?,
        ?,
        ?
     )
    """
  }

  private def getSelectSql(tableName: String, topic: String, group: String) = {
    s"""
      SELECT
          `topic`,
          `partition`,
          `offset`
      FROM
          ${tableName}
      WHERE
          `topic` = '${topic}'
      AND
          `group` = '${group}'
      AND
          `batch_time` =
          (
            SELECT
                `batch_time`
            FROM
                ${tableName}
            WHERE
                `topic` = '${topic}'
            AND
                `group` = '${group}'
            ORDER BY
                `batch_time` DESC
            LIMIT 1
          )
    """
  }
}
