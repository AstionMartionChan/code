package com.kingnetdc.sink

import com.kingnetdc.UnitSpecs

/**
 * Created by zhouml on 20/08/2018.
 */
class MySqlSinkSuite extends UnitSpecs {

    val fixture = {
        new {
            val config = Map(
                "mysql.url" -> "jdbc:mysql://localhost:3306/kingnet?useSSL=false",
                "mysql.user" -> "root",
                "mysql.password" -> "123456",
                "output.table" -> "realtime_wutiao_device_kpi"
            )
        }
    }

    "retriableInsertOrUpdate" should "pass check" in {
        import fixture._
        val columns = List("fds", "window", "duration", "appver", "channel", "os", "active", "new", "old")
        val rowIterator = List(
            "2018-08-20", "2018-08-20 14:23:00", "600000", "1.3.5", "huawei", "ios",
            1: java.lang.Integer, 0: java.lang.Integer, 1: java.lang.Integer
        )
        MySqlSink.retriableInsertOrUpdate(config, columns, Iterator(rowIterator), List("active", "new", "old"))
    }

}
