package com.kingnetdc.offline.utils

import com.kingnetdc.offline.UnitSpecs
import com.kingnetdc.offline.job.FollowPageRecommend
import org.joda.time.DateTime

/**
 * Created by zhouml on 23/06/2018.
 */
class FollowPageRecommendSuite extends UnitSpecs {

    val fixture = {
        new {
            val tableName = "wutiao.adl_wutiao_itemid_realtime"
        }
    }

    "topViewedSQL" should "pass check" in {
        import fixture._

        val morning = new DateTime(2018, 6, 23, 0, 20)
        FollowPageRecommend.topViewedSQL(morning, tableName, 1000, 7)

        val expectedOne =
            """
            select
                itemid, sum(read_cnt) as total_read
            from
                wutiao.adl_wutiao_itemid_realtime
            where
                (`ds` >= '2018-06-16' and `ds` <= '2018-06-22' and `hour` = '99') and itemid is not null
            group by
                itemid
            order by
                total_read desc
            limit 1000
            """

        val normalHour = new DateTime(2018, 6, 23, 1, 20)
        FollowPageRecommend.topViewedSQL(normalHour, tableName, 1000, 7)

        val expectedTwo =
            """
             select
                  itemid, sum(read_cnt) as total_read
              from
                  wutiao.adl_wutiao_itemid_realtime
              where
                  ((`hour` >= '2018062300' and `hour` < '2018062301') or (`ds` >= '2018-06-17' and `ds` <= '2018-06-22' and `hour` = '99') or (`hour` >= '2018061601' and `hour` < '2018061700')) and itemid is not null
              group by
                  itemid
              order by
                  total_read desc
              limit 1000
            """
    }

    "topLikedOrCommentedSQL" should "pass check" in {
        import fixture._

        val morning = new DateTime(2018, 6, 23, 0, 20)
        FollowPageRecommend.topLikedOrCommentedSQL(morning, tableName, 24)
    }

}
