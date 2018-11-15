package com.cfy.job

import com.cfy.config.ConfigurationManager
import com.cfy.constants.ConfigurationKey._
import com.cfy.log.Logging
import com.cfy.utils.{KafkaOffsetManager, MyUtils}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import org.apache.spark.streaming.{Duration, StreamingContext, Time}

object SingleTopicToHive extends Logging{

  def getKafkaParmas = {
    Map (
      "topics" -> ConfigurationManager.getString(KAFKA_TOPIC).get,
      "bootstrap.servers" -> ConfigurationManager.getString(KAFKA_BOOTSTRAP_SERVERS).get,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> ConfigurationManager.getString(KAFKA_CONSUMER_GROUP).get,
      "auto.offset.reset" -> ConfigurationManager.getString(KAFKA_OFFSET_RESET).getOrElse("latest"),
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
  }

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf()
          .setMaster("local[*]")
            .setAppName("SingleToHive")
            .set("spark.streaming.duration", "5000")
            .set("kafka.offset.mysql.table", "t_kafka2hive_offset")

    val ssc = new StreamingContext(sparkConf, Duration(sparkConf.get(SPARK_STREAMING_DURATION, "5000").toLong))

    var offsetRanges = Array[OffsetRange]()
    var batchTime: Time = null
    val kafkaParams = getKafkaParmas

    val jsonStream = MyUtils.createDirectStream[String, String](ssc, kafkaParams).transform((rdd, time) => {
      offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      batchTime = time
      rdd
    })

    jsonStream.foreachRDD(rdd => {
      rdd.map(record => logger.info(record.value()))
      KafkaOffsetManager.saveOffsetToMysql(sparkConf, offsetRanges, batchTime, kafkaParams.get(GROUP).get.toString)
    })


    ssc.start()
    ssc.awaitTermination()

  }

}
