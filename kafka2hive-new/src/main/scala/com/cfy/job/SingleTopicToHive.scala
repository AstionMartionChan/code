package com.cfy.job

import com.cfy.config.ConfigurationManager
import com.cfy.constants.ConfigurationKey._
import com.cfy.log.Logging
import com.cfy.utils.{JdbcHandler, KafkaOffsetManager, MyUtils}
import com.jayway.jsonpath.JsonPath
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types._
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import org.apache.spark.streaming.{Duration, StreamingContext, Time}
import org.joda.time.DateTime

import scala.collection.mutable.ListBuffer
import scala.collection.JavaConverters._

object SingleTopicToHive extends Logging{



  def caseType(fieldType: String) = {
    fieldType match {
      case STRING => StringType
      case INT => IntegerType
      case DOUBLE => DoubleType
    }
  }


  def getSchemaAndJsonPath(relationJson: String) = {

    val fieldList = JsonPath.parse(relationJson).read[java.util.List[String]]("$.relation[*].hiveField")
    val typeList = JsonPath.parse(relationJson).read[java.util.List[String]]("$.relation[*].hiveType")
    val jsonPathList = JsonPath.parse(relationJson).read[java.util.List[String]]("$.relation[*].jsonPath").asScala.toList

    if (fieldList.size() == typeList.size()){
      var structFields = new ListBuffer[StructField]

      for (index <- 0 until fieldList.size){
        structFields.append(StructField(fieldList.get(index), caseType(typeList.get(index))))
      }

      (StructType.apply(structFields), jsonPathList)
    } else {
      throw new Exception("config hiveField and hiveType size is different")
    }


  }



  def getHiveRelationConfig(topic: String) = {
    val sql =
      s"""
         SELECT
              `topic`,
              `hive_table`,
              `relation_json`
         FROM
              t_kafka2hive_relation_config
         WHERE
              `topic` = '${topic}'
       """

    JdbcHandler.select(sql) { rs =>
      (rs.string(1), rs.string(2), rs.string(3))
    }.head
  }

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
            .set("spark.streaming.duration", "60000")
            .set("kafka.offset.mysql.table", "t_kafka2hive_offset")


    val ssc = new StreamingContext(sparkConf, Duration(sparkConf.get(SPARK_STREAMING_DURATION, "5000").toLong))
    // 获取基本配置
    val kafkaParams = getKafkaParmas
    val (topic, hiveTable, relationJson) = getHiveRelationConfig(kafkaParams.get(KAFKA_TOPIC).get.toString)

    // 获取schema 和 jsonPath
    val (schema, jsonPathList) = getSchemaAndJsonPath(relationJson)

    // 创建dstream并获取offset
    var offsetRanges = Array[OffsetRange]()
    val jsonStream = MyUtils.createDirectStream[String, String](ssc, kafkaParams).transform(rdd => {
      offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      rdd
    })

    val sparkSession = SparkSession.builder().config(sparkConf).getOrCreate()
    import sparkSession.implicits._

    jsonStream.foreachRDD((rdd, batchTime) => {

      val parsedRdd = rdd.map(record => {
        Row(
          jsonPathList.map(jsonPath => {
            JsonPath.parse(record.value()).read[Any]("$." + jsonPath)
          })
        : _*)
      })

      parsedRdd.foreach(r=>println(r.toString()))

//      val df = sparkSession.createDataFrame(parsedRdd, schema)
//      df.printSchema()
//      df.show()


      // 保存offset
      KafkaOffsetManager.saveOffsetToMysql(sparkConf, offsetRanges, new DateTime(batchTime.milliseconds).toString(YMDHMS), kafkaParams.get(GROUP).get.toString)
    })


    ssc.start()
    ssc.awaitTermination()

  }

}
