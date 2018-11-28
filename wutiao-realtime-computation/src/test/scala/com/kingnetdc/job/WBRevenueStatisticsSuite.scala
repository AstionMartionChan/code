package com.kingnetdc.job

import com.kingnetdc.UnitSpecs
import com.kingnetdc.model.WBRevenueLog
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.functions._

/**
  * Created by zhouml on 18/05/2018.
  */
// scalastyle:off
class WBRevenueStatisticsSuite extends UnitSpecs {

  val fixture = {
    new {
      // 1, 2 同为类型2并且同时段
      val wbRevenueLog1 =
        """{"all_comment":1000,"all_likes":1420,"all_read":200,"all_share":12382,"all_view":20000,"coin":"121.921420801991","contributions":{211:"521",212:"534"},"item_id":"111001","item_type":2,"media_id":"1002","plat_avg_comment_rate": 0.23,"plat_avg_like_rate": 0.12,"plat_avg_read_rate": 0.22,"plat_avg_share_rate": 0.12,"time":1526354640,"verifys":{1111:"122",1112:"112",1113:"105",1114:"109"}}"""
      val wbRevenueLog2 =
        """{"all_comment":1200,"all_likes":100,"all_read":200,"all_share":986,"all_view":2343,"coin":"163.07857919800895","contributions":{211:"631",212:"734"},"item_id":"111002","item_type":2,"media_id":"1003","plat_avg_comment_rate": 0.19,"plat_avg_like_rate": 0.22,"plat_avg_read_rate": 0.32,"plat_avg_share_rate": 0.42,"time":1526354640,"verifys":{1111:"132",1112:"142",1113:"105",1114:"109"}}"""

      // 3, 4 同为类型1并且同时段
      val wbRevenueLog3 =
        """{"all_comment":800,"all_likes":1420,"all_read":200,"all_share":12382,"all_view":20000,"coin":"170.0147104492775","contributions":{211:"521",212:"534"},"item_id":"111003","item_type":1,"media_id":"1002","plat_avg_comment_rate": 0.23,"plat_avg_like_rate": 0.12,"plat_avg_read_rate": 0.22,"plat_avg_share_rate": 0.12,"time":1526354640,"verifys":{1111:"122",1112:"112",1113:"105",1114:"109"}}"""
      val wbRevenueLog4 =
        """{"all_comment":210,"all_likes":100,"all_read":200,"all_share":986,"all_view":2343,"coin":"114.9852895507225","contributions":{211:"631",212:"534"},"item_id":"111004","item_type":1,"media_id":"1003","plat_avg_comment_rate": 0.19,"plat_avg_like_rate": 0.22,"plat_avg_read_rate": 0.32,"plat_avg_share_rate": 0.42,"time":1526354640,"verifys":{1111:"132",1112:"142",1113:"105",1114:"109"}}"""

      // 5, 6 同为类型3, 同时段但是与1,2,3,4不同时段
      val wbRevenueLog5 =
        """{"all_comment":1200,"all_likes":110,"all_read":200,"all_share":986,"all_view":2143,"coin":"407.57353506149605","contributions":{211:"631",212:"734"},"item_id":"111013","item_type":3,"media_id":"1003","plat_avg_comment_rate": 0.19,"plat_avg_like_rate": 0.22,"plat_avg_read_rate": 0.32,"plat_avg_share_rate": 0.42,"time":1526354700,"verifys":{1111:"132",1112:"142",1113:"115",1114:"109"}}"""
      val wbRevenueLog6 =
        """{"all_comment":750,"all_likes":100,"all_read":200,"all_share":986,"all_view":2343,"coin":"352.4264649385039","contributions":{211:"631",212:"734"},"item_id":"111093","item_type":3,"media_id":"1003","plat_avg_comment_rate": 0.19,"plat_avg_like_rate": 0.22,"plat_avg_read_rate": 0.32,"plat_avg_share_rate": 0.42,"time":1526354700,"verifys":{1111:"132",1112:"142",1113:"105",1114:"109"}}"""

      val sparkContext = new SparkContext(new SparkConf())
    }
  }

  /*
    +---------+-------------+-------+-----+------------------+------------------+-----------------+------------------+------------------+
    |item_type|   event_time|item_id|    Y|                 A|              coin|unique_identifier|           total_A|           revenue|
    +---------+-------------+-------+-----+------------------+------------------+-----------------+------------------+------------------+
    |        1|1526354640000| 111004|285.0|3244.3843030635603| 114.9852895507225|           p0-104| 8041.459303063561| 114.9852895507225|
    |        3|1526354700000| 111013|760.0| 6744.893740417306|407.57353506149605|           p0-105|12577.164123140887|407.57353506149605|
    |        2|1526354640000| 111002|285.0|  6416.42928831021|163.07857919800895|           p0-102| 11213.50428831021|163.07857919800895|
    |        3|1526354700000| 111093|760.0|  5832.27038272358| 352.4264649385039|           p0-106|12577.164123140887| 352.4264649385039|
    |        2|1526354640000| 111001|285.0|          4797.075|  121.921420801991|           p0-101| 11213.50428831021|121.92142080199102|
    |        1|1526354640000| 111003|285.0|          4797.075| 170.0147104492775|           p0-103| 8041.459303063561| 170.0147104492775|
    +---------+-------------+-------+-----+------------------+------------------+-----------------+------------------+------------------+
  */
  "calculateRevenueInGroup with duplicate offset" should "pass check" in {
    import fixture._

    val revenue1 = WBRevenueLog.convert(
      new ConsumerRecord[String, String]("test", 0, 101, null, wbRevenueLog1)
    ).get

    val revenue2 = WBRevenueLog.convert(
      new ConsumerRecord[String, String]("test", 0, 102, null, wbRevenueLog2)
    ).get

    val revenue3 = WBRevenueLog.convert(
      new ConsumerRecord[String, String]("test", 0, 103, null, wbRevenueLog3)
    ).get

    val revenue4 = WBRevenueLog.convert(
      new ConsumerRecord[String, String]("test", 0, 104, null, wbRevenueLog4)
    ).get

    val revenue5 = WBRevenueLog.convert(
      new ConsumerRecord[String, String]("test", 0, 105, null, wbRevenueLog5)
    ).get

    val revenue6 = WBRevenueLog.convert(
      new ConsumerRecord[String, String]("test", 0, 106, null, wbRevenueLog6)
    ).get

    val revenueRDD = sparkContext.makeRDD(List(revenue1, revenue2, revenue3, revenue4, revenue5, revenue6))

    // Math.abs(res0 - 121.92142080199102) ~~ 0.0
    val resultDataFrame =
      WBRevenueStatistics.calculateRevenueInGroup(revenueRDD)

    val columns = Seq("item_type", "event_time", "item_id", "Y", "A", "coin", "total_A", "revenue")
    resultDataFrame.schema.map(_.name) shouldBe columns

    resultDataFrame
      .withColumn("event_time", from_unixtime(col("event_time") / 1000))
      .foreachPartition { rowIter =>
        rowIter.foreach { row =>
          println(columns.map(row.getAs[AnyRef]).mkString("|"))
        }
    }

  }


}
// scalastyle:on