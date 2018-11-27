package com.kingnetdc.watermelon.output

import com.kingnetdc.watermelon.utils.Logging
import org.apache.hadoop.hbase.{HBaseConfiguration, HConstants, TableName}
import org.apache.hadoop.hbase.client.{Put, Result, Connection, ConnectionFactory, Table, Get}
import scala.collection.JavaConversions._

/**
 * Created by zhouml on 19/06/2018.
 */
class HBaseConnection(connectionFactory: () => Connection) extends Serializable with Logging {

    // workaround to avoid Serialization issue, every JVM just get one
    private lazy val connection = connectionFactory()

    def getConnection = connection

    def bulkGet(tableName: String, gets: List[Get]): Array[Result] = {
        var table: Table = null
        try {
            table = connection.getTable(TableName.valueOf(tableName))
            table.get(gets)
        } finally {
            Option(table).foreach { _.close() }
        }
    }

    def bulkPut(tableName: String, puts: List[Put]) {
        var table: Table = null
        try {
            table = connection.getTable(TableName.valueOf(tableName))
            table.put(puts)
        } finally {
            Option(table).foreach { _.close() }
        }
    }

}

object HBaseConnection extends Logging {

    def create(hbaseConfig: Map[String, String]) = {
        val connectionFactory = () => {
            logger.info("Initializing hbase connection")

            val config = HBaseConfiguration.create()
            config.set(HConstants.ZOOKEEPER_QUORUM, hbaseConfig(HConstants.ZOOKEEPER_QUORUM))
            val connection = ConnectionFactory.createConnection(config)

            // 每个JVM上注册hook, 在JVM关闭的时候, Producer将所有消息发出去
            sys.addShutdownHook {
                try {
                    logger.info("Close hbase connection")
                    connection.close()
                } catch {
                    case ex: Exception =>
                        logger.error("Failed to close hbase connection", ex)
                }
            }
            connection
        }
        new HBaseConnection(connectionFactory)
    }

}
