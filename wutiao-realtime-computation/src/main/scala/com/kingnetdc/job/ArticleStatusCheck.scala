package com.kingnetdc.job

import java.io.{File, FileInputStream}

import com.kingnetdc.blueberry.cache.KdcCache
import com.kingnetdc.blueberry.cache.base.Tuple3
import com.kingnetdc.blueberry.core.io.Path
import com.kingnetdc.model.{ApplicationConfig, ApplicationConfigParser}
import com.kingnetdc.utils.{AbstractOptions, JsonUtils, KafkaOffsetUtil}
import com.kingnetdc.watermelon.input.KingnetInputDStream
import com.kingnetdc.watermelon.utils.ConfigurationKeys._
import com.kingnetdc.watermelon.utils.{CommonUtils, ConfigUtils, DateUtils, Logging}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Duration, StreamingContext}

import scala.collection.JavaConversions._
import scala.util.Random

object ArticleStatusCheck extends Logging{

  class Options(className: String, args: Seq[String]) extends AbstractOptions(className, args) {}

  private val prefix = "wutiao:item"

  private val ONE_DAY_S = 86400


  def getKafkaParams(
          bootstrapServers: String, topic: String,
          group: String, offSetOpt: Option[String] = None
  ): Map[String, Object] = {
    Map(
      BootstrapServers -> bootstrapServers,
      TOPIC -> topic,
      KAFKA_GROUP -> group,
      "auto.offset.reset" -> offSetOpt.getOrElse("latest"),
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
  }


  //判断status是否为99
  def checkInfo(line: String): Boolean = {
    try {
      val jsonNode = JsonUtils.getJsonNode(line)
      val status = jsonNode.at("/status").asInt()
      status != 99
    } catch {
      case ex:Exception =>
        logger.error("Fail to check log", ex)
        false
    }
  }

  def checkTime(line: String): Boolean = {
    try {
      val jsonNode = JsonUtils.getJsonNode(line)
      val createTime = jsonNode.at("/create_time").asText()
      if (DateUtils.getYMDHMS.parse(createTime).getTime > DateUtils.getYMDHMS.parse("2018-06-05 00:00:00").getTime) {
        true
      } else {
        false
      }
    } catch {
      case e: Exception => {
        logger.error("Fail to check line")
        false
      }
    }
  }

  def checkSparkConf(sparkConf: SparkConf) = {
    require(sparkConf.contains(SPARK_STREAMING_DURATION), s"${SPARK_STREAMING_DURATION} is missing")
  }

  def main(args: Array[String]): Unit = {
    if (args.length < 1) {
      throw new IllegalArgumentException("configuration path is missing")
    }

    val config = ConfigUtils.loadFromFile(args(0)).toMap
    val bootstrapServers = config("bootstrap.servers")
    val kafkaTopic = config("source.topic")
    val kafkaTopicGroup = config("kafka.topic.group")
    val kdcCacheConfig = config("cache.config")
    val expire = config.get("redis.exprire").map(_.toInt).getOrElse(14)


    val sparkConf = new SparkConf()
    checkSparkConf(sparkConf)

    val sparkContext = new SparkContext(sparkConf)
    val streamingContext = new StreamingContext(sparkContext, Duration(sparkConf.get(SPARK_STREAMING_DURATION).toLong))

    var offsetRanges = Array[OffsetRange]()
    val articleDStream = KingnetInputDStream.createDirectKafkaStream[String, String](
      streamingContext, getKafkaParams(bootstrapServers, kafkaTopic, kafkaTopicGroup)
    ).transform(rdd => {
      offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      rdd
    }).map(_.value).filter(checkInfo).map( line => {
      val jsonNode = JsonUtils.getJsonNode(line)
      val itemId = jsonNode.at("/item_id").asText()
      val createTime = jsonNode.at("/create_time").asText()
      val createTimeL = DateUtils.getYMDHMS.parse(createTime).getTime / 1000
      val status = jsonNode.at("/status").asInt()
      val category = jsonNode.at("/category").asText().trim
      new Tuple3(s"${prefix}:${itemId}", s"${status},${createTimeL},${category}",
        expire * ONE_DAY_S + 4 * new Random().nextInt(ONE_DAY_S))
    })

    //缓存结果到redis和mysql
    articleDStream.foreachRDD((rdd, batchTime) => {
      rdd.foreachPartition(
        itemIter => {
          val kdcCache = KdcCache.builder(getClass.getClassLoader.getResourceAsStream(kdcCacheConfig))
          val setTry = CommonUtils.safeRelease(kdcCache)( kdcCache => {
            kdcCache.multiSet(itemIter.toList)
          })()
        }
      )
      //提交offset到zk
      KafkaOffsetUtil.saveOffsetToMysql(
        sparkConf.get(SPARK_OFFSET_MYSQL_TABLE),
        kafkaTopicGroup,
        Map(
          MYSQL_URL -> sparkConf.get(SPARK_OFFSET_MYSQL_URL),
          MYSQL_USER -> sparkConf.get(SPARK_OFFSET_MYSQL_USER),
          MYSQL_PASSWORD -> sparkConf.get(SPARK_OFFSET_MYSQL_PW)
        ),
        batchTime.milliseconds,
        offsetRanges
      )

    })

    streamingContext.start()
    streamingContext.awaitTermination()

  }
}
