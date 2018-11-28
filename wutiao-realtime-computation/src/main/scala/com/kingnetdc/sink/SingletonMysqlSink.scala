package com.kingnetdc.sink

import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.LockSupport

import com.kingnetdc.watermelon.output.MysqlSink
import com.kingnetdc.watermelon.utils.Logging

/**
 * Created by zhouml on 06/08/2018.
 */
// TODO 换成单例连接池 --> 减少单个失败
class SingletonMysqlSink(mysqlSinkFactory: () => MysqlSink) extends Serializable {

    private lazy val mysqlSink = mysqlSinkFactory()

    def insertOrUpdate(
        tableName: String, columns: List[String],
        rowIterator: Iterator[List[AnyRef]], onDuplicateUpdatedKeys: List[String]
    ): Unit = {
        mysqlSink.insertOrUpdate(tableName, columns, rowIterator, onDuplicateUpdatedKeys)
    }

}

object SingletonMysqlSink extends Logging {

    def create(config: Map[String, String]) = {
        val mysqlSinkFactory = () => {
            logger.info("Initializing MysqlSink")
            val mysqlSink = new MysqlSink(config)

            // 每个JVM上注册hook
            sys.addShutdownHook {
                try {
                    // Wait for the queued sql to complete
                    LockSupport.parkNanos(TimeUnit.NANOSECONDS.convert(5, TimeUnit.MINUTES))
                    mysqlSink.close()
                } catch {
                    case ex: Exception =>
                        logger.error("Failed to close mysql sink", ex)
                }
            }

            mysqlSink
        }
        new SingletonMysqlSink(mysqlSinkFactory)
    }

}
