package com.kingnetdc.job

import com.kingnetdc.UnitSpecs
import com.kingnetdc.metrics.StateEnum
import com.kingnetdc.sql.StatisticsFunction._
import com.kingnetdc.model.WindowDisplayMode._
import com.kingnetdc.sql.{Equal, ComputationRule}
import com.kingnetdc.utils.StatisticsUtils
import com.kingnetdc.watermelon.output.MysqlSink
import com.kingnetdc.watermelon.utils.StringUtils
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkConf
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window
import com.kingnetdc.model.{IdlExchangeRate, DurationConfiguration, Event}
import org.apache.spark.sql.types.{ByteType, StringType}
import org.joda.time.DateTime
import scala.collection.mutable
import scala.util.Random
import com.kingnetdc.utils.StatisticsUtils._
import org.apache.kafka.clients.consumer.ConsumerRecord

/**
 * Created by zhouml on 20/05/2018.
 */
//scalastyle:off
class EventStatisticsSuite extends UnitSpecs {

    private def deviceEventGenerator(timestamp: Long, did: String) = {
        val newDevice = new Random().nextBoolean()
        val newDeviceByte: Byte = if (newDevice) 1 else 0
        val oldDeviceByte: Byte = 1

        val dimensionValues = List(List("2.8.8", "360"))
        new Event(
            "active", timestamp, dimensionValues, Map("did" -> did)
        ).setFilterFieldValueMap(
            Map(
                "is_new_device_60000" -> newDeviceByte,
                "is_new_device_3600000" -> newDeviceByte,
                "is_new_device_86400000" -> newDeviceByte,
                "old_device" -> oldDeviceByte
            )
        )
    }

    /*
      import org.apache.spark.sql.DataFrameWindowFunctionsSuite
    */
    lazy val fixture = {
        new {
            val sparkConf = new SparkConf().setAppName("EventStatistics").setMaster("local[*]")
            val sparkSession = SparkSession.builder().config(sparkConf).getOrCreate()

            import sparkSession.implicits._

            val userViewStatistics = List(("A1", 10), ("A2", 9), ("A3", 32), ("A4", 8), ("A5", 7))
            val dataFrame = sparkSession.sparkContext.makeRDD(userViewStatistics).toDF("user_id", "view")
            val rankedUserViewDataFrame =
                dataFrame.select(
                    col("*"), row_number().over(Window.orderBy(col("view").desc)).alias("rank")
                ).where(col("rank") <= 10)
        }
    }

    /*
      +-------+----+----------+
      |user_id|view|    rank|
      +-------+----+----------+
      |     A3|  32|         1|
      |     A1|  10|         2|
      |     A2|   9|         3|
      |     A4|   8|         4|
      |     A5|   7|         5|
      +-------+----+----------+
    */
    "UserViewDataFrame rank" should "pass check" in {
        import fixture._
        import sparkSession.implicits._
        rankedUserViewDataFrame.explain()

        val row =
            rankedUserViewDataFrame.filter(
                ($"user_id" === lit("A3")) and ($"view" === lit(32))
            ).collect().head

        row.getAs[Int]("rank") shouldBe 1
    }

    "EventDataFrame statistics" should "pass check" in {
        val sparkConf = new SparkConf().setAppName("EventStatistics").setMaster("local[*]")
        val sparkSession = SparkSession.builder().config(sparkConf).getOrCreate()
        import sparkSession.implicits._

        val tsAndDdids = List(
            (1526815890000L, "BCA00-CCFFF-E000001"),
            (1526815890000L, "BCA00-CCFFF-E000001"),
            (1526815980000L, "BCA00-CCFFF-E000003"),
            (1526815950000L, "BCA00-CCFFF-E000003"),
            (1526815890000L, "BCA00-CCFFF-E000001"),
            (1526815950000L, "BCA00-CCFFF-E000003"),
            (1526815980000L, "BCA00-CCFFF-E000003"),
            (1526815920000L, "BCA00-CCFFF-E000002"),
            (1526815950000L, "BCA00-CCFFF-E000003"),
            (1526815890000L, "BCA00-CCFFF-E000001")
        )

        val events =
            tsAndDdids.map {
                case (ts, did) => deviceEventGenerator(ts, did)
            }

        val dummyStatistics = new EventStatistics {

            private val DID = "did"
            private val IS_NEW_DEVICE_IN_MIN = "is_new_device_60000"
            private val IS_NEW_DEVICE_IN_HOUR = "is_new_device_3600000"
            private val IS_NEW_DEVICE_IN_DAY = "is_new_device_86400000"

            override val computationRules: List[ComputationRule] = {
                ComputationRule(
                    DID, StringType, StateEnum.ACTIVE.toString, COUNT_DISTINCT.toString, None
                ) ::
                ComputationRule(
                    DID, StringType, "dad_new_60000", COUNT_DISTINCT.toString,
                    Some(Equal(IS_NEW_DEVICE_IN_MIN, 1, ByteType))
                ) ::
                ComputationRule(
                    DID, StringType, "dad_new_3600000", COUNT_DISTINCT.toString,
                    Some(Equal(IS_NEW_DEVICE_IN_HOUR, 1, ByteType))
                ) ::
                ComputationRule(
                    DID, StringType, "dad_new_86400000", COUNT_DISTINCT.toString,
                    Some(Equal(IS_NEW_DEVICE_IN_DAY, 1, ByteType))
                ) ::
                ComputationRule(
                    DID, StringType, StateEnum.OLD.toString, COUNT_DISTINCT.toString,
                    Some(Equal("old_device", 1, ByteType))
                ) :: Nil
            }

            override val dimensions: List[String] = List("appver", "channel")

            private val oneMin: Long = 1000 * 60
            // 1min
            private val oneHour: Long = 1000 * 3600
            // 1hour
            private val oneDay: Long = 1000 * 3600 * 24 // 1day

            override val durationConfigurations: List[DurationConfiguration] = {
                List(
                    DurationConfiguration(oneMin, oneMin, EVENT_TIME.toString),
                    DurationConfiguration(oneHour, oneMin, EVENT_TIME.toString),
                    DurationConfiguration(oneDay, oneMin, FLUSH_TIME.toString)
                )
            }
        }

        val eventRDD: RDD[Event] = sparkSession.sparkContext.makeRDD(events)
        dummyStatistics.preAggregate(eventRDD, 60000)
    }

    "DeviceStatistics parse" should "pass check" in {
        // normal record
        val record1 =
            """
              {"timestamp":1534325284000,"did":"BCA00-CCFFF-E000003",
              "event":"active","properties":{"_appver":"2.0.7", "_channel":"360", "_os": "Ios", "_sst": "1534325284030"}}
            """

        val normalRecordConfig: Map[String, String] = Map.empty

        val (eventTime, appVersion, appChannel, os, did, event) =
            DeviceStatistics.parse(record1, normalRecordConfig).get

        eventTime shouldBe 1534325284030L
        did shouldBe "BCA00-CCFFF-E000003".toLowerCase
        event shouldBe "active"
        appChannel shouldBe "360"
        os shouldBe "ios"
        appVersion shouldBe "2.0.7"

        val logTsStartInValid: Map[String, String] = Map("log.timestamp.start" -> "1534325285000")
        DeviceStatistics.parse(record1, logTsStartInValid).isEmpty shouldBe true

        val logReceiveStartInValid: Map[String, String] = Map("log.receive-time.start" -> "1534325284032")
        DeviceStatistics.parse(record1, logReceiveStartInValid).isEmpty shouldBe true

        val bothOky = Map(
            "log.timestamp.start" -> "1534325283000",
            "log.receive-time.start" -> "1534325284000"
        )

        DeviceStatistics.parse(record1, bothOky).nonEmpty shouldBe true

        val logReceiveStartValid: Map[String, String] = Map("log.receive-time.start" -> "1534325284030")
        DeviceStatistics.parse(record1, logReceiveStartValid).nonEmpty shouldBe true
    }

    "WithdrawStatistics parse" should "pass check" in {
        val withdrawLog = """{"ouid":10101010101010,"timestamp":1434556935000,"did":"BCA00-CCFFF-E000001","event":"withdraw","project":"qkl","properties":{"_ip":"180.79.35.65","_appver":"2.0.7","_os":"ios","_osver":"iOS 7.0","_model":"iPhone 5","_mfr":"apple","_res":"1920*1080","_nettype":"4g","_carrier":"cmcc","_channel":"360","isvisitor":0,"order_id":"aaaaa","status":1,"coin":3.5,"rate":0.5,"rmb":1.75}}"""
        val config: Map[String, String] = Map.empty
        val (eventTime, ouid, appVersion, appChannel, os, status, withdrawType, rmb) = WithdrawStatistics.parse(withdrawLog, config).get
        eventTime shouldBe 1434556935000L
        ouid shouldBe "10101010101010"
        appVersion shouldBe "2.0.7"
        appChannel shouldBe "360"
        os shouldBe "ios"
        status shouldBe "1"
        withdrawType shouldBe "1"
        rmb shouldBe 1.75D
    }

    "DeviceStatistics buildDimensionValues" should "pass check" in {
        DeviceStatistics.buildDimensionValues("0.1.1", "360", "ios").foreach(println)
    }

    "ShowInPageStatistics parse" should "pass check" in {
        val onlineUserShowpageLog =
            """
              {"eventid":"ba238970bd2b74a63abeb1695220cd24","ouid":"2160962","project":"qkl","event":"showinpage","properties":{"itemlist":"278112866959089664@video,278090762633732096@video,278090822574482432@video","_res":"1920*1080","_osver":"7.1.1","_appver":"1.0.5.0807","_sst":"1534227027844"},"did":"B7EED9C9C9552B78077EBE28DF73BDD4","timestamp":1534227026451}
            """

        val ((uidOpt, didOpt), items) = ShowInPageStatistics.parse(onlineUserShowpageLog).get

        uidOpt.get shouldBe "2160962"
        didOpt.get shouldBe "B7EED9C9C9552B78077EBE28DF73BDD4"
        items shouldBe List("278112866959089664", "278090762633732096", "278090822574482432")

        // ouid == -1 and did is not null
        val offlineUserShowpageLog1 =
            """
              {"eventid":"ba238970bd2b74a63abeb1695220cd24","ouid":"-1","project":"qkl","event":"showinpage","properties":{"itemlist":"278112866959089664@video,278090762633732096@video,278090822574482432@video","_res":"1920*1080","_osver":"7.1.1","_appver":"1.0.5.0807","_sst":"1534227027844"},"did":"B7EED9C9C9552B78077EBE28DF73BDD4","timestamp":1534227026451}
            """

        val ((uidOpt1, didOpt1), _) = ShowInPageStatistics.parse(offlineUserShowpageLog1).get

        uidOpt1 shouldBe None
        didOpt1.get shouldBe "B7EED9C9C9552B78077EBE28DF73BDD4"

        // ouid is null and did is not null
        val offlineUserShowpageLog2 =
            """
              {"eventid":"ba238970bd2b74a63abeb1695220cd24","project":"qkl","event":"showinpage","properties":{"itemlist":"278112866959089664@video,278090762633732096@video,278090822574482432@video","_res":"1920*1080","_osver":"7.1.1","_appver":"1.0.5.0807","_sst":"1534227027844"},"did":"B7EED9C9C9552B78077EBE28DF73BDD4","timestamp":1534227026451}
            """

        val ((uidOpt2, didOpt2), _) = ShowInPageStatistics.parse(offlineUserShowpageLog2).get
        uidOpt2 shouldBe None
        didOpt2.get shouldBe "B7EED9C9C9552B78077EBE28DF73BDD4"

        // ouid is null and did = -1
        val offlineUserShowpageLog3 =
            """
              {"eventid":"ba238970bd2b74a63abeb1695220cd24","project":"qkl","event":"showinpage","properties":{"itemlist":"278112866959089664@video,278090762633732096@video,278090822574482432@video","_res":"1920*1080","_osver":"7.1.1","_appver":"1.0.5.0807","_sst":"1534227027844"},"did":"-1","timestamp":1534227026451}
            """

        val result = ShowInPageStatistics.parse(offlineUserShowpageLog3)
        result shouldBe None
    }

    "ExchangeRateStatistics parseConsumerRecord" should "pass check" in {
        val registerLog = """{"ouid":2157073,"project":"qkl","event":"register","did":"A69270906177F4E7FD016A197D0726A3","properties":{"_os":"Android","_carrier":"中国移动","_ip":"100.125.12.195","_gps":"","idfa":"","_sdkver":"2.0","gps_province":"","type":"1","_res":"1280*720","_osver":"5.1","_appver":"","phone":"15017917324","_sdk":"php","_nettype":"WIFI","gps_city":"","_sst":"1532497669664","_systime":"1532497669590","_model":"OPPOA59m","gps_area":"","idfv":"","step":"0","_channel":""},"timestamp":1532497669590}"""
        val registerConsumerRecord = new ConsumerRecord[String, String]("wutiao_super", 0, 1, null, registerLog)

        val withdrawLog = """{"ouid":2129024,"project":"qkl","event":"withdraw","did":-1,"properties":{"_os":"","_carrier":"","rmb":"3.07","_ip":"119.3.18.155, 100.125.6.53","_gps":"","idfa":"","_sdkver":"2.0","gps_province":"","_res":"","_osver":"","_appver":"","rate":"0.00072","_sdk":"php","_nettype":"","gps_city":"","_sst":"1532214260472","_systime":"","_model":"","gps_area":"","idfv":"","order_id":"269856690582366208","_channel":"","coin":"4263.8888888889","status":"2"},"timestamp":1532214260062}"""
        val withdrawConsumerRecord = new ConsumerRecord[String, String]("wutiao_lower", 0, 2, null, withdrawLog)

        val moneyGainLog = """{"messageId":"cbef4c7b7413acd9acb8112c2a120f0d","partitionId":10,"offset":105198,"uid":2131115,"sourceId":0,"sourceType":0,"itemId":273519127790494720,"itemType":2,"actionType":11,"value":"1.19037491","time":1533102240}"""
        val moneyGainConsumerRecord = new ConsumerRecord[String, String]("wtwbgainlist", 0, 3, null, moneyGainLog)

        ExchangeRateStatistics.parseConsumerRecord(
            registerConsumerRecord, "wutiao_super", "wutiao_lower", "wtwbgainlist"
        ) shouldBe List((1532497669664L, "register", 1D))

        ExchangeRateStatistics.parseConsumerRecord(
            withdrawConsumerRecord, "wutiao_super", "wutiao_lower", "wtwbgainlist"
        ) shouldBe List(
            (1532214260472L, "withdraw_rmb", 3.07),
            (1532214260472L, "withdraw_coin", 4263.8888888889)
        )

        ExchangeRateStatistics.parseConsumerRecord(
            moneyGainConsumerRecord, "wutiao_super", "wutiao_lower", "wtwbgainlist"
        ) shouldBe List(
            (1533102240000L, "money_gain", 1.19037491)
        )
    }

    "ExchangeRateStatistics convertEventRDD" should "pass check" in {
        val sparkSession = SparkSession.builder
                .master("local[*]")
                .appName("ExchangeRateStatistics convertEventRDD")
                .getOrCreate()
        val sparkContext = sparkSession.sparkContext

        val rdd1: RDD[(Long, String, Double)] = sparkContext.makeRDD(
            List(
                (1533106428000L, "register", 1D),
                (1533106453000L, "withdraw_rmb", 2.37D),
                (1533106453000L, "withdraw_rmb", 2.47D),
                (1533106460000L, "withdraw_coin", 1202D),
                (1533106460000L, "withdraw_coin", 1302D)
            )
        )
        val result1 = ExchangeRateStatistics.convertEventRDD(0L, rdd1).collect()(0)

        // Wed Aug 01 14:50:00 CST 2018
        result1._1 shouldBe 1533106200000L
        result1._2 shouldBe Map(
            "register" -> 1.0, "withdraw_rmb" -> 4.84, "withdraw_coin" -> 2504, "money_gain" -> 0.0
        )

        val rdd2: RDD[(Long, String, Double)] = sparkContext.makeRDD(
            List(
                (1533106428000L, "register", 1D),
                (1533106453000L, "withdraw_rmb", 2.37D),
                (1533106453000L, "withdraw_rmb", 2.47D),
                (1533106460000L, "withdraw_coin", 1202D),
                (1533106460000L, "withdraw_coin", 1302D),
                // 只要单个, 所以local merge之后有新key进来
                (1533107038000L, "withdraw_coin", 1000D)
            )
        )

        val result2 = ExchangeRateStatistics.convertEventRDD(0L, rdd2).collect().toMap

        result2.get(1533106200000L).get shouldBe Map(
            "register" -> 1.0, "withdraw_rmb" -> 4.84, "withdraw_coin" -> 2504, "money_gain" -> 0.0
        )

        result2.get(1533106800000L).get shouldBe Map("withdraw_coin" -> 1000.0)


        val rdd3: RDD[(Long, String, Double)] = sparkContext.emptyRDD

        val result3 = ExchangeRateStatistics.convertEventRDD(100L, rdd3).collect().toMap

        result3.get(100L).get shouldBe Map(
            "register" -> 0.0,
            "withdraw_rmb" -> 0.0,
            "withdraw_coin" -> 0.0,
            "money_gain" -> 0.0
        )
    }

    "ExchangeRateStatistics calculatePrice" should "pass check" in {
        val onlineDate1 = new DateTime(2018, 8, 1, 0, 0)
        val current1 = new DateTime(2018, 8, 1, 17, 0)

        ExchangeRateStatistics.calculatePriceByMinute(current1, onlineDate1) shouldBe 0.9869620905143763

        val onlineDate2 = new DateTime(2018, 7, 31, 0, 0)
        val current2 = new DateTime(2018, 8, 1, 17, 0)

        ExchangeRateStatistics.calculatePriceByMinute(current2, onlineDate2) shouldBe 1.0314874166785617

        ExchangeRateStatistics.calculatePriceByMinute(
            new DateTime(2018, 8, 1, 0, 0),  new DateTime(2018, 8, 1, 0, 0)
        ) shouldBe 0.9869620905143763
    }

    "ExchangeRateStatistics getIdlExchangeRate" should "pass check" in {
        val mysqlConfig = Map(
            "url" -> "jdbc:mysql://localhost:3306/kingnet?useSSL=false",
            "user" -> "root",
            "password" -> "123456"
        )

        val mysqlSink: MysqlSink = new MysqlSink(mysqlConfig)

        try {
            val startTime = new DateTime(2018, 8, 1, 0, 0)
            val endTime = new DateTime(2018, 8, 1, 19, 0)

            ExchangeRateStatistics.getIdlExchangeRate(
                startTime, endTime,
                "idl_wutiao_exchange_rate_realtime", mysqlSink
            ).head shouldBe
            IdlExchangeRate("2018-08-01 18:00:00", 100, 1.75, 30, 3.5)
        } finally {
            mysqlSink.close()
        }
    }

    "ExchangeRateStatistics getRemainingMoneyAndCoin" should "pass check" in {
        val mysqlConfig = Map(
            "url" -> "jdbc:mysql://localhost:3306/kingnet?useSSL=false",
            "user" -> "root",
            "password" -> "123456"
        )

        val mysqlSink: MysqlSink = new MysqlSink(mysqlConfig)

        try {
            val startTime = new DateTime(2018, 7, 30, 0, 0)
            val endTime = new DateTime(2018, 8, 1, 0, 0)
            val resultMap =
                ExchangeRateStatistics.getHistoryRemainingMoneyAndCoin(
                    startTime, endTime, "report_wutiao_exchange_rate", mysqlSink
                )

            resultMap.get("2018-07-31").get shouldBe (232.23, 354545.43)
            resultMap.get("2018-07-30").get shouldBe (312.23, 454545.43)
        } finally {
            mysqlSink.close()
        }
    }

    "ExchangeRateStatistics calculateExchangeRate" should "pass check" in {
        val window = new DateTime(2018, 8, 2, 15, 10)

        val config = mutable.Map(
            "mysql.url" -> "jdbc:mysql://localhost:3306/kingnet?useSSL=false",
            "mysql.user" -> "root",
            "mysql.password" -> "123456",
            "mysql.exchange-rate.interim.table" -> "idl_wutiao_exchange_rate_realtime",
            "mysql.exchange-rate.interim.offline.table" -> "idl_wutiao_exchange_rate",
            "mysql.exchange-rate.table" -> "report_wutiao_exchange_rate",
            "online.date" -> "2018-08-01"
        )

        val mysqlSink: MysqlSink = new MysqlSink(getMysqlConfig(config.toMap))

        try {
            ExchangeRateStatistics.calculateExchangeRate(window, config, mysqlSink)
        } finally {
            mysqlSink.close()
        }
    }

    "ExchangeRateStatistics saveExchangeInterimTable" should "pass check" in {
        val config = mutable.Map(
            "mysql.url" -> "jdbc:mysql://localhost:3306/kingnet?useSSL=false",
            "mysql.user" -> "root",
            "mysql.password" -> "123456",
            "mysql.exchange-rate.interim.table" -> "idl_wutiao_exchange_rate_realtime",
            "mysql.exchange-rate.interim.offline.table" -> "idl_wutiao_exchange_rate",
            "mysql.exchange-rate.table" -> "report_wutiao_exchange_rate"
        )

        val currentWindow = new DateTime(2018, 8, 2, 15, 10)

        ExchangeRateStatistics.saveToInterimExchangeRate(
            currentWindow.getMillis,
            Map(
                "register" -> 30,
                "withdraw_rmb" -> 32.0,
                "money_gain" -> 500000,
                "withdraw_coin" -> 232.0
            ),
            config
        )
    }

    "ExchangeRateStatistics stateUpdateAndDumpExchangeRate" should "pass check" in {

        val config = mutable.Map(
            "mysql.url" -> "jdbc:mysql://localhost:3306/kingnet?useSSL=false",
            "mysql.user" -> "root",
            "mysql.password" -> "123456",
            "mysql.exchange-rate.interim.table" -> "idl_wutiao_exchange_rate_realtime",
            "mysql.exchange-rate.interim.offline.table" -> "idl_wutiao_exchange_rate",
            "mysql.exchange-rate.table" -> "report_wutiao_exchange_rate",
            "online.date" -> "2018-08-01"
        )

        val currentValueByContinuousWindow: List[(Long, Map[String, Double])] =
            List(
                new DateTime(2018, 8, 2, 15, 30).getMillis -> Map(
                    "register" -> 1,
                    "withdraw_rmb" -> 2,
                    "money_gain" -> 3,
                    "withdraw_coin" -> 4
                ),
                new DateTime(2018, 8, 2, 15, 35).getMillis -> Map(
                    "register" -> 2,
                    "withdraw_rmb" -> 3,
                    "money_gain" -> 4,
                    "withdraw_coin" -> 5
                )
            )

        ExchangeRateStatistics.stateUpdateAndDumpExchangeRate(
            currentValueByContinuousWindow, config
        )

    }

    "StatisticsUtils getOrSetFirstDimension" should "pass check" in {
        val cacheConfigPath = "user-dimension-cache-dev.yml"
        val idAndDimension: Map[String, (Long, String, String, String)] =
            Map(
                "2142002" -> (1533709884000L, "ios", "1.0.0.0806test", "weibo")
            )
        val keyPrefix = "wt:user-dim:dev:"

        StatisticsUtils.getOrSetFirstDimension(cacheConfigPath, idAndDimension, keyPrefix)
    }

    "StatisticsUtils getOrSetNewFirstDimension" should "pass check" in {
        val cacheConfigPath = "device-dimension-cache-test.yml"

        val idAndDimension1: Map[String, (Long, String, String, String)] =
            Map("96695C72E6F9AE896D6632743A6CAC35" -> (1533869289000L, "-1", "1.0.0.0806test", "weibo"))

        val keyPrefix = "wt:device-dim:test:"

        val result1 = StatisticsUtils.getOrSetFirstDimension(cacheConfigPath, idAndDimension1, keyPrefix)

        result1.get("96695C72E6F9AE896D6632743A6CAC35").get shouldBe ("-1", "1.0.0.0806test", "weibo", Nil)

        val idAndDimension2: Map[String, (Long, String, String, String)] =
            Map("96695C72E6F9AE896D6632743A6CAC35" -> (1533869289000L, "weibo", "1.0.0.0806test", "weibo"))

        val result2 = StatisticsUtils.getOrSetFirstDimension(cacheConfigPath, idAndDimension2, keyPrefix)
    }

    "DeviceStatistics getDidMinEventTimeAndDimension" should "pass check" in {
        // (eventTime, appVersion, appChannel, os, did, event)
        val deviceBehaviorLogs: List[(Long, String, String, String, String, String)] =
            List(
                (1533712364000L, "1.0.0.0806test", "toutiao", "ios", "96695C72E6F9AE896D6632743A6CAC35", "event"),
                (1533712462000L, "1.0.0.0806test", "weibo", "-1", "96695C72E6F9AE896D6632743A6CAC35", "event"),
                (1533712762000L, "1.0.0.0807test", "weixin", "-1", "96695C72E6F9AE896D6632743A6CAC35", "event")
            )

        val didMinEventTimeAndDimension = DeviceStatistics.getDidMinEventTimeAndDimension(deviceBehaviorLogs)

        didMinEventTimeAndDimension.get("96695C72E6F9AE896D6632743A6CAC35").get shouldBe (1533712364000L, "1.0.0.0806test", "toutiao", "ios")
    }

    "DeviceStatistics rdd agg" should "pass check" in {
        val key = "2.7.0,360,10"
        val longUid: Int = StringUtils.hashString("BCA00-CCFFF-E000001").asInt()
        val value1 = (longUid, List(BYTE_ONE, BYTE_ONE, BYTE_ZERO, BYTE_ONE, BYTE_ZERO, BYTE_ONE, BYTE_ZERO))
        val value2 = (longUid, List(BYTE_ONE, BYTE_ZERO, BYTE_ONE, BYTE_ONE, BYTE_ZERO, BYTE_ONE, BYTE_ZERO))

        import fixture._
        // import org.roaringbitmap.longlong.{Roaring64NavigableMap => BitMap}
        import org.roaringbitmap.{RoaringBitmap => BitMap}

        val keyValueRDD: RDD[(String, ValueType)] =
            sparkSession.sparkContext.makeRDD(List((key -> value1), (key -> value2)))

        val partitionNum = sparkConf.getInt("spark.sql.shuffle.partitions", 3)

        /*
          createCombiner: V => C,
          mergeValue: (C, V) => C,
          mergeCombiners: (C, C) => C,
          numPartitions: Int
        */

        /*
        def bitmapOf(long: Long*): BitMap = {
            val bitmap =  new BitMap(false, false)
            bitmap.add(long: _*)
            bitmap
        }

        def initializeBitMap(b: Byte, uid: Long) = {
            if (b == BYTE_ONE) bitmapOf(uid) else bitmapOf()
        }

        def fillBitMap(bitMap: BitMap, b: Byte, uid: Long) = {
            if (b == BYTE_ONE) {
                bitMap.add(uid)
                bitMap
            } else bitMap
        }
        */

        def bitmapOf(int: Int*): BitMap = {
            val bitmap =  new BitMap()
            bitmap.add(int: _*)
            bitmap
        }

        def initializeBitMap(b: Byte, uid: Int) = {
            if (b == BYTE_ONE) bitmapOf(uid) else bitmapOf()
        }

        def fillBitMap(bitMap: BitMap, b: Byte, uid: Int) = {
            if (b == BYTE_ONE) {
                bitMap.add(uid)
                bitMap
            } else bitMap
        }


        type ValueType = (Int, List[Byte])

        type CombinerType = List[BitMap]

        def createCombiner(value: ValueType) = {
            val (id, bytes) = value
            bytes.map { byte => initializeBitMap(byte, id) }
        }

        def mergeValue(combiner: CombinerType, value: ValueType) = {
            val (id, bytes) = value

            (combiner zip bytes).map {
                case (combiner, byte) => fillBitMap(combiner, byte, id)
            }
        }

        def mergeCombiner(oneCombiner: CombinerType, otherCombiner: CombinerType): CombinerType = {
            (oneCombiner zip otherCombiner).map {
                case (one, other) =>
                    one.or(other)
                    one
            }
        }

        val result: RDD[(String, CombinerType)] =
            keyValueRDD.combineByKey[CombinerType](
                createCombiner _,
                mergeValue _,
                mergeCombiner _,
                partitionNum
            )
    }

}
//scalastyle:on

