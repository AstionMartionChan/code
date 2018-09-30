package com.cfy.utils


import java.sql.ResultSet

import org.apache.spark.streaming.StreamingContext
import com.cfy.constants.ConfigurationKey._
import com.cfy.jdbc.JDBCHandler
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils}
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent

import scala.util.{Failure, Success}



object ExternalStorageUtil {


  def createDirectStream[K, V](ssc: StreamingContext, kafkaParams: Map[String, AnyRef]) = {

    val (topics, partitionOffsets) = getSavedPartitionOffset(ssc.sparkContext.getConf, kafkaParams)

    val directKafkaDstream =
      if (partitionOffsets.nonEmpty){
        KafkaUtils.createDirectStream(ssc,
          PreferConsistent,
          ConsumerStrategies.Assign[K, V](
            partitionOffsets.keys.toList, kafkaParams, partitionOffsets
          )
        )
      } else {
        KafkaUtils.createDirectStream[K, V](ssc,
          PreferConsistent,
          ConsumerStrategies.Subscribe(topics, kafkaParams)
        )
      }
    directKafkaDstream
  }


  def getSavedPartitionOffset(sparkConf: SparkConf, kafkaParams: Map[String, AnyRef]) = {
    val topics = kafkaParams.get(KAFKA_TOPIC).map(_.toString).get.split(COMMA).toSet
    val consumerGroup = kafkaParams.get(KAFKA_CONSUMER_GROUP)

    val offsetStorage = sparkConf.get(KAFKA_OFFSET_STORAGE)

    if (KafkaOffsetStorage.MYSQL.toString == offsetStorage){
      //TODO check param

      val tableName = sparkConf.get(MYSQL_STORAGE_TABLENAME)

      (
      topics,
      KafkaOffsetManager.saveOffsetToMysql(consumerGroup.get.toString, tableName)
        .filter {
          case (topicPartition, offset) => topics.contains(topicPartition.topic)
        }
      )

    } else {
      (null, null)
    }
  }


  def getTopicConfig(topics: String, tableName: String):List[(String,String,String,String,String)] = {
    val sql =
      s"""
         select
               topic
              ,hive_table
              ,nested_fields
              ,partition_statements
              ,where_statements
         from
              ${tableName}
         where
              topic in (${topics.map(_.toString.split(COMMA).mkString("'", COMMA, "'"))})
       """
    val proccessFun = (rs: ResultSet) => {
      val hiveConfigList = List()[(String,String,String,String,String)]
      while(rs.next()) {
        val topic = rs.getString("topic")
        val hiveTable = rs.getString("hive_table")
        val nestedFields = rs.getString("nested_fields")
        val partitionStatements = rs.getString("partition_statements")
        val whereStatements = rs.getString("where_statements")

        hiveConfigList.:+((topic, hiveTable, nestedFields, partitionStatements, whereStatements))
      }
      hiveConfigList
    }
    JDBCHandler.prepareQuery(sql, proccessFun) match {
      case Success(hiveConfigList) => hiveConfigList
      case Failure(e) => throw e
    }

  }
}
