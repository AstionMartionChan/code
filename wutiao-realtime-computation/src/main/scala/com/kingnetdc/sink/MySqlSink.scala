package com.kingnetdc.sink

import com.kingnetdc.model.{RetryConfig, KPIRecord}
import com.kingnetdc.utils.StatisticsUtils
import com.kingnetdc.watermelon.output.MysqlSink
import com.kingnetdc.watermelon.utils.{DateUtils, CommonUtils}
import org.apache.spark.HashPartitioner
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.Time
import scala.util.{Success, Failure}

object MySqlSink extends Sink[KPIRecord] {

    @Deprecated
    def save(
        batchTime: Time, kpiRecords: RDD[KPIRecord], mysqlConfig: Map[String, String],
        tableName: String, dimensions: List[String]
    ): Unit = {
        val partitionNumber = getOutputPartition(kpiRecords.sparkContext.getConf)

        // 确保多个分区数据同时插入时候的顺序
        kpiRecords.map { kpiRecord =>
            (kpiRecord.duration, kpiRecord.window, kpiRecord.createdAt) -> kpiRecord
        }.repartitionAndSortWithinPartitions(new HashPartitioner(partitionNumber)).foreachPartition { iter =>
            val kpiIter = iter.map(_._2)

            val mysqlSink = new MysqlSink(mysqlConfig)
            CommonUtils.safeRelease(mysqlSink)(mysqlSink => {
                // 注意顺序
                val columns = KPIRecord.getColumns(dimensions)
                val rowValueIter = kpiIter.map(_.rowValues)
                val onDuplicateUpdatedKeys = List("metrics_value")
                mysqlSink.insertOrUpdate(tableName, columns, rowValueIter, onDuplicateUpdatedKeys)
            })() match {
                case Success(_) =>
                case Failure(e) =>
                    val time = DateUtils.getYMDHMS.format(batchTime.milliseconds)
                    logger.error(s"Failed to save kpi records to mysql for batch: ${time}", e)
            }
        }
    }

    def saveKPIRecord(kpiRecords: List[KPIRecord], config: Map[String, String]) = {
        if (kpiRecords.nonEmpty) {
            val columns = kpiRecords.head.columns
            val metricNames = kpiRecords.head.metricNames
            retriableInsertOrUpdate(
                config, columns,
                kpiRecords.map(_.rowValues).iterator,
                metricNames
            )
            /*
            CommonUtils.safeRelease(mysqlSink)(mysqlSink => {
                mysqlSink.insertOrUpdate(
                    tableName, columns,
                    kpiRecords.map(_.rowValues).iterator, metricNames
                )
            })() match {
                case Success(_) =>
                case Failure(e) =>
                    logger.error(s"Failed to save kpi records to mysql", e)
            }
            */
        }
    }


    /**
     *  可重试的InsertOrUpdate, 达到重试之后, 发消息报警
     *
     * @param config
     * @param columns
     * @param rowIterator
     * @param onDuplicateUpdatedKeys
     */
    def retriableInsertOrUpdate(
        config: Map[String, String], columns: List[String],
        rowIterator: Iterator[List[AnyRef]], onDuplicateUpdatedKeys: List[String]
    ) = {
        val tableName: String = config("output.table")
        val retryConfig: RetryConfig = RetryConfig.parse(config)

        var insertResult: Option[Unit] = None
        var retryAttempt = 1

        while (retryAttempt <= retryConfig.maxRetry && insertResult.isEmpty) {
            if (retryAttempt > 1) {
                try {
                    Thread.sleep(retryConfig.retryIntervalMs)
                } catch {
                    case ex: Exception =>
                }
            }

            logger.info(s"Mysql insert or update for the ${retryAttempt} time")

            val mysqlConfig = StatisticsUtils.getMysqlConfig(config)

            var mysqlSink: MysqlSink = null
            try {
                mysqlSink = new MysqlSink(mysqlConfig)

                mysqlSink.insertOrUpdate(
                    tableName, columns,
                    rowIterator, onDuplicateUpdatedKeys
                )

                insertResult = Some()
            } catch {
                case ex: Exception =>
                    logger.error("Failed to save kpi records to mysql", ex)
                    insertResult = None
            } finally {
                Option(mysqlSink).foreach { _.close() }
            }

            retryAttempt += 1
        }

        // 重试完成之后, 还是为空, 则发报警
        if (insertResult.isEmpty) {
            // 发送报警
            val formattedDate = DateUtils.getYMDHMS.format(System.currentTimeMillis)
            val alertMessage = s"${formattedDate} 五条实时指标 ${tableName}写入失败"
            StatisticsUtils.alertByChannel(
                alertMessage, Map(StatisticsUtils.RECEIVERS_KEY -> retryConfig.notificationReceivers)
            )
        }
    }

    /**
     * + repartitionAndSortWithinPartitions 确保多个批次数据同时插入时, 特别是对于小时更新时, 是按照先后顺序更新的
     * + 行转列
     *
     * @param batchTime
     * @param kpiRecordRDD
     * @param mysqlConfig
     * @param tableName
     */
    @Deprecated
    def saveMetricsByColumn(
        batchTime: Time, kpiRecordRDD: RDD[KPIRecord], mysqlConfig: Map[String, String], tableName: String
    ): Unit = {
        val partitionNumber = getOutputPartition(kpiRecordRDD.sparkContext.getConf)

        kpiRecordRDD.map { kpiRecord =>
            (kpiRecord.duration, kpiRecord.window, kpiRecord.createdAt) -> kpiRecord
        }.repartitionAndSortWithinPartitions(
            new HashPartitioner(partitionNumber)
        ).map(_._2).foreachPartition { kpiRecordIter =>
            if (kpiRecordIter.nonEmpty) {
                val mysqlSink = new MysqlSink(mysqlConfig)
                val kpiRecords = kpiRecordIter.toList

                val columns = kpiRecords.head.columns
                val metricNames = kpiRecords.head.metricNames

                CommonUtils.safeRelease(mysqlSink)(mysqlSink => {
                    mysqlSink.insertOrUpdate(
                        tableName, columns,
                        kpiRecords.map(_.rowValues).iterator, metricNames
                    )
                })() match {
                    case Success(_) =>
                    case Failure(e) =>
                        val time = DateUtils.getYMDHMS.format(batchTime.milliseconds)
                        logger.error(s"Failed to save kpi records to mysql for batch: ${time}", e)
                }
            }
        }
    }

}

