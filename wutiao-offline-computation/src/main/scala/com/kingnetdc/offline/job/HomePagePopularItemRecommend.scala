package com.kingnetdc.offline.job

import com.google.common.base.Charsets
import com.google.common.hash.Hashing
import com.kingnetdc.blueberry.cache.{KdcCache, RedisClusterCache}
import com.kingnetdc.offline.utils.SQLBuilder._
import com.kingnetdc.offline.utils.{JsonUtils, SQLBuilder}
import com.kingnetdc.watermelon.output.KafkaSink
import com.kingnetdc.watermelon.utils.AppConstants._
import com.kingnetdc.watermelon.utils.SparkUtils._
import com.kingnetdc.watermelon.utils.{DateUtils, StringUtils, ConfigUtils, CommonUtils, Logging}
import org.apache.hadoop.hbase.client.{Get, Connection, ConnectionFactory}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{TableName, HBaseConfiguration, HConstants}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.{Row, SparkSession, DataFrame}
import org.joda.time.DateTime
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.math.min
import scala.util.{Random, Failure, Success}

/**
 * Created by zhouml on 08/06/2018.
 *
 *  + 分类型计算大众热门, 并且输出到Redis中
 *  + 另外基于分类型的大众热门计算, 根据指定规则, 生成大众热门
 *
 */
object HomePagePopularItemRecommend extends BaseStatistics with Logging {

    private val NEWS_TYPE = "1"

    private val VIDEO_TYPE = "2"

    private val REDIS_KEY_PREFIX = "wutiao:item:"

    private val NewsRedisKey = "wutiao:homepage-rcmd:popular-news"

    private val VideoRedisKey = "wutiao:homepage-rcmd:popular-video"

    def topViewedByItemtypeSQL(
        datetime: DateTime, tableName: String,
        topN: Long, inRecentDay: Int = 7
    ) = {
        val selectSQL =
            s"""
               select itemtype, itemid, total_read, rank from (
                   select itemtype, itemid, total_read, row_number() over (partition by itemtype order by total_read desc) as rank from (
                       select itemtype, itemid, sum(read_cnt) as total_read
                       from ${tableName}
                       where
                            itemtype in ('${NEWS_TYPE}', '${VIDEO_TYPE}') and
                            itemid is not null and
                            (${SQLBuilder.getInRecentDay(datetime, inRecentDay)})
                       group by itemtype, itemid
                   ) temp
               ) temp1 where rank <= ${topN}
            """

        logger.info(s"TopViewedByItemtypeSQL: ${selectSQL}")
        selectSQL
    }

    def topLikedOrCommentedSQL(
        dateTime: DateTime, tableName: String, inRecentHour: Int = 24
    ) = {
        val selectSQL =
            s"""
                select
                    itemtype,
                    itemid,
                    cast((sum(like_cnt) * 0.5 + sum(comment_cnt) * 0.5) as double) as total_like_comment_score
                from ${tableName}
                where
                    ${getInRecentHour(dateTime, inRecentHour)}
                and
                    itemtype in ('${NEWS_TYPE}', '${VIDEO_TYPE}')
                and
                    itemid is not null
                group by
                    itemtype, itemid
            """

        logger.info(s"TopLikedOrCommentedSQL: ${selectSQL}")
        selectSQL
    }

    def popularItemSQL(
        topViewed: DataFrame, topLikedOrCommented: DataFrame, topN: Int
    ): String = {
        topViewed.createOrReplaceTempView("tbl_viewed")
        topLikedOrCommented.createOrReplaceTempView("tbl_liked_or_commented")

        val selectSQL =
            s"""
                select *
                from (
                    select
                        itemtype, itemid, score,
                        row_number() over (
                            partition by itemtype order by score desc
                        ) as rank
                    from (
                        select
                           l.itemtype as itemtype,
                           l.itemid as itemid,
                           nvl(total_like_comment_score, 0) as score
                        from
                           tbl_viewed l
                           left outer join
                           tbl_liked_or_commented r
                        on
                           l.itemtype = r.itemtype and
                           l.itemid = r.itemid
                    ) temp
                ) temp1
                where rank <= ${topN}
            """

        logger.info(s"PopularItemSQL: ${selectSQL}")
        selectSQL
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

    private def getKafkaParams(bootstrapServers: String): Map[String, Object] = {
        Map(
            "bootstrap.servers" -> bootstrapServers,
            "key.serializer" -> classOf[StringSerializer],
            "value.serializer" -> classOf[StringSerializer],
            "enable.auto.commit" -> (false: java.lang.Boolean)
        )
    }

    def savePopularItemToRedis(
        normalizedNewsItemScore: List[(String, Double)],
        normalizedVideoItemScore: List[(String, Double)],
        redisConnect: String, expireAt: Int
    ) {
        import com.kingnetdc.blueberry.cache.base.Constants._
        val redisCluster = new RedisClusterCache(
            Map(REDIS_CLUSTER_CONNECT -> redisConnect)
        )

        CommonUtils.safeRelease(redisCluster)(redisCluster => {
            if (normalizedNewsItemScore.nonEmpty) {
                val newsRedisValue = JsonUtils.render(
                    normalizedNewsItemScore.map {
                        case (itemId, score) => Map(
                            itemId -> score
                        )
                    }
                )

                redisCluster.set(NewsRedisKey, newsRedisValue, expireAt)
            }

            if (normalizedVideoItemScore.nonEmpty) {
                val videoRedisValue = JsonUtils.render(
                    normalizedVideoItemScore.map {
                        case (itemId, score) => Map(
                            itemId -> score
                        )
                    }
                )
                redisCluster.set(VideoRedisKey, videoRedisValue, expireAt)
            }
        })() match {
            case Success(_) =>
            case Failure(e) => throw e
        }
    }

    def coldMostUserLikeJson(itemUidAndScore: List[(String, String, Double)]) = {
        val jsonData =
            itemUidAndScore.map {
                case (itemId, uid, score) =>
                    Map(
                        "id" -> itemId,
                        "uid" -> uid,
                        "score" -> score
                    )
            }

        val resultMap =
            Map(
                "index" -> "blockchain_focus_rcmd",
                "type" -> "cold_most_users_like_rcmd_v1",
                "id" -> "cold_rcmd",
                "op" -> 1,
                "last_updated_at" -> new DateTime().toString(YMDHMS),
                "data" -> jsonData
            )

        JsonUtils.render(resultMap)
    }

    def takePopularItemByRatio(
        popularNews: List[(String, Double)],
        popularVideo: List[(String, Double)],
        mixRatio: (Int, Int), topN: Int
    ): (List[(String, Double)], List[(String, Double)]) = {
        val (newsRatio, videoRatio) = mixRatio
        val newsLen = popularNews.size
        val videoLen = popularVideo.size

        val maxNewsToFetch = ((newsRatio / (newsRatio + videoRatio).toDouble) * topN).toInt

        val maxVideoToFetch = ((videoRatio / (newsRatio + videoRatio).toDouble) * topN).toInt

        if (newsLen >= newsRatio * videoLen) {
            val newsToFetch = min(maxNewsToFetch, newsRatio * videoLen)

            val videoToFetch = min(videoLen, maxVideoToFetch)

            (popularNews.take(newsToFetch), popularVideo.take(videoToFetch))
        } else {
            val newsToFetch = min(maxNewsToFetch, newsLen)
            val videoToFetch = min(maxVideoToFetch, (videoRatio / newsRatio.toDouble) * newsLen).toInt

            (popularNews.take(newsToFetch), popularVideo.take(videoToFetch))
        }
    }

    def getMixRatio(config: Map[String, String]): (Int, Int) = {
        val newsAndVideoRatio = config("news.video.ratio")
        val ratio = newsAndVideoRatio.split(COLON)
        (ratio(0).toInt, ratio(1).toInt)
    }

    def kickDeletedOut(
        popularNews: List[(String, Double)],
        popularVideo: List[(String, Double)],
        config: Map[String, String]
    ) = {
        val kdcItemCacheConfig: String = config("cache.config")
        val topN = config("user.recommend.top").toInt

        // Fail fast
        val kdcCache = KdcCache.builder(getClass().getClassLoader().getResourceAsStream(kdcItemCacheConfig))

        try {
            val itemInfo: Map[String, (String, Long)] =
                getItemStatusAndCreateTime(
                    (popularNews ::: popularVideo).map(_._1),
                    REDIS_KEY_PREFIX,
                    kdcCache
                )

            (filterOutDeleted(popularNews, itemInfo).take(topN), filterOutDeleted(popularVideo, itemInfo).take(topN))
        } finally {
            Option(kdcCache).foreach { _.close() }
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
     *  原始的热门数据 ---> 剔除已删除的 ---> 按照比例提取, 穿插排序
     *
     * @param popularNews
     * @param popularVideo
     * @param config
 *
     * @return
     */
    def getRandomOrderedPopularItem(
        popularNews: List[(String, Double)],
        popularVideo: List[(String, Double)],
        config: Map[String, String]
    ): List[(String, Double)] = {
        val pageSize = config("page.size").toInt
        val newsVideoRatio = getMixRatio(config)
        val topN = config("user.recommend.top").toInt
        val pushThreshold = config("push.threshold").toInt

        val (popularNewsInRatio, popularVideoInRatio) =
            takePopularItemByRatio(popularNews, popularVideo, newsVideoRatio, topN)

        val recommendedItem =
            randomOrdered(
                popularNewsInRatio, popularVideoInRatio,
                pageSize, newsVideoRatio
            )

        val recommendSize = recommendedItem.size

        if (recommendSize >= pushThreshold) {
            recommendedItem
        } else {
            Nil
        }
    }

    def sendColdMostUserLikeToKafka(
        itemUidAndScore: List[(String, String, Double)],
        producerBroadCast: Broadcast[KafkaSink[String, String]],
        config: Map[String, String]
    ): Unit = {
        val topN = config("user.recommend.top").toInt

        logger.info(s"Send ${itemUidAndScore.size} cold most user like record to kafka")

        if (itemUidAndScore.size < topN / 2) {
            val formattedDate = DateUtils.getYMDHMS.format(System.currentTimeMillis)
            val alertMessage = s"${formattedDate} 五条大众热门-Kafka消息推送数过低"
            alertByChannel(alertMessage, config)
        }

        if (itemUidAndScore.nonEmpty) {
            val topic: String = config("topic")
            val kafkaSink = producerBroadCast.value
            val recordValue = coldMostUserLikeJson(itemUidAndScore)
            val record = new ProducerRecord[String, String](topic, recordValue)
            // Fail fast
            kafkaSink.send(record).get()
        }
    }

    def getMd5Hash(input: String): String =  {
        Hashing.md5().hashString(input, Charsets.UTF_8).toString
    }

    def getUserIdByItemFromHbase(
        popularItems: List[(String, Double)], config: Map[String, String]
    ): List[(String, String, Double)] = {
        val zkQuoram = config("hbase.zookeeper.connect")
        val tableName: String = config("hbase.item.detail.table")

        val hbaseConfig = HBaseConfiguration.create()
        hbaseConfig.set(HConstants.ZOOKEEPER_QUORUM, zkQuoram)

        var connection: Connection = null

        try {
            connection = ConnectionFactory.createConnection(hbaseConfig)

            val gets = popularItems.map(_._1).map { itemId =>
                new Get(getMd5Hash(itemId).getBytes())
                    .addColumn("base_info".getBytes(), "uid".getBytes())
            }
            val hbaseResult = connection.getTable(TableName.valueOf(tableName)).get(gets)

            (popularItems zip hbaseResult).flatMap {
                case ((itemId, score), rawResult) =>
                    for {
                        result <- Option(rawResult)
                        uid <- Option(result.getValue("base_info".getBytes(), "uid".getBytes())).map { uidInByte =>
                            Bytes.toString(uidInByte)
                        }.filter(StringUtils.nonEmpty)
                    } yield {
                        (itemId, uid, score)
                    }
            }
        } finally {
            Option(connection).foreach { conn =>
                 logger.info("Closing hbase connection")
                 conn.close()
            }
        }
    }

    def main(args: Array[String]) {
        if (args.length < 1) {
            throw new IllegalArgumentException("configuration path is missing")
        }

        val config = ConfigUtils.loadFromFile(args(0)).toMap

        val bootstrapServers = config("bootstrap.servers")
        val userBehaviorTable = config("source.table")
        val topViewedN = config("viewed.top").toInt
        val topN = config("user.recommend.top").toInt
        val successMarkPath = config("success.mark.path")

        val sparkSession = SparkSession.builder.enableHiveSupport().getOrCreate()
        val sparkContext = sparkSession.sparkContext

        val current = new DateTime()
        val lastHour = current.minusHours(1)

        val lastHourPath = buildSuccessPath(sparkContext, successMarkPath, lastHour)

        if (!existsPath(sparkContext, lastHourPath)) {
            var producerBroadCast: Broadcast[KafkaSink[String, String]] = null

            try {
                producerBroadCast =
                    sparkContext.broadcast(
                        KafkaSink.create[String, String](getKafkaParams(bootstrapServers))
                    )

                val topViewed = sparkSession.sql(
                    topViewedByItemtypeSQL(current, userBehaviorTable, topViewedN)
                )
                val topLikedOrCommented = sparkSession.sql(
                    topLikedOrCommentedSQL(current, userBehaviorTable)
                )

                // 冷启动推荐时候, 有剔除逻辑, 所以多取一点
                val popularItem = sparkSession.sql(
                    popularItemSQL(topViewed, topLikedOrCommented, topN * 2)
                )

                val (newsItemScore, videoItemScore) =
                    popularItem.collect().map {
                        case Row(itemType: String, itemId: String, score: Double, _) => (itemType, itemId, score)
                    }.partition(pair => pair._1 == NEWS_TYPE) match {
                        case (news, videos) =>
                            (
                                news.map(pair => (pair._2, pair._3)).toList,
                                videos.map(pair => (pair._2, pair._3)).toList
                           )
                    }

                // 冷启动 cold_most_users_like_rcmd_v1
                val (withDeletedOutNews, withDeletedOutVideo) = kickDeletedOut(newsItemScore, videoItemScore, config)

                val randomOrderedPopularItem =
                    getRandomOrderedPopularItem(withDeletedOutNews, withDeletedOutVideo, config)

                logger.info(s"${randomOrderedPopularItem.size} records before mapping uid")

                val withUserIdMapped = getUserIdByItemFromHbase(randomOrderedPopularItem, config)
                logger.info(s"${withUserIdMapped.size} records after mapping uid")

                sendColdMostUserLikeToKafka(withUserIdMapped, producerBroadCast, config)

                touchSuccessMark(sparkContext, successMarkPath, lastHour)
            } finally {
                Option(producerBroadCast).foreach { _.destroy() }
            }
        } else {
            logger.info(s"Path ${lastHourPath} already exists !!")
        }
    }

}
