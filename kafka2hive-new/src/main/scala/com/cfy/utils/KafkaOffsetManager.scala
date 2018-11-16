package com.cfy.utils

import org.apache.kafka.common.TopicPartition
import org.apache.spark.SparkConf
import com.cfy.constants.ConfigurationKey._
import com.cfy.log.Logging
import org.apache.spark.streaming.kafka010.OffsetRange

object KafkaOffsetManager extends Logging{

  def getPartitionOffset(sparkConf: SparkConf, kafkaParams: Map[String, AnyRef]) = {
    check(sparkConf, kafkaParams)
    val topics = kafkaParams.get(KAFKA_TOPIC).get.toString.split(COMMA).toList
    val group = kafkaParams.get(GROUP).get.toString
    val tableName = sparkConf.get(KAFKA_OFFSET_MYSQL_TABLE).toString

    val sql =
      s"""
         SELECT
              `topic`,
              `partition`,
              `offset`
         FROM
              ${tableName}
         WHERE
              `topic` in (${topics.mkString("'","','","'")})
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
                    `topic` = '${topics.head}'
                AND
                    `group` = '${group}'
                ORDER BY
                    `batch_time` DESC
                LIMIT 1
              )
      """

    logger.info(s"sql: ${sql}")
    var topicPartitionOffsets = Map[TopicPartition, Long]()
    JdbcHandler.select(sql) { rs =>
      (rs.string(1), rs.int(2), rs.int(3))
    }.map(t => topicPartitionOffsets += (new TopicPartition(t._1, t._2) -> t._3))

    (topics, topicPartitionOffsets)
  }


  def saveOffsetToMysql(sparkConf: SparkConf,
                        offsetRanges: Array[OffsetRange],
                        batchTime: String,
                        group: String) = {
    val tableName = sparkConf.get(KAFKA_OFFSET_MYSQL_TABLE).toString
    val batchParams = offsetRanges.toList.map(o => Seq(group, o.topic, o.partition, o.untilOffset, batchTime)).toSeq
    val sql =
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
    JdbcHandler.insertOrUpdate(sql, batchParams)
  }


  private def check(sparkConf: SparkConf, kafkaParams: Map[String, AnyRef] ): Unit = {
    require(sparkConf.contains(KAFKA_OFFSET_MYSQL_TABLE), s"config ${KAFKA_OFFSET_MYSQL_TABLE} is missing")
    require(kafkaParams.contains(KAFKA_TOPIC), s"${KAFKA_TOPIC} is missing")
    require(kafkaParams.contains(GROUP), s"${GROUP} is missing")
  }

}
