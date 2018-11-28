package com.kingnetdc.offline.utils

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.nio.charset.Charset

import com.google.common.hash.{BloomFilter, Funnel}
import com.kingnetdc.blueberry.cache.{KdcRedis, KdcRedisCluster}
import com.kingnetdc.watermelon.utils.AppConstants._
import org.xerial.snappy.Snappy
import redis.clients.jedis.Jedis

/**
 * Created by zhouml on 15/08/2018.
 */
object RedisBasedBloomFilter {

    /**
     *  使用snappy压缩, 减少内存占用
     *
     * @param bloomFilter
     * @tparam T
     *
     * @return
     */
    def toByteArray[T](bloomFilter: BloomFilter[T]): Array[Byte] = {
        val byteArrayOutputStream = new ByteArrayOutputStream()

        try {
            bloomFilter.writeTo(byteArrayOutputStream)
            Snappy.compress(byteArrayOutputStream.toByteArray)
        } finally {
            byteArrayOutputStream.close()
        }
    }

    def toBloomFilter[T](bytes: Array[Byte], funnel: Funnel[T]): BloomFilter[T] = {
        val byteArrayInputStream = new ByteArrayInputStream(Snappy.uncompress(bytes))

        try {
            BloomFilter.readFrom[T](byteArrayInputStream, funnel)
        } finally {
            byteArrayInputStream.close()
        }
    }

    def setBloomFilter[T](key: String, bloomFilter: BloomFilter[T], redisCluster: KdcRedisCluster
        , config: Map[String, String]) = {
        val keyInBytes = key.getBytes(Charset.forName(UTF8))
        val expire = config.get("expire.time").map(_.toInt).getOrElse(86400 * 14)
        redisCluster.getJedisCluster().setex(keyInBytes, expire, toByteArray(bloomFilter))
    }

    def getBloomFilter[T](
        key: String, funnel: Funnel[T], redisCluster: KdcRedisCluster
    ): Option[BloomFilter[T]] = {
        val keyInBytes = key.getBytes(Charset.forName(UTF8))

        Option(redisCluster.getJedisCluster().get(keyInBytes)).map { bloomFilterBytes =>
            toBloomFilter(bloomFilterBytes, funnel)
        }
    }

    def setBloomFilter[T](key: String, bloomFilter: BloomFilter[T], redis: Jedis, config: Map[String, String]) = {
        val keyInBytes = key.getBytes(Charset.forName(UTF8))
        val expire = config.get("expire.time").map(_.toInt).getOrElse(120)
        redis.setex(keyInBytes, expire, toByteArray(bloomFilter))
    }

    def getBloomFilter[T](
        key: String, funnel: Funnel[T], redis: Jedis
    ): Option[BloomFilter[T]] = {
        val keyInBytes = key.getBytes(Charset.forName(UTF8))

        Option(redis.get(keyInBytes)).map { bloomFilterBytes =>
            toBloomFilter(bloomFilterBytes, funnel)
        }
    }

}
