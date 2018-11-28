package com.kingnetdc.offline.job

import java.nio.charset.Charset

import com.google.common.hash.{BloomFilter, Funnel, PrimitiveSink}
import com.kingnetdc.blueberry.cache.KdcRedisCluster
import com.kingnetdc.offline.utils.RedisBasedBloomFilter
import com.kingnetdc.watermelon.utils.AppConstants._
import com.kingnetdc.watermelon.utils._
import org.apache.spark.SparkContext
import org.apache.spark.sql.{Row, SparkSession}
import org.joda.time.DateTime

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

object ShowinpageStatistics extends Logging with BaseStatistics {

    val SHOWINPAGE_KEY = "wutiao:showinpage"

    override def touchSuccessMark(sparkContext: SparkContext, successMarkPath: String, time: DateTime) = {
        val timeFormat = "yyyy-MM-dd"
        SparkUtils.touchFile(sparkContext, s"${successMarkPath}/${time.toString(timeFormat)}_${sparkContext.appName}")
    }

    def buildShowInPageRedisKey(env: String, uniqueId: String) = {
        List(SHOWINPAGE_KEY, env, uniqueId).mkString(COLON)
    }

    def getAllShowinpageWithDays(current: DateTime, days: Int) = {
        val endDs = current.toString(YMD)
        val startDs = current.minusDays(days).toString(YMD)
        val sql =
            s"""select
                t1.ouid as ouid,
                t1.did as did,
                concat(t1.itemlist,',',nvl(t2.itemlist, '')) as itemlist
                    from  (select ouid,did,concat_ws(',', collect_set(itemlist)) as itemlist
                    from wutiao.odl_event_qkl
                    where ds > '${startDs}' and ds < '${endDs}'  and event='showinpage'
                    and ouid is not null and ouid !='-1' group by ouid,did) t1
                left outer join
                    (select did,concat_ws(',', collect_set(itemlist)) as itemlist
                    from wutiao.odl_event_qkl
                    where ds > '${startDs}' and ds < '${endDs}'  and event='showinpage'
                    and (ouid is null or ouid ='-1') group by did) t2
                on t1.did=t2.did """
        sql
    }

    def getAllShowinpageWithDaysAndUids(current: DateTime, days: Int, uids: String) = {
        val endDs = current.toString(YMD)
        val startDs = current.minusDays(days).toString(YMD)
        val sql =
            s"""select
                t1.ouid as ouid,
                t1.did as did,
                concat(t1.itemlist,',',nvl(t2.itemlist, '')) as itemlist
                    from  (select ouid,did,concat_ws(',', collect_set(itemlist)) as itemlist
                    from wutiao.odl_event_qkl
                    where ds > '${startDs}' and ds < '${endDs}'  and event='showinpage'
                    and ouid in (${uids}) group by ouid,did) t1
                left outer join
                    (select did,concat_ws(',', collect_set(itemlist)) as itemlist
                    from wutiao.odl_event_qkl
                    where ds > '${startDs}' and ds < '${endDs}'  and event='showinpage'
                    and (ouid is null or ouid ='-1') group by did) t2
                on t1.did=t2.did """
        sql
    }

    def getNotLoginDidShowinpage(current: DateTime, days: Int) = {
        val endDs = current.toString(YMD)
        val startDs = current.minusDays(days).toString(YMD)
        val sql =
            s"""select
                t1.did as did,
                t1.itemlist as itemlist
                from (select ouid,did,concat_ws(',',collect_set(itemlist)) as itemlist
                    from wutiao.odl_event_qkl where ds<'${endDs}' and ds>'${startDs}'
                    and event='showinpage'
                    and (ouid is null or ouid ='-1') group by ouid,did) t1
                left outer join (select distinct ouid,did
                    from wutiao.odl_event_qkl where ds<'${endDs}' and ds>'${startDs}' and eventtype='low'
                    and ouid is not null and ouid != '-1')t2
                on t1.did=t2.did
                where t2.ouid is null"""
        sql
    }

    def parse(row: Row) = {
        try {
            val uid = Option(row.getAs[String]("ouid")).filter(StringUtils.nonEmpty).getOrElse(ABNORMAL_VALUE)
            val items = Option(row.getAs[String]("itemlist")).filter(StringUtils.nonEmpty).getOrElse(ABNORMAL_VALUE)
            if (uid.equals(ABNORMAL_VALUE) || items.equals(ABNORMAL_VALUE)) {
                None
            } else {
                val itemList = items.trim.split(COMMA).map(item => {
                    item.trim.split(ALT)(0)
                }).toList
                Some(uid -> itemList)
            }
        } catch {
            case e: Exception =>
                logger.error("parse row error. row: " + row.toString(), e)
                None
        }

    }

    def parseDid(row: Row) = {
        try {
            val did = Option(row.getAs[String]("did")).filter(StringUtils.nonEmpty).getOrElse(ABNORMAL_VALUE)
            val items = Option(row.getAs[String]("itemlist")).filter(StringUtils.nonEmpty).getOrElse(ABNORMAL_VALUE)
            if (did.equals(ABNORMAL_VALUE) || items.equals(ABNORMAL_VALUE)) {
                None
            } else {
                val itemList = items.trim.split(COMMA).map(item => {
                    item.trim.split(COLON)(0)
                }).toList
                Some(did -> itemList)
            }
        } catch {
            case e: Exception =>
                logger.error("parse row error. row: " + row.toString(), e)
                None
        }
    }

    def saveHistoryShowinpage(showinpageItems: List[(String, List[String])], config: Map[String, String]) = {
        val redisEnv = config("redis.env")
        val redisConnect: String = config("redis.connect")
        val redisPassword: String = Option(config.getOrDefault("redis.password", ABNORMAL_VALUE))
            .filter(StringUtils.nonEmpty).getOrElse(ABNORMAL_VALUE)
        var kdcRedisCluster: KdcRedisCluster = null
        val fpp: Double = config.get("bloomfilter.fpp").map(_.toDouble).getOrElse(0.001)

        try {
            kdcRedisCluster = if (redisPassword.equals(ABNORMAL_VALUE)) {
                new KdcRedisCluster(redisConnect)
            } else {
                new KdcRedisCluster(redisConnect, redisPassword)
            }

            showinpageItems.foreach{
                case (id, itemList) =>
                    try {
                        val distinctItemList = itemList.distinct
                        val uidHistoryRedisKey = buildShowInPageRedisKey(redisEnv, id)
                        val currentBloomFilter =
                            buildBloomFilter(distinctItemList, stringFunnel, distinctItemList.size, fpp)
                        RedisBasedBloomFilter.setBloomFilter(uidHistoryRedisKey,
                            currentBloomFilter, kdcRedisCluster, config)
                    } catch {
                        case e: Exception =>
                            logger.error("Failed to save show in page results for id: " + id, e)
                            val formattedDate = DateUtils.getYMD.format(System.currentTimeMillis)
                            val alertMessage = s"${formattedDate} 真曝光离线 ${id}写入redis失败"
                            alertByChannel(alertMessage, config)
                    }
            }
        } catch {
            case ex: Exception =>
                logger.error("Failed to save show in page results", ex)
                val formattedDate = DateUtils.getYMD.format(System.currentTimeMillis)
                val alertMessage = s"${formattedDate} 真曝光离线 批次写入redis失败"
                alertByChannel(alertMessage, config)
        } finally {
            Option(kdcRedisCluster).foreach { _.close() }
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

    def main(args: Array[String]) = {
        if (args.length < 1) {
            throw new IllegalArgumentException("configuration path is missing")
        }

        val config = ConfigUtils.loadFromFile(args(0)).toMap
        val days = config.get("history.days").map(_.toInt).getOrElse(14)
        val batchSize = config.get("batch.size").map(_.toInt).getOrElse(100)
        val uids = Option(config.getOrDefault("restart.uids", ABNORMAL_VALUE)).filter(StringUtils.nonEmpty)
            .getOrElse(ABNORMAL_VALUE)
        val successMarkPath = config("success.mark.path")

        val sparkSession = SparkSession.builder
            .enableHiveSupport()
            .config("hive.exec.dynamic.partition.mode", "nonstrict")
            .getOrCreate()
        val sparkContext = sparkSession.sparkContext

        val current = new DateTime()
        val lastDay = current.minusDays(1)

        // 说明程序是正常情况
        if (uids.equals(ABNORMAL_VALUE)) {
            // 登录用户
            sparkSession.sql(
                getAllShowinpageWithDays(current, days)
            ).rdd.flatMap(parse).reduceByKey(_ ::: _).foreachPartition(iter => {
                if (iter.nonEmpty) {
                    var rowCount = 0
                    val uidDidAndItemBuffer = new ListBuffer[(String, List[String])]()
                    while (iter.hasNext) {
                        uidDidAndItemBuffer += iter.next()
                        rowCount += 1
                        if (rowCount % batchSize == 0) {
                            saveHistoryShowinpage(uidDidAndItemBuffer.toList, config)
                            uidDidAndItemBuffer.clear()
                            rowCount = 0
                        }
                    }

                    if (rowCount > 0) {
                        saveHistoryShowinpage(uidDidAndItemBuffer.toList, config)
                    }

                }
            })

            // 均未登录的设备
            sparkSession.sql(
                getNotLoginDidShowinpage(current, days)
            ).rdd.flatMap(parseDid).reduceByKey(_ ::: _).foreachPartition(iter => {
                if (iter.nonEmpty) {
                    var rowCount = 0
                    val didAndItemBuffer = new ListBuffer[(String, List[String])]()
                    while (iter.hasNext) {
                        didAndItemBuffer += iter.next()
                        rowCount += 1
                        if (rowCount % batchSize == 0) {
                            saveHistoryShowinpage(didAndItemBuffer.toList, config)
                            didAndItemBuffer.clear()
                            rowCount = 0
                        }
                    }

                    if (rowCount > 0) {
                        saveHistoryShowinpage(didAndItemBuffer.toList, config)
                    }
                }
            })

            // 创建成功标记
            touchSuccessMark(sparkContext, successMarkPath, lastDay)

        } else { // 需要对某些uid重跑时
            logger.info(s"rerunning insert redis failed uid: ${uids}")

            val stringUids = uids.split(COMMA).map(id => s"'${id}'").toList.mkString(COMMA)
            // 只需要重跑登录用户
            sparkSession.sql(
                getAllShowinpageWithDaysAndUids(current, days, stringUids)
            ).rdd.flatMap(parse).reduceByKey(_ ::: _).foreachPartition(iter => {
                if (iter.nonEmpty) {
                    var rowCount = 0
                    val uidDidAndItemBuffer = new ListBuffer[(String, List[String])]()
                    while (iter.hasNext) {
                        uidDidAndItemBuffer += iter.next()
                        rowCount += 1
                        if (rowCount % batchSize == 0) {
                            saveHistoryShowinpage(uidDidAndItemBuffer.toList, config)
                            uidDidAndItemBuffer.clear()
                            rowCount = 0
                        }
                    }

                    if (rowCount > 0) {
                        saveHistoryShowinpage(uidDidAndItemBuffer.toList, config)
                    }

                }
            })
        }




    }

}
