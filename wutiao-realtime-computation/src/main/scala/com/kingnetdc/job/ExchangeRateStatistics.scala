package com.kingnetdc.job

import java.sql.ResultSet
import com.kingnetdc.model.{IdlExchangeRate, EventTypeEnum}
import com.kingnetdc.watermelon.output.MysqlSink
import com.kingnetdc.watermelon.utils.AppConstants._
import com.kingnetdc.utils.{KafkaOffsetUtil, JsonUtils}
import com.kingnetdc.utils.StatisticsUtils._
import com.kingnetdc.watermelon.input.KingnetInputDStream
import com.kingnetdc.watermelon.utils.{DateUtils, ConfigUtils}
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import com.kingnetdc.watermelon.utils.ConfigurationKeys._
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.{Duration, StreamingContext}
import org.apache.spark.{SparkContext, SparkConf}
import org.joda.time.{Minutes, Days, DateTime}
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.collection.mutable
import com.kingnetdc.sink.MySqlSink

/**
* Created by zhouml on 11/06/2018.
*/
object ExchangeRateStatistics {

    private val SUCCESS_WITHDRAW = 2

    private val REGISTER = "register"

    private val WITHDRAW_RMB = "withdraw_rmb"

    private val WITHDRAW_COIN = "withdraw_coin"

    private val MONEY_GAIN = "money_gain"

    private val WB_IN_MINUTE = 4756

    private val WB_IN_DAY = WB_IN_MINUTE * 60 * 24

    // 5 min
    private val DISPLAY_WINDOW: Long = 1000 * 60 * 5

    /**
     *  TODO 如果superTopic量太大的話, 考虑从别的地方读取注册用户
      * register, withdraw_rmb, withdraw_coin, money_gain 对应的值
      *
      * @param consumerRecord Kafka consumer record
      * @param superTopic  行为事件topic
      * @param mediumTopic  行为事件topic
      * @param moneyGainTopic 领币topic
      *
      * @return
      */
    def parseConsumerRecord(
        consumerRecord: ConsumerRecord[String, String],
        superTopic: String, mediumTopic: String, moneyGainTopic: String
    ): List[(Long, String, Double)] = {
        val topic = consumerRecord.topic()
        val value = consumerRecord.value()

        try {
            val message = JsonUtils.getJsonNode(value)

            if (topic == superTopic) {
                val event = message.at("/event").asText()
                val eventTime = message.at("/properties/_sst").asLong()

                if (event == "register") {
                    (eventTime, REGISTER, 1D) :: Nil
                } else {
                    Nil
                }
            } else if (topic == mediumTopic) {
                val event = message.at("/event").asText()
                val eventTime = message.at("/properties/_sst").asLong()

                if (event == EventTypeEnum.WITHDRAW.toString) {
                    val successWithdrawStatusOpt =
                        Option(message.at("/properties/status")).map(_.asInt()).filter(_ == SUCCESS_WITHDRAW)

                    successWithdrawStatusOpt.map { _ =>
                        val withdrawRMB = message.at("/properties/rmb").asDouble
                        val withdrawCoin = message.at("/properties/coin").asDouble

                        (eventTime, WITHDRAW_RMB, withdrawRMB) ::
                        (eventTime, WITHDRAW_COIN, withdrawCoin) ::
                        Nil
                    }.getOrElse(Nil)
                } else {
                    Nil
                }
            } else if (topic == moneyGainTopic) {
                val timestamp = message.at("/time").asLong() * 1000
                (timestamp, MONEY_GAIN, message.at("/value").asDouble()) :: Nil
            } else {
                Nil
            }
        } catch {
            case ex: Exception =>
                logger.error(s"Failed to parse ${value}", ex)
                Nil
        }
    }

    def calculatePriceByDay(
        currentWindow: DateTime, onlineDate: DateTime
    ) = {
        val gapDay = Days.daysBetween(onlineDate, currentWindow).getDays() + 1
        val currentPrice = (1 - scala.math.pow(0.8, (gapDay + 19) / 20)) / (1 - 0.8)
        logger.info(s"Current day price: ${currentWindow.toString(YMDHMS)}: ${currentPrice}")
        currentPrice
    }

    def calculatePriceByMinute(
        currentWindow: DateTime, onlineDate: DateTime
    ) = {
        val gapDay = Days.daysBetween(onlineDate, currentWindow).getDays()

        val gapMinuteInToDay = Minutes.minutesBetween(currentWindow.withTimeAtStartOfDay(), currentWindow).getMinutes()

        val yInPow = (gapDay + gapMinuteInToDay.toDouble / 1440 + 19) / 20

        val currentPrice = (1 - scala.math.pow(0.8, yInPow)) / (1 - 0.8)
        logger.info(s"Current minute price: ${currentWindow.toString(YMDHMS)}: ${currentPrice}")
        currentPrice
    }

    /**
     *  获取历史剩余钱 和 剩余币
     *
     * @param startTime
     * @param mysqlSink
     *
     * @return
     */
    def getHistoryRemainingMoneyAndCoin(
        startTime: DateTime, endTime: DateTime,
        interimOfflineTable: String, mysqlSink: MysqlSink
    ): Map[String, (Double, Double)] = {
        val selectSQL =
            s"""
               select
                    date_format(ds, '%Y-%m-%d') as ds,
                    remaining_rmb, remaining_coin
               from
                    ${interimOfflineTable}
                where
                    ds >= '${startTime.toString(YMD)}' and
                    ds < '${endTime.toString(YMD)}'
            """

        val resultSetFunc = (rs: ResultSet) => {
            val resultMap: mutable.Map[String, (Double, Double)] = mutable.Map.empty

            while (rs.next()) {
                resultMap += (rs.getString(1) -> (rs.getDouble(2), rs.getDouble(3)))
            }
            resultMap
        }

        mysqlSink.getSimpleDataSet(selectSQL, resultSetFunc).toMap
    }

    def getIdlExchangeRate(
        startTime: DateTime, endTime: DateTime,
        exchangeRateInterimTable: String, mysqlSink: MysqlSink
    ): List[IdlExchangeRate] = {
        val selectSQL =
            s"""
                select
                    date_format(window, '%Y-%m-%d %H:%i:%s')  as window,
                    total_register, total_withdraw_rmb, total_money_gain, total_withdraw_coin
                from ${exchangeRateInterimTable}
                where
                    window >= '${startTime.toString(YMDHMS)}' and
                    window <= '${endTime.toString(YMDHMS)}'
            """

        val resultSetFunc = (rs: ResultSet) => {
            val resultBuffer: ListBuffer[IdlExchangeRate] = ListBuffer()

            while (rs.next()) {
                resultBuffer += IdlExchangeRate(
                    rs.getString(1), rs.getDouble(2), rs.getDouble(3),
                    rs.getDouble(4), rs.getDouble(5)
                )
            }
            resultBuffer
        }

        mysqlSink.getSimpleDataSet(selectSQL, resultSetFunc).toList
    }

    def getRemainingMoneyAndCoinOfYesterday(
        idlExchangeRates: List[IdlExchangeRate], config: mutable.Map[String, String]
    ) = {
        if (idlExchangeRates.isEmpty) {
            (0D, 0D)
        } else {
            val window = idlExchangeRates.head.window
            val date = DateUtils.getYMDHMS.parse(window)
            val onlineDate: DateTime = DateTime.parse(config("online.date"))

            val price = calculatePriceByDay(onlineDate, new DateTime(date.getTime))

            val remainingMoney =
                idlExchangeRates.map(_.register).sum * price - idlExchangeRates.map(_.withdrawRMB).sum

            val remainingCoin =
                idlExchangeRates.map { idlExchangeRate =>
                    idlExchangeRate.moneyGain - idlExchangeRate.withdrawCoin
                }.sum

            (remainingMoney, remainingCoin)
        }
    }

    def getMoneyWillingToPay(
        currentWindow: DateTime, idlExchangeRates: List[IdlExchangeRate], onlineDate: DateTime
    ) = {
        val registerInRecent24 =
            idlExchangeRates.filter { idlExchangeRate =>
                val window = idlExchangeRate.window
                window >= currentWindow.minusHours(24).toString(YMDHMS) && window < currentWindow.toString(YMDHMS)
            }.map(_.register).sum

        registerInRecent24 * calculatePriceByMinute(currentWindow, onlineDate)
    }

    /**
     *
     *   (24小时新增用户数 x 新增单价 + 剩余钱_(n - 1) + 日剩余钱_n) / (剩余币_(n - 1) + 日剩余币_n + 日发币)
     *
     *  关于截止昨日的剩余钱 和 剩余币
     *
     *  + 如果计算的时候, 离线表中已经计算好的, 则直接使用, 即 昨日 + 今日动态
     *  + 如果计算的时候, 离线表中还没有计算好的, 则前天 + 实时的昨日到计算点
     *
     *      对于公式中的 日剩余钱_n + 剩余钱_(n-1)
     *      对于公式中的 日剩余币_n + 剩余币_(n-1)
     *
     * @param currentWindow
     * @param config
     *
     * @return
     */
    def calculateExchangeRate(
        currentWindow: DateTime, config: mutable.Map[String, String], mysqlSink: MysqlSink
    ): Double = {
        val yesterday = currentWindow.minusDays(1).withTimeAtStartOfDay()
        val onlineDate: DateTime = DateTime.parse(config("online.date"))

        val idlExchangeRates = getIdlExchangeRate(
            yesterday, currentWindow,
            config("mysql.exchange-rate.interim.table"), mysqlSink
        )

        val historyRemainingMoneyAndCoin = getHistoryRemainingMoneyAndCoin(
            currentWindow.minusDays(2), currentWindow,
            config("mysql.exchange-rate.interim.offline.table"), mysqlSink
        )

        val (remainingRMBUntilYesterDay, remainingCoinUntilYesterDay) =
            historyRemainingMoneyAndCoin.get(yesterday.toString(YMD)) match {
                // 截止昨日的已经有了
                case Some((remainingRMB, remainingCoin)) =>
                    (remainingRMB, remainingCoin)
                // 前天 + 实时昨日
                case None =>
                    val formattedTheDayBefore = currentWindow.minusDays(2).toString(YMD)
                    val (theDayBeforeRemainingMoney, theDayBeforeRemainingCoin) =
                        historyRemainingMoneyAndCoin.get(formattedTheDayBefore).getOrElse((0D, 0D))

                    val idlExchangeRateOfYesterday =
                        idlExchangeRates.filter { idlExchangeRate =>
                            val window = idlExchangeRate.window
                            window >= yesterday.toString(YMDHMS) &&
                            window < currentWindow.withTimeAtStartOfDay().toString(YMDHMS)
                        }

                    val (yesterdayRemainingMoney, yesterdayRemainingCoin) =
                        getRemainingMoneyAndCoinOfYesterday(idlExchangeRateOfYesterday, config)

                    (
                       theDayBeforeRemainingMoney + yesterdayRemainingMoney,
                       theDayBeforeRemainingCoin + yesterdayRemainingCoin
                    )
            }

        val idlExchangeRateInToday =
            idlExchangeRates.filter(_.window >= currentWindow.withTimeAtStartOfDay().toString(YMDHMS))

        val registerUntilNow = idlExchangeRateInToday.map(_.register).sum
        val withdrawRMBUntilNow = idlExchangeRateInToday.map(_.withdrawRMB).sum
        val dailyRemainingRMB =
            registerUntilNow * calculatePriceByDay(currentWindow, onlineDate) - withdrawRMBUntilNow

        val dailyRemainingCoin = idlExchangeRateInToday.map { idlExchangeRate =>
            idlExchangeRate.moneyGain - idlExchangeRate.withdrawCoin
        }.sum

        val moneyWillingToPay = getMoneyWillingToPay(currentWindow, idlExchangeRates, onlineDate)
        val numerator = (moneyWillingToPay + remainingRMBUntilYesterDay + dailyRemainingRMB)
        val denominator = (remainingCoinUntilYesterDay + dailyRemainingCoin + WB_IN_DAY)
        numerator / denominator
    }

    def mergeValue(
        currentValue: Map[String, Double], lastValue: Map[String, Double]
    ) = {
        val register: Double =
            lastValue.get(REGISTER).getOrElse(0D) + currentValue.get(REGISTER).getOrElse(0D)

        val withdrawRMB: Double =
            lastValue.get(WITHDRAW_RMB).getOrElse(0D) + currentValue.get(WITHDRAW_RMB).getOrElse(0D)

        val moneyGain: Double =
            lastValue.get(MONEY_GAIN).getOrElse(0D) + currentValue.get(MONEY_GAIN).getOrElse(0D)

        val withdrawCoin: Double =
            lastValue.get(WITHDRAW_COIN).getOrElse(0D) + currentValue.get(WITHDRAW_COIN).getOrElse(0D)

        Map(
            REGISTER -> register,
            WITHDRAW_RMB -> withdrawRMB,
            WITHDRAW_COIN -> withdrawCoin,
            MONEY_GAIN -> moneyGain
        )
    }

    def getFromInterimExchangeRate(config: mutable.Map[String, String]): Option[(Long, Map[String, Double])] = {
        val recoverFromOpt = config.get("mysql.exchange-rate.interim.table.recover.point")
        val interimExchangeRateTable: String = config("mysql.exchange-rate.interim.table")
        val mysqlConfig = getMysqlConfig(config.toMap)

        val windowClause =
            recoverFromOpt match {
                case Some(recoverPoint) =>
                    // 避免后续再次读取
                    config.remove("mysql.exchange-rate.interim.table.recover.point")
                    logger.info("Remove mysql.exchange-rate.interim.table.recover.point key")

                    s"""where window = ${recoverPoint} limit 1"""
                case None =>
                    """order by window desc limit 1 """
            }

        val selectSQL =
            s"""
              select
                    window,
                    total_register, total_withdraw_rmb,
                    total_money_gain, total_withdraw_coin
              from ${interimExchangeRateTable} ${windowClause}
            """

        // Fail fast or the following logic will be affected
        var mysqlSink: MysqlSink = null

        try {
            mysqlSink = new MysqlSink(mysqlConfig)

            val resultToExchangeRate = (rs: ResultSet) => {
                var totalValue: Option[(Long, Map[String, Double])] = None

                if (rs.next()) {
                    val window = rs.getTimestamp(1).getTime
                    val totalRegister = rs.getDouble(2)
                    val totalWithDrawRMB = rs.getDouble(3)
                    val totalMoneyGain = rs.getDouble(4)
                    val totalWithDrawCoin = rs.getDouble(5)

                    totalValue =
                            Some(window ->
                                    Map(
                                        REGISTER -> totalRegister,
                                        WITHDRAW_RMB -> totalWithDrawRMB,
                                        MONEY_GAIN -> totalMoneyGain,
                                        WITHDRAW_COIN -> totalWithDrawCoin
                                    )
                            )
                }

                totalValue
            }

            mysqlSink.getSimpleDataSet(selectSQL, resultToExchangeRate)
        } finally {
            Option(mysqlSink).foreach { _.close() }
        }
    }

    def saveToInterimExchangeRate(
        window: Long, value: Map[String, Double], config: mutable.Map[String, String]
    ) = {
        val exchangeRateInterimTable: String = config("mysql.exchange-rate.interim.table")

        val formattedWindow = new DateTime(window).toString(YMDHMS)
        val columns = List(
            "window", "total_register",
            "total_withdraw_rmb", "total_money_gain",
            "total_withdraw_coin"
        )

        val register = value.get(REGISTER.toString).getOrElse(0D)
        val rmb = value.get(WITHDRAW_RMB.toString).getOrElse(0D)
        val moneyGain = value.get(MONEY_GAIN.toString).getOrElse(0D)
        val withdrawCoin = value.get(WITHDRAW_COIN.toString).getOrElse(0D)

        val rowIterator: Iterator[List[AnyRef]] = Iterator(List(
            formattedWindow,
            register: java.lang.Double, rmb: java.lang.Double,
            moneyGain: java.lang.Double, withdrawCoin: java.lang.Double
        ))

        val insertConfig = Map(config.toList: _*) + ("output.table" -> exchangeRateInterimTable)
        MySqlSink.retriableInsertOrUpdate(
            insertConfig, columns,
            rowIterator, columns.tail
        )
    }

    def saveExchangeRate(window: Long, config: mutable.Map[String, String]) = {
        val windowTime = new DateTime(window)
        val exchangeRateTable = config("mysql.exchange-rate.table")

        val mysqlSink = new MysqlSink(getMysqlConfig(config.toMap))
        val exchangeRate: Double = calculateExchangeRate(windowTime, config, mysqlSink)

        val rowIterator: Iterator[List[AnyRef]] = Iterator(List(
            windowTime.toString(YMD), windowTime.toString(YMDHMS), exchangeRate: java.lang.Double
        ))

        val insertConfig = Map(config.toList: _*) + ("output.table" -> exchangeRateTable)
        MySqlSink.retriableInsertOrUpdate(
            insertConfig, List("fds", "window", "exchange_rate"),
            rowIterator, "exchange_rate" :: Nil
        )
    }

    def convertEventRDD(
        window: Long, rdd: RDD[(Long, String, Double)]
    ): RDD[(Long, Map[String, Double])] = {
        if (rdd.isEmpty) {
            val defaultBatchValue =
                DateUtils.floor(window, DISPLAY_WINDOW) -> Map(
                    REGISTER -> 0.0, WITHDRAW_RMB -> 0.0,
                    WITHDRAW_COIN -> 0.0, MONEY_GAIN -> 0.0
                )
            rdd.sparkContext.makeRDD(defaultBatchValue :: Nil)
        } else {
            rdd.map {
                case (eventTime, eventType, value) =>
                    DateUtils.floor(eventTime, DISPLAY_WINDOW) -> Map(eventType -> value)
            }.reduceByKey {
                case (prev, next) =>
                    val register: Double =
                        prev.get(REGISTER).getOrElse(0D) + next.get(REGISTER).getOrElse(0D)

                    val withdrawRMB: Double =
                        prev.get(WITHDRAW_RMB).getOrElse(0D) + next.get(WITHDRAW_RMB).getOrElse(0D)

                    val moneyGain: Double =
                        prev.get(MONEY_GAIN).getOrElse(0D) + next.get(MONEY_GAIN).getOrElse(0D)

                    val withdrawCoin: Double =
                        prev.get(WITHDRAW_COIN).getOrElse(0D) + next.get(WITHDRAW_COIN).getOrElse(0D)

                    Map(
                        REGISTER -> register,
                        WITHDRAW_RMB -> withdrawRMB,
                        WITHDRAW_COIN -> withdrawCoin,
                        MONEY_GAIN -> moneyGain
                    )
            }
        }
    }

    def stateUpdateAndDumpExchangeRate(
        currentValueByWindow: List[(Long, Map[String, Double])],
        config: mutable.Map[String, String]
    ) = {
        var lastWindowAndValueOpt = getFromInterimExchangeRate(config)

        currentValueByWindow.foreach {
            case (currentWindow, currentValue) =>
                if (lastWindowAndValueOpt.isEmpty) {
                    // 初始的可能为空
                    saveToInterimExchangeRate(currentWindow, currentValue, config)
                    lastWindowAndValueOpt = Some(currentWindow -> currentValue)
                } else {
                    val (lastWindow, lastValue) = lastWindowAndValueOpt.get

                    if (currentWindow >= lastWindow + DISPLAY_WINDOW) {
                        // 防止出现数据出现断点 15:10, 15:20(中间缺了15:05)
                        val gap = (currentWindow - lastWindow) / DISPLAY_WINDOW

                        (0 until gap.toInt).map { num =>
                            lastWindow + num * DISPLAY_WINDOW
                        }.foreach { window =>
                            saveExchangeRate(window, config)
                        }

                        // 保存状态
                        saveToInterimExchangeRate(currentWindow, currentValue, config)
                        lastWindowAndValueOpt = Some(currentWindow -> currentValue)
                    } else {
                        // 合并状态后, 并保存
                        saveToInterimExchangeRate(lastWindow, mergeValue(currentValue, lastValue), config)
                    }
                }
        }
    }

    def formatCurrentWindowAndValue(currentValueByWindow: List[(Long, Map[String, Double])]) = {
        if (logger.isDebugEnabled()) {
            currentValueByWindow.foreach {
                case (window, value) =>
                    logger.info(s"Window: ${DateUtils.getYMDHMS.format(window)}, value: ${value}")
            }
        }
    }

    def main(args: Array[String]) = {
        if (args.length < 1) {
            throw new IllegalArgumentException("configuration path is missing")
        }

        val config: mutable.Map[String, String] = ConfigUtils.loadFromFile(args(0))

        val bootstrapServers = config("bootstrap.servers")

        val kafkaSuperTopic = config("kafka.super.topic")
        val kafkaMediumTopic = config("kafka.medium.topic")
        val kafkaMoneyGainTopic = config("kafka.money.gain.topic")
        val kafkaLogStartOpt = config.get("kafka.log.start").map(_.toLong)

        val topics = List(kafkaSuperTopic, kafkaMediumTopic, kafkaMoneyGainTopic).mkString(COMMA)
        val kafkaTopicGroup = config("kafka.topic.group")

        val sparkConf = new SparkConf()

        val sparkContext = new SparkContext(sparkConf)
        val duration = Duration(sparkConf.get(SPARK_STREAMING_DURATION).toLong)
        val streamingContext = new StreamingContext(sparkContext, duration)

        var startTime = 0L
        var offsetRanges = Array[OffsetRange]()

        val consumerRecordStream =
            KingnetInputDStream.createDirectKafkaStream[String, String](
                streamingContext,
                getKafkaParams(bootstrapServers, topics, kafkaTopicGroup)
            ).transform((rdd, time) => {
                if (startTime == 0L) {
                    startTime = time.milliseconds
                }
                offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
                rdd
            }).flatMap { record =>
                parseConsumerRecord(record, kafkaSuperTopic, kafkaMediumTopic, kafkaMoneyGainTopic)
            }.filter { record =>
                kafkaLogStartOpt.isEmpty || kafkaLogStartOpt.exists { kafkaLogStart =>
                    record._1 >= kafkaLogStart
                }
            }

        consumerRecordStream.foreachRDD { (rdd, batchTime) =>
           val currentValueByWindow =
                convertEventRDD(batchTime.milliseconds, rdd).collect().toList.sortWith {
                    case (prev, next) => prev._1 < next._1
                }

            formatCurrentWindowAndValue(currentValueByWindow)

            stateUpdateAndDumpExchangeRate(currentValueByWindow, config)

            // 如果是2min, 00:08 offset --> 00:05的interim table
            KafkaOffsetUtil.periodicSaveOffsetToMysql(
                sparkConf, kafkaTopicGroup, startTime,
                batchTime.milliseconds, offsetRanges
            )
        }

        streamingContext.start()
        streamingContext.awaitTermination()
    }

}