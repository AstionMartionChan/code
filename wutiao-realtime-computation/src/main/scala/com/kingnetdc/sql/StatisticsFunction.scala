package com.kingnetdc.sql

/**
  * Created by zhouml on 20/05/2018.
  */
/*
    + 计数  Long
    + 去重  Set
    + 近似去重 Set
    + 汇总  Long
    + 排名  BoundedQueue
*/
object StatisticsFunction extends Enumeration {

  val SUM = Value(1, "sum")
  val COUNT = Value(2, "count")
  val COUNT_DISTINCT = Value(3, "count_distinct")
  val APPROX_COUNT_DISTINCT = Value(4, "approx_count_distinct")
  val TOP_N = Value(8, "top_n")

  def summaryCategory = SUM :: Nil

  def countingCategory = COUNT :: Nil

  def deduplicationCategory = COUNT_DISTINCT :: APPROX_COUNT_DISTINCT :: Nil

  def rankCategory = TOP_N :: Nil

}
