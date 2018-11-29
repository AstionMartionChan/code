#功能：
读取Kafka里的json格式数据存入对应的Hive表中

#支持的需求
支持读取单topic里的数据存入Hive表，
支持读取多topic里的数据存入Hive表

#使用说明
SingleTopic2Hive：支持单个或者多个topic里的数据共同存入一张Hive表。（前提：json数据结构相同）
MultiTopics2Hive：支持多个topic里的数据分别存入多张Hive表。

#配置参数
application.conf：该文件目前在resources包下，里面具体定义了scalikejdbc需要的连接数据库的配置
（后期可以改成把参数配置在properties文件中，JdbcHandler中做读取配置的修改）
```properties
#JDBC settings
db.default.driver="com.mysql.jdbc.Driver"
db.default.url="jdbc:mysql://localhost:3306/leo?useSSL=false"
db.default.user="root"
db.default.password="123456"
# Connection Pool settings
db.default.poolInitialSize=10
db.default.poolMaxSize=20
db.default.poolConnectionTimeoutMillis=1000
```

xxx.properties：名称根据业务名定义，里面具体定义kafka地址以及topic和group
```properties
kafka.bootstrap.servers=172.16.32.112:9092,172.16.32.140:9092,172.16.32.52:9092
topics=wtwblist,wutiao_super
consumer.group=cfy
```

xxx.conf: 名称根据业务名定义，里面具体定义了spark相关参数和offset管理的外部存储zk , mysql，如果是zk还需定义zk的地址
```properties
# 程序相关
spark.kafka.offset.storage=zk
spark.zk.connect=172.27.6.54:2181,172.27.6.103:2181,172.27.6.141:2181
# spark相关
spark.output.partition=10
spark.streaming.duration=300000
spark.task.maxFailures=4
spark.streaming.kafka.maxRatePerPartition=10000
spark.sql.shuffle.partitions=50
spark.streaming.ui.retainedBatches=500
spark.streaming.stopGracefullyOnShutdown=true
spark.ui.showConsoleProgress=false
spark.yarn.submit.waitAppCompletion=false
spark.hive.url=hwwg-bigdata-hadoopnn-prod-1
spark.hive.port=9083
```

#数据库配置表
表名：t_kafka2hive_relation_config

topic -> topic的名称

hive_table -> 对应需要存入的hive表名称

relation_json -> 描述hive表字段和json中数据的对应关系，以及hive partition语句的详情

#relation_json对应关系
```properties
{
    "relation":[ //描述hive表结构与json数据的关系， 字段一定要按照hive表字段顺序罗列
        {
            "jsonPath":"ouid",    // json中的key，如果有嵌套用.的形式，例 properties.timestamp
            "hiveField":"ouid",   // hive表的字段名称
            "hiveType":"string"   // hive表的字段类型 string, long, int, double, boolean 暂时只支持这五种
        },
        {
            "jsonPath":"properties.timestamp",
            "hiveField":"timestamp",
            "hiveType":"long"
        }
    ],
    "partition":[ // 最后insert 语句中的partition定义
        {
            "name":"eventtype", // partition的名称
            "expression":"'low' as eventtype" // partition的值
        },
        {
            "name":"ds",
            "expression":"from_unixtime(unix_timestamp(),'yyyy-MM-dd') as ds"
        },
        {
            "name":"hour",
            "expression":"from_unixtime(unix_timestamp(),'yyyyMMddHH') as hour"
        }
    ],
    "where":"" // 过滤条件
}
```
