## blockchain-realtime-computation

区块链实时指标计算

## 1.基本结构

+ clients -- 客户端相关封装
+ job -- 具体的指标计算入口, EventStatistics为所有指标计算的抽象
+ metrics -- 指标聚合的相关的抽象, 分为局部聚合(Aggregator)和最终聚合(MetricsBuffer)
+ model -- 对于事件(Event), 结果记录(KPIRecord)等封装
+ sink -- 输出源的抽象, MySqlSink | InfluxDBSink
+ streaming -- 对于DirectKafkaStream的封装
+ utils -- 各种帮助类

## 2.配置文件

目前分为三类:

+ 流式项目相关的配置, 如Kafka, Zookeeper, MySQL, InfluxDB等, 采用yaml文件, 使用snakeyaml进行解析

+ 对于spark相关的配置, 统一采用脚本启动时进行传递

```bash
# 通过conf
spark-submit  --conf
```

```bash
# 通过conf
spark-submit  --properties-file
```

+ 对于需要使用cache的统计, 则需要额外配置cache.yaml, 具体配置参见blueberry README

## 3.命名规范

### 3.1 Appname命名规则

kingnetdc-${项目}-${指标大类}-statistics

eg:

kingnetdc-blockchain-device-statistics
kingnetdc-blockchain-user-statistics

### 3.2 Kafka group命名规则

kingnetdc-${项目}-${指标大类}-group

eg:

kingnetdc-blockchain-user-group
kingnetdc-blockchain-device-group

### 3.3 Checkpoint & Snapshot命名规则

hdfs://${namespace}/kingnetdc/streaming/${项目}/${指标大类}/checkpoint
hdfs://${namespace}/kingnetdc/streaming/${项目}/${指标大类}/snapshot

eg:

hdfs://localhost/kingnetdc/streaming/blockchain/user/checkpoint
hdfs://localhost/kingnetdc/streaming/blockchain/user/snapshot
hdfs://localhost/kingnetdc/streaming/blockchain/device/checkpoint
hdfs://localhost/kingnetdc/streaming/blockchain/device/snapshot

### 3.4  存储Offset的MySQL表相关命名

+ 表

tbl_${项目}_${指标大类}_offset

eg:

tbl_wutiao_exchange_rate_offset

+ 主键

pk_tbl_${项目}_${指标大类}_offset

eg:

pk_tbl_wutiao_exchange_rate_offset


### 3.4 项目部署文件结构

```bash
/srv/apps/kingnetdc/${项目}/
                            target/ --- jar包所在位置
                            bin/    --- 脚本所在位置

/srv/logs/kingnetdc/${项目}/
                           blockchain-device-statistics.log
```


## 基本用例

参见com.kingnetdc.job.DeviceStatistics

## 项目部署

+ 运行项目根目录下面的Build.scala生成相应的jar包, xx.jar


## 分支管理

每一个项目新建一个分支, 开发完成并测试通过之后再合并到主分支上, 形成如下结构

```bash
master
    blockchain-phase-1
    blockchain-phase-2
```

如果有公共部分的修改, 如帮助类, 底层逻辑修改则直接从master新开分支, 如temp-fix, 开发完成之后合并进入主分支, 其它分支再拉取更新

```bash
master
   temp-fix
```

























