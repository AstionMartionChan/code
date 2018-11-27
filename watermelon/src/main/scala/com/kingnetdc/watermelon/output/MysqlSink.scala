package com.kingnetdc.watermelon.output

import java.sql.{ResultSet, DriverManager, Connection}
import com.google.common.annotations.VisibleForTesting
import com.kingnetdc.watermelon.utils.Logging
import org.apache.commons.dbutils.QueryRunner
import org.apache.commons.dbutils.handlers.ArrayHandler
import scala.collection.mutable.ArrayBuffer
import com.kingnetdc.watermelon.utils.ConfigurationKeys._
import com.kingnetdc.watermelon.utils.AppConstants._

/**
  * Created by zhouml on 16/05/2018.
  */
class MysqlSink(config: Map[String, String]) extends AutoCloseable with Logging {

    private val batchSize = config.get(MYSQL_BATCHSIZE).map(_.toInt).getOrElse(1000)

    private val connection: Connection = getConnection(config)

    def getConnection(config: Map[String, String]) = {
        require(config.contains(MYSQL_URL), s"MySQL ${MYSQL_URL} is missing")
        require(config.contains(MYSQL_USER), s"MySQL ${MYSQL_USER} is missing")
        require(config.contains(MYSQL_PASSWORD), s"MySQL ${MYSQL_PASSWORD} is missing")

        val url = config(MYSQL_URL)
        val user = config(MYSQL_USER)
        val password = config(MYSQL_PASSWORD)

        Class.forName(MYSQL_JDBC_DRIVER)
        val connection = DriverManager.getConnection(url, user, password)
        connection
    }


    override def close(): Unit = {
        try {
            logger.info("Closing sql connection")
            connection.close()
        } catch {
            case ex: Exception =>
                logger.error("Failed to close connection", ex)
        }
    }

    def batchExecute(statementsAndParams: (String, List[Array[AnyRef]])*) {
        val queryRunner = new QueryRunner()
        var committed = false

        try {
            connection.setAutoCommit(false)
            statementsAndParams.foreach {
                case (statement, params) =>
                    logger.info(s"Execute sql: ${statement}")
                    queryRunner.batch(connection, statement, params.toArray)
            }
            connection.commit()
            committed = true
        } catch {
            case ex: Exception =>
                logger.error("Failed to execute", ex)
                throw ex
        } finally {
            if (!committed) {
                connection.rollback()
            }
        }
    }

    def insertOrUpdate(
        tableName: String, columns: List[String],
        rowIterator: Iterator[List[AnyRef]], onDuplicateUpdatedKeys: List[String]
     ): Unit = {
        val insertSQL = MysqlSink.getInsertStatement(tableName, columns, onDuplicateUpdatedKeys)

        if (logger.isDebugEnabled) {
            logger.debug(s"Insert sql: ${insertSQL}")
        }

        val params: Iterator[Array[AnyRef]] =
            rowIterator.map { rowValue =>
                val columnValueMap = (columns zip rowValue).toMap

                val wholeRowValue =
                    (rowValue ::: onDuplicateUpdatedKeys.map { key =>
                        columnValueMap(key)
                    }).toArray
                wholeRowValue
            }

        val handler = new ArrayHandler()
        val queryRunner = new QueryRunner()

        var rowCount = 0
        val arrayBuffer = new ArrayBuffer[Array[AnyRef]]()

        while (params.hasNext) {
            arrayBuffer += params.next()
            rowCount += 1
            if (rowCount % batchSize == 0) {
                if (logger.isDebugEnabled) {
                    logger.debug(s"${rowCount} records sent in batch")
                }
                queryRunner.insertBatch(connection, insertSQL, handler, arrayBuffer.toArray)
                arrayBuffer.clear()
                rowCount = 0
            }
        }

        // 不足最小批次, 则直接导出
        if (rowCount > 0) {
            if (logger.isDebugEnabled) {
                logger.debug(s"${rowCount} records remaining, can be sent in one batch")
            }
            queryRunner.insertBatch(connection, insertSQL, handler, arrayBuffer.toArray)
        }
    }

    def insertOrUpdateWithSql(
        tableName: String, columns: List[String],
        rowIterator: Iterator[List[AnyRef]], onDuplicateUpdatedKeys: List[String],
        sql: String
    ): Unit = {
        val insertSQL = sql

        if (logger.isDebugEnabled) {
            logger.debug(s"Insert sql: ${insertSQL}")
        }

        val params: Iterator[Array[AnyRef]] =
            rowIterator.map { rowValue =>
                val columnValueMap = (columns zip rowValue).toMap

                val wholeRowValue =
                    (rowValue ::: onDuplicateUpdatedKeys.map { key =>
                        columnValueMap(key)
                    }).toArray
                wholeRowValue
            }

        val handler = new ArrayHandler()
        val queryRunner = new QueryRunner()

        var rowCount = 0
        val arrayBuffer = new ArrayBuffer[Array[AnyRef]]()

        while (params.hasNext) {
            arrayBuffer += params.next()
            rowCount += 1
            if (rowCount % batchSize == 0) {
                if (logger.isDebugEnabled) {
                    logger.debug(s"${rowCount} records sent in batch")
                }
                queryRunner.insertBatch(connection, insertSQL, handler, arrayBuffer.toArray)
                arrayBuffer.clear()
                rowCount = 0
            }
        }

        // 不足最小批次, 则直接导出
        if (rowCount > 0) {
            if (logger.isDebugEnabled) {
                logger.debug(s"${rowCount} records remaining, can be sent in one batch")
            }
            queryRunner.insertBatch(connection, insertSQL, handler, arrayBuffer.toArray)
        }
    }

    def getSimpleDataSet[V](sql: String, function: ResultSet => V) = {
        val statement = connection.createStatement()
        logger.info(s"Executed sql: ${sql}")
        function(statement.executeQuery(sql))
    }


    @VisibleForTesting
    private[watermelon] def truncateTable(table: String): Unit = {
        connection.prepareStatement(s"truncate table ${table}").execute()
    }

}


object MysqlSink {

    def getInsertStatement(
        tableName: String, columnNames: List[String], onDuplicateUpdatedKeys: List[String]
    ) = {
        val columnNamePlaceHolder = columnNames.map(_ => "?").mkString(COMMA)
        val normalInsert =
            s"insert into ${tableName} (${columnNames.map(name => s"`${name}`").mkString(COMMA)}) " +
            s"values (${columnNamePlaceHolder})"

        if (onDuplicateUpdatedKeys.isEmpty) {
            normalInsert
        } else {
            val updateKeyPlaceHolder =
                onDuplicateUpdatedKeys.map { key =>
                    s"`${key}` = ?"
                }.mkString(COMMA)
            s"${normalInsert} on duplicate key update ${updateKeyPlaceHolder}"
        }
    }

}