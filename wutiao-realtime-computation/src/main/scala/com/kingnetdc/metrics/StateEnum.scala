package com.kingnetdc.metrics

object StateEnum extends Enumeration {

    /**
      * 活跃，新增，老用户|设备
      * 因为各个统计区间(5min, 1h, 1day)对于新|老的计算不能简单的累加, 所以需要单独区分
      */
    val NEW = Value(1, "new")

    val OLD = Value(2, "old")

    val ACTIVE = Value(3, "active")

    val ALL = Value(4, "all")

}
