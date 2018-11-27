package com.kingnetdc.watermelon.utils

import com.kingnetdc.watermelon.UnitSpecs
import com.kingnetdc.watermelon.clients.TopicPartitionOffset
import com.kingnetdc.watermelon.output.MysqlSink
import com.kingnetdc.watermelon.utils.ConfigurationKeys._
import org.apache.kafka.common.TopicPartition

/**
  * Created by zhouml on 17/05/2018.
  */
class KafkaOffsetManagerSuite extends UnitSpecs {

  val fixture = {
    new {
      val mysqlConfig = Map(
        MYSQL_URL -> "jdbc:mysql://localhost:3306/kingnet?useSSL=false",
        MYSQL_USER -> "root",
        MYSQL_PASSWORD -> "123456"
      )

      val table = "tbl_kafka_topic_partition"
      val consumerGroup = "test-group"
    }
  }

  "saveToMySQL" should "pass check" in {
    import fixture._

    val mysqlSink = new MysqlSink(mysqlConfig)
    mysqlSink.truncateTable(table)
    mysqlSink.close()

    val date = DateUtils.getYMDHMS.format(new java.util.Date())
    KafkaOffsetManager.saveToMySQL(
      table, mysqlConfig, date, consumerGroup,
      TopicPartitionOffset("test", 1, 1000L) :: TopicPartitionOffset("test", 2, 2000L) :: Nil
    )

   val expectedTopicPartitionOffset =
     Map(
       new TopicPartition("test", 1) -> 1000L,
       new TopicPartition("test", 2) -> 2000L
     )

    val actualTopicPartitionOffset =
      KafkaOffsetManager.getFromMySQL(table, mysqlConfig, date, consumerGroup)

    actualTopicPartitionOffset shouldBe expectedTopicPartitionOffset
  }

}
