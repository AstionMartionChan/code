package com.kingnetdc.model

import com.kingnetdc.watermelon.utils.DateUtils
import scala.collection.mutable

/**
 * @param window       窗口时间 yyyy-MM-dd HH:mm:ss
 * @param duration     计算周期 millis
 * @param dimensionValues 维度以及相应的值List((new, 30), (old, 40))
 * @param metricsValues  指标名以及对应的值
 */
case class KPIRecord(
    val window: Long,
    val duration: Long,
    dimensionValues: List[(String, String)],
    metricsValues: List[(String, Any)]
) {

    val createdAt = System.currentTimeMillis()

    def metricNames = metricsValues.map(_._1)

    def columns = List("fds", "window", "duration") ::: dimensionValues.map(_._1) ::: metricNames

    def rowValues: List[AnyRef] = {
        List(
            DateUtils.getYMD.format(window),
            DateUtils.getYMDHMS.format(window),
            duration: java.lang.Long
        ) :::
        dimensionValues.map(_._2) :::
        metricsValues.map { metricsValue => metricsValue._2.asInstanceOf[AnyRef] }
    }

    @Deprecated
    def changeDimensionsToAll(toChangeDim: Set[String]): KPIRecord = {
        /*
        val dimMap: mutable.Map[String, String] = mutable.Map.empty
        dimensionValues.foreach { pair => {
            if (toChangeDim.contains(pair._1)) {
                dimMap.put(pair._1, "all")
            } else {
                dimMap.put(pair._1, pair._2)
            }
        }
        */
        ???
    }

}


object KPIRecord {

    def getParamsType(dimensionNames: List[String]): Map[String, String] = {
        val map: mutable.Map[String, String] = mutable.Map.empty
        map.put("window", "time")
        map.put("duration", "tag")
        map.put("index", "tag")
        dimensionNames.foreach(dim => {
            map.put(dim, "tag")
        })
        map.put("usertype", "tag")
        map.put("val", "field")
        map.toMap
    }

    def getColumns(dimensionNames: List[String]): List[String] = {
        List("fds", "window", "duration") ::: dimensionNames ::: List("metrics_name", "metrics_value")
    }

    def getParamsTypeInteract(dimensionNames: List[String]): Map[String, String] = {
        val map: mutable.Map[String, String] = mutable.Map.empty
        map.put("window", "time")
        map.put("duration", "tag")
        dimensionNames.foreach(dim => {
            map.put(dim, "tag")
        })
        map.put("type", "tag")
        map.put("usertype", "tag")
        map.put("val", "field")
        map.toMap
    }

}

