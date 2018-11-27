package com.kingnetdc.blueberry.cache;


import com.kingnetdc.blueberry.cache.base.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author jake.zhang <zhangxj@kingnet.com>
 */
public class KdcRedisCluster {

    private static final Logger logger = LoggerFactory.getLogger(KdcRedisCluster.class);

    private JedisCluster jedisCluster;

    public KdcRedisCluster(String connector) {
        this(connector, null, Constants.DEFAULT_REDIS_POOL_NUM);
    }

    public KdcRedisCluster(String connector, String password) {
        this(connector, password, Constants.DEFAULT_REDIS_POOL_NUM);
    }

    public KdcRedisCluster(String connector, String password, int poolNum) {
        checkNotNull(connector, "redis connector can't null.");
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(poolNum);
        config.setMaxTotal(poolNum);
        config.setTestOnBorrow(true);
        jedisCluster = new JedisCluster(parseConnector(connector), Protocol.DEFAULT_TIMEOUT, Protocol.DEFAULT_TIMEOUT,
                3, password, config);
    }

    private Set<HostAndPort> parseConnector(String connector) {
        Set<HostAndPort> hostAndPorts = new HashSet<>();
        if (null != connector && connector.trim().length() > 0) {
            String[] hostsAndPorts = connector.split(",");
            for (int i = 0; i < hostsAndPorts.length; i++) {
                String[] hostAndPort = hostsAndPorts[i].split(":");
                String host = hostAndPort[0];
                String port = hostAndPort[1];
                if (host.length() > 0 && port.length() > 0) {
                    hostAndPorts.add(new HostAndPort(host, Integer.valueOf(port)));
                }
            }
        }
        return hostAndPorts;
    }

    public JedisCluster getJedisCluster() {
        return jedisCluster;
    }

    public void close() {
        try {
            jedisCluster.close();
        } catch (Throwable e) {
            logger.error("close jedis cluster error.", e);
        }
    }

}
