package com.kingnetdc.offline.utils

import com.kingnetdc.offline.UnitSpecs
import com.kingnetdc.offline.job.WalletRankStatistics
import com.kingnetdc.offline.utils.SQLBuilder._
import org.joda.time.DateTime

/**
 * Created by zhouml on 23/06/2018.
 */
class WalletRankStatisticsSuite extends UnitSpecs {

    val fixture = {
        new {
            val config1: Map[String, String] = Map(
                "source.table" -> "wutiao.adl_wutiao_money_realtime",
                "excluded.uids" -> "2127118,2161212,2161214"
            )

            val config2: Map[String, String] = Map(
                "source.table" -> "wutiao.adl_wutiao_money_realtime"
            )
        }
    }

    "getSummarySQL" should "pass check" in {
        import fixture._
        val current = new DateTime

        val morning = new DateTime(current.getYear(), current.getMonthOfYear(), current.getDayOfMonth, 0, 20)
        WalletRankStatistics.getSummarySQL(config1, getInRecentNaturalDay(morning, 1))

        val expectedOne =
        """
         select
              uid, total_money_gain,
              row_number() over (order by total_money_gain desc, uid asc) as rank
          from (
              select uid, sum(money_gain) as total_money_gain
              from wutiao.adl_wutiao_money_realtime
              where
                  (
          `ds` = '2018-06-22'
          and `hour` = '99'
          ) and uid is not null and uid <> '0'
              group by uid
              having total_money_gain > 0
          ) temp
        """

        val normalHour = new DateTime(current.getYear(), current.getMonthOfYear(), current.getDayOfMonth, 2, 20)
        WalletRankStatistics.getSummarySQL(config1, getInRecentNaturalDay(normalHour, 1))

        val expectedTwo =
        """
            select
              uid, sum(money_gain) as total_money_gain
            from wutiao.adl_wutiao_money_realtime
            where
              (
              `hour` >= '2018062500'
              and `hour` < '2018062502'
            ) and uid is not null and uid <> '0'
            group by uid
            having total_money_gain > 0
            order by total_money_gain desc, uid asc
        """

        WalletRankStatistics.getSummarySQL(config1, getHistoryTotalUntilYesterday(current))


        WalletRankStatistics.getSummarySQL(config1, getInRecentNaturalDay(current, 1))
        
        val expectedThree =
            """
              select
                  uid, sum(money_gain - creative_money_gain) as total_money_gain
              from wutiao.adl_wutiao_money_realtime
              where
                  (
                  `hour` >= '2018090300'
                  and `hour` < '2018090302'
               ) and
                  uid is not null and
                  uid <> '0' and uid not in ('2127118','2161212','2161214')
              group by uid
              having total_money_gain > 0
              order by total_money_gain desc, uid asc
            """


        WalletRankStatistics.getSummarySQL(config2, getInRecentNaturalDay(current, 1))

        val expectedFour =
            """
              select
                  uid, sum(money_gain - creative_money_gain) as total_money_gain
              from wutiao.adl_wutiao_money_realtime
              where
                  (
                  `hour` >= '2018090300'
                  and `hour` < '2018090302'
               ) and
                  uid is not null and
                  uid <> '0'
              group by uid
              having total_money_gain > 0
              order by total_money_gain desc, uid asc
            """
    }



}



