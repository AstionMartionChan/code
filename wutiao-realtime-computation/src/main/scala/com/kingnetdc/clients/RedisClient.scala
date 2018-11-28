package com.kingnetdc.clients

import com.kingnetdc.watermelon.utils.ConfigurationKeys._
import redis.clients.jedis.{Jedis, JedisCluster, Response}
import scala.collection.mutable
import scala.collection.JavaConversions._

class RedisClient private (client: Jedis) extends AutoCloseable {

  def get(key: String): Option[String] = Option(client.get(key))

  def multiGet(keys: Iterator[String]): Map[String, Option[String]] = {
    val list = keys.toList
    (list zip client.mget(list : _*)).map {
      case (key, value) => key -> Option(value)
    }.toMap
  }

  /**
   * pipeline 最好是用再不同操作 get|set|incr 需要批次发送的
<<<<<<< HEAD
   *
   * @param keys
   *
=======
   * @param keys
>>>>>>> prod/master
   * @return
   */
  def batchGet(keys: Iterator[String]): Map[String, Option[String]] = {
    val pipe = client.pipelined()
    val keyResponseMap: mutable.Map[String, Response[String]] = mutable.Map[String, Response[String]]()
    keys.foreach { key =>
      keyResponseMap.put(key, pipe.get(key))
    }
    pipe.sync()

    keyResponseMap.map {
      case (key, response) => key -> Option(response.get())
    }.toMap
  }

  def batchSet(keyValueExpirationPair: Iterator[(String, String, Int)]) = {
    val pipe = client.pipelined()
    keyValueExpirationPair.foreach {
      case (key, value, expirationTimeInSec) =>
        pipe.setex(key, expirationTimeInSec, value)
    }
    pipe.sync()
  }

  def batchGetBit(key: String, offsets: List[Long]) = {
    val pipe = client.pipelined()

    val keyResponseMap: mutable.Map[String, Response[Boolean]] = mutable.Map[String, Response[Boolean]]()
    offsets.foreach { offset =>
      pipe.getbit(key, offset)
    }

    pipe.sync()

    keyResponseMap.map {
      case (key, response) => key -> response.get()
    }
  }

  def getSet(key: String): Set[String] = {
    client.smembers(key).toSet
  }

  def setSet(key: String, member: String*) = {
    if (member.nonEmpty) {
      client.sadd(key, member: _*)
    }
  }

  def inSet(key: String, member: String): Boolean = {
    client.sismember(key, member)
  }

  def getJedis(): Jedis = client

  override def close(): Unit = client.close()

}

object RedisClient {

  def connect(redisConfig: Map[String, String]): RedisClient = {
    require(redisConfig.contains(REDIS_HOST), s"Redis ${REDIS_HOST} is missing")
    require(redisConfig.contains(REDIS_PORT), s"Redis ${REDIS_PORT} is missing")

    val host = redisConfig(REDIS_HOST)
    val port = redisConfig(REDIS_PORT).toInt
    val jedis = new Jedis(host, port)
    new RedisClient(jedis)
  }

  def connect(host: String, port: Int): RedisClient = {
    val jedis = new Jedis(host, port)
    new RedisClient(jedis)
  }

}
