package com.kingnetdc.blueberry.cache;

import com.kingnetdc.blueberry.cache.base.Constants;
import com.kingnetdc.blueberry.cache.base.Tuple3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.*;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.kingnetdc.blueberry.cache.base.Constants.*;

/**
 * @author zhouml <zhouml@kingnet.com>
 */
public class RedisCache implements ICache, AutoCloseable {

    private final static Logger logger = LoggerFactory.getLogger(RedisCache.class);

    private KdcRedis kdcRedis;


    public RedisCache(String host, int port, String password, int database) {
        checkNotNull(host, "redis host can't null.");
        kdcRedis = new KdcRedis(host, port, password, Constants.DEFAULT_REDIS_POOL_NUM, database);
    }

    public RedisCache(String host, int port, String password, int poolNum, int database) {
        checkNotNull(host, "redis host can't null.");
        kdcRedis = new KdcRedis(host, port, password, poolNum, database);
    }

    public RedisCache(Map<String, String> configs) {
        checkNotNull(configs.get(REDIS_HOST), "redis host can't null.");
        int port = null != configs.get(REDIS_PORT) ?
                Integer.valueOf(configs.get(REDIS_PORT)) : Constants.DEFAULT_REDIS_PORT;
        int database = null != configs.get(REDIS_DATABASE) ?
                Integer.valueOf(configs.get(REDIS_DATABASE)) : Constants.DEFAULT_REDIS_DATABASE;
        int poolNum = null != configs.get(REDIS_POOL_NUM) ? Integer.valueOf(configs.get(REDIS_POOL_NUM)) :
                Constants.DEFAULT_REDIS_POOL_NUM;
        kdcRedis = new KdcRedis(configs.get(REDIS_HOST), port, configs.get(REDIS_PASSWORD), poolNum, database);
    }

    @Override
    public String get(String key) {
        Jedis jedis = kdcRedis.getJedis();
        String ret = null;
        try {
            ret = jedis.get(key);
        } finally {
            jedis.close();
        }
        return ret;
    }

    @Override
    public void set(String key, String value, int expire) {
        Jedis jedis = kdcRedis.getJedis();
        try {
            jedis.setex(key, expire, value);
        } finally {
            jedis.close();
        }
    }

    /**
     * 检查单个值是否存在
     * @param key
     * @return
     */
    @Override
    public boolean exists(String key) {
        Jedis jedis = kdcRedis.getJedis();
        boolean ret;
        try {
            ret = jedis.exists(key);
        } finally {
            jedis.close();
        }
        return ret;
    }

    /**
     * 批量检查数据数据是否存在
     * @param keys
     * @return
     */
    @Override
    public Map<String, Boolean> multiExists(Collection<String> keys) {
        Jedis jedis = kdcRedis.getJedis();
        Pipeline pipe = jedis.pipelined();
        Map<String, Boolean> resultSet = new HashMap<>();

        try {
            Map<String, Response<Boolean>> responseMap = new HashMap<>();
            for (String key : keys) {
                responseMap.put(key, pipe.exists(key));
            }
            pipe.sync();

            responseMap.forEach((String key, Response<Boolean> response) -> {
                if (response.get()) {
                    resultSet.put(key, true);
                } else {
                    resultSet.put(key, false);
                }
            });

        } finally {
            jedis.close();
        }
        return resultSet;
    }


    @Override
    public void remove(String key) {
        Jedis jedis = kdcRedis.getJedis();
        try {
            jedis.del(key);
        } finally {
            jedis.close();
        }
    }

    @Override
    public Map<String, String> multiGet(Collection<String> keys) {
        Jedis jedis = kdcRedis.getJedis();
        Map<String, String> map = new HashMap<>();
        try {
            String[] keyArray = keys.toArray(new String[keys.size()]);
            List<String> values = jedis.mget(keyArray);
            IntStream
                    .range(0, Math.min(keyArray.length, values.size()))
                    .forEach(index ->
                            map.put(keyArray[index], values.get(index))
                    );
        } finally {
            jedis.close();
        }
        return map;
    }

    /**
     * redis mset does not support expire till 2018.05.10, so we turn to pipeline
     */
    @Override
    public void multiSet(Collection<Tuple3> tuples) {
        Jedis jedis = kdcRedis.getJedis();
        try {
            Pipeline pipe = jedis.pipelined();
            for (Tuple3 tuple : tuples) {
                pipe.setex(tuple.getKey(), tuple.getExpire(), tuple.getValue());
            }
            pipe.sync();
        } finally {
            jedis.close();
        }
    }

    /**
     * 关闭 Jedis 的连接池
     */
    @Override
    public void close() {
        kdcRedis.close();
    }

}
