package com.cfy.utils


import com.cfy.log.Logging
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils}

import scala.collection.mutable.ListBuffer


object MyUtils extends Logging{

  def createDirectStream[K, V](ssc: StreamingContext, kafkaParams: Map[String, AnyRef]) = {
    val sparkConf = ssc.sparkContext.getConf
    val topicPartitionOffsets = KafkaOffsetManager.getOffsets(sparkConf, kafkaParams)
    var notStorageTopics = new ListBuffer[String]

    val filtedTopicPartitionOffsets = topicPartitionOffsets.filter{ kv =>
      if (kv._2.nonEmpty){
        true
      } else {
        notStorageTopics += kv._1
        false
      }
    }



    // 如果部分topic有存储offset，部分topic没有存储，则分别创建两个DStream合并返回
    if (notStorageTopics.size == 0){
      KafkaUtils.createDirectStream[K, V](ssc,
        PreferConsistent, ConsumerStrategies.Assign[K, V](
          filtedTopicPartitionOffsets.values.flatMap(_.keys), kafkaParams, filtedTopicPartitionOffsets.values.reduce(_ ++ _)
        )
      )
    } else if (notStorageTopics.size > 0 && filtedTopicPartitionOffsets.values.nonEmpty) {
//      KafkaUtils.createDirectStream[K, V](ssc,
//        PreferConsistent, ConsumerStrategies.Assign[K, V](
//          filtedTopicPartitionOffsets.values.flatMap(_.keys), kafkaParams, filtedTopicPartitionOffsets.values.reduce(_ ++ _)
//        )
//      ) union (
//        KafkaUtils.createDirectStream[K, V](ssc,
//          PreferConsistent, ConsumerStrategies.Subscribe[K, V](notStorageTopics.toList, kafkaParams))
//      )

      throw new UnsupportedOperationException("some topic not storage from mysql")
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


