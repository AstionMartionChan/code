package com.itcast.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by leochan on 2018/2/4.
 */
public class RedisUtil {

    static {
        Properties prop = new Properties();
        InputStream resourceAsStream = RedisUtil.class.getResourceAsStream("/systemConfig.properties");

        try {
            prop.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String address = prop.getProperty("redis.address").trim();
        String port = prop.getProperty("redis.port").trim();
        String maxTotal = prop.getProperty("redis.max_total").trim();
        String maxIdle = prop.getProperty("redis.max_idle").trim();
        String maxWait = prop.getProperty("redis.max_wait").trim();

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(Integer.valueOf(maxTotal));
        config.setMaxIdle(Integer.valueOf(maxIdle));
        config.setMaxWaitMillis(Integer.valueOf(maxWait));
        config.setTestOnBorrow(true);
        jedisPool = new JedisPool(config, address, Integer.valueOf(port));
    }

    private static JedisPool jedisPool;

    private static Jedis jedis = jedisPool.getResource();

    public static void lpush(String key, String value) {
        jedis.lpush(key, value);
    }

    public static String rpop(String key) {
        return jedis.rpop(key);
    }

    public static Long llen(String key) {
        return jedis.llen(key);
    }

    public static void rpush(String key, String value){
        jedis.rpush(key, value);
    }
}
