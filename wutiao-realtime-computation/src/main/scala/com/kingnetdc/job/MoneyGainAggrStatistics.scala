package com.kingnetdc.job

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import com.kingnetdc.utils.{JsonUtils, KafkaOffsetUtil}
import com.kingnetdc.utils.StatisticsUtils.getKafkaParams
import com.kingnetdc.watermelon.input.KingnetInputDStream
import com.kingnetdc.watermelon.output.KafkaSink
import com.kingnetdc.watermelon.utils.{ConfigUtils, DateUtils, Logging}
import com.kingnetdc.watermelon.utils.ConfigurationKeys.{SPARK_KAFKA_OFFSET_ZK_CONNECT, SPARK_STREAMING_DURATION}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import org.apache.spark.streaming.{Duration, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.JavaConversions._
import scala.collection.mutable

object MoneyGainAggrStatistics extends Logging{


  def main(args: Array[String]): Unit = {
    if (args.length < 1) {
      throw new IllegalArgumentException("configuration path is missing")
    }

    val config = ConfigUtils.loadFromFile(args(0)).toMap
    val bootstrapServers = config("bootstrap.servers")
    val kafkaTopic = config("source.topic")
    val kafkaTopicGroup = config("kafka.topic.group")
    val zookeeperConnect = config("zookeeper.connect")
    val outputTopic = config("output.topic")
    val targetTable = config("target.table")

    val sparkConf = new SparkConf()
    val sc = new SparkContext(sparkConf)
    val ssc = new StreamingContext(
      sc, Duration(sparkConf.get(SPARK_STREAMING_DURATION).toLong)
    )

    var offsetRanges = Array[OffsetRange]()
    KingnetInputDStream.createDirectKafkaStream[String, String](
      ssc,
      getKafkaParams(bootstrapServers, kafkaTopic, kafkaTopicGroup)
    ).transform((rdd, time) => {
      offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      rdd
    }).map(_.value()).foreachRDD((jsonStringRDD, batchTime) => {

      val sparkSession = getSparkSesstion(sparkConf)
      import sparkSession.implicits._

      // 生成临时表
      val ds = sparkSession.createDataset(jsonStringRDD)
      val jsonDs = sparkSession.read.json(ds)
      jsonDs.createOrReplaceTempView("t_temp")

      // 对批次数据进行聚合
      val dsTime = getBatchTime(batchTime.milliseconds)
      val batchDataFrame = sparkSession.sql(
        getAggregateSql()
      )

      // 缓存批次聚合数据
      batchDataFrame.persist(StorageLevel.MEMORY_AND_DISK).createOrReplaceTempView("t_cache")

      // 推送到下游topic
      var producerBroadCast: Broadcast[KafkaSink[String, String]] = null
      try {
        producerBroadCast = sparkSession.sparkContext.broadcast(
          KafkaSink.create[String, String](Map(
            "bootstrap.servers" -> bootstrapServers,
            "key.serializer" -> classOf[StringSerializer],
            "value.serializer" -> classOf[StringSerializer]
          ))
        )

        saveToKafka(batchDataFrame, producerBroadCast, outputTopic, dsTime)
      } finally {
        Option(producerBroadCast).foreach { _.destroy() }
      }

      //聚合当天历史数据
      sparkSession.sql(getUnionSql(dsTime, targetTable))


      //提交offset
      KafkaOffsetUtil.saveOffsetToZookeeper(
        zookeeperConnect,
        kafkaTopicGroup,
        offsetRanges
      )
    })




    ssc.start()
    ssc.awaitTermination()
  }


  def saveToKafka(
          resultDataFrame: DataFrame,
          producerBroadCast: Broadcast[KafkaSink[String, String]],
          topic: String,
          ds: String ) = {
    resultDataFrame.foreach(row => {
      val jsonResult = new mutable.HashMap[String, Any]()
      jsonResult.put("uid", row.getLong(0))
      jsonResult.put("actionType", row.getLong(1))
      jsonResult.put("sourceId", row.getLong(2))
      jsonResult.put("value", row.getDouble(3))
      jsonResult.put("maxMessageId", row.getString(4))
      jsonResult.put("messageId", row.getString(5))
      jsonResult.put("maxTime", row.getLong(6))
      jsonResult.put("time", row.getLong(7))
      jsonResult.put("minTime", row.getLong(8))
      jsonResult.put("ds", ds)
      val kafkaSink = producerBroadCast.value
      val record = new ProducerRecord[String, String](topic, row.getLong(0).toString, JsonUtils.render(jsonResult))

      kafkaSink.send(record).get()
    })
  }

  def getAggregateSql() = {
    s"""
       select
           uid as uid
          ,actiontype as actiontype
          ,sourceid as sourceid
          ,sum(value) as value
          ,max(messageid) as max_messageid
          ,max(messageid) as messageid
          ,max(time) as max_time
          ,max(time) as time
          ,min(time) as min_time
       from
          t_temp
       group by
          uid,actiontype,sourceid
     """
  }
  
  
  def getUnionSql(ds: String, targetTable: String) = {
    s"""
       insert overwrite table ${targetTable} partition (ds='${ds}')
       select 
       	    uid as uid
           ,actiontype as actiontype
           ,sourceid as sourceid
           ,sum(value) as value
           ,max(max_messageid) as max_messageid
           ,max(max_time) as max_time
           ,min(min_time) as min_time
       from 
       (
       select 
       	  uid
          ,actiontype
          ,sourceid
          ,value
          ,max_messageid
          ,max_time
          ,min_time
       from
          ${targetTable}
       where
          ds = '${ds}'
       
       union all

       select
           uid as uid
          ,actiontype as actiontype
          ,sourceid as sourceid
          ,value as value
          ,max_messageid as max_messageid
          ,max_time as max_time
          ,min_time as min_time
       from
          t_cache
       )
       group by uid,actiontype,sourceid
     """
  }


  def getBatchTime(millis: Long) = {
    val sdf = new SimpleDateFormat("yyyy-MM-dd")
    val calender = Calendar.getInstance()
    calender.setTimeInMillis(millis)
    sdf.format(calender.getTime)
  }

  def getSparkSesstion(conf: SparkConf) = {
    SparkSession
      .builder()
      .config(conf)
      .enableHiveSupport()
      .getOrCreate()
  }

  def sparkConfCheck(conf: SparkConf) = {
    require(conf.contains(SPARK_STREAMING_DURATION), s"${SPARK_STREAMING_DURATION} is missing")
  }
}
