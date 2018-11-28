package com.kingnetdc.offline.job

import com.kingnetdc.offline.utils.JsonUtils
import com.kingnetdc.offline.utils.SQLBuilder._
import com.kingnetdc.watermelon.output.KafkaSink
import com.kingnetdc.watermelon.utils.{ConfigUtils, Logging}
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql._
import com.kingnetdc.watermelon.utils.AppConstants._
import com.kingnetdc.watermelon.utils.SparkUtils._
import org.joda.time.DateTime

/**
* Created by zhouml on 28/05/2018.
*/
object DiscovererRankStatistics extends BaseStatistics with Logging {

    private val totalRankIdentifier = "discoverer_total_rank"

    /**
     *
     * 7日收益 = 6日收益 + 今日动态(按uid分组, 各个小时累加)
     * 7日审核力 = 6日审核力 + 今日动态(按uid分组, 获取值最大的)
     * 7日投票 = 6日投票 + 今日动态(按uid分组, 获取值最大的)
     *
     * @param datetime
     * @param tableName
     *
     * @return
     */
    def sevenDayRankSQL(datetime: DateTime, tableName: String) = {
        val lastHour = datetime.minusHours(1)

        // 如果计算点的上一个小时跨天, 应该特殊考虑
        if (!sameDay(lastHour, datetime)) {
            s"""
               select
                   uid,
                   sum(nvl(discover_money_gain, 0)) as discover_money_gain,
                   sum(nvl(verifypoint, 0)) as verifypoint,
                   sum(nvl(vote_cnt, 0)) as vote_cnt
                from
                    ${tableName}
                where
                    uid is not null and uid <> '0' and
                    `${dayPartitionName}` >= '${datetime.minusDays(7).toString(dayFormat)}'
                    and `${dayPartitionName}` <= '${datetime.minusDays(1).toString(dayFormat)}'
                    and `${hourPartitionName}` = '${dayTotalMark}'
                group by uid
                having discover_money_gain > 0
                order by discover_money_gain desc, uid asc
            """
        } else {
            s"""
               select
                    uid, discover_money_gain, verifypoint, vote_cnt
               from (
                   select
                        nvl(l.uid, r.uid) as uid,
                        (nvl(l.discover_money_gain, 0) + nvl(r.discover_money_gain, 0)) as discover_money_gain,
                        (nvl(l.verifypoint, 0) + nvl(r.verifypoint,0)) as verifypoint,
                        (nvl(l.vote_cnt, 0) + nvl(r.vote_cnt,0)) as vote_cnt
                   from
                      (
                          select
                             uid,
                             sum(nvl(discover_money_gain, 0)) as discover_money_gain,
                             sum(nvl(verifypoint, 0)) as verifypoint,
                             sum(nvl(vote_cnt, 0)) as vote_cnt
                          from
                              ${tableName}
                          where
                               uid is not null and uid <> '0' and
                              `${dayPartitionName}` >= '${datetime.minusDays(6).toString(dayFormat)}'
                              and `${dayPartitionName}` <= '${datetime.minusDays(1).toString(dayFormat)}'
                              and `${hourPartitionName}` = '${dayTotalMark}'
                          group by uid
                      ) l full outer join
                      (
                         select
                            uid,
                            sum(nvl(discover_money_gain, 0)) as discover_money_gain,
                            max(nvl(verifypoint, 0)) as verifypoint,
                            max(nvl(vote_cnt, 0)) as vote_cnt
                         from
                             ${tableName}
                         where
                              uid is not null and uid <> '0' and
                             `${hourPartitionName}` >= '${datetime.withTimeAtStartOfDay().toString(hourFormat)}'
                               and `${hourPartitionName}` < '${datetime.toString(hourFormat)}'
                         group by uid
                      ) r on l.uid = r.uid
                ) temp
                where discover_money_gain > 0
                order by discover_money_gain desc, uid asc
             """
        }
    }

    def verifyPointDateCondition(datetime: DateTime) = {
        // 近30天, 如果是计算昨日的最后一次累计的话, 则应该直接计算30天的;
        val lastHour = datetime.minusHours(1)
        if (!sameDay(lastHour, datetime)) {
            s"""
               `${dayPartitionName}` >= '${datetime.minusDays(30).toString(dayFormat)}' and
               `${dayPartitionName}` <= '${datetime.minusDays(1).toString(dayFormat)}' and
               `${hourPartitionName}` = '${dayTotalMark}'
             """
        } else {
            s"""
               `${dayPartitionName}` >= '${datetime.minusDays(30 - 1).toString(dayFormat)}' and
               `${dayPartitionName}` <= '${datetime.minusDays(1).toString(dayFormat)}' and
               `${hourPartitionName}` = '${dayTotalMark}'
             """
        }
    }

    /**
     * 历史累计(累计至昨日的发现者收益, 累计至昨日的投票, 最近29天的审核力) + 今日的
     *
     * @return
     */
    def getSummarySQL(datetime: DateTime, tableName: String) = {
        val summarySQL =
            s"""
               select
                  l.uid,
                  l.discover_money_gain,
                  nvl(r.verifypoint, 0) as verifypoint,
                  l.vote_cnt
               from
                  (
                      select
                           uid,
                           sum(nvl(discover_money_gain, 0)) as discover_money_gain,
                           sum(nvl(vote_cnt, 0)) as vote_cnt
                      from
                           ${tableName}
                      where
                           ${getHistoryTotalUntilYesterday(datetime)} and
                           uid is not null and
                           uid <> '0'
                      group by uid
                      having discover_money_gain > 0
                  ) l
                  left outer join
                  (
                       select
                           uid, sum(nvl(verifypoint, 0)) as verifypoint
                       from
                           ${tableName}
                       where
                           uid is not null and
                           uid <> '0' and
                           (${verifyPointDateCondition(datetime)})
                       group by uid
                  ) r
               on l.uid = r.uid
               order by l.discover_money_gain desc, l.uid asc
            """

        logger.info(s"GetSummarySQL: ${summarySQL}")
        summarySQL
    }

    def rankJson(rowIter: Iterator[Row], id: String) = {
        val jsonData =
            rowIter.zipWithIndex.map {
                case (Row(uid: String, total: Double, verifyPoint: Double, vote: Long), index) =>
                    Map(
                        "uid" -> uid,
                        "rank" -> (index + 1),
                        "value" -> total,
                        "permitValue" -> verifyPoint,
                        "voteValue" -> vote
                    )
            }

        val resultMap =
            Map(
                "index" -> "blockchain_rank_rcmd",
                "type" -> "blockchain_rank",
                "id" -> id,
                "op" -> 1,
                "last_updated_at" -> s"${new DateTime().toString(YMDHMS)}",
                "data" -> jsonData
            )

        JsonUtils.render(resultMap)
    }

    private def getKafkaParams(bootstrapServers: String): Map[String, Object] = {
        Map(
            "bootstrap.servers" -> bootstrapServers,
            "key.serializer" -> classOf[StringSerializer],
            "value.serializer" -> classOf[StringSerializer],
            "enable.auto.commit" -> (false: java.lang.Boolean)
        )
    }

    def mergeWithDailyTableSQL(tempTable: DataFrame, tableName: String, datetime: DateTime) = {
        tempTable.createOrReplaceTempView("tbl_temp")

        val mergeWithDailyTableSQL =
            s"""
              select
                uid, discover_money_gain, verifypoint, vote_cnt
              from (
                  select
                    nvl(l.uid, r.uid) as uid,
                    (nvl(l.discover_money_gain, 0) + nvl(r.discover_money_gain, 0)) as discover_money_gain,
                    (nvl(l.verifypoint, 0) + nvl(r.verifypoint,0)) as verifypoint,
                    (nvl(l.vote_cnt, 0) + nvl(r.vote_cnt,0)) as vote_cnt
                  from
                     (
                            select
                               uid,
                               sum(nvl(discover_money_gain, 0)) as discover_money_gain,
                               max(nvl(verifypoint, 0)) as verifypoint,
                               max(nvl(vote_cnt, 0)) as vote_cnt
                            from
                                ${tableName}
                            where
                                 uid is not null and uid <> '0' and
                                `${hourPartitionName}` >= '${datetime.withTimeAtStartOfDay().toString(hourFormat)}'
                                and `${hourPartitionName}` < '${datetime.toString(hourFormat)}'
                            group by uid
                     ) l
                  full outer join
                     tbl_temp r
                   on l.uid = r.uid
              ) temp
              where discover_money_gain > 0
              order by discover_money_gain desc, uid asc
            """

        logger.info(s"mergeWithDailyTableSQL: ${mergeWithDailyTableSQL}")
        mergeWithDailyTableSQL
    }


    def getOrCreateTemporaryTable(
        sparkSession: SparkSession, datetime: DateTime, config: Map[String, String]
    ) = {
        import sparkSession.implicits._

        val sourceTable = config("source.table")
        val temporaryTableName = config("temporary.table")
        val temporaryTablePath = config("temporary.table.path")
        val temporaryTableSize = config("temporary.table.size").toInt

        val temporaryTableExists =
            existsPath(sparkSession.sparkContext, s"${temporaryTablePath}/ds=${datetime.toString(YMD)}")

        if (!temporaryTableExists) {
            val discovererTotal = sparkSession.sql(
                getSummarySQL(datetime, sourceTable)
            ).limit(temporaryTableSize)

            logger.info(s"Save records to ${temporaryTableName}")
            saveResultDataFrameToHive(discovererTotal, temporaryTableName)
            discovererTotal
        } else {
            val selectSQL =
                s"""
                    select uid, discover_money_gain, verifypoint, vote_cnt
                    from ${temporaryTableName}
                    where `${dayPartitionName}` = '${datetime.toString(YMD)}'
                """
            sparkSession.sql(selectSQL)
        }
    }

    def getDiscovererTotal(
        sparkSession: SparkSession, datetime: DateTime, config: Map[String, String]
    ) = {
        val sourceTable = config("source.table")
        val sourceTablePath = config("source.table.path")
        val lastHour = datetime.minusHours(1)
        val totalUntilYesterdayPath =
            s"${sourceTablePath}/ds=${datetime.minusDays(1).toString(YMD)}/hour=${historyTotalMark}"

        if (!existsPath(sparkSession.sparkContext, totalUntilYesterdayPath)) {
            // throw new Exception(s"${totalUntilYesterdayPath} should be there")
            logger.warn(s"${totalUntilYesterdayPath} should be there")
        }

        val tempTable = getOrCreateTemporaryTable(sparkSession, datetime, config)
        if (!sameDay(lastHour, datetime)) {
            tempTable
        } else {
            val discovererTotal =
                sparkSession.sql(
                    mergeWithDailyTableSQL(tempTable, sourceTable, datetime)
                )
            discovererTotal
        }
    }

    /**
     * @param configPath
     *
     * @return
     */
    def parseConfig(configPath: String): Map[String, String] = {
        val config = ConfigUtils.loadFromFileAsMap(configPath)
        val bootstrapServers = config("bootstrap.servers")
        val sourceTable = config("source.table")
        val sourceTablePath = config("source.table.path")
        val temporaryTableName = config("temporary.table")
        val temporaryTablePath = config("temporary.table.path")
        val temporaryTableSize = config("temporary.table.size")
        val topic = config("topic")
        val topN = config("top")
        val successMarkPath = config("success.mark.path")

        Map(
            "bootstrap.servers" -> bootstrapServers,
            "source.table" -> sourceTable,
            "source.table.path" -> sourceTablePath,
            "temporary.table" -> temporaryTableName,
            "temporary.table.path" -> temporaryTablePath,
            "temporary.table.size" -> temporaryTableSize,
            "topic" -> topic,
            "top" -> topN,
            "success.mark.path" -> successMarkPath
        )
    }

    def main(args: Array[String]) {
        if (args.length < 1) {
            throw new IllegalArgumentException("configuration path is missing")
        }

        val config = parseConfig(args(0))

        val bootstrapServers = config("bootstrap.servers")
        val sourceTable = config("source.table")
        val sourceTablePath = config("source.table.path")
        val topic = config("topic")
        val topN = config("top").toInt
        val successMarkPath = config("success.mark.path")

        val sparkSession = SparkSession.builder
                .enableHiveSupport()
                .config("hive.exec.dynamic.partition.mode", "nonstrict")
                .getOrCreate()
        val sparkContext = sparkSession.sparkContext
        import sparkSession.implicits._

        val current = new DateTime()
        val lastHour = current.minusHours(1)

        val lastHourPath = buildSuccessPath(sparkContext, successMarkPath, lastHour)

        val sourceTableLastHourPath =
            s"${sourceTablePath}/ds=${lastHour.toString(YMD)}/hour=${lastHour.toString(hourFormat)}"

        if (
            !existsPath(sparkContext, lastHourPath) &&
            existsPath(sparkContext, sourceTableLastHourPath)
        ) {
            var producerBroadCast: Broadcast[KafkaSink[String, String]] = null

            try {
                producerBroadCast = sparkSession.sparkContext.broadcast(
                    KafkaSink.create[String, String](getKafkaParams(bootstrapServers))
                )

                val discovererSevenDayRank = sparkSession.sql(sevenDayRankSQL(current, sourceTable))
                val dailyIdentifier = s"discoverer_daily_rank.${lastHour.toString(YMD)}"

                saveTopNToKafka(
                    discovererSevenDayRank.limit(topN), rankJson,
                    dailyIdentifier, producerBroadCast, topic
                )

                val discovererTotalRank = getDiscovererTotal(sparkSession, current, config)
                saveTopNToKafka(
                    discovererTotalRank.limit(topN), rankJson, totalRankIdentifier,
                    producerBroadCast, topic
                )

                touchSuccessMark(sparkSession.sparkContext, successMarkPath, lastHour)
            } finally {
                Option(producerBroadCast).foreach { _.destroy() }
            }
        } else {
            logger.warn("Not the right time !!!")
        }
    }

}