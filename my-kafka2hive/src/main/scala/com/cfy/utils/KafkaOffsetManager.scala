package com.cfy.utils

import java.sql.ResultSet

import com.cfy.jdbc.JDBCHandler
import org.apache.kafka.common.TopicPartition

import scala.util.{Failure, Success}

object KafkaOffsetManager {


  def saveOffsetToMysql(consumerGroup: String, tableName: String) :Map[TopicPartition, Long] = {
    val sql =
      s"""
        select
              topic,
              partition,
              offset
        from
              ${tableName}
        where
              group = '${consumerGroup}'
      """


    val resultSetProccess = (rs: ResultSet) => {
      var topicPartitions = Map[TopicPartition, Long]()
      while(rs.next()) {
        val topic = rs.getString(1)
        val partition = rs.getInt(2)
        val offset = rs.getLong(3)

        topicPartitions += (new TopicPartition(topic, partition) -> offset)
      }
      topicPartitions
    }

    JDBCHandler.prepareQuery(sql, resultSetProccess) match {
      case Success(topicPartitions) => topicPartitions
      case Failure(e) => throw e
    }
  }

}
