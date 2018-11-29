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
import java.math.{BigDecimal => JBigDecimal}
import java.util.{LinkedHashMap => JLinkedHashMap, List => JList}

import org.apache.commons.lang.StringUtils
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.sql.types._

import scala.collection.mutable.ListBuffer
import scala.collection.JavaConverters._

object SingleTopic2Hive extends BaseStatistical with Logging with Serializable {


  def toStructFields(kafka2HiveConfigs: List[Kafka2HiveConfig]) = {
    kafka2HiveConfigs.head.fieldRelationMap.map(f => {
      StructField(f._1, caseDataType(f._2._1))
    }).toList
  }

  override def generateSQL(kafka2HiveConfig: Kafka2HiveConfig): String = {
    val head :: tail = kafka2HiveConfig.fieldRelationMap.keys.toList
    val tailFields = new StringBuilder
    tail.foreach(f => tailFields.append(s"${COMMA}${f}${NEWLINE}${BLANKSPACE}"))
    kafka2HiveConfig.partition.values.foreach(v => tailFields.append(s"${COMMA}${v}${NEWLINE}${BLANKSPACE}"))

    s"""
       INSERT INTO TABLE ${kafka2HiveConfig.hiveTableName} PARTITION (${kafka2HiveConfig.partition.keySet.mkString(COMMA)})
       SELECT
            ${head}
            ${tailFields.toString()}
       FROM
            ${SPARK_TEMP_TABLE_NAME}
       WHERE
            `topic` = '${kafka2HiveConfig.topic}'
       ${kafka2HiveConfig.where}
     """
  }



  /**
    * 遍历schema 如果配置中有于schema字段相同的则取到对应jsonPath从json中解析
    * @param record
    * @param configAndSchema
    * @return
    */
  override def convertToRow(record: ConsumerRecord[String, String], configAndSchema: (List[Kafka2HiveConfig], StructType)) = {
    val fieldRelationMap = configAndSchema._1.head.fieldRelationMap
    val schema = configAndSchema._2
    val values = schema.map(sf => {
      if(fieldRelationMap.contains(sf.name)){
        val jsonPath = fieldRelationMap.get(sf.name).get._2
        jsonPath match {
          case KAFKA_UNIQUE_ID => s"${record.topic}_${record.partition}_${record.offset}"
          case _ => {
            val fullJsonPath = "$." + fieldRelationMap.get(sf.name).get._2
            val jsonObj = JSON.parseObject(record.value())
            if(JSONPath.contains(jsonObj, fullJsonPath)){
              val value = JSONPath.eval(jsonObj, fullJsonPath)
              if (StringUtils.isNotBlank(value.toString)){
                val decimalType = DataTypes.createDecimalType(23,8)
                sf.dataType match {
                  case StringType => value.toString
                  case IntegerType => value.toString.toInt
                  case LongType => value.toString.toLong
                  case decimalType => new JBigDecimal(value.toString)
                  case BooleanType => value.toString.toBoolean
                  case _ => value
                }
              } else {
                null
              }
            } else{
              null
            }
          }
        }
      } else{
        sf.name match {
          case KAFKA_TOPIC => record.topic
//          case KAFKA_UNIQUE_ID => s"${record.topic}_${record.partition}_${record.offset}"
          case _ => null
        }
      }
    })

    Row(values : _*)
  }


  def main(args: Array[String]): Unit = {

    if (args.length < 1) {
      throw new IllegalArgumentException("configuration path is missing")
    }

    val prop = ConfigurationManager.load(args(0))

    val sparkConf = new SparkConf()
      .set("hive.exec.dynamic.partition.mode", "nonstrict")

//          .setMaster("local[*]")
//            .setAppName("SingleToHive")
//            .set("spark.streaming.duration", "30000")
//            .set("kafka.offset.mysql.table", "t_kafka2hive_offset")
//              .set("hive.exec.dynamic.partition.mode", "nonstrict")
//      .set(KAFKA_OFFSET_STORAGE, "zk")
//          .set(ZK_CONNECT, "172.16.32.112:2181,172.16.32.140:2181,172.16.32.52:2181")


    val ssc = new StreamingContext(sparkConf, Duration(sparkConf.get(SPARK_STREAMING_DURATION, "5000").toLong))
    // 获取基本配置
    val kafkaParams = getKafkaParmas(prop)
    val configs = getHiveRelationConfig(kafkaParams.get(KAFKA_TOPICS).get.toString.split(COMMA).toList)

    // 获取config 和 schema
    val configsAndSchema = getConfigAndSchema(configs, toStructFields)
    val bc = ssc.sparkContext.broadcast(configsAndSchema)

    // 创建dstream并获取offset
    var offsetRanges = Array[OffsetRange]()
    val jsonStream = MyUtils.createDirectStream[String, String](ssc, kafkaParams).transform(rdd => {
      offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      rdd
    })

    val sparkSession = SparkSession.builder().config(sparkConf).enableHiveSupport().getOrCreate()
    import sparkSession.implicits._

    jsonStream.foreachRDD((rdd, batchTime) => {
      val configsAndSchema = bc.value
      val rowRDD = rdd.map(record => {
        // 解析json转换成row
        convertToRow(record, configsAndSchema)
      })

      // 生成临时表
      val df = sparkSession.createDataFrame(rowRDD, configsAndSchema._2)
      df.createOrReplaceTempView(SPARK_TEMP_TABLE_NAME)

      if (logger.isDebugEnabled){
        df.printSchema()
        df.show()
      }

      // 拼接sql 并执行
      configsAndSchema._1.foreach(config => {
        val sql = generateSQL(config)
        sparkSession.sql(sql)
      })


      // 保存offset
      KafkaOffsetManager.saveOffsets(sparkConf, offsetRanges, new DateTime(batchTime.milliseconds).toString(YMDHMS), kafkaParams.get(GROUP).get.toString)
    })


    ssc.start()
    ssc.awaitTermination()

  }



}
