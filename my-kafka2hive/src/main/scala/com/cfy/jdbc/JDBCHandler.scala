package com.cfy.jdbc


import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}

import com.cfy.config.ConfigurationManager
import com.cfy.constants.ConfigurationKey._
import com.cfy.utils.Logging

import scala.collection.mutable
import scala.util.{Failure, Success}


object JDBCHandler extends Logging{

  Class.forName(ConfigurationManager.getString(JDBC_DRIVER))
  private val connectionPool = new mutable.Queue[Connection]()
  for (i <- 0 until ConfigurationManager.getInt(JDBC_POOL_NUM)){
    val conn = DriverManager.getConnection(ConfigurationManager.getString(JDBC_URL), ConfigurationManager.getString(JDBC_USERNAME), ConfigurationManager.getString(JDBC_PASSWORD))
    connectionPool.enqueue(conn)
  }


  private def getConnection = {
    connectionPool.dequeue()
  }

  private def pushConnection(conn: Connection) = {
    connectionPool.enqueue(conn)
  }


  def prepareUpdate(sql: String, params: Array[Any]) = {
    var conn: Connection = null
    var statement: PreparedStatement = null
    var rst = 0
    try {
      conn = getConnection
      statement = conn.prepareStatement(sql)
      for (i <- 0 until params.length) statement.setObject(i + 1, params(i))
      rst = statement.executeUpdate()
    } catch {
      case e: Exception => {
        e.printStackTrace()
      }
    } finally {
      if (null != conn) pushConnection(conn)
    }
    rst
  }


  def prepareQuery[V](sql: String, f: ResultSet => V) = {
    var conn: Connection = null
    var statement: PreparedStatement = null
    var resultSet: ResultSet = null

    try {
      conn = getConnection
      statement = conn.prepareStatement(sql)
      resultSet = statement.executeQuery()
      Success(f(resultSet))
    } catch {
      case e: Exception => {
        logger.error(e.getMessage)
        Failure(e)
      }
    } finally {
      if (null != conn) pushConnection(conn)
    }

  }

}
