## watermelon

提供大数据相关的工具, eg: Spark offset提交, Spark计算输出的的相关封装

## 1. Offset存储与提交

目前支持Zookeeper和MySQL, 前者只支持从最新的Offset恢复, 后者支持从指定的时间点恢复, 同时对于表的结构进行了约束, 如下(表名可自由指定, 支持自动表创建):


```sql
create table if not exists tbl_xxx_xxx_xxx  (
  `time`  TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '存储的时间', -- 插入或更新时不修改此字段
  `group` VARCHAR(255) NOT NULL COMMENT '消费组' ,
  `topic` VARCHAR(255) NOT NULL COMMENT '话题' ,
  `partition` int NOT NULL COMMENT '分区' ,
  `offset` bigint NOT NULL COMMENT '偏置' ,
  `last_update` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间' ,
   PRIMARY KEY `time_topic_partition` (`time`, `group`, `topic`, `partition`)
);
```

其中初始化DirectKafkaInputStream的时候会根据配置从指定存储读取, 而存储则交由调用方, 在适当时间点自行提交.

支持**同时从多个topic读取数据并提交, 但这些topic必须同组**

另外首次启动的时候，确保对应的topic都没有相应的Offset存储(无论是Zookeeper还是MySQL)

```scala
def getKafkaParams(): Map[String, Object] = {
        Map(
            "topic" -> "kafka-word-count1,kafka-word-count2",
            "bootstrap.servers" -> "localhost:9092",
            "group.id" -> "kafka-word-count-group",
            "zookeeper.connect" -> "localhost:2181",
            "key.deserializer" -> classOf[StringDeserializer],
            "value.deserializer" -> classOf[StringDeserializer],
            "enable.auto.commit" -> (false: java.lang.Boolean)
        )
}
```




**建议使用Streaming batch time或者是统一到某个时间窗口的时间**


```bash
spark.kafka.offset.storage=zookeeper or
spark.kafka.offset.storage=mysql
```

+ Zookeeper存储相关配置

```bash
spark.kafka.offset.zookeeper.connect
```

+ MySQL存储相关配置

```bash
spark.kafka.offset.mysql.url
spark.kafka.offset.mysql.user
spark.kafka.offset.mysql.password
spark.kafka.offset.mysql.table
spark.kafka.offset.mysql.restart-from
```

restart-from 格式为yyyy-MM-dd HH:mm:ss, 此处设置为必选, 防止后续重启时忘记传递导致从最新的开始读取;
第一次的话, 可以设置为当前的某个时间点

spark.kafka.offset.mysql.restart-from 传递的格式:

+ 在spark-defaults.conf

```bash
# 注意转义字符
spark.kafka.offset.mysql.restart-from=2018-05-19\ 15:57:30
```

+ conf

```bash
--conf spark.kafka.offset.mysql.restart-from="2018-05-19 15:57:30"
```

+ environment

```bash
-Dspark.kafka.offset.mysql.restart-from="2018-05-19 15:57:30"
```


另外由于Spark默认的DirectKafkaInpuStream构造时, 会要求传递消费组group.id, 所以不用在上面两种存储使用中再次传递

详见com.kingnetdc.watermelon.example.KafkaWordCount.sparkConfForMySQLStorage()以及sparkConfForZKStorage()

## 2. RDD输出

在Spark, 向外输出结果一般是以分区为单位, 所以MySQLSink提供的方法也是基于Partition, 这样可以自行调整分区数以及灵活处理写入之后的失败
目前支持MySQL, HBase, Kafka

### 2.1 MySQL

对于插入, 采用**insert into ... on duplicate key update**, 若重复则更新的column以及row value

详见com.kingnetdc.watermelon.example.KafkaWordCount.writeResultToMySQL

### 2.2 Kafka

Kafka消息发送分为异步和同步两种方式

+ 同步, 每次发送之后会阻塞等待发送结果. 如果失败会抛出相应的错误, 成功则返回RecordMetadata

+ 异步, 发送之后立即开始下一条

基于吞吐量以及稳定性的考虑, 所以一般目前只提供了异步的封装. 另外, 由于异步执行, 所以不能像MySQL那样, 每一个分区建立连接之后销毁,
因为需要在每一个Executor上建立一个, 然后在Executor关闭时同步销毁. 因此, 延迟初始化并且使用broadcast进行广播

详见com.kingnetdc.watermelon.example.KafkaWordCount.writeResultToKafka
