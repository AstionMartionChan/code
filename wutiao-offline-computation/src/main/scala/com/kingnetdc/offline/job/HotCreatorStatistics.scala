package com.kingnetdc.offline.job

import com.kingnetdc.offline.utils.{SQLBuilder, JsonUtils}
import com.kingnetdc.offline.utils.SQLBuilder._
import com.kingnetdc.watermelon.output.KafkaSink
import com.kingnetdc.watermelon.utils.AppConstants._
import com.kingnetdc.watermelon.utils.SparkUtils._
import com.kingnetdc.watermelon.utils.{ConfigUtils, Logging}
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.{Row, SparkSession}
import org.joda.time.DateTime

/**
* Created by zhouml on 22/05/2018.
*/
object HotCreatorStatistics extends BaseStatistics with Logging {

    def rankByMediaTypeMoneyGainSQL(
        datetime: DateTime, tableName: String,
        topN: Int, inRecentDay: Int = 7
    ) = {
        val selectSQL =
            s"""
               select
                   uid, sum(creative_money_gain) as total
               from ${tableName}
               where
                   (${SQLBuilder.getInRecentDay(datetime, inRecentDay)}) and
                    uid is not null
                    and uid <> '0'
                    and creative_money_gain > 0
                group by uid
                order by total desc, uid asc
             """

        logger.info(s"rankByMediaTypeMoneyGainSQL: ${selectSQL}")
        selectSQL
    }

    def hotCreatorByMoneyGainJson(partitionIter: Iterator[Row], id: String) = {
        val jsonData =
            partitionIter.zipWithIndex.map {
                case (Row(uid: String, _: Double), index) =>
                Map(
                    "uid" -> uid,
                    "rank" -> (index + 1)
                )
            }

        val resultMap =
            Map(
                "index" -> "blockchain_rank_rcmd",
                "type" -> "blockchain_rank",
                "id" -> "hot_creator_rank",
                "op" -> 1,
                "last_updated_at" -> s"${new DateTime().toString(YMDHMS)}",
                "data" -> jsonData
            )

        JsonUtils.render(resultMap)
    }

    def main(args: Array[String]) {
        if (args.length < 1) {
            throw new IllegalArgumentException("configuration path is missing")
        }

        val config = ConfigUtils.loadFromFileAsMap(args(0))
        val sourceTable = config("source.table")
        val sourceTablePath = config("source.table.path")

        val kafkaTopic = config("topic")
        val topN = config("creator.recommend.top").toInt
        val successMarkPath = config("success.mark.path")

        val sparkSession = SparkSession.builder.enableHiveSupport().getOrCreate()
        val sparkContext = sparkSession.sparkContext

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
                    sparkSession.sparkContext.broadcast(
                        KafkaSink.create[String, String](getKafkaParams(config))
                    )

                val resultDataFrame = sparkSession.sql(
                    rankByMediaTypeMoneyGainSQL(current, sourceTable, topN)
                )

                saveTopNToKafka(
                    resultDataFrame, hotCreatorByMoneyGainJson,
                    "hot_creator_rank", producerBroadCast, kafkaTopic
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
