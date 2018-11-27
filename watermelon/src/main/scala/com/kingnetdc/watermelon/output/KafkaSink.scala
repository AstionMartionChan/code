package com.kingnetdc.watermelon.output

import java.util.concurrent.Future
import com.kingnetdc.watermelon.utils.Logging
import org.apache.kafka.clients.producer.{ProducerConfig, RecordMetadata, KafkaProducer, ProducerRecord, Callback}
import scala.collection.JavaConversions._

/**
  * Created by zhouml on 17/05/2018.
  */
class KafkaSink[K, V](producerFactory: () => KafkaProducer[K, V]) extends Serializable {

    // workaround to avoid Serialization issue, every JVM just get one
    private lazy val producer = producerFactory()

    def send(record: ProducerRecord[K, V]): Future[RecordMetadata] = {
        producer.send(record)
    }

    def batchSend(recordIter: Iterator[ProducerRecord[K, V]]): Unit = {
        recordIter.foreach { record => send(record) }
    }

    def send(record: ProducerRecord[K, V], callback: Callback) = {
        producer.send(record, callback)
    }

    def batchSend(recordIter: Iterator[ProducerRecord[K, V]], callback: Callback) = {
        recordIter.foreach { record =>
            send(record, callback)
        }
    }

}

object KafkaSink extends Logging {

    private val DEFAULT_KAFKA_CONFIG: Map[String, Object]= Map(
        ProducerConfig.COMPRESSION_TYPE_CONFIG -> "snappy",
        ProducerConfig.ACKS_CONFIG -> "all"
    )

    /**
      * Kafka producer important configs
      * retries -- 重试 -- 0
      * max.in.flight.requests.per.connection -- 同时允许多少个未被acknowledged请求 -- 5
      * buffer.memory -- 生产者消息Buffer -- 33554432(32M)
      * batch.size -- 针对某个分区发送时的buffer(amount of memory in bytes) -- 16384(16KB)
      * max.block.ms -- KafkaProducer.send() 或 KafkaProducer.partitionsFor
      * 因阻塞(buffer is full or metadata unavailable)等待的最大时长
      * linger.ms -- accomplishes delay record sending by adding a small amount
      *
      */
    def create[K, V](kafkaConfig: Map[String, Object]) = {
        val mergedOne = DEFAULT_KAFKA_CONFIG ++ kafkaConfig
        val producerFactory =
            () => {
                logger.info("Initializing KafkaProducer")
                val producer = new KafkaProducer[K, V](mergedOne)
                // 每个JVM上注册hook, 在JVM关闭的时候, Producer将所有消息发出去
                sys.addShutdownHook {
                    try {
                        logger.info("Close KafkaProducer")
                        producer.close()
                    } catch {
                        case ex: Exception =>
                            logger.error("Failed to close KafkaProducer", ex)
                    }
                }
                producer
            }
        new KafkaSink[K, V](producerFactory)
    }

}
