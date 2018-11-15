package com.cfy.utils

import com.cfy.log.Logging
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils}


object MyUtils extends Logging{

  def createDirectStream[K, V](ssc: StreamingContext, kafkaParams: Map[String, AnyRef]) = {
    val sparkConf = ssc.sparkContext.getConf
    val (topics, topicPartitionOffsets) = KafkaOffsetManager.getPartitionOffset(sparkConf, kafkaParams)

    if (topicPartitionOffsets.nonEmpty){
      logger.info(s"create direct stream with offset ${topicPartitionOffsets}")

      KafkaUtils.createDirectStream[K, V](ssc,
        PreferConsistent, ConsumerStrategies.Assign[K, V](
          topicPartitionOffsets.keys.toList, kafkaParams, topicPartitionOffsets
        )
      )
    } else {
      logger.info(s"create direct stream with topic ${topics}")

      KafkaUtils.createDirectStream[K, V](ssc, PreferConsistent, ConsumerStrategies.Subscribe[K, V](topics, kafkaParams))
    }


  }



}


