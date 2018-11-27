package com.kingnetdc.blueberry.cache;


import com.kingnetdc.blueberry.cache.base.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * @author jake.zhang <zhangxj@kingnet.com>
 */
public class KdcRedis {

    private static final Logger logger = LoggerFactory.getLogger(KdcRedis.class);

    private JedisPool jedisPool;

    public KdcRedis(String host, int port) {
        this(host, port, null, Constants.DEFAULT_REDIS_POOL_NUM, 0);
    }

    public KdcRedis(String host, int port, int poolNum) {
        this(host, port, null, poolNum, 0);
    }

    public KdcRedis(String host, int port, String password) {
        this(host, port, password, Constants.DEFAULT_REDIS_POOL_NUM, 0);
    }

    public KdcRedis(String host, int port, String password, int poolNum) {
        this(host, port, password, poolNum, 0);
    }

    public KdcRedis(String host, int port, String password, int poolNum, int database) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(poolNum);
        config.setMaxTotal(poolNum);
        config.setTestOnBorrow(true);
        jedisPool = new JedisPool(config, host, port, Protocol.DEFAULT_TIMEOUT, password, database);
    }

    public Jedis getJedis() {
        return jedisPool.getResource();
    }

    public void close() {
        try {
            jedisPool.close();
        } catch (Throwable e) {
            logger.error("close jedis pool error.", e);
        }
    }

}
