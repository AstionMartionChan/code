package com.kingnetdc.metrics

import com.kingnetdc.model.{AggregatorMergeStrategy, WindowDisplayMode, KPIRecord}
import com.kingnetdc.sql.ComputationRule
import com.kingnetdc.watermelon.utils.{Logging, DateUtils}
import it.unimi.dsi.fastutil.objects.{ReferenceArrayList => ScalaMutableList}
import scala.collection.JavaConversions._


/*
   关于时间参数的说明:
   eventTime: 事件时间, 原始日志中的时间字段, 一般为timestamp之类的
   calculationDuration: 统计区间所代表的毫秒数, 比如说30s ~ 30000, 1min ~ 60000
   windowStart: 一般指的是事件时间映射到统计区间的开始时间

   eventTime 2018-05-02 18:30:23
   calculationDuration 30s
   windowStart 2018-05-02 18:30:00

underlying      5min -- Map[Long, Aggregator]  ---         2018-05-02 18:05:00 -> Aggregator
                                                           2018-05-02 18:10:00 -> Aggregator
                                                           2018-05-02 18:15:00 -> Aggregator
                                                           .....
underlying      1hour -- Map[Long, Aggregator] ---         2018-05-02 18:00:00 -> Aggregator
                                                           2018-05-02 19:00:00 -> Aggregator
                                                           .....
underlying      1day  ---	Map[Long, Aggregator] ---      2018-05-02 13:00:00 -> Aggregator
                                                           2018-05-03 13:00:00 -> Aggregator
                                                           .....

*/
class MetricsBuffer(
    val calculationDuration: Long, val flushDuration: Long, val windowDisplayMode: String
) extends Serializable {

    private val underlying = new Aggregator(0L)
    private var dimensionValues: List[(String, String)] = Nil

    // visible for testing
    protected[metrics] var eventTime: Long = 0L
    protected[metrics] var flushTime: Long = 0L

    def merge(aggregator: Aggregator, rules: List[ComputationRule]): Unit = {
        underlying.mergeByMetrics(aggregator, rules)
    }

    private def export(
        dimensionValue: List[(String, String)], displayWindow: Long,
        aggregator: Aggregator, rules: List[ComputationRule]
    ): ScalaMutableList[KPIRecord] = {
        val buffer = new ScalaMutableList[KPIRecord]()

        val metricsValues = aggregator.evaluate(calculationDuration, rules).toList
        buffer.add(new KPIRecord(displayWindow, calculationDuration, dimensionValue, metricsValues))
        buffer
    }

    def filledFlushGaps(currentWindow: Long) = {
        if (WindowDisplayMode.FLUSH_TIME.toString == windowDisplayMode) {
            val gap = (currentWindow - flushTime) / flushDuration

            (0 until gap.toInt).map { num =>
                flushTime + num * flushDuration
            }
        } else {
            eventTime :: Nil
        }
    }


    /**
     *  如果是增加值的话, 需要先确认是否超時, 然后进行合并, 确保不影响上一个批次的值
     *  如果是减少值的话, 则需要先对于原来的值进行更新, 然后再判断是否超时
    *
     * @param thatAggregator 当前批次生成的aggregator
     * @param rules
     *
     * @return
     */
    @Deprecated
    def increaseOrDecrease(thatAggregator: Aggregator, rules: List[ComputationRule]): ScalaMutableList[KPIRecord] = {
        if (thatAggregator.getMergeStrategy == AggregatorMergeStrategy.Increase.toString) {
            val resultList = checkTimeoutAndFlush(thatAggregator.window, rules)
            merge(thatAggregator, rules)
            resultList
        } else {
            merge(thatAggregator, rules)
            checkTimeoutAndFlush(thatAggregator.window, rules)
        }
    }

    def checkTimeoutAndFlush(
        currentWindow: Long, rules: List[ComputationRule]
    ): ScalaMutableList[KPIRecord] = {
        val buffer = new ScalaMutableList[KPIRecord]()

        if (eventTimeout(currentWindow)) {
            val displayWindow =
                if (WindowDisplayMode.FLUSH_TIME.toString == windowDisplayMode) flushTime
                else eventTime

            if (!underlying.isEmpty) {
                buffer.addAll(export(dimensionValues, displayWindow, underlying, rules))
                underlying.clear()
            }

            setEventTime(currentWindow)
            setFlushTime(currentWindow)
        } else if (flushTimeout(currentWindow)) {
            val kpiRecords = filledFlushGaps(currentWindow).filter { _ =>
                !underlying.isEmpty
            }.flatMap { displayWindow =>
                export(dimensionValues, displayWindow, underlying, rules)
            }

            buffer.addAll(kpiRecords)
            setFlushTime(currentWindow)
        }

        buffer
    }

    def setEventTime(eventTime: Long): this.type = {
        this.eventTime = DateUtils.floor(eventTime, calculationDuration)
        this
    }

    // 只有需要累计的指标 设置这个属性
    def setFlushTime(flushTime: Long): this.type = {
        this.flushTime = DateUtils.floor(flushTime, flushDuration)
        this
    }

    def setDimensionValue(dimensionValue: List[(String, String)]): this.type = {
        this.dimensionValues = dimensionValue
        this
    }

    def getDimensionValue = dimensionValues

    def nonEmpty = !underlying.isEmpty()

    protected def eventTimeout(time: Long): Boolean = time >= this.eventTime + this.calculationDuration

    protected def flushTimeout(window: Long): Boolean = window >= this.flushTime + this.flushDuration

}
