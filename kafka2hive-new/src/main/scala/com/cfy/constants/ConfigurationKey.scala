package com.cfy.constants

object ConfigurationKey {


  val COMMA = ","
  val POINT = "."
  val UNDERLINE = "_"
  val OF: String = "OF"
  val BLANKSPACE = "            "
  val NEWLINE = System.getProperty("line.separator")

  val YMDHMS = "yyyy-MM-dd HH:mm:ss"

  val INT = "int"
  val LONG = "long"
  val DOUBLE = "double"
  val STRING = "string"
  val BOOLEAN = "boolean"
  val NULL = "null"


  val JDBC_DRIVER = "jdbc.driver"
  val JDBC_URL = "jdbc.url"
  val JDBC_USERNAME = "jdbc.username"
  val JDBC_PASSWORD = "jdbc.password"
  val JDBC_POOL_NUM = "jdbc.pool.num"

  val GROUP = "group.id"
  val KAFKA_TOPIC = "topic"
  val KAFKA_UNIQUE_ID = "kafka_unique_id"
  val KAFKA_TOPICS = "topics"
  val KAFKA_CONSUMER_GROUP = "consumer.group"
  val KAFKA_BOOTSTRAP_SERVERS = "kafka.bootstrap.servers"
  val KAFKA_OFFSET_RESET = "kafka.offset.reset"
  val KAFKA_OFFSET_STORAGE = "kafka.offset.storage"
  val ZK_CONNECT = "zk.connect"
  val MYSQL = "mysql"
  val ZK = "zk"


  val MYSQL_STORAGE_TABLENAME = "mysql.storage.tablename"
  val MYSQL_HIVE_CONFIG_TABLENAME = "mysql.hive.config.tablename"

  val HIVE_IP = "hive.ip"
  val HIVE_PORT = "hive.port"

  val SPARK_STREAMING_DURATION = "spark.streaming.duration"
  val SPARK_TEMP_TABLE_NAME = "t_temp"


  val KAFKA_OFFSET_MYSQL_TABLE = "kafka.offset.mysql.table"


}
