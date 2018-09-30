package com.cfy.utils

object KafkaOffsetStorage extends Enumeration {

  val ZK = Value(1, "zookeeper")
  val MYSQL = Value(2, "mysql")

}
