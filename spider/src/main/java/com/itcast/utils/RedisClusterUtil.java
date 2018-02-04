package com.itcast.utils;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Created by leochan on 2018/2/3.
 */
public class RedisClusterUtil {

    static {

        try {
            Properties prop = new Properties();
            InputStream resourceAsStream = MySqlUtil.class.getResourceAsStream("/systemConfig.properties");

            prop.load(resourceAsStream);
            String address = prop.getProperty("redis.address").trim();
            String ports = prop.getProperty("redis.ports").trim();

            Set<HostAndPort> nodes = new HashSet<HostAndPort>();
            for (String port : ports.split(",")){
                nodes.add(new HostAndPort(address, Integer.valueOf(port)));
            }
            jedisCluster = new JedisCluster(nodes);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static JedisCluster jedisCluster;


    public static void lpush(String key, String value) {
        jedisCluster.lpush(key, value);
    }

    public static String rpop(String key) {
        return jedisCluster.rpop(key);
    }
}
