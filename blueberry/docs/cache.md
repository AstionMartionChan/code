# 缓存说明

## 功能列表
### 可伸缩的三级缓存功能

### 文件读写功能

### MySQL连接池功能


## 缓存功能配置文件模板

+ 项目名字要配置(用于通用的报错汇总)
+ set int = 0 时不过期
+ Mysql中的表格如果没有建立，会自动建表
+ 使用完成之后，要记得关闭KdcCache


```yaml
retryPolicy:
  retryNum: 1
  retryIdle: 1000
keyPrefix: ""
items:
    -
        cacheLevel: 1
        cacheType: local
        props:
            capacity: 10000
    -
        cacheLevel: 2
        cacheType: redis
        props:
            host: localhost
            port: 6379
    -
        cacheLevel: 3
        cacheType: mysql
        props:
            dbUrl: jdbc:mysql://localhost:3306/kingnet?useSSL=false
            user: xxxx
            password: localhost
            table: tbl_key_value
            keyColumnName: key -- 可选 默认为key
            valueColumnName: value -- 可选 默认为value
            batchSize: 1000 -- 可选 默认为1000
            writeBack : false -- 可选 默认为false
            keyPartition: 20 -- 可选 默认为20

```


### 使用说明

+ 创建KdcCache

```java
// cache.yml文件的FileInputStream
KdcCache cache = KdcCacheLoader.load(is)

// cache.yml文件的绝对路径
KdcCache cache = KdcCacheLoader.load(path)

// 直接传入CacheConfig
KdcCache cache = KdcCacheLoader.load(config)
```

+ get or set操作

```java
kdcCache.get
kdcCache.set
```

+ 关闭

```java
kdcCache.close()
```



