package com.kingnetdc.watermelon.clients

import com.kingnetdc.watermelon.utils.Logging
import com.kingnetdc.watermelon.utils.AppConstants._
import kafka.utils.ZKGroupTopicDirs
import org.apache.commons.lang3.StringUtils
import org.apache.curator.framework.{CuratorFramework, CuratorFrameworkFactory}
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.curator.utils.CloseableUtils
import org.apache.kafka.common.TopicPartition
import scala.collection.JavaConversions._

case class TopicPartitionOffset(topic: String, partition: Int, offset: Long)

class ZookeeperClient private(val client: CuratorFramework)
        extends AutoCloseable with Logging {

    def commitOffset(consumerGroupId: String, storageOffsets: List[TopicPartitionOffset]): Unit = {
        require(StringUtils.isNoneBlank(consumerGroupId), "Consumer group id should not be empty")

        storageOffsets.foreach { storageOffset =>
            val groupTopicDirs = new ZKGroupTopicDirs(consumerGroupId, storageOffset.topic)
            val consumerOffSetPath = s"${groupTopicDirs.consumerOffsetDir}/${storageOffset.partition}"

            try {
                if (client.checkExists().forPath(consumerOffSetPath) == null) {
                    client.create.creatingParentsIfNeeded().forPath(consumerOffSetPath)
                }

                client.setData.forPath(
                    consumerOffSetPath,
                    storageOffset.offset.toString.getBytes(UTF8)
                );
            } catch {
                case e: Exception =>
                    logger.error(s"Exception during commit offset ${storageOffset.offset} for topic" +
                            s"${storageOffset.topic}, partition ${storageOffset.partition}", e)
            }

            logger.info(s"Committed offset ${storageOffset.offset} for topic ${storageOffset.topic}, " +
                    s"partition ${storageOffset.partition}")
        }
    }

    def getOffsets(consumerGroupId: String, topic: String): Map[TopicPartition, Long] = {
        var fromOffsets = Map[TopicPartition, Long]()

        // /consumers/{consumer-group-id}/offsets/topics/
        val topicDirs = new ZKGroupTopicDirs(consumerGroupId, topic)

        val childPath =
            if (client.checkExists().forPath(topicDirs.consumerOffsetDir) != null) {
                client.getChildren().forPath(topicDirs.consumerOffsetDir).toList
            } else {
                Nil
            }

        childPath.map { partition =>
            val completePath = s"${topicDirs.consumerOffsetDir}/${partition}"
            try {
                val currentOffset =
                    new String(client.getData().forPath(completePath), UTF8).toLong
                fromOffsets += (new TopicPartition(topic, partition.toInt) -> currentOffset)
            } catch {
                case e: Exception =>
                    logger.error(s"Exception during reading consumer offsets from path ${completePath}", e)
                    throw e
            }
        }
        fromOffsets
    }

    def close(): Unit = {
        logger.info("ZookeeperClient is closing")
        CloseableUtils.closeQuietly(client)
    }

}

object ZookeeperClient extends Logging {

    def connect(connectString: String) = {
        logger.info("Initializing ZookeeperClient")
        val retryPolicy = new ExponentialBackoffRetry(1000, 3)
        val client = CuratorFrameworkFactory.newClient(connectString, retryPolicy)
        client.start()
        new ZookeeperClient(client)
    }

}
