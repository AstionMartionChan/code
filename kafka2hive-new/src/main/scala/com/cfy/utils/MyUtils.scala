package com.cfy.utils

import java.util

import com.cfy.log.Logging
import org.apache.kafka.common.TopicPartition
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils}

import scala.collection.mutable.ListBuffer


object MyUtils extends Logging{

  def createDirectStream[K, V](ssc: StreamingContext, kafkaParams: Map[String, AnyRef]) = {
    val sparkConf = ssc.sparkContext.getConf
    val topicPartitionOffsets = KafkaOffsetManager.getPartitionOffset(sparkConf, kafkaParams)
    var notStorageTopics = new ListBuffer[String]

    topicPartitionOffsets.filter{
      case (topic, topicPartitionOffsets) => {
        topicPartitionOffsets match {
          case topicPartitionOffsets => false
          case null => {
            notStorageTopics += topic
            true
          }
        }
      }
    }

    // 如果部分topic有存储offset，部分topic没有存储，则分别创建两个DStream合并返回
    if (notStorageTopics.size == 0){
      KafkaUtils.createDirectStream[K, V](ssc,
        PreferConsistent, ConsumerStrategies.Assign[K, V](
          topicPartitionOffsets.values.flatMap(_.keys), kafkaParams, topicPartitionOffsets.values.toMap[TopicPartition, Long]
        )
      )
    } else if (notStorageTopics.size > 0 && topicPartitionOffsets.values.nonEmpty) {
      KafkaUtils.createDirectStream[K, V](ssc,
        PreferConsistent, ConsumerStrategies.Assign[K, V](
          topicPartitionOffsets.values.flatMap(_.keys), kafkaParams, topicPartitionOffsets.values.toMap[TopicPartition, Long]
        )
      ) union (
        KafkaUtils.createDirectStream[K, V](ssc,
          PreferConsistent, ConsumerStrategies.Subscribe[K, V](notStorageTopics.toList, kafkaParams))
      )
    } else {
      KafkaUtils.createDirectStream[K, V](ssc,
        PreferConsistent, ConsumerStrategies.Subscribe[K, V](notStorageTopics.toList, kafkaParams))
    }





//    if (topicPartitionOffsets.nonEmpty){
//      logger.info(s"create direct stream with offset ${topicPartitionOffsets}")
//
//      KafkaUtils.createDirectStream[K, V](ssc,
//        PreferConsistent, ConsumerStrategies.Assign[K, V](
//          topicPartitionOffsets.keys.toList, kafkaParams, topicPartitionOffsets
//        )
//      )
//    } else {
//      logger.info(s"create direct stream with topic ${topics}")
//
//      KafkaUtils.createDirectStream[K, V](ssc, PreferConsistent, ConsumerStrategies.Subscribe[K, V](topics, kafkaParams))
//    }

  }



}


