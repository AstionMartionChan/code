package com.kingnetdc.watermelon.input

import com.kingnetdc.watermelon.UnitSpecs
import com.kingnetdc.watermelon.utils.KafkaOffsetStorage
import org.apache.spark.SparkConf
import com.kingnetdc.watermelon.utils.ConfigurationKeys._


/**
  * Created by zhouml on 17/05/2018.
  */
class KingnetInputDStreamSuite extends UnitSpecs {

  val fixture = {
    new {
      val kafkaParams = Map(
        TOPIC -> "test",
        KAFKA_GROUP -> "test"
      )
    }
  }

  "Spark conf for saving to zookeeper" should "pass check" in {
      val sparkConf = new SparkConf()
      sparkConf.set(SPARK_KAFKA_OFFSET_STORAGE, KafkaOffsetStorage.ZK.toString)
      sparkConf.set(SPARK_KAFKA_OFFSET_ZK_CONNECT, "localhost:2181")

      val (topic, topicPartitionOffsets) = KingnetInputDStream.getSavedTopicOffsets(sparkConf,fixture.kafkaParams)
      topic shouldBe Set("test")
      topicPartitionOffsets.isEmpty shouldBe true
  }

  "Spark conf for saving to mysql" should "pass check" in {
    val sparkConf = new SparkConf()
    sparkConf.set(SPARK_KAFKA_OFFSET_STORAGE, KafkaOffsetStorage.MYSQL.toString)
    sparkConf.set(SPARK_OFFSET_MYSQL_URL, "jdbc:mysql://localhost:3306/kingnet")
    sparkConf.set(SPARK_OFFSET_MYSQL_USER, "root")
    sparkConf.set(SPARK_OFFSET_MYSQL_PW, "123456")
    sparkConf.set(SPARK_OFFSET_MYSQL_TABLE, "tbl_kafka_topic_partition")
    sparkConf.set(SPARK_KAFKA_OFFSET_MYSQL_RESTARTFROM, "2018-05-16 20:50:31")

    val (topic, topicPartitionOffsets) = KingnetInputDStream.getSavedTopicOffsets(sparkConf, fixture.kafkaParams)
    topic shouldBe Set("test")
    topicPartitionOffsets.isEmpty shouldBe true
  }


}
