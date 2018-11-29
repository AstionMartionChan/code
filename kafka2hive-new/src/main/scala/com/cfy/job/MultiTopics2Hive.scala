package com.cfy.job

import com.cfy.constants.ConfigurationKey._
import com.cfy.log.Logging
import com.cfy.utils.{KafkaOffsetManager, MyUtils}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.SparkConf
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types._
import org.apache.spark.streaming.{Duration, StreamingContext}
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import org.joda.time.DateTime
import java.math.{BigDecimal => JBigDecimal}

import scala.collection.JavaConverters._
import com.alibaba.fastjson.{JSON, JSONPath}
import com.cfy.config.ConfigurationManager
import org.apache.commons.lang.StringUtils



object MultiTopics2Hive extends BaseStatistical with Logging with Serializable {


  def toStructFields(kafka2HiveConfigs: List[Kafka2HiveConfig]) = {
    kafka2HiveConfigs.flatMap(config => {
      val topic = config.topic
      config.fieldRelationMap.map(kv => {
        StructField(s"${topic}${OF}${kv._1}", caseDataType(kv._2._1))
      })
    }).toList
  }

  override def generateSQL(kafka2HiveConfig: Kafka2HiveConfig): String = {
    val head :: tail = kafka2HiveConfig.fieldRelationMap.keys.toList
    val tailFields = new StringBuilder
    tail.foreach(f => tailFields.append(s"${COMMA}${kafka2HiveConfig.topic}${OF}${f}${NEWLINE}${BLANKSPACE}"))
    kafka2HiveConfig.partition.values.foreach(v => tailFields.append(s"${COMMA}${v}${NEWLINE}${BLANKSPACE}"))

    s"""
       INSERT INTO TABLE ${kafka2HiveConfig.hiveTableName} PARTITION (${kafka2HiveConfig.partition.keySet.mkString(COMMA)})
       SELECT
            ${kafka2HiveConfig.topic}${OF}${head}
            ${tailFields.toString()}
       FROM
            ${SPARK_TEMP_TABLE_NAME}
       WHERE
            `topic` = '${kafka2HiveConfig.topic}'
       ${kafka2HiveConfig.where}
     """
  }

  override def convertToRow(record: ConsumerRecord[String, String], configAndSchema: (List[Kafka2HiveConfig], StructType)): Row = {
    val topicConfigMap = configAndSchema._1.map(configs => (configs.topic -> configs)).toMap[String, Kafka2HiveConfig]
    val schema = configAndSchema._2
    val topic = record.topic
    val values = topicConfigMap.get(topic) match {
      case Some(config) => {
        schema.map(sf => {
          val splitedField = sf.name.toString.split(OF)
          if(splitedField.length > 1 && topic.equals(splitedField(0)) && config.fieldRelationMap.contains(splitedField(1))){
            val jsonPath = config.fieldRelationMap.get(splitedField(1)).get._2
            jsonPath match {
              case KAFKA_UNIQUE_ID => s"${record.topic}_${record.partition}_${record.offset}"
              case _ => {
                val fullJsonPath = "$." + jsonPath
                val jsonObj = JSON.parseObject(record.value())
                if (JSONPath.contains(jsonObj, fullJsonPath)){
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
                } else {
                  null
                }
              }
            }
          } else {
            sf.name match {
              case KAFKA_TOPIC => record.topic
//              case KAFKA_UNIQUE_ID => s"${record.topic}_${record.partition}_${record.offset}"
              case _ => null
            }
          }
        })
      }
      case None => throw new RuntimeException(s"no match config topic: ${record.topic}")
    }

    Row(values : _*)
  }

  def main(args: Array[String]): Unit = {

    if (args.length < 1) {
      throw new IllegalArgumentException("configuration path is missing")
    }
    val prop = ConfigurationManager.load(args(0))

    val sparkConf = new SparkConf()
      .set("hive.exec.dynamic.partition.mode", "nonstrict")

//    sparkConf.setAll(prop.asScala.toMap[String, String])
//      .setMaster("local[*]")
//      .setAppName("MultiTopics2Hive")
//      .set("spark.streaming.duration", "30000")
//      .set("kafka.offset.mysql.table", "t_kafka2hive_offset")
//      .set("hive.exec.dynamic.partition.mode", "nonstrict")
//      .set(KAFKA_OFFSET_STORAGE, "zk")
//      .set(ZK_CONNECT, "172.16.32.112:2181,172.16.32.140:2181,172.16.32.52:2181")

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

      val rowRDD =  rdd.map(record => {
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
