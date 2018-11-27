package com.cfy

import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry

/**
  * Created by IntelliJ IDEA.
  * User: Leo_Chan
  * Date: 2018/11/25
  * Time: 21:59
  * Work contact: Astion_Leo@163.com
  */


object ZkClient {

  def main(args: Array[String]) {
    val retryPolicy  = new ExponentialBackoffRetry(1000, 3);
    val client = CuratorFrameworkFactory.newClient("localhost:2181", retryPolicy)
    client.start()

    client.create().creatingParentContainersIfNeeded().forPath("/kafka2hive1/group/topic/1", "CFY111".getBytes())


    val exists = client.checkExists.forPath("/kafka2hive1")
    println(exists)

  }

}
