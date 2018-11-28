package com.kingnetdc.offline.utils

import com.kingnetdc.watermelon.utils.AppConstants._
import org.joda.time.DateTime

/**
 * Created by zhouml on 25/05/2018.
 */
object SQLBuilder {

    val hourFormat = "yyyyMMddHH"

    val hourPartitionName = "hour"

    val dayFormat = YMD

    val dayPartitionName = "ds"

    /**
     * 99表示日汇总
     */
    val dayTotalMark = "99"

    /**
     * 999表示截止到统计日的汇总
     */
    val historyTotalMark = "999"

    def sameDay(one: DateTime, other: DateTime) = {
        one.getYear() == other.getYear() &&
        one.getMonthOfYear() == other.getMonthOfYear() &&
        one.getDayOfMonth() == other.getDayOfMonth()
    }

    def getHistoryTotalUntilYesterday(datetime: DateTime) = {
        s"""
           `${dayPartitionName}` = '${datetime.minusDays(1).toString(YMD)}' and
           `${hourPartitionName}` = '${historyTotalMark}'
        """
    }

    /**
     *  近n天 = (n - 1)天 + 今天动态变化
     * + 每日凌晨的时候 0 ~ 1, 统计的是(n - 1)天 + 昨天一整天的
     * + 其它时间段统计的是 (n - 1)天 + 今日0 ~ 23
     *
     *  最小时间粒度为小时
     *
     * @param datetime 最近一个计算
     * @param n 近n天
     *
     * @return
     */
    def getInRecentNaturalDay(datetime: DateTime, n: Int) = {
        require(n >= 1, "N should be >= 1")

        val lastHour = datetime.minusHours(1)

        // 如果计算点的上一个小时跨天, 应该特殊考虑
        if (!sameDay(lastHour, datetime)) {
            if (n == 1) {
                s"""
                `${dayPartitionName}` = '${datetime.minusDays(1).toString(dayFormat)}'
                and `${hourPartitionName}` = '${dayTotalMark}'
                """
            } else {
                s"""
                `${dayPartitionName}` >= '${datetime.minusDays(n).toString(dayFormat)}'
                and `${dayPartitionName}` <= '${datetime.minusDays(1).toString(dayFormat)}'
                and `${hourPartitionName}` = '${dayTotalMark}'
                """
            }
        } else {
            if (n == 1) {
                s"""
                    `${hourPartitionName}` >= '${datetime.withTimeAtStartOfDay().toString(hourFormat)}'
                    and `${hourPartitionName}` < '${datetime.toString(hourFormat)}'
                 """
            } else {
                s"""
                (
                    `${dayPartitionName}` >= '${datetime.minusDays(n - 1).toString(dayFormat)}'
                     and `${dayPartitionName}` <= '${datetime.minusDays(1).toString(dayFormat)}'
                     and `${hourPartitionName}` = '${dayTotalMark}'
                )
                or
                (
                    `${hourPartitionName}` >= '${datetime.withTimeAtStartOfDay().toString(hourFormat)}'
                    and `${hourPartitionName}` < '${datetime.toString(hourFormat)}'
                )
                """
            }
        }
    }


    /**
    *  datetime - n hour <= x < datetime
    *  最小时间粒度为小时
     *
     * @param datetime
    * @param n 近n小时
     *
     * @return
    */
    def getInRecentHour(datetime: DateTime, n: Int) = {
        s"""
        `${hourPartitionName}` >= '${datetime.minusHours(n).toString(hourFormat)}'
        and
        `${hourPartitionName}` < '${datetime.toString(hourFormat)}'
        """
    }

    /**
     *  datetime - n day <= x < datetime
     *  最小时间粒度为小时
     *
     * @param datetime 进行计算的时候, 默认计算上一个小时对应的数据
     *  datetime -- 2018-06-23 10:20: 00 实际上计算的是2018062309, 代表的就是[2018062309, 2018062310) 区间的值,
     *  然后根据是需要累加 还是 算当前区间的进行 查询条件拼接
     * @param n 近n天
     *
     * @return
     */
    def getInRecentDay(datetime: DateTime, n: Int) = {
        require(n > 1, "N should be > 1")

        val lastHour = datetime.minusHours(1)

        // 如果计算点的上一个小时跨天, 应该特殊考虑
        if (!sameDay(lastHour, datetime)) {
            s"`${dayPartitionName}` >= '${datetime.minusDays(n).toString(YMD)}' and " +
            s"`${dayPartitionName}` <= '${datetime.minusDays(1).toString(YMD)}' and " +
            s"`${hourPartitionName}` = '${dayTotalMark}'"
        } else {
            List(
                s"(`${hourPartitionName}` >= '${datetime.withTimeAtStartOfDay().toString(hourFormat)}' and " +
                s"`${hourPartitionName}` < '${datetime.toString(hourFormat)}')",

                s"(`${dayPartitionName}` >= '${datetime.minusDays(n - 1).toString(YMD)}' and " +
                s"`${dayPartitionName}` <= '${datetime.minusDays(1).toString(YMD)}' and " +
                s"`${hourPartitionName}` = '${dayTotalMark}')",

                s"(`${hourPartitionName}` >= '${datetime.minusDays(n).toString(hourFormat)}' and " +
                s"`${hourPartitionName}` < '${datetime.minusDays(n - 1).withTimeAtStartOfDay().toString(hourFormat)}')"
            ).mkString(" or ")
        }
    }

}
