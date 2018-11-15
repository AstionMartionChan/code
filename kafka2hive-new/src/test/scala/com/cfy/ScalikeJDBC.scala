package com.cfy

import scalikejdbc._
import scalikejdbc.config._

object ScalikeJDBC {

  DBs.setupAll()

  def main(args: Array[String]): Unit = {

    val body = DB readOnly { implicit session =>
      SQL("select * from t_kafka2hive_offset").map(rs => rs.string("group")).single.apply
    }
    println(body)

  }
}
