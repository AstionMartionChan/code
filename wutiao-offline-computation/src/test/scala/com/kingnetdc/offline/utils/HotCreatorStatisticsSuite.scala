package com.kingnetdc.offline.utils

import com.kingnetdc.offline.UnitSpecs
import com.kingnetdc.offline.job.HotCreatorStatistics
import org.joda.time.DateTime

/**
 * Created by zhouml on 23/06/2018.
 */
class HotCreatorStatisticsSuite extends UnitSpecs {

    val fixture = {
        new {
            val tableName = "wutiao.adl_wutiao_money_realtime"
        }
    }

    "rankByMoneyDistributeSQL" should "pass check" in {
        import fixture._
        val current = new DateTime
        val morning = new DateTime(current.getYear(), current.getMonthOfYear(), current.getDayOfMonth, 0, 20)

        HotCreatorStatistics.rankByMediaTypeMoneyGainSQL(morning, tableName, 1000, 7)

        val expectedOne =
        """
             select
                 uid, sum(creative_money_gain) as total
             from wutiao.adl_wutiao_money_realtime
             where
                 (`ds` >= '2018-08-09' and `ds` <= '2018-08-15' and `hour` = '99') and
                  uid is not null
                  and uid <> '0'
                  and creative_money_gain > 0
              group by uid
              order by total desc, uid asc
        """

        val normalHour = current
        HotCreatorStatistics.rankByMediaTypeMoneyGainSQL(normalHour, tableName, 1000, 7)

        val expectedTwo =
        """
             select
                 uid, sum(creative_money_gain) as total
             from wutiao.adl_wutiao_money_realtime
             where
                 ((`hour` >= '2018081600' and `hour` < '2018081614') or (`ds` >= '2018-08-10' and `ds` <= '2018-08-15' and `hour` = '99') or (`hour` >= '2018080914' and `hour` < '2018081000')) and
                  uid is not null
                  and uid <> '0'
                  and creative_money_gain > 0
              group by uid
              order by total desc, uid asc
        """
    }

}
