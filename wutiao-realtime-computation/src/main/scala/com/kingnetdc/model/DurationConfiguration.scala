package com.kingnetdc.model


object WindowDisplayMode extends Enumeration {

    // 按事件时间对应的窗口来展示
    val EVENT_TIME = Value(1, "event_time")

    // 按刷新时间对应的窗口来展示
    val FLUSH_TIME = Value(2, "flush_time")

}


case class DurationConfiguration(calculationDuration: Long, flushDuration: Long, windowDisplayMode: String)

