package com.kingnetdc.utils

import org.apache.spark.Partitioner

class Tuple2Partitioner(partitions: Int) extends Partitioner {

  def nonNegativeMod(x: Int, mod: Int): Int = {
    val rawMod = x % mod
    rawMod + (if (rawMod < 0) mod else 0)
  }

  override def numPartitions: Int = partitions

  override def getPartition(key: Any): Int = {
    val realKey = key.asInstanceOf[(Any, Any)]._1

    realKey match {
      case null => 0
      case _ => nonNegativeMod(realKey.hashCode, numPartitions)
    }
  }

}
