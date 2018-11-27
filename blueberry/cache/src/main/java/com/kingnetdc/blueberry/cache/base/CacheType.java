package com.kingnetdc.blueberry.cache.base;


/**
 * @author jake.zhang <zhangxj@kingnet.com>
 */
public enum CacheType {

    /**
     * 内存缓存
     */
    MEMORY(1, "memory"),

    /**
     * MYSQL 数据库缓存
     */
    MYSQL(2, "mysql"),

    /**
     * REDIS 单实例缓存
     */
    REDIS(3, "redis"),

    /**
     * REDIS 集群缓存
     */
    REDIS_CLUSTER(4, "redis-cluster"),

    /**
     * MYSQL 数据库连接池缓存
     */
    MYSQL_DATASOURCE(5, "mysql-datasource");

    /**
     * 索引
     */
    private int index;

    /**
     * 名称
     */
    private String name;

    public String getName() {
        return name;
    }

    CacheType(int index, String name) {
        this.index = index;
        this.name = name;
    }

}
