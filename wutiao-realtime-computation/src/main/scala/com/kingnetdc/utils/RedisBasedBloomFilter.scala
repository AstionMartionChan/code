package com.kingnetdc.utils

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.nio.charset.Charset
import com.kingnetdc.watermelon.utils.AppConstants.UTF8
import com.google.common.hash.{Funnel, BloomFilter}
import com.kingnetdc.blueberry.cache.KdcRedisCluster
import org.xerial.snappy.Snappy

/**
 * Created by zhouml on 17/07/2018.
 */
object RedisBasedBloomFilter {

    /**
     *  使用snappy压缩, 减少内存占用
     * @param bloomFilter
     * @tparam T
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

    def setBloomFilter[T](key: String, bloomFilter: BloomFilter[T], redisCluster: KdcRedisCluster) = {
        val keyInBytes = key.getBytes(Charset.forName(UTF8))
        redisCluster.getJedisCluster().set(keyInBytes, toByteArray(bloomFilter))
    }

    def getBloomFilter[T](
        key: String, funnel: Funnel[T], redisCluster: KdcRedisCluster
    ): Option[BloomFilter[T]] = {
        val keyInBytes = key.getBytes(Charset.forName(UTF8))

        Option(redisCluster.getJedisCluster().get(keyInBytes)).map { bloomFilterBytes =>
            toBloomFilter(bloomFilterBytes, funnel)
        }
    }

}
