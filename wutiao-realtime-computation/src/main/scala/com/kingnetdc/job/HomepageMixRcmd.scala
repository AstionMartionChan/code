package com.kingnetdc.job

import com.kingnetdc.utils.{StatisticsUtils, KafkaOffsetUtil, JsonUtils}
import com.kingnetdc.blueberry.cache.{KdcCache, RedisClusterCache}
import com.kingnetdc.watermelon.utils.ConfigurationKeys._
import com.kingnetdc.blueberry.cache.base.Constants.REDIS_CLUSTER_CONNECT
import com.kingnetdc.watermelon.clients.TopicPartitionOffset
import com.kingnetdc.watermelon.input.KingnetInputDStream
import com.kingnetdc.watermelon.output.{HBaseConnection, KafkaSink}
import com.kingnetdc.watermelon.utils.{StringUtils, DateUtils, KafkaOffsetManager, ConfigUtils, CommonUtils, Logging}
import org.apache.hadoop.hbase.HConstants
import org.apache.hadoop.hbase.client.Get
import org.apache.hadoop.hbase.util.Bytes
import org.apache.kafka.clients.producer.{ProducerConfig, RecordMetadata, Callback, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Time, Duration, StreamingContext}
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.util.{Random, Failure, Success}
import com.kingnetdc.watermelon.utils.AppConstants._
import math.min

/**
 * Created by zhouml on 02/06/2018.
 */
object HomePageMixRcmd extends Logging {

    private val UID = "uid"

    private val NewsRedisKey = "wutiao:homepage-rcmd:popular-news"

    private val VideoRedisKey = "wutiao:homepage-rcmd:popular-video"

    private val REDIS_KEY_PREFIX = "wutiao:item:"

    private val VALID_ITEM_STATUS = "0"

    private val MILLIS_IN_DAY: Long = 1000 * 3600 * 24

    private def getOutputKafkaParams(config: Map[String, String]): Map[String, Object] = {
        val bootstrapServers: String = config("bootstrap.servers")
        // 5m
        val maxRequestSize = config.get(ProducerConfig.MAX_REQUEST_SIZE_CONFIG).map(_.toInt).getOrElse(5242880)
        val retryTimes = config.get(ProducerConfig.RETRIES_CONFIG).map(_.toInt).getOrElse(3)

        Map(
            "bootstrap.servers" -> bootstrapServers,
            "key.serializer" -> classOf[StringSerializer],
            "value.serializer" -> classOf[StringSerializer],
            "enable.auto.commit" -> (false: java.lang.Boolean),
            ProducerConfig.MAX_REQUEST_SIZE_CONFIG -> Integer.valueOf(maxRequestSize),
            ProducerConfig.RETRIES_CONFIG -> Integer.valueOf(retryTimes)
        )
    }

    def normalizeItemScore(itemScore: List[(String, Double)]) = {
        if (itemScore.nonEmpty) {
            val scores = itemScore.map(_._2)
            val min = scores.min
            val max = scores.max

            itemScore.map {
                case (item, score) =>
                    val normalizedScore = {
                        if (min == max) score
                        else (score - min) / (max - min)
                    }
                    item -> normalizedScore
            }
        } else Nil
    }

    private def getHbaseConfig(zkQuoram: String): Map[String, String] = {
        Map(HConstants.ZOOKEEPER_QUORUM -> zkQuoram)
    }

    private[job] def getItemScoreFromRedis(redisCluster: RedisClusterCache, key: String): List[(String, Double)] = {
        Option(
            redisCluster.get(key)
        ).map { value =>
            val deserializedItemScore = JsonUtils.parse(value, classOf[List[Map[String, Any]]])
            deserializedItemScore.flatMap { itemScore =>
                itemScore.map {
                    case (itemId, score) => itemId -> score.toString.toDouble
                }
            }
        }.getOrElse(Nil)
    }

    def getHomePagePopularItem(redisConnect: String): (List[(String, Double)], List[(String, Double)]) = {
        val redisCluster = new RedisClusterCache(
            Map(REDIS_CLUSTER_CONNECT -> redisConnect)
        )

        CommonUtils.safeRelease(redisCluster)(redisCluster => {
            (
                getItemScoreFromRedis(redisCluster, NewsRedisKey),
                getItemScoreFromRedis(redisCluster, VideoRedisKey)
            )
        })() match {
            case Success(pair) => pair
            case Failure(e) =>
                logger.error("Failed to get popular news | video content", e)
                (Nil, Nil)
        }
    }


    /**
     *  [order issue](https://stackoverflow.com/questions/12270821/htable-getlistget-results-order)
     *
     * @param userIds
     * @param hbaseConnectionBroadCast
     * @param config
     *
     * @return
     */
    def getUserRecommendFromHbase(
        userIds: List[String],
        hbaseConnectionBroadCast: Broadcast[HBaseConnection],
        config: Map[String, String]
    ): Map[String, (List[(String, Double)], List[(String, Double)])] = {
        val tableName: String = config("hbase.item.rcmd.table")

        try {
            val hbaseConnection = hbaseConnectionBroadCast.value

            val gets = userIds.map(uid => new Get(StringUtils.md5AsByteArray(uid)))
            val hbaseResults = hbaseConnection.bulkGet(tableName, gets)

            (userIds zip hbaseResults).flatMap {
                case (userId, rawResult) =>
                    for {
                        result <- Option(rawResult)
                        rowByte <- Option(result.getRow())
                    } yield {
                        val news =
                            Option(
                                result.getValueAsByteBuffer("item".getBytes(), "news".getBytes())
                            ).map { newsValueInByte =>
                                StringUtils.deserializeObject[Array[(String, Double)]](
                                    Bytes.getBytes(newsValueInByte)
                                ).toList
                            }.getOrElse(Nil)

                        val videos =
                            Option(
                                result.getValueAsByteBuffer("item".getBytes(), "video".getBytes())
                            ).map { videoValueInByte =>
                                StringUtils.deserializeObject[Array[(String, Double)]](
                                    Bytes.getBytes(videoValueInByte)
                                ).toList
                            }.getOrElse(Nil)


                        userId -> (normalizeItemScore(news), normalizeItemScore(videos))
                    }
             }.toMap
        } catch {
            case ex: Exception =>
                val formattedDate = DateUtils.getYMDHMS.format(System.currentTimeMillis)
                val alertMessage = s"${formattedDate} 五条首页融合-HBase 从${tableName}读取推荐数据失败"
                StatisticsUtils.alertByChannel(alertMessage, config)
                logger.error(s"Failed to get user recommend from ${tableName}", ex)
                Map.empty
        }
    }

    /**
     * 需要同时满足news:video的混合比例, 与最大取出多少
     * 若news:video=4:1, 最大取出5000
     * 则除了按比例去取之外, 还要确保news最多取出4000, video最多取出1000
     *
     * @param mixedNews  混合后的资讯
     * @param mixedVideo 混合后的视频
     * @param mixRatio   混合比例: 资讯在前面, 视频在后面
     * @param topN 最多取多少条
     *
     * @return
     */
    def takeMixedItemByRatio(
        mixedNews: List[(String, Double)],
        mixedVideo: List[(String, Double)],
        mixRatio: (Int, Int), topN: Int
    ): (List[(String, Double)], List[(String, Double)]) = {
        val (newsRatio, videoRatio) = mixRatio
        val newsLen = mixedNews.size
        val videoLen = mixedVideo.size

        val maxNewsToFetch = ((newsRatio / (newsRatio + videoRatio).toDouble) * topN).toInt
        val maxVideoToFetch = ((videoRatio / (newsRatio + videoRatio).toDouble) * topN).toInt

        if (newsLen >= newsRatio * videoLen) {
            val newsToFetch = min(maxNewsToFetch, newsRatio * videoLen)

            val videoToFetch = min(videoLen, maxVideoToFetch)

            (mixedNews.take(newsToFetch), mixedVideo.take(videoToFetch))
        } else {
            val newsToFetch = min(maxNewsToFetch, newsLen)
            val videoToFetch = min(maxVideoToFetch, (videoRatio / newsRatio.toDouble) * newsLen).toInt

            (mixedNews.take(newsToFetch), mixedVideo.take(videoToFetch))
        }
    }

    def recommendJson(uid: String, tpe: String, itemRecommend: List[(String, Double)]) = {
        val jsonData =
            itemRecommend.map {
                case (itemId, score) =>
                    Map(
                        "id" -> itemId,
                        "score" -> score
                    )
            }

        val resultMap =
            Map(
                "index" -> "blockchain_list_rcmd",
                "type" -> tpe,
                "id" -> uid,
                "op" -> 1,
                "last_updated_at" -> DateUtils.getYMDHMS.format(System.currentTimeMillis),
                "data" -> jsonData
            )

        JsonUtils.render(resultMap)
    }


    /**
     * 保留double最大的对应的item
     *
     * @param itemScore
     */
    def distinctByItemId(itemScore: List[(String, Double)]) = {
        if (itemScore.isEmpty) {
            Nil
        } else {
            itemScore.groupBy(_._1).mapValues { itemScorePair =>
                itemScorePair.maxBy(_._2)._2
            }.toList
        }
    }

    def getMixedItem(popularItem: List[(String, Double)], recommendItem: List[(String, Double)]) = {
        distinctByItemId(recommendItem ::: popularItem)
    }

    /**
     * 视频推荐部分
     *
     * + 如果用户推荐视频为空, 则不用进行推荐
     * + 如果用户推荐视频不为空, 则将热门视频和用户推荐视频进行融合, 去重 --> 剔除曝光的 ---> 剔除删除的 ---> 创建时间排序
     *
     * @return
     */
    def getVideoRecommend(
        userId: String, mixedVideo: List[(String, Double)],
        itemInfo: Map[String, (String, Long)], config: Map[String, String]
    ): List[(String, Double)] = {
        val topN = config("user.recommend.top").toInt
        val pushThreshold = config("push.threshold").toInt

        val videoRecommend = filterOutDeleted(mixedVideo, itemInfo, config).take(topN)

        val recommendSize = videoRecommend.size
        if (recommendSize >= pushThreshold) {
            videoRecommend
        } else {
            Nil
        }
    }

    private def interleave(
        elements: List[(String, Double)], substitutes: List[(String, Double)], maxGap: Int
    ) = {
        val randomGap = new Random().nextInt(maxGap) + 1
        if (elements.size >= substitutes.size * randomGap) {
            val groupedFirst = elements.grouped(randomGap)
            ((groupedFirst zip substitutes.iterator) flatMap { case (partInFirst, elementInSecond) =>
                if (partInFirst.size == randomGap) {
                    partInFirst :+ elementInSecond
                } else {
                    partInFirst
                }
            }) ++ groupedFirst.flatten
        } else {
            elements ::: substitutes
        }
    }

    /**
     *  将形成的两份数据, 以pageSize为一组, 分别从mixedNews和mixedVideo对应比例的数据, 最后每一个pageSize中再随机的穿插
     *
     * @param news 混合之后的新闻
     * @param video 混合之后的视频
     * @param pageSize 每一页展示的数字
     * @param newsVideoRatio 新闻和视频混合比例, 决定了在每一页显示的新闻/视频数
     */
    def randomOrdered(
        news: List[(String, Double)],
        video: List[(String, Double)],
        pageSize: Int, newsVideoRatio: (Int, Int)
    ): List[(String, Double)] = {
        val (newsRatio, videoRatio) = newsVideoRatio
        val newsInPage = ((newsRatio / (newsRatio + videoRatio).toDouble) * pageSize).toInt
        val videoInPage = ((videoRatio / (newsRatio + videoRatio).toDouble) * pageSize).toInt

        def loop(
            left: Iterator[List[(String, Double)]],
            right: Iterator[List[(String, Double)]],
            buffer: ListBuffer[(String, Double)]
        ): ListBuffer[(String, Double)] = {
            (left.hasNext, right.hasNext) match {
                case (true, true) =>
                    buffer ++= interleave(left.next(), right.next(), newsRatio)
                    loop(left, right, buffer)
                case (true, false) =>
                    buffer ++= left.flatten
                case (false, true) =>
                    buffer ++= right.flatten
                case (false, false) => buffer
            }
        }

        val buffer: ListBuffer[(String, Double)] = ListBuffer()
        loop(news.grouped(newsInPage), video.grouped(videoInPage), buffer)
        buffer.toList
    }


    /**
     * 融合推荐部分
     *
     * + 如果用户资讯推荐为空, 则不用进行推荐
     * + 如果用户资讯推荐不为空, 则将用户资讯推荐, 用户视频推荐与热门资讯, 热门视频分别进行融合, 剔除删除 & 曝光
     *
     *  按照8:2比例融合 ---> 随机穿插, 以10个一页为例, 8个新闻和2个视频, 保证同类型有序
     *
     * @param mixedVideo    混合视频
     * @param itemInfo      item的相关信息 状态以及创建时间
     *
     * @return 最终的混合推荐
     */
    private def getMixedRecommend(
        userId: String,
        mixedNews: List[(String, Double)], mixedVideo: List[(String, Double)],
        itemInfo: Map[String, (String, Long)], config: Map[String, String]
    ): List[(String, Double)] = {
        val pageSize = config("page.size").toInt
        val topN = config("user.recommend.top").toInt
        val pushThreshold = config("push.threshold").toInt
        val mixInRatio: (Int, Int) = getMixRatio(config)

        val recommendedItem = {
            val start1 = System.currentTimeMillis()

            val withDeletedOutNews = filterOutDeleted(mixedNews, itemInfo, config)
            val withDeletedOutVideo = filterOutDeleted(mixedVideo, itemInfo, config)

            val filterExposureTaken = System.currentTimeMillis() - start1

            if (filterExposureTaken >= 2000L) {
                logger.info(s"Time taken in redis exposure and deletion ${filterExposureTaken} for userId ${userId}")
            }

            if (mixedVideo.isEmpty) {
                withDeletedOutNews.take(topN)
            } else {
                // 按比例融合之后, 进行随机打散
                val (news, videos) =
                    takeMixedItemByRatio(withDeletedOutNews, withDeletedOutVideo, mixInRatio, topN)
                randomOrdered(news, videos, pageSize, mixInRatio)
            }
        }

        val recommendSize = recommendedItem.size
        if (recommendSize >= pushThreshold) {
            recommendedItem
        } else {
            Nil
        }
    }

    def sendUserRecommendToKafka(
        userId: String, tpe: String, itemRecommend: List[(String, Double)],
        recommendJsonFunc: (String, String, List[(String, Double)]) => String,
        producerBroadCast: Broadcast[KafkaSink[String, String]], config: Map[String, String]
    ): Unit = {
        if (itemRecommend.nonEmpty) {
            val topic: String = config("destination.topic")
            val kafkaSink = producerBroadCast.value
            val recordValue = recommendJsonFunc(userId, tpe, itemRecommend)
            val record = new ProducerRecord[String, String](topic, recordValue)

            kafkaSink.send(record, new Callback {
                override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
                    Option(exception).foreach { ex =>
                        logger.error(s"Failed to send ${tpe} to ${topic}: ${recordValue}", ex)

                        val formattedDate = DateUtils.getYMDHMS.format(System.currentTimeMillis)
                        val alertMessage = s"${formattedDate} 五条首页融合-Kafka 发送${tpe}数据到${topic}失败"
                        StatisticsUtils.alertByChannel(alertMessage, config)
                    }
                }
            })
        }
    }

    def saveOffsetToZookeeper(
        batchTime: Time, zkQuoram: String,
        group: String, offsetRanges: Array[OffsetRange]
    ) {
        val topicPartitionOffsets =
            offsetRanges.toList.map { offsetRange =>
                TopicPartitionOffset(offsetRange.topic, offsetRange.partition, offsetRange.untilOffset)
            }

        KafkaOffsetManager.saveToZookeeper(zkQuoram, group, topicPartitionOffsets) match {
            case Success(_) =>
            case Failure(e) =>
                logger.error(
                    s"Failed to save offset for batch: ${DateUtils.getYMDHMS.format(batchTime.milliseconds)}", e
                )
        }
    }

    def getItemStatusAndCreateTime(
        itemIds: List[String], keyPrefix: String, kdcCache: KdcCache, config: Map[String, String]
    ): Map[String, (String, Long)] = {
        try {
            val prefixedItemIds = itemIds.map { itemId => s"${keyPrefix}${itemId}" }
            val itemStatusMap = kdcCache.multiGet(prefixedItemIds)

            itemStatusMap.toMap.flatMap {
                case (prefixedItemId, info) =>
                    Option(info).map { value =>
                        val status = value.split(COMMA)(0).trim()
                        val ts = value.split(COMMA)(1).trim()
                        prefixedItemId.stripPrefix(keyPrefix) -> (status, ts.toLong)
                    }
            }
        } catch {
            case ex: Exception =>
                logger.error("Failed to get item status and create time", ex)

                val formattedDate = DateUtils.getYMDHMS.format(System.currentTimeMillis)
                val alertMessage = s"${formattedDate} 五条首页融合-Redis 读取Item状态数据失败"
                StatisticsUtils.alertByChannel(alertMessage, config)
                Map.empty
        }
    }

    def filterOutDeleted(
        itemRecommend: List[(String, Double)],
        itemInfo: Map[String, (String, Long)],
        config: Map[String, String]
    ): List[(String, Double)] = {
        val currentMillis = System.currentTimeMillis
        val withInDay = config.get("within.day").map(_.toInt).getOrElse(14)

        // 默认只推送近14天的
        itemRecommend.flatMap {
            case (itemId, score) =>
                itemInfo.get(itemId).flatMap {
                    case (status, createdAt) =>
                        if (
                            status == VALID_ITEM_STATUS &&
                            currentMillis - createdAt * 1000 <= withInDay * MILLIS_IN_DAY
                        ) {
                            Some((itemId, score, createdAt))
                        } else None
                }
        }.sortWith {
            case (pair1, pair2) => pair1._3 > pair2._3
        }.map { pair =>
            pair._1 -> pair._2
        }
    }

    def periodicRenewPopularItem(
        popularItemScore: (List[(String, Double)], List[(String, Double)]),
        startTime: Long, currentTime: Long,
        config: Map[String, String]
    ) = {
        val redisConnect: String = config("redis.connect")
        val popularItemUpdateInterval: Long = config("popular.item.update.interval").toLong

        if ((currentTime - startTime) % popularItemUpdateInterval == 0) {
            logger.info("Trying to update popular item")
            getHomePagePopularItem(redisConnect)
        } else {
            popularItemScore
        }
    }

    def pushUserRecommendInBatch(
        userIds: List[String],
        popularItem: Broadcast[(List[(String, Double)], List[(String, Double)])],
        producerBroadCast: Broadcast[KafkaSink[String, String]],
        hbaseConnectionBroadCast: Broadcast[HBaseConnection],
        config: Map[String, String]
    ) = {
        var kdcCache: KdcCache = null

        try {
            val kdcCacheConfig = config("cache.config")
            kdcCache = KdcCache.builder(getClass().getClassLoader().getResourceAsStream(kdcCacheConfig))

            val (_, popularVideo) = popularItem.value

            val start = System.currentTimeMillis()
            val recommendItems =
                getUserRecommendFromHbase(userIds, hbaseConnectionBroadCast, config)
            val taken = System.currentTimeMillis() - start
            if (taken >= 3000L) {
                logger.info("Time taken in hbase read: " + taken)
            }

            userIds.foreach { userId =>
                try {
                    val (recommendNews, recommendVideo) = recommendItems.getOrElse(userId, (Nil, Nil))

                    val start2 = System.currentTimeMillis()
                    val itemIds = (popularVideo ::: recommendNews ::: recommendVideo).map(_._1).distinct
                    val itemInfo = getItemStatusAndCreateTime(itemIds, REDIS_KEY_PREFIX, kdcCache, config)
                    val itemInfoTaken = System.currentTimeMillis() - start2

                    if (itemInfoTaken >= 3000L) {
                        logger.info(s"Time taken in redis item info ${itemInfoTaken}")
                    }

                    if (recommendVideo.nonEmpty) {
                        val mixedVideo = distinctByItemId(popularVideo ::: recommendVideo)

                        val videoRecommend = getVideoRecommend(
                            userId, mixedVideo, itemInfo, config
                        )

                        sendUserRecommendToKafka(
                            userId, "video_rcmd_v1", videoRecommend,
                            recommendJson, producerBroadCast, config
                        )
                    }

                    if (recommendNews.nonEmpty) {
                        val mixedRecommend =
                            getMixedRecommend(
                                userId, recommendNews, recommendVideo, itemInfo, config
                            )

                        sendUserRecommendToKafka(
                            userId, "homepage_mix_rcmd_v1", mixedRecommend,
                            recommendJson, producerBroadCast, config
                        )
                    }
                } catch {
                    case ex: Exception =>
                        logger.error(s"Failed to push recommend for ${userId}", ex)
                }
            }
        } catch {
            case ex: Exception =>
                logger.error(s"Failed to push recommend", ex)
        } finally {
            Option(kdcCache).foreach { _.close() }
        }
    }

    def getMixRatio(config: Map[String, String]): (Int, Int) = {
        val newsAndVideoRatio = config("news.video.ratio")
        val ratio = newsAndVideoRatio.split(COLON)
        (ratio(0).toInt, ratio(1).toInt)
    }

    def main(args: Array[String]) = {
        if (args.length < 1) {
            throw new IllegalArgumentException("configuration path is missing")
        }

        val config = ConfigUtils.loadFromFile(args(0)).toMap

        val batchSize = config("batch.size").toInt

        val sparkConf = new SparkConf()
        val sparkContext = new SparkContext(sparkConf)
        val streamingContext = new StreamingContext(
            sparkContext, Duration(sparkConf.get(SPARK_STREAMING_DURATION).toLong)
        )

        val producerBroadCast: Broadcast[KafkaSink[String, String]] =
            sparkContext.broadcast(KafkaSink.create[String, String](
                getOutputKafkaParams(config)
            ))

        val hbaseConnectionBroadCast: Broadcast[HBaseConnection] =
            sparkContext.broadcast(HBaseConnection.create(
                getHbaseConfig(config("hbase.zookeeper.connect"))
            ))

        var startTime: Long = 0L
        var offsetRanges = Array[OffsetRange]()
        var popularItem: (List[(String, Double)], List[(String, Double)]) = (Nil, Nil)

        val userIdDStream: DStream[String] =
            KingnetInputDStream.createDirectKafkaStream[String, String](
                streamingContext,
                StatisticsUtils.getKafkaParams(
                    config("bootstrap.servers"), config("source.topic"), config("source.topic.group")
                )
            ).transform { (rdd, time) =>
                offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges

                if (startTime == 0L) {
                    startTime = time.milliseconds
                }

                rdd
            }.flatMap { consumerRecord =>
                try {
                    Option(
                        JsonUtils.parse(consumerRecord.value(), classOf[Map[String, Any]])(UID).toString
                    )
                } catch {
                    case ex: Exception =>
                        logger.error(s"Failed to parse ${consumerRecord.value()}", ex)
                        None
                }
            }

        userIdDStream.foreachRDD { (userIdRdd, time) =>
            popularItem = periodicRenewPopularItem(
                popularItem, startTime, time.milliseconds, config
            )

            val popularItemBroadCast = sparkContext.broadcast(popularItem)

            val outputPartition = sparkConf.getInt(SPARK_OUTPUT_PARTITION, DEFAULT_OUTPUT_PARTITION)

            userIdRdd.repartition(outputPartition).foreachPartition { userIdIterator =>
                if (userIdIterator.nonEmpty) {
                    var rowCount = 0
                    val userIdBuffer = new ListBuffer[String]()

                    while (userIdIterator.hasNext) {
                        userIdBuffer += userIdIterator.next()
                        rowCount += 1

                        if (rowCount % batchSize == 0) {
                            pushUserRecommendInBatch(
                                userIdBuffer.toList.distinct, popularItemBroadCast,
                                producerBroadCast, hbaseConnectionBroadCast, config
                            )

                            userIdBuffer.clear()
                            rowCount = 0
                        }
                    }

                    if (rowCount > 0) {
                        pushUserRecommendInBatch(
                            userIdBuffer.toList.distinct, popularItemBroadCast,
                            producerBroadCast, hbaseConnectionBroadCast, config
                        )
                    }
                }
            }

            popularItemBroadCast.destroy()

            KafkaOffsetUtil.saveOffsetToZookeeper(
                sparkConf.get(SPARK_KAFKA_OFFSET_ZK_CONNECT),
                config("source.topic.group"), offsetRanges
            )
        }

        streamingContext.start()
        streamingContext.awaitTermination()

        producerBroadCast.destroy()
        hbaseConnectionBroadCast.destroy()
    }

}
