package com.cfy.job

import com.alibaba.fastjson.{JSON, JSONPath}
import com.cfy.config.ConfigurationManager
import com.cfy.constants.ConfigurationKey._
import com.cfy.log.Logging
import com.cfy.utils.{JdbcHandler, KafkaOffsetManager, MyUtils}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import org.apache.spark.streaming.{Duration, StreamingContext, Time}
import org.joda.time.DateTime
import java.util.{List => JList}
import java.math.{BigDecimal => JBigDecimal}

import org.apache.spark.sql.types._

import scala.collection.mutable.ListBuffer
import scala.collection.JavaConverters._

object SingleTopicToHive extends Logging{


  def generateSQL(hiveTableName: String, partition: String, where: String) = {
    s"""
       INSERT INTO ${hiveTableName} PARTITION (${partition})
       SELECT
            *
       FROM
            t_temp_view
       WHERE
            1 = 1
       ${where}
     """
  }


  def caseType(fieldType: String) = {
    fieldType match {
      case STRING => StringType
      case INT => IntegerType
      case DOUBLE => DataTypes.createDecimalType(23,8)
      case _ => throw new UnsupportedOperationException(fieldType)
    }
  }

  /**
    * 根据json描述信息构建并返回相关信息
    * @param relationJson
    * @return
    */
  case class Kafka2HiveConfig(topic: String, hiveTableName: String, schema: StructType, jsonPathList: List[String], partition: String, where: String)

  def getKafka2HiveConfig(config: (String, String, String)) = {
    val topic = config._1
    val hiveTableName = config._2
    val relationJson = config._3

    val jsonObj = JSON.parseObject(relationJson)

    val fieldList = JSONPath.eval(jsonObj, "$.relation.hiveField").asInstanceOf[JList[String]]
    val typeList = JSONPath.eval(jsonObj, "$.relation.hiveType").asInstanceOf[JList[String]]
    val jsonPathList = JSONPath.eval(jsonObj, "$.relation.jsonPath").asInstanceOf[JList[String]].asScala.toList
    val partition = JSONPath.eval(jsonObj, "$.partition").asInstanceOf[String]
    val where = JSONPath.eval(jsonObj, "$.where").asInstanceOf[String]


    if (fieldList.size == typeList.size){
      var structFields = new ListBuffer[StructField]

      for (index <- 0 until fieldList.size){
        structFields.append(StructField(fieldList.get(index), caseType(typeList.get(index))))
      }

      new Kafka2HiveConfig(topic, hiveTableName, StructType(structFields), jsonPathList, partition, where)
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
    }
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
            .set("spark.streaming.duration", "30000")
            .set("kafka.offset.mysql.table", "t_kafka2hive_offset")


    val ssc = new StreamingContext(sparkConf, Duration(sparkConf.get(SPARK_STREAMING_DURATION, "5000").toLong))
    // 获取基本配置
    val kafkaParams = getKafkaParmas
    val config = getHiveRelationConfig(kafkaParams.get(KAFKA_TOPIC).get.toString)

    // 获取schema 和 jsonPath
    val bc = ssc.sparkContext.broadcast(getKafka2HiveConfig(config.head))

    // 创建dstream并获取offset
    var offsetRanges = Array[OffsetRange]()
    val jsonStream = MyUtils.createDirectStream[String, String](ssc, kafkaParams).transform(rdd => {
      offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      rdd
    })

    val sparkSession = SparkSession.builder().config(sparkConf).getOrCreate()
    import sparkSession.implicits._

    jsonStream.foreachRDD((rdd, batchTime) => {
      val kafka2hiveConfig = bc.value
      val rowRdd = rdd.map(record => {
        Row(
          kafka2hiveConfig.jsonPathList.map(jsonPath => {
            jsonPath match {
              case NULL => null
              case _ => {
                val fullJsonPath = "$." + jsonPath
                val jsonObj = JSON.parseObject(record.value())
                JSONPath.contains(jsonObj, fullJsonPath) match {
                  case true => JSONPath.eval(jsonObj, fullJsonPath)
                  case _ => null
                }
              }
            }
          })
        : _*)
      })


      val df = sparkSession.createDataFrame(rowRdd, kafka2hiveConfig.schema)

      df.printSchema()
      df.show()

      df.createOrReplaceTempView("t_temp_view")

      val sql = generateSQL(kafka2hiveConfig.hiveTableName, kafka2hiveConfig.partition, kafka2hiveConfig.where)
      logger.info(sql)

      // 保存offset
      KafkaOffsetManager.saveOffsetToMysql(sparkConf, offsetRanges, new DateTime(batchTime.milliseconds).toString(YMDHMS), kafkaParams.get(GROUP).get.toString)
    })


    ssc.start()
    ssc.awaitTermination()

  }

}
