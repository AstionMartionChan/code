package com.kingnetdc.offline.utils

import com.kingnetdc.offline.UnitSpecs
import com.kingnetdc.offline.job.FamilyContributionRankStatistics
import org.joda.time.DateTime

/**
 * Created by zhouml on 25/06/2018.
 */
class FamilyContributionRankStatisticsSuite extends UnitSpecs {

    val fixture = {
        new {
            val tableName = "wutiao.adl_wutiao_family_contribute_realtime"
        }
    }

    "getDateCondition" should "pass check" in {
        import fixture._

        val current = new DateTime

        val morning = new DateTime(current.getYear(), current.getMonthOfYear(), current.getDayOfMonth, 0, 20)
        FamilyContributionRankStatistics.getDateCondition(morning)
        val expectedOne =
        """
          `ds` = 2018-06-22 and `hour` =  2018062223
        """

        val normalHour = new DateTime(current.getYear(), current.getMonthOfYear(), current.getDayOfMonth, 15, 57)
        FamilyContributionRankStatistics.getDateCondition(normalHour)

        val expectedTwo =
        """
           (`ds` = 2018-06-22 and `hour` = '29day') or ( `hour` >= 2018062300 and `hour` < 2018062302)
        """

        FamilyContributionRankStatistics.getFamilyContributionSQL(normalHour, tableName, 100)
        
        val expectedThree =
        """
        select * from (
           select
              familyid, family_member_count, total_contributionpoint,
              row_number() over(order by total_contributionpoint desc) as rank
           from (
               select
                  familyid,
                  count(distinct ouid) as family_member_count,
                  sum(contributionpoint) as total_contributionpoint
               from
                 wutiao.adl_wutiao_family_contribute_realtime
               where
                 (
         (`ds` = 2018-06-22 and `hour` = '29day') or
         (
             `hour` >= 2018062300 and
             `hour` < 2018062302
         )
        ) and
                 ouid is not null and
                 ouid <> '0'
              group by familyid
              having total_contributionpoint > 0
          ) temp
        ) temp1 where rank <= 100
        """
    }


}
