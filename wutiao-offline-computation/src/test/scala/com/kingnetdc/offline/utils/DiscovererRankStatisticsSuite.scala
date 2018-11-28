package com.kingnetdc.offline.utils

import com.kingnetdc.offline.UnitSpecs
import com.kingnetdc.offline.job.DiscovererRankStatistics
import org.joda.time.DateTime

/**
 * Created by zhouml on 23/06/2018.
 */
class DiscovererRankStatisticsSuite extends UnitSpecs {

    val fixture = {
        new {
            val tableName = "wutiao.adl_wutiao_discover_moneygain_rank_realtime"
        }
    }

    "getInRecentSummarySQL" should "pass check" in {
        import fixture._
        val current = new DateTime
        val morning = new DateTime(current.getYear(), current.getMonthOfYear(), current.getDayOfMonth, 0, 20)

        val expectedOne =
        s"""
           select
               uid,
               sum(nvl(discover_money_gain, 0)) as discover_money_gain,
               sum(nvl(verifypoint, 0)) as verifypoint,
               sum(nvl(vote_cnt, 0)) as vote_cnt
           from wutiao.adl_wutiao_discover_moneygain_rank_realtime
           where
           (
                `ds` >= '2018-06-18'
                and `ds` <= '2018-06-24'
                and `hour` = '99'
           ) and uid is not null and uid <> '0'
           group by uid
           having discover_money_gain > 0
           order by discover_money_gain desc, uid asc
        """

        val normalHour = new DateTime(current.getYear(), current.getMonthOfYear(), current.getDayOfMonth, 2, 20)

        val expectedTwo =
        """
         select * from (
             select
                 uid, discover_money_gain,
                 verifypoint, vote_cnt,
                 row_number() over(order by discover_money_gain desc) as rank
             from (
                     select
                          uid,
                          sum(nvl(discover_money_gain, 0)) as discover_money_gain,
                          sum(nvl(verifypoint, 0)) as verifypoint,
                          sum(nvl(vote_cnt, 0)) as vote_cnt
                     from wutiao.adl_wutiao_discover_moneygain_rank_realtime
                     where
                      (
          (
              `ds` >= '2018-06-17'
               and `ds` <= '2018-06-22'
               and `hour` = '99'
          )
          or
          (
              `hour` >= '2018062300'
              and `hour` < '2018062302'
          )
          ) and uid is not null and uid <> '0'
                     group by uid
                     having discover_money_gain > 0
              ) temp
         ) temp1
        """
    }

    "getSummarySQL" should "pass check" in {
        import fixture._
        val morning = new DateTime(2018, 6, 23, 0, 20)
        DiscovererRankStatistics.getSummarySQL(morning, tableName)

        val verifyPointSQL1 =
        """
          select
            uid, sum(nvl(verifypoint, 0)) as verifypoint
          from
              wutiao.adl_wutiao_discover_moneygain_rank_realtime
          where
              uid is not null and
              uid <> '0' and
              (
                 `ds` >= '2018-05-24' and
                 `ds` <= '2018-06-22' and
                 `hour` = '99'
               )
          group by uid
        """

        val moneyGain1 =
         """
         select
              uid,
              sum(nvl(discover_money_gain, 0)) as discover_money_gain,
              sum(nvl(vote_cnt, 0)) as vote_cnt
         from
              wutiao.adl_wutiao_discover_moneygain_rank_realtime
         where
              `ds` = '2018-06-22' and
              `hour` = '999' and
              uid is not null and
              uid <> '0'
         group by uid
         having discover_money_gain > 0
         """

        val joinSQL =
        """
        select * from (
             select
                l.uid, l.discover_money_gain,
                nvl(r.verifypoint, 0) as verifypoint, l.vote_cnt,
                row_number() over (order by l.discover_money_gain desc, r.uid asc) as rank
             from
                  (
         select
              uid,
              sum(nvl(discover_money_gain, 0)) as discover_money_gain,
              sum(nvl(vote_cnt, 0)) as vote_cnt
         from
              wutiao.adl_wutiao_discover_moneygain_rank_realtime
         where

        `ds` = '2018-06-22' and
        `hour` = '999'
        and
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
              wutiao.adl_wutiao_discover_moneygain_rank_realtime
          where
              uid is not null and
              uid <> '0' and
              (
         `ds` >= '2018-05-24' and
         `ds` <= '2018-06-22' and
         `hour` = '99'
        )
          group by uid
        ) r
              on l.uid = r.uid
         )
        """

        val normalHour = new DateTime(2018, 6, 23, 1, 20)
        DiscovererRankStatistics.getSummarySQL(normalHour, tableName)

        val verifyPointSQL2 =
        """
          select
            uid, sum(nvl(verifypoint, 0)) as verifypoint
          from
              wutiao.adl_wutiao_discover_moneygain_rank_realtime
          where
              uid is not null and
              uid <> '0' and
              (
                 `ds` >= '2018-05-25' and
                 `ds` <= '2018-06-22' and
                 `hour` = '99'
               )
          group by uid
        """
    }


}
