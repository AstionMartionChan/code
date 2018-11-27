package com.kingnetdc.blueberry.cache;

import com.kingnetdc.blueberry.cache.base.Constants;
import com.kingnetdc.blueberry.cache.base.JedisClusterPipeline;
import com.kingnetdc.blueberry.cache.base.Tuple3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Response;

import java.util.*;

import static com.kingnetdc.blueberry.cache.base.Constants.*;

/**
 * @author zhouml <zhouml@kingnet.com>
 */
public class RedisClusterCache implements ICache, AutoCloseable {

    private final static Logger logger = LoggerFactory.getLogger(RedisClusterCache.class);

    private KdcRedisCluster kdcRedisCluster;

    public RedisClusterCache(Map<String, String> configs) {
        String redisConnector = configs.get(REDIS_CLUSTER_CONNECT);
        String password = null != configs.get(REDIS_PASSWORD) ?
                configs.get(REDIS_PASSWORD) : null;
        int poolNum = null != configs.get(REDIS_POOL_NUM) ?
                Integer.valueOf(configs.get(REDIS_POOL_NUM)) : Constants.DEFAULT_REDIS_POOL_NUM;
        kdcRedisCluster = new KdcRedisCluster(redisConnector, password, poolNum);
    }

    public RedisClusterCache(String redisConnector, String password, int pooNum) {
        kdcRedisCluster = new KdcRedisCluster(redisConnector, password, pooNum);
    }

    /**
     * 解析配置文件，转换IP主机列表
     * @param connect
     * @return
     */
    private Set<HostAndPort> parseConnect(String connect) {
        if (connect == null || connect.trim().length() == 0) {
            throw new IllegalArgumentException("Redis cluster connect should be in format host1:port1,host2:port2");
        }
        Set<HostAndPort> hostsAndPortSet = new HashSet<>();
        String[] hostsAndPorts = connect.split(",");
        for (int i = 0; i < hostsAndPorts.length; i++) {
            String[] hostAndPort = hostsAndPorts[i].split(":");
            String host = hostAndPort[0];
            String port = hostAndPort[1];
            hostsAndPortSet.add(new HostAndPort(host, Integer.valueOf(port)));
        }
        return hostsAndPortSet;
    }

    @Override
    public String get(String key) {
        return kdcRedisCluster.getJedisCluster().get(key);
    }

    @Override
    public void set(String key, String value, int expire) {
        kdcRedisCluster.getJedisCluster().setex(key, expire, value);
    }

    /**
     * 检查单个key是否存在
     * @param key
     * @return
     */
    @Override
    public boolean exists(String key) {
        return kdcRedisCluster.getJedisCluster().exists(key);
    }


    /**
     * 判断多个key是否存在
     * @param keys 待判断的key
     * @return 存在的key, 减少network overload
     */
    @Override
    public Map<String, Boolean> multiExists(Collection<String> keys) {
        JedisClusterPipeline clusterPipeline = JedisClusterPipeline.pipelined(kdcRedisCluster.getJedisCluster());
        Map<String, Boolean> resultSet = new HashMap<>();

        try {
            clusterPipeline.refreshCluster();
            Map<String, Response<Boolean>> responseMap = new HashMap<>();

            for (String key : keys) {
                responseMap.put(key, clusterPipeline.exists(key));
            }
            clusterPipeline.sync();

            responseMap.forEach((String key, Response<Boolean> response) -> {
                if (response.get()) {
                    resultSet.put(key, true);
                } else {
                    resultSet.put(key, false);
                }
            });

        } finally {
            if (clusterPipeline != null) {
                clusterPipeline.close();
            }
        }
        return resultSet;
    }

    @Override
    public void remove(String key) {
        kdcRedisCluster.getJedisCluster().del(key);
    }


    @Override
    public Map<String, String> multiGet(Collection<String> keys) {
        JedisClusterPipeline clusterPipeline = JedisClusterPipeline.pipelined(kdcRedisCluster.getJedisCluster());
        Map<String, String> resultMap = new HashMap<>();

        try {
            Map<String, Response<String>> responseMap = new HashMap<>();

            for (String key : keys) {
                responseMap.put(key, clusterPipeline.get(key));
            }
            clusterPipeline.sync();

            responseMap.forEach((String key, Response<String> response) -> {
                resultMap.put(key, response.get());
            });
        } finally {
            if (clusterPipeline != null) {
                clusterPipeline.close();
            }
        }
        return resultMap;
    }

    @Override
    public void multiSet(Collection<Tuple3> tuples) {
        JedisClusterPipeline clusterPipeline = JedisClusterPipeline.pipelined(kdcRedisCluster.getJedisCluster());
        try {
            clusterPipeline.refreshCluster();
            for (Tuple3 tuple : tuples) {
                clusterPipeline.setex(tuple.getKey(), tuple.getExpire(), tuple.getValue());
            }
            clusterPipeline.sync();
        } finally {
            if (clusterPipeline != null) {
                clusterPipeline.close();
            }
        }
    }

    @Override
    public void close() {
        kdcRedisCluster.close();
    }

}
