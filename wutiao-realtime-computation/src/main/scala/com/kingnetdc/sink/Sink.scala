package com.kingnetdc.sink

import com.kingnetdc.watermelon.utils.Logging
import org.apache.spark.SparkConf
import com.kingnetdc.watermelon.utils.ConfigurationKeys._

trait Sink[T] extends Serializable with Logging {

  protected def getOutputPartition(sparkConf: SparkConf) = {
    sparkConf.getInt(SPARK_OUTPUT_PARTITION, DEFAULT_OUTPUT_PARTITION)
  }

}
