package com.kingnetdc.clients

import java.sql.{DriverManager, Connection}
import com.kingnetdc.watermelon.utils.Logging
import org.apache.commons.dbutils.handlers.ArrayHandler
import org.apache.commons.dbutils.{ResultSetHandler, QueryRunner}
import scala.collection.mutable.ArrayBuffer
import com.kingnetdc.watermelon.utils.ConfigurationKeys._

// 底层借助commons-dbutils封装 https://commons.apache.org/proper/commons-dbutils/examples.html
class MySqlClient private (conn: Connection)
    extends AutoCloseable with Logging {

  private val batchSize = 1000

  override def close(): Unit = {
    Option(conn).foreach {
      logger.info("SQL connection is closing")
      _.close()
    }
  }

  def batchInsert(sql: String, params: Iterator[Array[AnyRef]]) = {
    val handler = new ArrayHandler()
    val queryRunner = new QueryRunner()

    var rowCount = 0
    val arrayBuffer = new ArrayBuffer[Array[AnyRef]]()

    while (params.hasNext) {
      arrayBuffer += params.next()
      rowCount += 1
      if (rowCount % batchSize == 0) {
        logger.info(s"${rowCount} records sent in batch")
        queryRunner.insertBatch(conn, sql, handler, arrayBuffer.toArray)
        arrayBuffer.clear()
        rowCount = 0
      }
    }

    // 不足最小批次, 则直接导出
    if (rowCount > 0) {
      logger.info(s"${rowCount} records remaining, can be sent in one batch")
      queryRunner.insertBatch(conn, sql, handler, arrayBuffer.toArray)
    }
  }

  def selectByKey[T](sql: String, rsh: ResultSetHandler[T]): T = {
    val queryRunner = new QueryRunner()
    queryRunner.query(conn, sql, rsh)
  }

  def getConn(): Connection = conn

}


object MySqlClient extends Logging {

  def connect(mysqlConfig: Map[String, String]): MySqlClient = {
    require(mysqlConfig.contains(MYSQL_URL), s"MySQL ${MYSQL_URL} is missing")
    require(mysqlConfig.contains(MYSQL_USER), s"MySQL ${MYSQL_USER} is missing")
    require(mysqlConfig.contains(MYSQL_PASSWORD), s"MySQL ${MYSQL_PASSWORD} is missing")

    val url = mysqlConfig(MYSQL_URL)
    val user = mysqlConfig(MYSQL_USER)
    val password = mysqlConfig(MYSQL_PASSWORD)
    connect(url, user, password)
  }

  def connect(url: String, user: String, password: String): MySqlClient = {
    logger.info(s"Connecting to ${url}")
    Class.forName(MYSQL_JDBC_DRIVER)
    val connection = DriverManager.getConnection(url, user, password)
    new MySqlClient(connection)
  }

}