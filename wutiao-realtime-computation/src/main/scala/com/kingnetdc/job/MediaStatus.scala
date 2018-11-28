package com.kingnetdc.job

import java.io.{File, FileInputStream}

import com.kingnetdc.blueberry.cache.KdcCache
import com.kingnetdc.blueberry.cache.base.Tuple3
import com.kingnetdc.blueberry.core.io.Path
import com.kingnetdc.model.{ApplicationConfig, ApplicationConfigParser}
import com.kingnetdc.utils.{AbstractOptions, JsonUtils, KafkaOffsetUtil}
import com.kingnetdc.watermelon.input.KingnetInputDStream
import com.kingnetdc.watermelon.utils.ConfigurationKeys._
import com.kingnetdc.watermelon.utils._
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Duration, StreamingContext}

import scala.collection.JavaConversions._

object MediaStatus extends Logging{
    class Options(className: String, args: Seq[String]) extends AbstractOptions(className, args) {}

    private val prefix = "wutiao:media"


    def getKafkaParams(
            bootstrapServers: String, topic: String,
            group: String, offSetOpt: Option[String] = None
    ): Map[String, Object] = {
        Map(
            BootstrapServers -> bootstrapServers,
            TOPIC -> topic,
            KAFKA_GROUP -> group,
            "auto.offset.reset" -> offSetOpt.getOrElse("earliest"),
            "key.deserializer" -> classOf[StringDeserializer],
            "value.deserializer" -> classOf[StringDeserializer],
            "enable.auto.commit" -> (false: java.lang.Boolean)
        )
    }

    //判断status是否为1
    def checkInfo(line: String): Boolean = {
        try {
            val jsonNode = JsonUtils.getJsonNode(line)
            val status = jsonNode.at("/status").asInt()
            if (status == 1) {
                true
            } else {
                false
            }
        } catch {
            case ex:Exception =>
                logger.error("Fail to check log")
                false
        }
    }

    def checkTime(line: String): Boolean = {
        try {
            val jsonNode = JsonUtils.getJsonNode(line)
            val createTime = jsonNode.at("/create_time").asText()
            if (DateUtils.getYMDHMS.parse(createTime).getTime >
                    DateUtils.getYMDHMS.parse("2018-06-05 00:00:00").getTime) {
                true
            } else {
                false
            }
        } catch {
            case e: Exception => {
                logger.error("Fail to check line", e)
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

        val sparkConf = new SparkConf()
        checkSparkConf(sparkConf)

        val sparkContext = new SparkContext(sparkConf)
        val streamingContext = new StreamingContext(sparkContext,
            Duration(sparkConf.get(SPARK_STREAMING_DURATION).toLong))

        var offsetRanges = Array[OffsetRange]()
        val mediaDStream = KingnetInputDStream.createDirectKafkaStream[String, String](
            streamingContext, getKafkaParams(bootstrapServers, kafkaTopic, kafkaTopicGroup)
        ).transform(rdd => {
            offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
            rdd
        }).map(_.value).filter( line => {
            val jsonNode = JsonUtils.getJsonNode(line)
            val event = jsonNode.at("/event").asText()
            if (event.equals("media")) {
                true
            } else {
                false
            }
        }).map( line => {
            try {
                val jsonNode = JsonUtils.getJsonNode(line)
                val mediaid = Option(jsonNode.at("/ouid").asText()).filter(StringUtils.nonEmpty).get
                val mediaType = Option(jsonNode.at("/properties/type").asInt()).getOrElse(-1)
                val status = Option(jsonNode.at("/properties/status").asInt()).getOrElse(-1)
                Some(
                    new Tuple3(s"${prefix}:${mediaid}", s"${status},${mediaType}", 86400 * 2)
                )
            } catch {
                case e: Exception =>
                    logger.error(s"Failed to parse ${line}", e)
                    None
            }

        }).filter( line => {
            line match {
                case Some(tuple3) => true
                case None => false
            }
        })

        //缓存结果到redis和mysql
        mediaDStream.foreachRDD((rdd, batchTime) => {
            rdd.foreachPartition(
                itemIter => {
                    val kdcCache = KdcCache.builder(getClass.getClassLoader.getResourceAsStream(kdcCacheConfig))
                    val setTry = CommonUtils.safeRelease(kdcCache)( kdcCache => {
                        kdcCache.multiSet(itemIter.map(_.get).toList)
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
