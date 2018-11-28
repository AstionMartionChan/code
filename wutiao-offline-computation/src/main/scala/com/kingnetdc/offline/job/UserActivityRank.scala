package com.kingnetdc.offline.job

import com.kingnetdc.offline.utils.JsonUtils
import com.kingnetdc.watermelon.output.KafkaSink
import com.kingnetdc.watermelon.utils.SparkUtils._
import com.kingnetdc.watermelon.utils.ConfigUtils
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.{Row, DataFrame, SparkSession}
import org.apache.spark.storage.StorageLevel
import com.kingnetdc.watermelon.utils.AppConstants._
import org.joda.time.DateTime
import scala.collection.JavaConversions._

/**
 * Created by zhouml on 02/07/2018.
 */
@Deprecated
object UserActivityRank extends BaseStatistics {

    def activeUserWBRank(allUserWB: DataFrame) = {
        val sparkSession = allUserWB.sparkSession
        allUserWB.createOrReplaceTempView("tbl_all_user")

        val activeUserWBSQL =
            """
              select
                uid, sum(value) as total
              from tbl_all_user
              where actiontype in (1, 11, 12, 13, 14)
              and uid <> '2127118'
              group by uid
              order by total desc, uid asc
            """

        logger.info(s"activeUserWBSQL: ${activeUserWBSQL}")
        sparkSession.sql(activeUserWBSQL)
    }

    def discovererVoteWBRank(allUserWB: DataFrame) = {
        val sparkSession = allUserWB.sparkSession
        allUserWB.createOrReplaceTempView("tbl_discoverer")

        val discovererVoteWBSQL =
            """
              select
                uid, sum(value) as total
              from tbl_discoverer
              where actiontype = 15
              and uid <> '2127118'
              group by uid
              order by total desc, uid asc
            """

        logger.info(s"discovererVoteWBSQL: ${discovererVoteWBSQL}")
        sparkSession.sql(discovererVoteWBSQL)
    }

    def mediaContentGenerationWBRank(allUserWB: DataFrame, current: DateTime) = {
        val sparkSession = allUserWB.sparkSession
        allUserWB.createOrReplaceTempView("tbl_media")

        val mediaWBSQL =
            s"""
              select
                uid, sum(value) as total
              from (
                  select
                    uid, value
                  from tbl_media
                  where actiontype = 10
              ) l
              left outer join (
                select
                    cast(uid as string) ruid
                from
                    wutiao.idl_wutiao_mediauser_sync
                where
                     ds = '${current.toString(YMD)}' and
                     mediauser_type = 3
              ) r
              on l.uid = r.ruid
              where l.uid <> '1000042' and
              l.uid <> '2127118' and
              r.ruid is null
              group by uid
              order by total desc, uid asc
            """

        logger.info(s"mediaWBSQL: ${mediaWBSQL}")
        sparkSession.sql(mediaWBSQL)
    }

    def rankJson(rows: Iterator[Row], id: String): String = {
        val jsonData =
            rows.zipWithIndex.map {
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



    def getAllUserWBSQL(table: String) = {
        s"""select
                uid, actiontype, value
            from ${table}
            where
                uid is not null
            and
               actiontype in (1, 10, 11, 12, 13, 14, 15)
        """
    }

    def main(args: Array[String]) {
        if (args.length < 1) {
            throw new IllegalArgumentException("configuration path is missing")
        }

        val config = ConfigUtils.loadFromFile(args(0)).toMap

        val sourceTable = config("source.table")
        val topic = config("topic")
        val successMarkPath = config("success.mark.path")

        val sparkSession = SparkSession.builder
            .enableHiveSupport()
            .config("hive.exec.dynamic.partition.mode", "nonstrict")
            .getOrCreate()
        val sparkContext = sparkSession.sparkContext

        val current = new DateTime()
        val lastHour = current.minusHours(1)

        val lastHourPath = buildSuccessPath(sparkContext, successMarkPath, lastHour)

        val allUserWB = sparkSession.sql(
            getAllUserWBSQL(sourceTable)
        ).persist(StorageLevel.MEMORY_AND_DISK_SER)

        if (!existsPath(sparkContext, lastHourPath)) {
            var producerBroadCast: Broadcast[KafkaSink[String, String]] = null

            try {
                producerBroadCast =
                    sparkContext.broadcast(
                        KafkaSink.create[String, String](getKafkaParams(config))
                    )

                saveTopNToKafka(
                    activeUserWBRank(allUserWB), rankJson,
                    "user_activation_rank", producerBroadCast, topic
                )

                saveTopNToKafka(
                    discovererVoteWBRank(allUserWB), rankJson,
                    "discovery_activation_rank", producerBroadCast, topic
                )

                saveTopNToKafka(
                    mediaContentGenerationWBRank(allUserWB, current), rankJson,
                    "media_activation_rank", producerBroadCast, topic
                )

                touchSuccessMark(sparkContext, successMarkPath, lastHour)
            } finally {
                Option(producerBroadCast).foreach { _.destroy() }
            }
        } else {
            logger.warn(s"Path ${lastHourPath} already exists !!")
        }
    }

}
