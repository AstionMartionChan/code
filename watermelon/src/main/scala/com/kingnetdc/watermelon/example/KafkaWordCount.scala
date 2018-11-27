package com.kingnetdc.watermelon.example

import com.kingnetdc.watermelon.clients.TopicPartitionOffset
import com.kingnetdc.watermelon.input.KingnetInputDStream
import com.kingnetdc.watermelon.output.{KafkaSink, MysqlSink}
import com.kingnetdc.watermelon.utils.ConfigurationKeys._
import com.kingnetdc.watermelon.utils.{CommonUtils, DateUtils, KafkaOffsetManager, KafkaOffsetStorage}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{StringSerializer, StringDeserializer}
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.kafka010.{OffsetRange, HasOffsetRanges}
import org.apache.spark.streaming.{Duration, StreamingContext}
import org.apache.spark.{SparkContext, SparkConf}

/**
  * Created by zhouml on 17/05/2018.
  */
object KafkaWordCount {

    private def getKafkaParams(): Map[String, Object] = {
        Map(
            "topic" -> "kafka-word-count",
            "bootstrap.servers" -> "localhost:9092",
            "group.id" -> "kafka-word-count-group",
            "zookeeper.connect" -> "localhost:2181",
            "key.deserializer" -> classOf[StringDeserializer],
            "value.deserializer" -> classOf[StringDeserializer],
            "enable.auto.commit" -> (false: java.lang.Boolean)
        )
    }

    private def sparkConfForZKStorage() = {
        val sparkConf = new SparkConf()
        sparkConf.
            set(SPARK_KAFKA_OFFSET_STORAGE, KafkaOffsetStorage.ZK.toString)
            .set(SPARK_KAFKA_OFFSET_ZK_CONNECT, "localhost:2181")
            .set(SPARK_STREAMING_DURATION, "30000")
            .set(SPARK_STREAMING_STATE_CHECK_INTERVAL, "15000")
            .set(SPARK_STREAMING_STOP_SIGNAL, "kill-test-signal")
            .set(SPARK_STREAMING_STATE_CHECK_REDIS_CONNECT, "192.168.77.28:6380")
            .set(SPARK_STREAMING_STATE_CHECK_REDIS_PASSWORD, "123456")
            .setAppName("Kafka Word counter")
            .setMaster("local[*]")
    }

    private def sparkConfForMySQLStorage() = {
        val sparkConf = new SparkConf()

        sparkConf
            .set(SPARK_STREAMING_DURATION, "30000")
            .set(SPARK_STREAMING_STATE_CHECK_INTERVAL, "15000")
            .set(SPARK_STREAMING_STOP_SIGNAL, "kill-test-signal")
            .set(SPARK_STREAMING_STATE_CHECK_REDIS_CONNECT, "192.168.77.28:6380")
            .set(SPARK_STREAMING_STATE_CHECK_REDIS_PASSWORD, "123456")
            .set(SPARK_KAFKA_OFFSET_STORAGE, KafkaOffsetStorage.MYSQL.toString)
            .set(SPARK_OFFSET_MYSQL_URL, "jdbc:mysql://localhost:3306/kingnet?useSSL=false")
            .set(SPARK_OFFSET_MYSQL_USER, "root")
            .set(SPARK_OFFSET_MYSQL_PW, "")
            .set(SPARK_OFFSET_MYSQL_TABLE, "tbl_kafka_topic_partition")
            .set(SPARK_KAFKA_OFFSET_MYSQL_RESTARTFROM, "2018-06-06 19:07:30")
            .setAppName("Kafka Word counter")
            .setMaster("local[*]")
    }

    private def writeResultToMySQL(offsetMessageRDD: RDD[(Long, String)]) = {
        val mysqlConfig =
            Map(
                MYSQL_URL -> "jdbc:mysql://localhost:3306/kingnet?useSSL=false",
                MYSQL_USER -> "root",
                MYSQL_PASSWORD -> ""
            )

        offsetMessageRDD.foreachPartition { rowIter =>
            if (rowIter.nonEmpty) {
                val mysqlSink = new MysqlSink(mysqlConfig)
                CommonUtils.safeRelease(mysqlSink)(mysqlSink => {
                    // 注意顺序
                    val columns = List("offset", "message")
                    val rowValueIter = rowIter.map { row =>
                        List(row._1: java.lang.Long, row._2)
                    }
                    val onDuplicateUpdatedKeys = List("offset")
                    mysqlSink.insertOrUpdate("tbl_offset_message", columns, rowValueIter, onDuplicateUpdatedKeys)
                })()
            }
        }
    }

    private def writeResultToKafka(
        offsetMessageRDD: RDD[(Long, String)],
        producerBroadCast: Broadcast[KafkaSink[String, String]]
    ) = {
        offsetMessageRDD.foreachPartition { rowIter =>
            val kafkaSink = producerBroadCast.value

            val recordsIter =
                rowIter.map { row =>
                    new ProducerRecord[String, String]("kafka-word-count-bak", row._2)
                }

            kafkaSink.batchSend(recordsIter)
        }
    }

    def main(args: Array[String]): Unit = {
        // val sparkConf = sparkConfForZKStorage()
        val sparkConf = sparkConfForMySQLStorage()
        val sparkContext = new SparkContext(sparkConf)
        val producerBroadCast: Broadcast[KafkaSink[String, String]] =
            sparkContext.broadcast(KafkaSink.create[String, String](
                Map(
                    "bootstrap.servers" -> "localhost:9092",
                    "key.serializer" -> classOf[StringSerializer],
                    "value.serializer" -> classOf[StringSerializer],
                    "enable.auto.commit" -> (false: java.lang.Boolean)
                )
            ))

        val ssc = new StreamingContext(sparkContext, Duration(sparkConf.get(SPARK_STREAMING_DURATION).toLong))

        val dstream = KingnetInputDStream.createDirectKafkaStream[String, String](ssc, getKafkaParams())

        var offsetRanges = Array[OffsetRange]()
        dstream.transform { rdd =>
            offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
            rdd
        }.foreachRDD { (consumerRecordRDD, time) =>
            val result =
                consumerRecordRDD.map { consumerRecord =>
                    (consumerRecord.offset, consumerRecord.value)
                }

            // 输出结果到MySQL
            writeResultToKafka(result, producerBroadCast)
            writeResultToMySQL(result)

            val topicPartitionOffsets =
                offsetRanges.toList.map { offsetRange =>
                    TopicPartitionOffset(offsetRange.topic, offsetRange.partition, offsetRange.untilOffset)
                }

            // 存储offsetRanges -- Zookeeper
            /*
                KafkaOffsetManager.saveToZookeeper(
                  "localhost:2181", "kafka-word-count-group", topicPartitionOffsets
                )
            */

            // 存储offsetRanges -- MySQL
            // KafkaOffsetManager.saveToZookeeper()
            KafkaOffsetManager.saveToMySQL(
                "tbl_kafka_topic_partition",
                Map(
                    MYSQL_URL -> "jdbc:mysql://localhost:3306/kingnet?useSSL=false",
                    MYSQL_USER -> "root",
                    MYSQL_PASSWORD -> ""
                ), DateUtils.getYMDHMS.format(time.milliseconds), "kafka-word-count-group",
                topicPartitionOffsets
            )
        }

        KingnetInputDStream.start(ssc)
    }

}
