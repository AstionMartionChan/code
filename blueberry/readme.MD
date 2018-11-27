# blueberry 使用说明

[TOC]


## 模块列表和主要功能列表

### core 基础模块
基础模块, 提供了JDK 代码后的基本封装
1. JSON  : 使用 fastjson 作为 json 解析的类库
2. io    : 文件路径解析, 文件读写的功能
3. cache : 基于 guava cache 的使用封装
4. yml   : yml 文件的解析库

### cache 缓存模块
缓存模块, 提供可配置缓存访问
- 缓存可以分为多级
- 缓存支持的类型 memCache, redisCache, mysqlCache, localCache 4种类型
- 每种类型有自己特有的属性
- 通过构造缓存参数或加载 yml 配置文件设置

|类型|主要功能|实现方式|特殊说明|
| ------ |:-------:|:-------:|:-------:|
|memCache|容量可设置的内存级缓存|使用 guava cache 作为基础|缓存存储, Key不能是基础类型,需要是一个对象|
|redisCache|可扩展分布式内存缓存|redis 和 redis cluster 2种实现方式|缓存存储, key/value 可以是字符串和字节数组|
|mysqlCache|数据库缓存|有长连接和短连接的2种实现|持久化存储, 可以设置是否保存功能|
|localCache|通过设置本地目录放置的缓存|使用 rocksDB 作为本地磁盘存储实现的方式|持久化存储, 可以设置是否保存保存功能|

### mysql 数据库访问模块
数据库访问, 提供连接池和普通的连接

### kafka 访问模块
提供 Kafka 的发送和读取, 读取时触发保存 offset

### zookeeper 模块
使用 apache curator 访问 zookeeper

