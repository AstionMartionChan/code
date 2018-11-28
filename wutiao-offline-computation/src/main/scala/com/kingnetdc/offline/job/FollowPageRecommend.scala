package com.kingnetdc.offline.job

import java.io.ByteArrayInputStream
import com.kingnetdc.blueberry.cache.KdcCache
import com.kingnetdc.offline.utils.{JsonUtils, HBaseUtils}
import com.kingnetdc.watermelon.output.KafkaSink
import com.kingnetdc.watermelon.utils.AppConstants._
import com.kingnetdc.watermelon.utils.ConfigurationKeys._
import com.kingnetdc.watermelon.utils.SparkUtils._
import com.kingnetdc.watermelon.utils.{DateUtils, ConfigUtils, Logging}
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.kafka.clients.producer.{RecordMetadata, Callback, ProducerRecord}
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SparkSession, DataFrame}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.conf.Configuration
import com.kingnetdc.offline.utils.SQLBuilder._
import org.joda.time.DateTime
import org.xerial.snappy.Snappy
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import org.apache.commons.io.IOUtils
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.serializers.CollectionSerializer
import com.esotericsoftware.kryo.serializers.JavaSerializer
import com.esotericsoftware.kryo.io.Input

/**
 * Created by zhouml on 23/05/2018.
 *
 *  + 读取关注页推荐 与 大众热门(部分类型) 进行融合
 */
@Deprecated
object FollowPageRecommend extends BaseStatistics with Logging {

    private val REDIS_KEY_PREFIX = "wutiao:item:"

    def topViewedSQL(
        datetime: DateTime, tableName: String,
        topN: Long, inRecentDay: Int = 7
    ) = {
        val selectSQL =
            s"""
                select
                    itemid, sum(read_cnt) as total_read
                from
                    ${tableName}
                where
                    (${getInRecentDay(datetime, inRecentDay)}) and itemid is not null
                group by
                    itemid
                order by
                    total_read desc
                limit ${topN}
            """

        logger.info(s"TopViewedSQL: ${selectSQL}")
        selectSQL
    }

    def topLikedOrCommentedSQL(dateTime: DateTime, tableName: String, inRecentHour: Int = 24) = {
        val selectSQL =
            s"""
                select
                     itemid, cast((sum(like_cnt) * 0.5 + sum(comment_cnt) * 0.5) as double) as total_like_comment_score
                from
                    ${tableName}
                where
                    ${getInRecentHour(dateTime, inRecentHour)}
                    and
                    itemid is not null
                group by itemid
            """

        logger.info(s"TopLikedOrCommentedSQL: ${selectSQL}")
        selectSQL
    }

    def popularItemSQL(
        topViewed: DataFrame, topLikedOrCommented: DataFrame
    ) = {
        topViewed.createOrReplaceTempView("tbl_viewed")
        topLikedOrCommented.createOrReplaceTempView("tbl_liked_or_commented")

        val selectSQL =
            s"""
                select
                    l.itemid,
                    nvl(total_like_comment_score, 0) as score
                from
                    tbl_viewed l
                    left outer join
                    tbl_liked_or_commented r
                on l.itemid = r.itemid
                order by score desc
            """

        logger.info(s"PopularContent sql: ${selectSQL}")
        selectSQL
    }

    def userRecommendJson(
        userId: String, itemScore: List[(String, Double)], dateTime: DateTime
    ): String = {
        val jsonData =
            itemScore.map {
                case (itemId, score) =>
                    Map(
                        "id" -> itemId,
                        "score" -> score
                    )
            }

        val resultMap =
            Map(
                "index" -> "blockchain_focus_rcmd",
                "type" -> "most_users_like_rcmd_v1",
                "op" -> 1,
                "last_update_at" -> dateTime.toString(YMDHMS),
                "id" -> userId,
                "data" -> jsonData
            )

        JsonUtils.render(resultMap)
    }

    def sendUserRecommendToKafka(
        userId: String, itemRecommend: List[(String, Double)],
        producerBroadCast: Broadcast[KafkaSink[String, String]],
        config: Map[String, String],
        batchTime: DateTime
    ): Unit = {
        if (itemRecommend.nonEmpty) {
            val topic: String = config("topic")
            val kafkaSink = producerBroadCast.value
            val recordValue = userRecommendJson(userId, itemRecommend, batchTime)
            val record = new ProducerRecord[String, String](topic, recordValue)

            kafkaSink.send(record, new Callback {
                override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
                    Option(exception).foreach { ex =>
                        logger.error(s"Failed to send user recommend to ${topic}: ${recordValue}", ex)

                        val formattedDate = DateUtils.getYMDHMS.format(System.currentTimeMillis)
                        val alertMessage = s"${formattedDate} 五条关注页融合-Kafka 发送融合数据到${topic}失败"
                        alertByChannel(alertMessage, config)
                    }
                }
            })
        }
    }

    /**
     * 根据itemid进行去重, 保留score最大的
     *
     * @param itemScore
     */
    def distinctByItemId(itemScore: List[(String, Double)]) = {
        if (itemScore.isEmpty) {
            Nil
        } else {
            itemScore.groupBy(_._1).mapValues { itemScorePair =>
                itemScorePair.maxBy(_._2)._2
            }.toList.sortWith {
                case (pair1, pair2) => pair1._2 > pair2._2
            }
        }
    }

    def normalize(itemScore: List[(String, Double)]) = {
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

    def getPopularItem(
        sparkSession: SparkSession, datetime: DateTime,
        userBehaviorTable: String, viewedTopN: Int
    ) = {
        val topViewed =
            sparkSession.sql(
                topViewedSQL(datetime, userBehaviorTable, viewedTopN)
            )
        val topLikedOrCommented =
            sparkSession.sql(
                topLikedOrCommentedSQL(datetime, userBehaviorTable)
            )

        val popularContent =
            sparkSession.sql(
                popularItemSQL(topViewed, topLikedOrCommented)
            ).collect().map {
                case Row(itemId: String, score: Double) if itemId != null => (itemId, score)
            }.toList

        normalize(popularContent)
    }


    def getUnprefixedId(prefixedId: String) = {
        prefixedId.split(UNDERSCORE, 2).toList match {
            case head :: Nil => head
            case head :: other :: Nil => other
            case _ => prefixedId
        }
    }

    def deserializeObject[T](data: Array[Byte], clazz: Class[T]): java.util.List[T] = {
        val kryo = new Kryo()
        kryo.setReferences(false)
        kryo.setRegistrationRequired(true)

        val serializer = new CollectionSerializer()
        serializer.setElementClass(clazz, new JavaSerializer())
        serializer.setElementsCanBeNull(false)

        kryo.register(clazz, new JavaSerializer())
        kryo.register(classOf[java.util.ArrayList[T]], serializer)

        var input: Input = null
        try {
            val bais = new ByteArrayInputStream(data)
            input = new Input(bais)
            kryo.readObject(input, classOf[java.util.ArrayList[T]], serializer)
        } finally {
            IOUtils.closeQuietly(input)
        }
    }

    /**
     * @param sparkSession
     * @param hbaseConf
     * @param config
     *
     * @return
     *
     *    为了避免热点key问题, 所以在原始key之前拼接了一个随机串, xxx_uid | xxx_did;
     *    另外row value使用snappy进行了压缩, 所以需要反解
     */
    def getUserRecommendFromHbase(
        sparkSession: SparkSession, hbaseConf: Configuration, config: Map[String, String]
    ): RDD[(String, List[(String, Double)])] = {
        sparkSession.sparkContext.newAPIHadoopRDD(
            hbaseConf, classOf[TableInputFormat], classOf[ImmutableBytesWritable], classOf[Result]
        ).map(_._2).flatMap { result =>
            try {
                val userId = getUnprefixedId(Bytes.toString(result.getRow()))

                Option(result.getValue("base_info".getBytes(), "items".getBytes())).map { originalBytes =>
                    val uncompressedBytes = Snappy.uncompress(originalBytes)
                    userId -> deserializeObject(uncompressedBytes, classOf[(String, Float)]).toList.map {
                        case (itemId, score) => (itemId, score.toDouble)
                    }
                }
            } catch {
                case ex: Exception =>
                    logger.error(s"Failed to get user recommend from ${result}", ex)
                    val formattedDate = DateUtils.getYMDHMS.format(System.currentTimeMillis)
                    val alertMessage = s"${formattedDate} 五条关注页融合-HBase 读取${config("hbase.table")}推荐数据失败"
                    alertByChannel(alertMessage, config)
                    None
            }
        }
    }


    /**
     *  热门内容 与 每个用户推荐的Item进行融合
     *
     *  + 合并之后按itemid去重
     *  + 然后对于每一个用户的推荐, 删除 ---> 创建时间排序 ---> take(top)
     *
     * @param userItemRecommend
     * @param popularItemBroadcast
     * @param config Map[String, String]
     *
     * @return
     */
    def pushMixedUserRecommendInBatch(
        userItemRecommend: List[(String, List[(String, Double)])],
        popularItemBroadcast: Broadcast[List[(String, Double)]],
        producerBroadCast: Broadcast[KafkaSink[String, String]],
        config: Map[String, String]
    ): Unit = {
        val pushThreshold = config("push.threshold").toInt
        val kdcCacheConfig = config("cache.config")
        val top = config("user.recommend.top").toInt

        val popularItem = popularItemBroadcast.value

        var kdcCache: KdcCache = null
        val current = new DateTime()

        try {
            kdcCache = KdcCache.builder(getClass().getClassLoader().getResourceAsStream(kdcCacheConfig))

            userItemRecommend.foreach {
                case (userId, itemRecommend) =>
                    try {
                        val mixedItem = distinctByItemId(itemRecommend ::: popularItem)

                        // 查询状态以及时间 准备进行排序
                        val itemIds = mixedItem.map(_._1)
                        val itemInfo = getItemStatusAndCreateTime(
                            itemIds, REDIS_KEY_PREFIX, kdcCache
                        )

                        val mixedRecommend = filterOutDeleted(mixedItem, itemInfo).take(top)

                        val recommendSize = mixedRecommend.size
                        if (recommendSize >= pushThreshold) {
                            sendUserRecommendToKafka(
                                userId, mixedRecommend, producerBroadCast, config, current
                            )
                        }
                    } catch {
                        case ex: Exception =>
                            logger.error(s"Failed to push recommend for ${userId}", ex)
                            val formattedDate = DateUtils.getYMDHMS.format(System.currentTimeMillis)
                            val alertMessage = s"${formattedDate} 五条关注页融合, ${userId}数据推送失败"
                            alertByChannel(alertMessage, config)
                    }
            }
        } catch {
            case ex: Exception =>
                logger.error(s"Failed to push recommend", ex)
                val formattedDate = DateUtils.getYMDHMS.format(System.currentTimeMillis)
                val alertMessage = s"${formattedDate} 五条关注页融合, 数据推送失败"
                alertByChannel(alertMessage, config)
        } finally {
            Option(kdcCache).foreach { _.close() }
        }
    }

    def main(args: Array[String]) {
        if (args.length < 1) {
            throw new IllegalArgumentException("configuration path is missing")
        }

        val config = ConfigUtils.loadFromFile(args(0)).toMap

        val userBehaviorTable = config("source.table")
        val viewedTopN = config("viewed.top").toInt
        val hbaseZkQuoram = config("hbase.zkquoram")
        val hbaseTable = config("hbase.table")

        val successMarkPath = config("success.mark.path")
        val batchSize = config("batch.size").toInt

        val sparkSession = SparkSession.builder.enableHiveSupport().getOrCreate()
        val sparkContext = sparkSession.sparkContext

        val current = new DateTime()
        val lastHour = current.minusHours(1)

        val lastHourPath = buildSuccessPath(sparkContext, successMarkPath, lastHour)

        if (!existsPath(sparkContext, lastHourPath)) {
            var producerBroadCast: Broadcast[KafkaSink[String, String]] = null
            var popularItemBroadcast: Broadcast[List[(String, Double)]] = null

            try {
                producerBroadCast =
                    sparkContext.broadcast(
                        KafkaSink.create[String, String](getKafkaParams(config))
                    )

                val popularItem = getPopularItem(sparkSession, current, userBehaviorTable, viewedTopN)
                popularItemBroadcast = sparkContext.broadcast(popularItem)

                val hbaseConf = HBaseUtils.getHBaseConf(hbaseTable, hbaseZkQuoram)
                val userRecommendRDD = getUserRecommendFromHbase(sparkSession, hbaseConf, config)
                val partitionNumber = userRecommendRDD.sparkContext.getConf.getInt(
                    SPARK_OUTPUT_PARTITION, DEFAULT_OUTPUT_PARTITION
                )

                userRecommendRDD.repartition(partitionNumber).foreachPartition { userRecommendIter =>
                    if (userRecommendIter.nonEmpty) {
                        var rowCount = 0
                        val userRecommendBuffer = new ListBuffer[(String, List[(String, Double)])]()

                        while (userRecommendIter.hasNext) {
                            userRecommendBuffer += userRecommendIter.next()
                            rowCount += 1

                            if (rowCount % batchSize == 0) {
                                pushMixedUserRecommendInBatch(
                                    userRecommendBuffer.toList,
                                    popularItemBroadcast,
                                    producerBroadCast,
                                    config
                                )

                                userRecommendBuffer.clear()
                                rowCount = 0
                            }
                        }

                        if (rowCount > 0) {
                            logger.info(s"${rowCount} records remaining, can be sent in one batch")
                            pushMixedUserRecommendInBatch(
                                userRecommendBuffer.toList,
                                popularItemBroadcast,
                                producerBroadCast,
                                config
                            )
                        }
                    }
                }

                touchSuccessMark(sparkContext, successMarkPath, lastHour)
            } finally {
                Option(producerBroadCast).foreach { _.destroy() }
                Option(popularItemBroadcast).foreach { _.destroy() }
            }
        } else {
            logger.info(s"Path ${lastHourPath} already exists !!")
        }
    }

}
