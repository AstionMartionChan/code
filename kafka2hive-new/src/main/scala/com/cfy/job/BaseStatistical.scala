package com.cfy.job

import com.alibaba.fastjson.{JSON, JSONPath}
import com.cfy.constants.ConfigurationKey._
import com.cfy.utils.JdbcHandler
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.sql.types._
import java.util.{Properties, List => JList}

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.sql.Row

import scala.collection.JavaConverters._
import scala.collection.mutable

trait BaseStatistical {

  case class Kafka2HiveConfig(topic: String, hiveTableName: String,
                              fieldRelationMap: mutable.LinkedHashMap[String, (String, String)], partition: Map[String, String], where: String)

  /**
    * 解析json转换为row
    * @param record
    * @param configAndSchema
    * @return
    */
  def convertToRow(record: ConsumerRecord[String, String], configAndSchema: (List[Kafka2HiveConfig], StructType)): Row


  /**
    * 根据配置生成sql语句
    * @param kafka2HiveConfig
    * @return
    */
  def generateSQL(kafka2HiveConfig: Kafka2HiveConfig): String

  /**
    * 获取kafka配置
    * @return
    */
  def getKafkaParmas(prop: Properties) = {
    Map (
      "topics" -> prop.getProperty(KAFKA_TOPICS),
      "bootstrap.servers" -> prop.getProperty(KAFKA_BOOTSTRAP_SERVERS),
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> prop.getProperty(KAFKA_CONSUMER_GROUP),
      "auto.offset.reset" -> prop.getOrDefault(KAFKA_OFFSET_RESET, "latest"),
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
  }

  /**
    * 获取hive关系配置
    * @param topics
    * @return
    */
  def getHiveRelationConfig(topics: List[String]) = {
    val sql =
      s"""
         SELECT
              `topic`,
              `hive_table`,
              `relation_json`
         FROM
              t_kafka2hive_relation_config
         WHERE
              `topic` IN (${topics.mkString("'","','","'")})
       """

    JdbcHandler.select(sql) { rs =>
      (rs.string(1), rs.string(2), rs.string(3))
    }
  }

  /**
    * 匹配SparkSql schema数据类型
    * @param fieldType
    * @return
    */
  def caseDataType(fieldType: String) = {
    fieldType match {
      case STRING => StringType
      case INT => IntegerType
      case LONG => LongType
      case DOUBLE => DataTypes.createDecimalType(23,8)
      case BOOLEAN => BooleanType
      case _ => throw new UnsupportedOperationException(fieldType)
    }
  }

  /**
    * 添加默认 topic和unique_id 结构
    * @param structFields
    * @return
    */
  def addDefaultStructFields(structFields: List[StructField]) = {
    structFields.toList :::
    StructField(KAFKA_TOPIC, StringType) ::
//    StructField(KAFKA_UNIQUE_ID, StringType) ::
    Nil
  }


  final def getConfigAndSchema(configs: List[(String, String, String)],
                        toStructFields: List[Kafka2HiveConfig] => List[StructField]) = {

    val kafka2HiveConfigs = configs.map(config => {
      val topic = config._1
      val hiveTableName = config._2
      val relationJson = config._3

      val jsonObj = JSON.parseObject(relationJson)

      val fieldList = JSONPath.eval(jsonObj, "$.relation.hiveField").asInstanceOf[JList[String]]
      val typeList = JSONPath.eval(jsonObj, "$.relation.hiveType").asInstanceOf[JList[String]]
      val jsonPathList = JSONPath.eval(jsonObj, "$.relation.jsonPath").asInstanceOf[JList[String]]
      val partitionNameList = JSONPath.eval(jsonObj, "$.partition.name").asInstanceOf[JList[String]].asScala.toList
      val partitionExpressionList = JSONPath.eval(jsonObj, "$.partition.expression").asInstanceOf[JList[String]].asScala.toList
      val where = JSONPath.eval(jsonObj, "$.where").asInstanceOf[String]

      if (fieldList.size == typeList.size){

        var fieldRelationMap = new mutable.LinkedHashMap[String, (String, String)]
        for (index <- 0 until fieldList.size) {
          fieldRelationMap += (fieldList.get(index) -> (typeList.get(index), jsonPathList.get(index)))
        }

        val partition = partitionNameList.zip(partitionExpressionList).toMap[String, String]

        new Kafka2HiveConfig(topic, hiveTableName, fieldRelationMap, partition, where)
      } else {
        throw new Exception("config hiveField and hiveType size is different")
      }

    })

    val addedStructFields = addDefaultStructFields(toStructFields(kafka2HiveConfigs))

    (kafka2HiveConfigs, StructType(addedStructFields))

  }

}
