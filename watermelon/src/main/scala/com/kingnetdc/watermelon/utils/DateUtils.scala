package com.kingnetdc.watermelon.utils

import java.text.{DateFormat, SimpleDateFormat}
import java.util.Date
import com.kingnetdc.watermelon.utils.AppConstants._

object DateUtils {

    private val threadLocalYMDFormat = new ThreadLocal[DateFormat] {
        override def initialValue(): DateFormat = {
            new SimpleDateFormat(YMD)
        }
    }

    private val threadLocalYMDHMSFormat = new ThreadLocal[DateFormat] {
        override def initialValue(): DateFormat = {
            new SimpleDateFormat(YMDHMS)
        }
    }

    private val threadLocalYMDHMFormat = new ThreadLocal[DateFormat] {
        override def initialValue(): DateFormat = {
            new SimpleDateFormat(YMDHM)
        }
    }

    private val threadLocalYMDHMSMILLISFormat = new ThreadLocal[DateFormat] {
        override def initialValue(): DateFormat = {
            new SimpleDateFormat(YMDHMS_MILLIS)
        }
    }

    private val threadLocalHFormat = new ThreadLocal[DateFormat] {
        override def initialValue(): DateFormat = {
            new SimpleDateFormat(H)
        }
    }

    def getYMD = threadLocalYMDFormat.get

    def getYMDHMS = threadLocalYMDHMSFormat.get

    def getYMDHM = threadLocalYMDHMFormat.get

    def getH = threadLocalHFormat.get

    def getYMDHMSMILLIS = threadLocalYMDHMSMILLISFormat.get

    /*
        if the duration is 5 min
        2018-04-27 11:04:00 will be 2018-04-27 11:00:00
        2018-04-27 11:05:13 will be 2018-04-27 11:05:00

        2018-04-27 11:06:13 will be 2018-04-27 11:00:00

        new Date(0L)
        res0: java.util.Date = Thu Jan 01 08:00:00 CST 1970
        天的开始并不是再零点, 所以要减去8小时
    */
    private val oneDayInMillis = 1000 * 60 * 60 * 24L
    private val eightHourInMillis = oneDayInMillis / 3

    def floor(timeInMillis: Long, durationInMillis: Long): Long = {
        if (durationInMillis >= oneDayInMillis) {
            (
                math.floor((timeInMillis + eightHourInMillis).toDouble / durationInMillis)
            ).toLong * durationInMillis - eightHourInMillis
        } else {
            (math.floor(timeInMillis.toDouble / durationInMillis)).toLong * durationInMillis
        }
    }

    def longToDateOption(date: Long) = try Some(new Date(date)) catch {
        case _: Exception => None
    }

    def toLongOption(number: String) = try Some(number.trim.toLong) catch {
        case _: Exception => None
    }

    def toDateOption(number: String) =
        try {
            toLongOption(number).map(new Date(_))
        } catch {
            case _: Exception => None
        }

}
