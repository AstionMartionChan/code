package com.cfy.utils

import com.cfy.utils.SafaRelease.Closeable
import org.apache.curator.framework.{CuratorFramework, CuratorFrameworkFactory}
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.kafka.common.TopicPartition

import scala.collection.JavaConverters._

/**
  * Created by IntelliJ IDEA.
  * User: Leo_Chan
  * Date: 2018/11/26
  * Time: 10:50
  * Work contact: Astion_Leo@163.com
  */


class ZkClient (private val client: CuratorFramework) {


  def getOffsets(topic: String, group: String) : Map[TopicPartition, Long] = {
    val path = s"/kafka2hive/${group}/offsets/${topic}"

    if (client.checkExists.forPath(path) != null){
      val partitions = client.getChildren.forPath(path)
      partitions.asScala.toList.map(partition => {
        val fullPath = s"${path}/${partition}"
        val offset = client.getData.forPath(fullPath)
        (new TopicPartition(topic, partition.toInt) -> new String(offset).toLong)
      }).toMap
    } else {
      Map()
    }
  }


  def commitOffsets(topic: String, group: String, partition: Int, offset: Long) = {
    val path = s"/kafka2hive/${group}/offsets/${topic}/${partition}"
    if (client.checkExists.forPath(path) != null){
      client.setData.forPath(path, offset.toString.getBytes())
    } else {
      client.create.creatingParentContainersIfNeeded.forPath(path, offset.toString.getBytes())
    }
  }

  def close() = {
    if (client != null){
      client.close
    }
  }
}

object ZkClient {

  def getInstance(zkAddress: String) = {
    val retryPolicy  = new ExponentialBackoffRetry(1000, 3);
    val client = CuratorFrameworkFactory.newClient(zkAddress, retryPolicy)
    client.start()
    new ZkClient(client)
  }
}