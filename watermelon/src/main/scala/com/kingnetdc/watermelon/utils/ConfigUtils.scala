package com.kingnetdc.watermelon.utils

import java.io.FileInputStream
import java.util.Properties
import com.kingnetdc.watermelon.utils.AppConstants._
import redis.clients.jedis.HostAndPort
import scala.collection.JavaConversions._
import scala.collection.mutable

/**
* Created by zhouml on 26/05/2018.
*/
object ConfigUtils extends Logging {

    def loadFromFileAsMap(path: String): Map[String, String] = {
        ConfigUtils.loadFromFile(path).toMap
    }

    def loadFromFile(path: String): Properties = {
        try {
            val properties = new Properties()
            properties.load(new FileInputStream(path))
            properties
        } catch {
            case e: Exception =>
                logger.error(s"Failed to get config path from ${path}", e)
                throw e
        }
    }

    def parseRedisConnect(connect: String): Set[HostAndPort] = {
        require(
           StringUtils.nonEmpty(connect), "Redis cluster connect should be in format host1:port1,host2:port2"
        )

        connect.split(COMMA).map { pair =>
            val host :: port :: Nil = pair.split(COLON).toList
            new HostAndPort(host, port.toInt)
        }.toSet
    }

}
