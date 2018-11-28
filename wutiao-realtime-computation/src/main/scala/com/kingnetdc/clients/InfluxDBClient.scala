package com.kingnetdc.clients

import java.util.concurrent.TimeUnit
import com.kingnetdc.watermelon.utils.ConfigurationKeys._
import com.kingnetdc.watermelon.utils.Logging
import org.influxdb.InfluxDB.ConsistencyLevel
import org.influxdb.dto.{BatchPoints, Point, Query, QueryResult}
import org.influxdb.{InfluxDB, InfluxDBFactory}


class InfluxDBClient(
    private val influxDb: InfluxDB,
    private val dbName: String
) extends AutoCloseable with Logging {

    private val batchSize = 10000

    private val rpName = "autogen"

    private val FIELD = "field"

    private val TAG = "tag"

    private val TIME = "time"

    private def getBatchPoints(dbName: String): BatchPoints = {
        BatchPoints.database(dbName).tag("test", "true").retentionPolicy(rpName)
                .consistency(ConsistencyLevel.ALL).build()
    }

    override def close() = {
        Option(influxDb).foreach {
            logger.info("InfluxDB connection is closing")
            _.close()
        }
    }

    def batchInsert(tableName: String, params: Iterator[Map[String, Any]], paramsType: Map[String, String]) = {

        var batchPoints = getBatchPoints(dbName)
        var rowCount = 0

        while (params.hasNext) {
            val map: Map[String, Any] = params.next()
            rowCount += 1
            val point = Point.measurement(tableName)
            map.foreach(m => {
                m match {
                    case m if paramsType(m._1).equals(TIME) =>
                        point.time(m._2.toString.toLong, TimeUnit.MILLISECONDS)
                    case m if paramsType(m._1).equals(TAG) => point.tag(m._1, m._2.toString)
                    case m if paramsType(m._1).equals(FIELD) => point.addField(m._1, m._2.toString)
                }
            })
            batchPoints.point(point.build())
            if (rowCount % batchSize == 0) {
                logger.info(s"${rowCount} records insert into influxDB, dbName name: ${dbName}")
                influxDb.write(batchPoints)
                rowCount = 0
                batchPoints = getBatchPoints(dbName)
            }
        }
        if (rowCount > 0) {
            logger.info(s"${rowCount} records insert into influxDB, dbName name: ${dbName}")
            influxDb.write(batchPoints)
        }

    }

    def select(command: String): QueryResult = {
        val query = new Query(command, dbName)
        influxDb.query(query)
    }

}


object InfluxDBClient extends Logging {

    def connect(influxDBConfig: Map[String, String]): InfluxDBClient = {
        require(influxDBConfig.contains(INFLUXDB_HOST), s"missing influxDB ${INFLUXDB_HOST}")
        require(influxDBConfig.contains(INFLUXDB_USERNAME), s"missing influxDB ${INFLUXDB_USERNAME}")
        require(influxDBConfig.contains(INFLUXDB_PASSWORD), s"missing influxDB ${INFLUXDB_PASSWORD}")
        require(influxDBConfig.contains(INFLUXDB_DB), s"missing influxDB ${INFLUXDB_DB}")

        val host = influxDBConfig(INFLUXDB_HOST)
        val username = influxDBConfig(INFLUXDB_USERNAME)
        val password = influxDBConfig(INFLUXDB_PASSWORD)
        val db = influxDBConfig(INFLUXDB_DB)
        val influxDB: InfluxDB = connect(host, username, password)
        new InfluxDBClient(influxDB, db)
    }

    private def connect(host: String, username: String, password: String): InfluxDB = {
        logger.info(s"connect to influxdb: ${host}")
        InfluxDBFactory.connect(host, username, password)
    }

}