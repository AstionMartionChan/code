package com.kingnetdc.job

import java.nio.charset.Charset
import com.google.common.hash.{BloomFilter, PrimitiveSink, Funnel}
import com.kingnetdc.blueberry.cache.KdcRedisCluster
import com.kingnetdc.utils.{StatisticsUtils, KafkaOffsetUtil, RedisBasedBloomFilter, JsonUtils}
import com.kingnetdc.utils.StatisticsUtils._
import com.kingnetdc.watermelon.input.KingnetInputDStream
import com.kingnetdc.watermelon.utils.{DateUtils, ConfigUtils, Logging, StringUtils}
import com.kingnetdc.watermelon.utils.ConfigurationKeys._
import com.kingnetdc.watermelon.utils.AppConstants._
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import org.apache.spark.streaming.{Duration, StreamingContext}
import scala.collection.JavaConversions._
import com.kingnetdc.model.EventTypeEnum.SHOWINPAGE
import scala.collection.mutable.ListBuffer

/**
 * Created by zhouml on 17/07/2018.
 *  曝光数据存储
 */

/*
1. uid 和 did 同时存在(都是正常值)，则视为登录; 除了处理正常的uid曝光合并, 还需要将did的曝光合并到uid上, 并删除对应的did

2. 只有did，uid不是正常值时(不管是null还是-1)时，则视为未登录; 直接进行did的处理
*/
//scalastyle:off
object ShowInPageStatistics extends Logging {

    private val SHOWINPAGE_KEY = "wutiao:showinpage"

    def buildShowInPageRedisKey(env: String, uniqueId: String) = {
        List(SHOWINPAGE_KEY, env, uniqueId).mkString(COLON)
    }

    def parse(line: String): Option[((Option[String], Option[String]), List[String])] = {
        try {
            val jsonNode = JsonUtils.getJsonNode(line)
            val event = jsonNode.at("/event").asText()

            if (event == SHOWINPAGE.toString) {
                val rawUidOpt =
                    Option(jsonNode.at("/ouid"))
                        .map(_.asText())
                        .filter(StringUtils.nonEmpty)

                // val ts = Option(jsonNode.at("/_sst")).map(_.asLong).get
                val rawDidOpt =
                    Option(jsonNode.at("/did"))
                        .map(_.asText())
                        .filter(StringUtils.nonEmpty)

                val itemIds =
                    Option(jsonNode.at("/properties/itemlist"))
                        .map(_.asText())
                        .filter(StringUtils.nonEmpty)
                        .map { itemAndTypeStr =>
                            itemAndTypeStr.trim().split(COMMA).map { itemAndType =>
                                itemAndType.trim().split(ALT)(0)
                            }.toList
                        }.getOrElse(Nil)


                (rawUidOpt, rawDidOpt) match {
                    case (Some(uid), Some(did)) =>
                        // 视为登录
                        if (uid != ABNORMAL_VALUE && did != ABNORMAL_VALUE) {
                            Some((Some(uid), Some(did)), itemIds)
                        } else if (uid == ABNORMAL_VALUE && did != ABNORMAL_VALUE) {
                            // 视为未登录
                            Some((None, Some(did)), itemIds)
                        } else {
                            None
                        }
                    case (None, Some(did)) =>
                        if (did != ABNORMAL_VALUE) {
                            // 视为未登录
                            Some((None, Some(did)), itemIds)
                        } else {
                            None
                        }
                    case _ => None
                }
            } else {
                None
            }
        } catch {
            case ex: Exception =>
                logger.error(s"Failed to parse ${line}", ex)
                None
        }
    }

    private val stringFunnel: Funnel[String] = new Funnel[String] {
        override def funnel(from: String, into: PrimitiveSink): Unit = {
            into.putString(from, Charset.forName(UTF8))
        }
    }

    /**
     *  https://hur.st/bloomfilter/?n=100000&p=0.001&m=&k=
     *
     * @param items
     * @param funnel
     * @param expectedInsertions
     * @param fpp
     */
    def buildBloomFilter(
        items: List[String], funnel: Funnel[String],
        expectedInsertions: Long, fpp: Double
    ) = {
        val bloomFilter = BloomFilter.create[String](
            funnel, expectedInsertions, fpp
        )

        items.foreach { item =>
            bloomFilter.put(item)
        }
        bloomFilter
    }

    /**
     * @param showInPageItems
     * @param config
     */
    def saveShowInPageToRedis(
        showInPageItems: List[((Option[String], Option[String]), scala.List[String])],
        config: Map[String, String]
    ) = {
        val redisEnv = config("redis.env")
        val redisConnect: String = config("redis.connect")
        var kdcRedisCluster: KdcRedisCluster = null

        try {
            kdcRedisCluster = new KdcRedisCluster(redisConnect)

            showInPageItems.foreach {
                case ((uidOpt, didOpt), items) =>
                    try {
                        (uidOpt, didOpt) match {
                            case (Some(uid), Some(did)) =>
                                val uidRedisKey = buildShowInPageRedisKey(redisEnv, uid)
                                val mergedUidBloomFilter =
                                    getMergedBloomFilter(uidRedisKey, items, config, kdcRedisCluster)

                                val didRedisKey = buildShowInPageRedisKey(redisEnv, did)
                                val didBloomFilterOpt =
                                    RedisBasedBloomFilter.getBloomFilter[String](didRedisKey, stringFunnel, kdcRedisCluster)

                                didBloomFilterOpt match {
                                    case Some(didBloomFilter) =>
                                        mergedUidBloomFilter.putAll(didBloomFilter)

                                        RedisBasedBloomFilter.setBloomFilter(uidRedisKey, mergedUidBloomFilter, kdcRedisCluster)
                                        kdcRedisCluster.getJedisCluster().del(didRedisKey)
                                    case None =>
                                        RedisBasedBloomFilter.setBloomFilter(uidRedisKey, mergedUidBloomFilter, kdcRedisCluster)
                                }
                            case (None, Some(did)) =>
                                val didRedisKey = buildShowInPageRedisKey(redisEnv, did)
                                val mergedDidBloomFilter =
                                    getMergedBloomFilter(didRedisKey, items, config, kdcRedisCluster)
                                RedisBasedBloomFilter.setBloomFilter(didRedisKey, mergedDidBloomFilter, kdcRedisCluster)
                            case _ =>
                                logger.warn(s"${(uidOpt, didOpt)} will not be processed")
                        }
                    } catch {
                        case ex: Exception =>
                            logger.error(s"Failed to save show in page results for ${(uidOpt, didOpt)}", ex)

                            val formattedDate = DateUtils.getYMDHMS.format(System.currentTimeMillis)
                            val alertMessage = s"${formattedDate} 五条曝光-Redis 曝光数据写入失败 ${(uidOpt, didOpt)}"
                            StatisticsUtils.alertByChannel(alertMessage, config)
                    }
            }
        } catch {
            case ex: Exception =>
                logger.error("Failed to save show in page results", ex)

                val formattedDate = DateUtils.getYMDHMS.format(System.currentTimeMillis)
                val alertMessage = s"${formattedDate} 五条曝光-Redis 曝光数据写入失败"
                StatisticsUtils.alertByChannel(alertMessage, config)
        } finally {
            Option(kdcRedisCluster).foreach { _.close() }
        }
    }

    def getMergedBloomFilter(
        redisKey: String, itemIds: scala.List[String],
        config: Map[String, String], kdcRedisCluster: KdcRedisCluster
    ) = {
        val expectedInsertions: Long = config.get("bloomfilter.insertion").map(_.toLong).getOrElse(100000)
        val fpp: Double = config.get("bloomfilter.fpp").map(_.toDouble).getOrElse(0.001)

        val oldBloomFilterOpt =
            RedisBasedBloomFilter.getBloomFilter[String](redisKey, stringFunnel, kdcRedisCluster)

        val currentBloomFilter = buildBloomFilter(itemIds, stringFunnel, expectedInsertions, fpp)

        val mergedBloomFilter =
            oldBloomFilterOpt.fold(currentBloomFilter)(oldBloomFilter => {
                oldBloomFilter.putAll(currentBloomFilter)
                oldBloomFilter
            })

        mergedBloomFilter
    }

    def main(args: Array[String]) = {
        if (args.length < 1) {
            throw new IllegalArgumentException("configuration path is missing")
        }

        val config = ConfigUtils.loadFromFile(args(0)).toMap
        val bootstrapServers = config("bootstrap.servers")
        val kafkaTopic = config("source.topic")
        val kafkaTopicGroup = config("kafka.topic.group")

        val kafkaLogStartOpt: Option[Long] = config.get("kafka.log.start").map { dateStr =>
            DateUtils.getYMDHMS.parse(dateStr).getTime
        }
        val offSetOpt = config.get("offset.reset")

        val sparkConf = new SparkConf()
        val streamingContext = new StreamingContext(
            sparkConf, Duration(sparkConf.get(SPARK_STREAMING_DURATION).toLong)
        )
        val partitionNum = sparkConf.getInt(SPARK_OUTPUT_PARTITION, DEFAULT_OUTPUT_PARTITION)

        var offsetRanges = Array[OffsetRange]()
        val batchSize = config.get("batch.size").map(_.toInt).getOrElse(100)

        val showinPageStream =
            KingnetInputDStream.createDirectKafkaStream[String, String](
                streamingContext,
                getKafkaParams(bootstrapServers, kafkaTopic, kafkaTopicGroup, offSetOpt)
            ).transform { (rdd, time) =>
                offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
                rdd
            }.map(_.value()).flatMap(parse)

        showinPageStream.foreachRDD { (rdd, batchTime) =>
            // 减少连接, 分批
            rdd.reduceByKey(_ ::: _, partitionNum).foreachPartition { uidDidAndItemIter =>
                if (uidDidAndItemIter.nonEmpty) {
                    var rowCount = 0
                    val uidDidAndItemBuffer = new ListBuffer[((Option[String], Option[String]), List[String])]()

                    while (uidDidAndItemIter.hasNext) {
                        uidDidAndItemBuffer += uidDidAndItemIter.next()
                        rowCount += 1

                        if (rowCount % batchSize == 0) {
                            saveShowInPageToRedis(uidDidAndItemBuffer.toList, config)
                            uidDidAndItemBuffer.clear()
                            rowCount = 0
                        }
                    }

                    if (rowCount > 0) {
                        saveShowInPageToRedis(uidDidAndItemBuffer.toList, config)
                    }
                }
            }

            KafkaOffsetUtil.saveOffsetToZookeeper(
                sparkConf.get(SPARK_KAFKA_OFFSET_ZK_CONNECT),
                kafkaTopicGroup, offsetRanges
            )
        }

        streamingContext.start()
        streamingContext.awaitTermination()
    }

}
//scalastyle:on