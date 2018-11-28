package com.kingnetdc.offline.job

import com.kingnetdc.offline.utils.JsonUtils
import com.kingnetdc.watermelon.output.KafkaSink
import com.kingnetdc.watermelon.utils.SparkUtils._
import com.kingnetdc.watermelon.utils.{ConfigUtils, Logging}
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.{Row, SparkSession}
import org.joda.time.DateTime
import com.kingnetdc.watermelon.utils.AppConstants._
import com.kingnetdc.offline.utils.SQLBuilder._

/**
* Created by zhouml on 01/06/2018.
*/
object FamilyContributionRankStatistics extends BaseStatistics with Logging {


    /**
     *  + 若dateTime的上一个小时是前一天
     *
     *  eg 2018-06-25 00: 20: 00
     *
     *  则取前一天day分区 ds = 2018-06-24 & hour =2018062423 ---> 对应最近30天的
     *
     * + 若dateTime的上一个小时是同一天
     *
     * eg 2018-06-25 02: 20: 00
     *
     * (ds = 2018-06-24 & hour = 29day) or ([hour = 2018062500, hour = 2018062502) )
     *
     * @param dateTime
     */
    def getDateCondition(dateTime: DateTime) = {
        val lastHour = dateTime.minusHours(1)

        if (!sameDay(lastHour, dateTime)) {
            s"""
               `${dayPartitionName}` = '${lastHour.toString(YMD)}' and
               `${hourPartitionName}` =  '${lastHour.toString(hourFormat)}'
             """
        } else {
            s"""
                (`${dayPartitionName}` = '${dateTime.minusDays(1).toString(YMD)}' and `${hourPartitionName}` = '29day') or
                (
                    `${hourPartitionName}` >= '${dateTime.withTimeAtStartOfDay().toString(hourFormat)}' and
                    `${hourPartitionName}` < '${dateTime.toString(hourFormat)}'
                )
             """
        }
    }

    def getFamilyContributionSQL(dateTime: DateTime, tableName: String, topN: Int) = {
        val selectSQL =
            s"""
                select
                     familyid,
                     count(distinct ouid) as family_member_count,
                     sum(contributionpoint) as total_contributionpoint
                from
                    ${tableName}
                where
                    `${hourPartitionName}` = '${dateTime.minusHours(1).toString(hourFormat)}' and
                    ouid is not null and
                    ouid <> '0' and
                    familyid is not null
                 group by familyid
                 having total_contributionpoint > 0
                 order by total_contributionpoint desc, familyid asc
                 limit ${topN}
            """


        logger.info(s"GetFamilyContributionSQL: ${selectSQL}")
        selectSQL
    }

    def rankJson(partitionIter: Iterator[Row], id: String): String = {
        val jsonData =
            partitionIter.zipWithIndex.map {
                case (Row(familyid: String, memberCount: Long, contributionpoint: Double), index) =>
                    Map(
                        "homeId" -> familyid,
                        "rank" -> (index + 1),
                        "value" -> contributionpoint,
                        "member_count" -> memberCount
                    )
            }

        val resultMap =
            Map(
                "index" -> "blockchain_rank_rcmd",
                "type" -> "blockchain_rank",
                "id" -> "home_contribute_rank",
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

    def main(args: Array[String]) {
        if (args.length < 1) {
            throw new IllegalArgumentException("configuration path is missing")
        }

        val config = ConfigUtils.loadFromFileAsMap(args(0))
        val bootstrapServers = config("bootstrap.servers")
        val sourceTable = config("source.table")
        val sourceTablePath = config("source.table.path")
        val topN = config("top").toInt
        val topic = config("topic")
        val successMarkPath = config("success.mark.path")

        val sparkSession =
            SparkSession.builder
                .enableHiveSupport()
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

                val familyContributionTotal =
                    sparkSession.sql(
                        getFamilyContributionSQL(current, sourceTable, topN)
                    )

                saveTopNToKafka(
                    familyContributionTotal, rankJson, "home_contribute_rank",
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
