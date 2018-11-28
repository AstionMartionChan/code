package com.kingnetdc.sink

import com.kingnetdc.clients.InfluxDBClient
import com.kingnetdc.model.KPIRecord
import com.kingnetdc.watermelon.utils.{CommonUtils, DateUtils}
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.Time
import scala.util.{Failure, Success}

@Deprecated
object InfluxDBSink extends Sink[KPIRecord] {

    def save(
        batchTime: Time, kpiRDD: RDD[KPIRecord],
        influxDBConfig: Map[String, String], tableName: String,
        dimensions: List[String], indexName: String
    ) = {
        kpiRDD.foreachPartition(kpiIter => {
            if (kpiIter.nonEmpty) {
                val influxDBClient = InfluxDBClient.connect(influxDBConfig)
                val paramsType = KPIRecord.getParamsType(dimensions)
                val batchInsertTry =
                    CommonUtils.safeRelease(influxDBClient)(influxDBClient => {
                        // TODO
                        /*
                        val insertParams = kpiIter.map(_.insertInfluxDBParams(indexName))
                        influxDBClient.batchInsert(tableName, insertParams, paramsType)
                        */
                    })()

                batchInsertTry match {
                    case Success(_) =>
                    case Failure(e) =>
                        val time = DateUtils.getYMDHMS.format(batchTime.milliseconds)
                        logger.error(
                            s"Failed to insert kpi records for batch: ${time}", e
                        )
                }
            }
        })
    }

    // 用于输出用户行为指标
    def saveInteract(
            batchTime: Time, kpiRDD: RDD[KPIRecord],
            influxDBConfig: Map[String, String], tableName: String,
            dimensions: List[String]
    ) = {
        kpiRDD.foreachPartition(kpiIter => {
            if (kpiIter.nonEmpty) {
                val influxDBClient = InfluxDBClient.connect(influxDBConfig)
                val paramsType = KPIRecord.getParamsTypeInteract(dimensions)
                val batchInsertTry =
                    CommonUtils.safeRelease(influxDBClient)(influxDBClient => {
                        // TODO
                        /*
                        val insertParams = kpiIter.map(_.insertInfluxDBParamsInteract())
                        influxDBClient.batchInsert(tableName, insertParams, paramsType)
                        */
                    })()

                batchInsertTry match {
                    case Success(_) =>
                    case Failure(e) =>
                        val time = DateUtils.getYMDHMS.format(batchTime.milliseconds)
                        logger.error(
                            s"Failed to insert kpi records for batch: ${time}", e
                        )
                }
            }
        })
    }

}
