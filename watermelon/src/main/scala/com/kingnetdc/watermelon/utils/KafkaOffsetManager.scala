package com.kingnetdc.watermelon.utils

import java.sql.ResultSet
import com.kingnetdc.watermelon.clients.{TopicPartitionOffset, ZookeeperClient}
import com.kingnetdc.watermelon.output.MysqlSink
import org.apache.kafka.common.TopicPartition
import scala.util.{Try, Failure, Success}

/**
  * Created by zhouml on 16/05/2018.
  */
object KafkaOffsetManager extends Logging {

    private val MYSQL_COLUMNS = List("time", "group", "topic", "partition", "offset")
    private val ON_DUPLICATE_UPDATED_KEYS = List("offset")

    def getOffSetStatementAndRowValues(
        time: String, tableName: String, consumerGroup: String,
        topicPartitionOffsets: List[TopicPartitionOffset]
    ): (String, List[Array[AnyRef]]) = {
        val rowValues: List[List[AnyRef]] =
            topicPartitionOffsets.map { storageOffset =>
                List(
                    time,
                    consumerGroup,
                    storageOffset.topic,
                    storageOffset.partition: java.lang.Integer,
                    storageOffset.offset: java.lang.Long
                )
            }

        val wholeRowValues =
            rowValues.map { rowValue =>
                val columnValueMap = (MYSQL_COLUMNS zip rowValue).toMap

                val wholeRowValue =
                    (rowValue ::: ON_DUPLICATE_UPDATED_KEYS.map { key =>
                        columnValueMap(key)
                    }).toArray
                wholeRowValue
            }

        val statement = MysqlSink.getInsertStatement(tableName, MYSQL_COLUMNS, ON_DUPLICATE_UPDATED_KEYS)
        (statement, wholeRowValues)
    }

    def saveToMySQL(
        tableName: String, config: Map[String, String],
        time: String, consumerGroup: String,
        topicPartitionOffsets: List[TopicPartitionOffset]
    ): Try[Unit] = {
        val mysqlSink = new MysqlSink(config)

        val rowIterator =
            topicPartitionOffsets.map { storageOffset =>
                List(
                    time,
                    consumerGroup,
                    storageOffset.topic,
                    storageOffset.partition: java.lang.Integer,
                    storageOffset.offset: java.lang.Long
                )
            }.toIterator

        CommonUtils.safeRelease(mysqlSink)({ mysqlSink =>
            mysqlSink.insertOrUpdate(
                tableName, MYSQL_COLUMNS, rowIterator, ON_DUPLICATE_UPDATED_KEYS
            )
        })()
    }

    /**
      * @param tableName     表名
      * @param config        mysql配置
      * @param time          之前Offset保存时间点, 格式yyyy-MM-dd HH:mm:ss
      * @param consumerGroup 消费组
      * @return
      *
      * 保存Kafka offset的table为固定结构, 详见README.md
      */
    def getFromMySQL(
        tableName: String, config: Map[String, String], time: String, consumerGroup: String
    ): Map[TopicPartition, Long] = {
        val mysqlSink = new MysqlSink(config)

        val topicPartitionHandler = (rs: ResultSet) => {
            var topicPartitionOffset = Map[TopicPartition, Long]()

            while (rs.next()) {
                val topic = rs.getString(1)
                val partition = rs.getInt(2)
                val offset = rs.getLong(3)
                topicPartitionOffset += (new TopicPartition(topic, partition) -> offset)
            }

            topicPartitionOffset
        }

        CommonUtils.safeRelease(mysqlSink)(
            _.getSimpleDataSet(
                s"""
                  select
                    `topic`, `partition`, `offset`
                  from
                     ${tableName}
                 where
                    `time` = '${time}' and `group` = '${consumerGroup}'
                """,
                topicPartitionHandler
            )
        )() match {
            case Success(storedOffsets) => storedOffsets
            case Failure(e) =>
                logger.error(s"Failed to get offset from mysql", e)
                throw e
        }
    }

    def saveToZookeeper(
        zkConnect: String, consumerGroupId: String, storageOffsets: List[TopicPartitionOffset]
    ): Try[Unit] = {
        val zkClient = ZookeeperClient.connect(zkConnect)
        CommonUtils.safeRelease(zkClient)(
            _.commitOffset(consumerGroupId, storageOffsets)
        )()
    }

    def getFromZookeeper(zkConnect: String, consumerGroupId: String, topic: String): Map[TopicPartition, Long] = {
        val zkClient = ZookeeperClient.connect(zkConnect)
        val getFromOffsetsFunc: ZookeeperClient => Map[TopicPartition, Long] = _.getOffsets(consumerGroupId, topic)

        CommonUtils.safeRelease(zkClient)(getFromOffsetsFunc)() match {
            case Success(storedOffsets) => storedOffsets
            case Failure(e) =>
                logger.error(s"Failed to get offset from zookeeper", e)
                throw e
        }
    }

}
