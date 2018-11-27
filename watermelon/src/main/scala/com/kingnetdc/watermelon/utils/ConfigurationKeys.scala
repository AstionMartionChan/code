package com.kingnetdc.watermelon.utils

/**
  * Created by zhouml on 16/05/2018.
  */
object KafkaOffsetStorage extends Enumeration {

    val ZK = Value(1, "zookeeper")
    val MYSQL = Value(2, "mysql")

}

object ConfigurationKeys {

    // kafka
    val TOPIC = "topic"
    val BootstrapServers = "bootstrap.servers"
    val KAFKA_GROUP = "group.id"
    val ZkConnect = "zookeeper.connect"

    val SPARK_KAFKA_OFFSET_STORAGE = "spark.kafka.offset.storage"

    // zookeeper for storage
    val DEFAULT_SPARK_KAFKA_OFFSET_STORAGE = "zookeeper"
    val SPARK_KAFKA_OFFSET_ZK_CONNECT = "spark.kafka.offset.zookeeper.connect"


    // mysql
    val MYSQL_JDBC_DRIVER = "com.mysql.jdbc.Driver"
    val MYSQL_URL = "url"
    val MYSQL_USER = "user"
    val MYSQL_PASSWORD = "password"
    val MYSQL_TABLE = "table"
    val MYSQL_BATCHSIZE = "bacthSize"

    val SPARK_OFFSET_MYSQL_URL = "spark.kafka.offset.mysql.url"
    val SPARK_OFFSET_MYSQL_USER = "spark.kafka.offset.mysql.user"
    val SPARK_OFFSET_MYSQL_PW = "spark.kafka.offset.mysql.password"
    val SPARK_OFFSET_MYSQL_TABLE = "spark.kafka.offset.mysql.table"
    val SPARK_KAFKA_OFFSET_MYSQL_RESTARTFROM = "spark.kafka.offset.mysql.restart-from"


    // redis
    val REDIS_HOST = "host"
    val REDIS_PORT = "port"


    // spark
    val SPARK_CP_DIR = "spark.checkpoint.directory"

    // spark重启时, 指定restore-from, 即从cp.path下面的哪个时间点恢复, 格式为yyyyMMddHHmm
    val SPARK_RDD_CP_PATH = "spark.rdd.cp.path"
    val SPARK_RDD_CP_RESTORE_FROM = "spark.rdd.cp.restore-from"

    val SPARK_RDD_CP_INTERVAL = "spark.rdd.cp.interval"
    val DEFAULT_SPARK_RDD_CP_INTERVAL = 5 * 60 * 1000 // 5 min

    val SPARK_KAFKA_OFFSET_INTERVAL = "spark.kafka.offset.interval"
    val DEFAULT_SPARK_KAFKA_OFFSET_INTERVAL = 5 * 60 * 1000 // 5 min

    val SPARK_RDD_CP_PARTITION = "spark.rdd.cp.partition"
    val DEFAULT_RDD_CP_PARTITION = 10

    val SPARK_STREAMING_DURATION = "spark.streaming.duration"

    val SPARK_STREAMING_PARTITION = "spark.streaming.partition"
    val DEFAULT_STREAMING_PARTITION = 50

    val SPARK_OUTPUT_PARTITION = "spark.output.partition"
    val DEFAULT_OUTPUT_PARTITION = 20

    val SPARK_STREAMING_STATE_CHECK_INTERVAL = "spark.streaming.state.check.interval"
    val SPARK_STREAMING_STOP_SIGNAL = "spark.streaming.stop.signal"
    val SPARK_STREAMING_STATE_CHECK_REDIS_CONNECT = "spark.streaming.state.check.redis.connect"
    val SPARK_STREAMING_STATE_CHECK_REDIS_PASSWORD = "spark.streaming.state.check.redis.password"

    // influxdb
    val INFLUXDB_HOST = "host"
    val INFLUXDB_USERNAME = "username"
    val INFLUXDB_PASSWORD = "password"
    val INFLUXDB_DB = "database"

}
