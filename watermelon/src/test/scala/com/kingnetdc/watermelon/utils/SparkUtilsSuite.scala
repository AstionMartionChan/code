package com.kingnetdc.watermelon.utils

import com.kingnetdc.watermelon.UnitSpecs
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by zhouml on 04/08/2018.
 */
class SparkUtilsSuite extends UnitSpecs {

    "SparkUtils periodicRDDCheckpoint" should "pass check" in {
        val sparkConf = new SparkConf()
            .setMaster("local[1]")
            .setAppName("periodicRDDCheckpoint")
            .set("spark.rdd.cp.path", "hdfs://localhost/test")
            .set("spark.rdd.cp.interval", "60000")
            .set("spark.rdd.cp.partition", "1")

        val sparkContext = new SparkContext(sparkConf)

        val startTime = System.currentTimeMillis
        val currentTime1 = startTime + 60000
        val rdd1 = sparkContext.makeRDD(Seq(1,2,3))

        val currentTime2 = currentTime1 + 60000
        val rdd2 = sparkContext.makeRDD(Seq(3,4,5))

        SparkUtils.periodicRDDCheckpoint(rdd1, startTime, currentTime1)
        SparkUtils.periodicRDDCheckpoint(rdd2, startTime, currentTime2)

        val rdd2FromHDFS = SparkUtils.getInitialRDD[Int](sparkContext, "hdfs://localhost/test", None)
        rdd2FromHDFS.collect().toList shouldBe Seq(3,4,5).toList

        val formattedTime = DateUtils.getYMDHM.format(currentTime1)
        val rdd1FromHDFS = SparkUtils.getInitialRDD[Int](sparkContext, "hdfs://localhost/test", Some(formattedTime))
        rdd1FromHDFS.collect().toList shouldBe Seq(1,2,3).toList
    }

}
