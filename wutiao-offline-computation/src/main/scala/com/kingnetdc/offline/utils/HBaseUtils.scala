package com.kingnetdc.offline.utils

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.{HConstants, HBaseConfiguration}
import org.apache.hadoop.hbase.mapreduce.TableInputFormat

/**
* Created by zhouml on 25/05/2018.
*/
object HBaseUtils {

    def getHBaseConf(tableName: String, zkQuoram: String): Configuration = {
        val conf = HBaseConfiguration.create()
        conf.set(HConstants.ZOOKEEPER_QUORUM, zkQuoram)
        conf.set(TableInputFormat.INPUT_TABLE, tableName)
        conf
    }

}
