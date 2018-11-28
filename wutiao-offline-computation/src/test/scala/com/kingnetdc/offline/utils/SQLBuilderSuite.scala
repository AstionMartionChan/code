package com.kingnetdc.offline.utils

import com.kingnetdc.offline.UnitSpecs
import com.kingnetdc.offline.utils.SQLBuilder._
import org.joda.time.DateTime

/**
 * Created by zhouml on 23/06/2018.
 */
// scalastyle:off
class SQLBuilderSuite extends UnitSpecs {

    val fixture = {
        new {
            val current = new DateTime()

            val year = current.getYear
            val month = current.getMonthOfYear
            val day = current.getDayOfMonth

            // 这个点计算会有跨天问题, 需要特殊考虑
            val morning = new DateTime(year, month, day, 0, 20)

            // 一般不会在凌晨测试
            val normalHour = current
        }
    }

    "sameDay" should "pass check"  in {
        val one = new DateTime(2018, 6, 23, 0, 20)
        sameDay(one.minusHours(1), one) shouldBe false

        val other = new DateTime(2018, 6, 23, 1, 20)
        sameDay(other.minusHours(1), other) shouldBe true
    }

    "getInRecentDay" should "pass check"  in {
        import fixture._
        getInRecentDay(morning, 7) shouldBe """`ds` >= '2018-08-09' and `ds` <= '2018-08-15' and `hour` = '99'"""

        getInRecentDay(normalHour, 7) shouldBe """(`hour` >= '2018081600' and `hour` < '2018081613') or (`ds` >= '2018-08-10' and `ds` <= '2018-08-15' and `hour` = '99') or (`hour` >= '2018080913' and `hour` < '2018081000')"""
    }

    "getInRecentNaturalDay" should "pass check"  in {
        val morning = new DateTime(2018, 6, 23, 0, 20)
        println(getInRecentNaturalDay(morning, 1))
        val expectedOne = """`ds` = '2018-06-22' and `hour` = '99'"""

        println(getInRecentNaturalDay(morning, 7))
        val expectedTwo = """`ds` >= '2018-06-16' and `ds` <= '2018-06-22' and `hour` = '99'"""

        val normalHour = new DateTime(2018, 6, 23, 2, 20)
        println(getInRecentNaturalDay(normalHour, 1))
        val expectedThree ="""`hour` >= '2018062300' and `hour` < '2018062302'"""

        println(getInRecentNaturalDay(normalHour, 7))
        val expectedFour =
        """
          (
              `ds` >= '2018-06-17'
               and `ds` <= '2018-06-22'
               and `hour` = '99'
          )
          or
          (
              `hour` >= '2018062300' and `hour` < '2018062302'
          )
        """
    }

}
// scalastyle:on
