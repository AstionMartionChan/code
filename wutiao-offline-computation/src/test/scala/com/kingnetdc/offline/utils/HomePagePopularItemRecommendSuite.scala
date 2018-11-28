package com.kingnetdc.offline.utils

import com.kingnetdc.offline.UnitSpecs
import com.kingnetdc.offline.job.HomePagePopularItemRecommend
import org.joda.time.DateTime

/**
 * Created by zhouml on 23/06/2018.
 */
class HomePagePopularItemRecommendSuite extends UnitSpecs {

    val fixture = {
        new {
            val tableName = "wutiao.adl_wutiao_itemid_realtime"
        }
    }

    "topViewedByItemtypeSQL" should "pass check" in {
        import fixture._

        val morning = new DateTime(2018, 6, 23, 0, 20)
        HomePagePopularItemRecommend.topViewedByItemtypeSQL(morning, tableName, 1000, 7)

        val expectedOne =
        """
         select itemtype, itemid, total_read, rank from (
             select itemtype, itemid, total_read, row_number() over (partition by itemtype order by total_read desc) as rank from (
                 select itemtype, itemid, sum(read_cnt) as total_read
                 from wutiao.adl_wutiao_itemid_realtime
                 where
                      itemtype in ('1', '2') and
                      itemid is not null and
                      (`ds` >= '2018-06-16' and `ds` <= '2018-06-22' and `hour` = '99')
                 group by itemtype, itemid
             ) temp
         ) temp1 where rank <= 1000
        """

        val normalHour = new DateTime(2018, 6, 23, 1, 20)
        HomePagePopularItemRecommend.topViewedByItemtypeSQL(normalHour, tableName, 1000, 7)

        val expectedTwo =
        """
         select itemtype, itemid, total_read, rank from (
             select itemtype, itemid, total_read, row_number() over (partition by itemtype order by total_read desc) as rank from (
                 select itemtype, itemid, sum(read_cnt) as total_read
                 from wutiao.adl_wutiao_itemid_realtime
                 where
                      itemtype in ('1', '2') and
                      itemid is not null and
                      ((`hour` >= '2018062300' and `hour` < '2018062301') or (`ds` >= '2018-06-17' and `ds` <= '2018-06-22' and `hour` = '99') or (`hour` >= '2018061601' and `hour` < '2018061700'))
                 group by itemtype, itemid
             ) temp
         ) temp1 where rank <= 1000
        """
    }

    "topLikedOrCommentedSQL" should "pass check" in {
        import fixture._

        val morning = new DateTime(2018, 6, 23, 0, 20)
        HomePagePopularItemRecommend.topLikedOrCommentedSQL(morning, tableName, 24)

        val expectedOne =
            s"""
            select
                itemtype, itemid, (sum(like_cnt) + sum(comment_cnt)) / 2 as total_like_comment_score
                from wutiao.adl_wutiao_itemid_realtime
            where
                `hour` >= 2018062200
            and
                `hour` < 2018062300
            and
                itemtype in ('1', '2')
            and
                itemid is not null
            group by
                itemtype, itemid
            """
    }

}
