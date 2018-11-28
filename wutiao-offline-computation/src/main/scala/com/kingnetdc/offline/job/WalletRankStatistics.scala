package com.kingnetdc.offline.job

import com.kingnetdc.offline.utils.JsonUtils
import com.kingnetdc.watermelon.output.KafkaSink
import com.kingnetdc.watermelon.utils.{ConfigUtils, Logging}
import com.kingnetdc.watermelon.utils.SparkUtils._
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import com.kingnetdc.offline.utils.SQLBuilder._
import com.kingnetdc.watermelon.utils.AppConstants._
import org.apache.spark.storage.StorageLevel
import org.joda.time.DateTime

/**
  * Created by zhouml on 26/05/2018.
  */
object WalletRankStatistics extends BaseStatistics with Logging {

    private val totalRankIdentifier = "wallet_total_rank"

    private def getKafkaParams(bootstrapServers: String): Map[String, Object] = {
        Map(
            "bootstrap.servers" -> bootstrapServers,
            "key.serializer" -> classOf[StringSerializer],
            "value.serializer" -> classOf[StringSerializer],
            "enable.auto.commit" -> (false: java.lang.Boolean)
        )
    }

    /*
        五条攻略 2161212
        五条动态 2161214
        五条建议 2174566
        五条号 2127118
        五条高校号 2158697
        五条CEO 2135034
     */
    /**
     * 计算截止到计算点的累计收益排名的SQL(剔除掉创作者收益以及指定帐号)
     *
     * @param config
     * @param dateCondition 时间查询条件
     *
     * @return
     */
    def getSummarySQL(config: Map[String, String], dateCondition: String) = {
        val tableName = config("source.table")

        val uidToExcludeClause =
            config.get("excluded.uids").map { excludedUidStr =>
                val uidNotInClause =
                    excludedUidStr.split(COMMA).map { uid =>
                        s"'${uid}'"
                    }.mkString(COMMA)

                s"and uid not in (${uidNotInClause})"
            }.getOrElse("")

        val selectSQL =
            s"""
                select
                    uid, sum(money_gain) as total_money_gain
                from ${tableName}
                where
                    (${dateCondition}) and
                    uid is not null and
                    uid <> '0' ${uidToExcludeClause}
                group by uid
                having total_money_gain > 0
                order by total_money_gain desc, uid asc
            """

        logger.info("GetWalletSummarySQL: " + selectSQL)
        selectSQL
    }

    /**
      * 将截止计算点的今日收益汇总SQL和历史topN进行融合生成计算点的累计收益排行
      *
      * @param tempTable 收益中间表
      * @param walletDailyTotal  每日汇总表
      */
    def mergeWithTemporaryTableSQL(tempTable: DataFrame, walletDailyTotal: DataFrame) = {
        tempTable.createOrReplaceTempView("tbl_temp")
        walletDailyTotal.createOrReplaceTempView("tbl_daily_total")

        val selectSQL =
            s"""
               select
                    uid, total_money_gain
               from (
                   select
                       nvl(l.uid, r.uid) as uid,
                       (nvl(l.total_money_gain, 0) + nvl(r.total_money_gain,0)) as total_money_gain
                   from
                        tbl_daily_total l
                        full outer join
                        tbl_temp r
                        on l.uid = r.uid
               ) temp where total_money_gain > 0
               order by total_money_gain desc, uid asc
            """

        logger.info(s"GetTemporaryTableSQL: ${selectSQL}")
        selectSQL
    }

    def rankJson(partitionIter: Iterator[Row], id: String): String = {
        val jsonData =
            partitionIter.zipWithIndex.map {
                case (Row(uid: String, total: Double), index) =>
                    Map(
                        "uid" -> uid,
                        "rank" -> (index + 1),
                        "value" -> total
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


    def getOrCreateTemporaryTable(
        sparkSession: SparkSession, datetime: DateTime, config: Map[String, String]
    ): DataFrame = {
        import sparkSession.implicits._

        val temporaryTableName = config("temporary.table")
        val temporaryTablePath = config("temporary.table.path")
        val temporaryTableSize = config("temporary.table.size").toInt

        val temporaryTableExists =
            existsPath(sparkSession.sparkContext, s"${temporaryTablePath}/ds=${datetime.toString(YMD)}")

        if (!temporaryTableExists) {
            val walletTotal = sparkSession.sql(
                getSummarySQL(config, getHistoryTotalUntilYesterday(datetime))
            ).limit(temporaryTableSize)

            logger.info(s"Save records to ${temporaryTableName}")
            saveResultDataFrameToHive(walletTotal, temporaryTableName)
            walletTotal
        } else {
            val selectSQL =
                s"""
                    select uid, total_money_gain
                    from ${temporaryTableName}
                    where `${dayPartitionName}` = '${datetime.toString(YMD)}'
                """
            sparkSession.sql(selectSQL)
        }
    }

    def getWalletTotal(
        sparkSession: SparkSession, datetime: DateTime,
        config: Map[String, String], walletDailyTotal: DataFrame
    ) = {
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
            val walletTotal = sparkSession.sql(
                mergeWithTemporaryTableSQL(tempTable, walletDailyTotal)
            )
            walletTotal
        }
    }

    def main(args: Array[String]) {
        if (args.length < 1) {
            throw new IllegalArgumentException("configuration path is missing")
        }

        val config = ConfigUtils.loadFromFileAsMap(args(0))
        val bootstrapServers = config("bootstrap.servers")
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
                producerBroadCast =
                    sparkContext.broadcast(
                        KafkaSink.create[String, String](getKafkaParams(bootstrapServers))
                    )

                // 计算今日累计
                val walletDailyTotal =
                    sparkSession.sql(
                        getSummarySQL(config, getInRecentNaturalDay(current, 1))
                    ).persist(StorageLevel.MEMORY_AND_DISK)

                val dailyIdentifier = s"wallet_daily_rank.${lastHour.toString(YMD)}"
                saveTopNToKafka(
                    walletDailyTotal.limit(topN), rankJson, dailyIdentifier,
                    producerBroadCast, topic
                )

                // 计算历史累计
                val walletTotal = getWalletTotal(sparkSession, current, config, walletDailyTotal)
                saveTopNToKafka(
                    walletTotal.limit(topN), rankJson, totalRankIdentifier,
                    producerBroadCast, topic
                )

                touchSuccessMark(sparkContext, successMarkPath, lastHour)
            } finally {
                Option(producerBroadCast).foreach { _.destroy() }
            }
        } else {
            logger.warn("Not the right time !!!")
        }
    }

}
