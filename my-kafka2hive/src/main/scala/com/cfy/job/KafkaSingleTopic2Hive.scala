package com.cfy.job


import com.cfy.config.ConfigurationManager
import com.cfy.utils.{ExternalStorageUtil, HiveUtil, JSONUtil}
import com.cfy.constants.ConfigurationKey._
import org.apache.hadoop.hive.metastore.api.FieldSchema
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import org.apache.spark.streaming.{Seconds, StreamingContext}


object KafkaSingleTopic2Hive {



  def main(args: Array[String]): Unit = {

    // 初始化sparkconf
    val conf = new SparkConf()
      .setAppName("KafkaSingleTopic2Hive")
      .setMaster("local[2]")

    // 初始化streaming
    val ssc = new StreamingContext(conf, Seconds(30))

    // 配置kafka
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> ConfigurationManager.getString(KAFKA_BOOTSTRAP_SERVERS),
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> ConfigurationManager.getString(KAFKA_CONSUMER_GROUP),
      "auto.offset.reset" -> ConfigurationManager.getString(KAFKA_OFFSET_RESET),
      "enable.auto.commit" -> (false: java.lang.Boolean),
      "topics" -> ConfigurationManager.getString(KAFKA_TOPIC)
    )

    // 从外部系统获取offset 并创建dstream
    var offsetRanges = Array[OffsetRange]()
    val dstream = ExternalStorageUtil.createDirectStream[String, String](ssc, kafkaParams)
    .transform(rdd => {
      offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      rdd
    })

    // 获取字段配置信息
    val hiveConfig = ExternalStorageUtil.getTopicConfig(
        ConfigurationManager.getString(KAFKA_TOPIC),
        ConfigurationManager.getString(MYSQL_HIVE_CONFIG_TABLENAME))
    val nestedField = hiveConfig.head._3

    val (fields, partitions) = HiveUtil.getMetaDataAndPartition(ConfigurationManager.getString(HIVE_IP), ConfigurationManager.getInt(HIVE_PORT), hiveConfig.head._2)
    val broadcast = ssc.sparkContext.broadcast((fields, partitions))

    dstream.foreachRDD(recordRDD => {
      // 遍历展平json
      val rdd = recordRDD.map(record => {
        JSONUtil.parseJSON(record, nestedField)
      })

      rdd.mapPartitions(iter => {
        iter.map(rdd => {

        })
      })



    })


    ssc.start()
    ssc.awaitTermination()
  }


}
