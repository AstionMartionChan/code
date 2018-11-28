package com.kingnetdc.metrics

import com.kingnetdc.UnitSpecs
import org.joda.time.DateTime

/**
 * Created by zhouml on 27/07/2018.
 */
class MetricsBufferSuite extends UnitSpecs {

    val fiveMin: Long = 1000 * 60 * 5

    val oneHour: Long = 1000 * 3600

    val oneDay: Long = 1000 * 3600 * 24


    val fixture1 = {
        new {
            val time1 = new DateTime(2018, 7, 26, 23, 45).getMillis
            val time2 = new DateTime(2018, 7, 26, 23, 0).getMillis
            val time3 = new DateTime(2018, 7, 26, 0, 0).getMillis

            val minuteMetricsBuffer =
                new MetricsBuffer(fiveMin, fiveMin, "event_time")
                    .setEventTime(time1)
                    .setFlushTime(time1)

            val hourMetricsBuffer =
                new MetricsBuffer(oneHour, fiveMin, "event_time")
                    .setEventTime(time2)
                    .setFlushTime(time1)

            val dayMetricsBuffer =
                new MetricsBuffer(oneDay, fiveMin, "flush_time")
                    .setEventTime(time3)
                    .setFlushTime(time1)
        }
    }

    val fixture2 = {
        new {
            val time1 = new DateTime(2018, 7, 26, 22, 45).getMillis
            val time2 = new DateTime(2018, 7, 26, 22, 0).getMillis
            val time3 = new DateTime(2018, 7, 26, 0, 0).getMillis

            val minuteMetricsBuffer =
                new MetricsBuffer(fiveMin, fiveMin, "event_time")
                        .setEventTime(time1)
                        .setFlushTime(time1)

            val hourMetricsBuffer =
                new MetricsBuffer(oneHour, fiveMin, "event_time")
                        .setEventTime(time2)
                        .setFlushTime(time1)

            val dayMetricsBuffer =
                new MetricsBuffer(oneDay, fiveMin, "flush_time")
                        .setEventTime(time3)
                        .setFlushTime(time1)
        }
    }

    "fixture1" should "pass check" in {
        import fixture1._
        val currentWindow1 = new DateTime(2018, 7, 27, 0, 5).getMillis

        minuteMetricsBuffer.filledFlushGaps(currentWindow1).map { millis =>
            new DateTime(millis).toString("yyyy-MM-dd HH:mm:ss")
        } shouldBe List(
            "2018-07-26 23:45:00",
            "2018-07-26 23:50:00",
            "2018-07-26 23:55:00",
            "2018-07-27 00:00:00"
        )

        hourMetricsBuffer.filledFlushGaps(currentWindow1).map { millis =>
            new DateTime(millis).toString("yyyy-MM-dd HH:mm:ss")
        } shouldBe List(
            "2018-07-26 23:00:00"
        )

        dayMetricsBuffer.filledFlushGaps(currentWindow1).map { millis =>
            new DateTime(millis).toString("yyyy-MM-dd HH:mm:ss")
        } shouldBe List(
            "2018-07-26 23:45:00",
            "2018-07-26 23:50:00",
            "2018-07-26 23:55:00",
            "2018-07-27 00:00:00"
        )
    }

    "fixture2" should "pass check" in {
        import fixture2._
        val currentWindow1 = new DateTime(2018, 7, 27, 0, 5).getMillis

        minuteMetricsBuffer.filledFlushGaps(currentWindow1).map { millis =>
            new DateTime(millis).toString("yyyy-MM-dd HH:mm:ss")
        } shouldBe {
            List(
                "2018-07-26 22:45:00",
                "2018-07-26 22:50:00",
                "2018-07-26 22:55:00",
                "2018-07-26 23:00:00",
                "2018-07-26 23:05:00",
                "2018-07-26 23:10:00",
                "2018-07-26 23:15:00",
                "2018-07-26 23:20:00",
                "2018-07-26 23:25:00",
                "2018-07-26 23:30:00",
                "2018-07-26 23:35:00",
                "2018-07-26 23:40:00",
                "2018-07-26 23:45:00",
                "2018-07-26 23:50:00",
                "2018-07-26 23:55:00",
                "2018-07-27 00:00:00"
            )
        }

        hourMetricsBuffer.filledFlushGaps(currentWindow1).map { millis =>
            new DateTime(millis).toString("yyyy-MM-dd HH:mm:ss")
        } shouldBe List(
            "2018-07-26 22:00:00",
            "2018-07-26 23:00:00"
        )

        dayMetricsBuffer.filledFlushGaps(currentWindow1).map { millis =>
            new DateTime(millis).toString("yyyy-MM-dd HH:mm:ss")
        } shouldBe List(
            "2018-07-26 22:45:00",
            "2018-07-26 22:50:00",
            "2018-07-26 22:55:00",
            "2018-07-26 23:00:00",
            "2018-07-26 23:05:00",
            "2018-07-26 23:10:00",
            "2018-07-26 23:15:00",
            "2018-07-26 23:20:00",
            "2018-07-26 23:25:00",
            "2018-07-26 23:30:00",
            "2018-07-26 23:35:00",
            "2018-07-26 23:40:00",
            "2018-07-26 23:45:00",
            "2018-07-26 23:50:00",
            "2018-07-26 23:55:00",
            "2018-07-27 00:00:00"
        )
    }


}
