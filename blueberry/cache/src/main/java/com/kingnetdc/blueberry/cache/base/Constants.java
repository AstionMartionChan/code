package com.kingnetdc.blueberry.cache.base;


/**
 * @author zhouml <zhouml@kingnet.com>
 */
public class Constants {

    /**
     * memory cache 最大值
     */
    public static final String MEM_CACHE_CAPACITY = "capacity";

    public static final int DEFAULT_MEM_CACHE_CAPACITY_SIZE = 1000;


    /**
     * redis 集群的 主机和端口
     */
    public static final String REDIS_HOST = "host";
    public static final String REDIS_PORT = "port";
    public static final String REDIS_PASSWORD = "password";
    public static final String REDIS_DATABASE = "database";

    public static final int DEFAULT_REDIS_PORT = 6379;
    public static final int DEFAULT_REDIS_DATABASE = 0;


    /**
     * redis cluster 连接配置
     * host1:port1,host2:port2
     */
    public static final String REDIS_CLUSTER_CONNECT = "connect";

    /**
     * 数据库相关的配置
     */
    public static final String MYSQL_DB_URL = "dbUrl";
    public static final String MYSQL_USERNAME = "username";
    public static final String MYSQL_PASSWORD = "password";
    public static final String MYSQL_WRITE_BACK = "writeBack";
    public static final String MYSQL_CACHE_TABLE = "table";
    public static final String MYSQL_BATCH_SIZE = "batchSize";
    public static final String MYSQL_KEY_PARTITION = "keyPartition";
    public static final String MYSQL_AUTO_CREATE_TABLE = "autoCreateTable";

    public static final String MYSQL_KEY_NAME = "keyColumnName";
    public static final String MYSQL_VALUE_NAME = "valueColumnName";


    public static final boolean DEFAULT_MYSQL_WRITE_BACK = false;
    public static final int DEFAULT_MYSQL_BATCH_SIZE = 1000;
    public static final int DEFAULT_MYSQL_KEY_PARTITION = 20;
    public static final String DEFAULT_MYSQL_KEY_NAME = "key";
    public static final String DEFAULT_MYSQL_VALUE_NAME = "value";
    public static final boolean DEFAULT_MYSQL_AUTO_CREATE_TABLE = true;


    /**
     * MYSQL 连接池的最大连接个数
     */
    public static final String MYSQL_POOL_NUM = "poolNum";

    public static final int DEFAULT_MYSQL_POOL_NUM = 20;

    /**
     * REDIS 连接池的最大连接个数
     */
    public static final String REDIS_POOL_NUM = "poolNum";

    public static final int DEFAULT_REDIS_POOL_NUM = 10;



    /**
     * 默认重试次数
     */
    public static final int DEFAULT_CONFIG_RETRY_NUM = 1;

    /**
     * 默认重试的空闲时间
     */
    public static final int DEFAULT_CONFIG_RETRY_IDLE = 1000;

    /**
     * 默认的前缀
     */
    public static final String DEFAULT_CONFIG_PREFIX = "";

    /**
     * 设置默认hash开关
     */
    public static final boolean DEFAULT_NEED_HASH = true;

    /**
     * 设置在 get / mulitGet 的过程中是否往前面的缓存回写功能
     */
    public static final boolean DEFAULT_GET_WRITE_BACK = false;

    /**
     * 设置在 get / mulitGet 的过程中写入缓存的时间，单位是秒
     */
    public static final int DEFAULT_GET_WRITE_BACK_TIME = 86400;

}

