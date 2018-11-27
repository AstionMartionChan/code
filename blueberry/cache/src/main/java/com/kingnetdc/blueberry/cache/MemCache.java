package com.kingnetdc.blueberry.cache;


import com.kingnetdc.blueberry.cache.base.Constants;
import com.kingnetdc.blueberry.cache.base.Tuple3;
import com.kingnetdc.blueberry.core.cache.KdcMemCache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jake.zhang <zhangxj@kingnet.com>
 */
public class MemCache implements ICache {

    /**
     * 如果 memSize 大小为 1000 则使用默认值
     */
    private int capacity = Constants.DEFAULT_MEM_CACHE_CAPACITY_SIZE;

    /**
     * 支持字符串的缓存
     */
    private KdcMemCache<String, String> cache;

    public MemCache() {
        initLocalCacheString();
    }

    public MemCache(int capacity) {
        this.capacity = capacity;
        initLocalCacheString();
    }

    private void initLocalCacheString() {
        cache = new KdcMemCache<>(capacity);
    }

    @Override
    public String get(String key) {
        return cache.get(key);
    }

    /**
     * 内存缓存不考虑过期时间
     *
     * @param key
     * @param value
     * @param expire
     */
    @Override
    public void set(String key, String value, int expire) {
        cache.set(key, value);
    }


    /**
     * 判断单个key是否在内存中
     * @param key
     * @return
     */
    @Override
    public boolean exists(String key) {
        return null != cache.get(key);
    }

    /**
     * 一次判断的多个 Key 是否在内存中
     * @param keys
     * @return
     */
    @Override
    public Map<String, Boolean> multiExists(Collection<String> keys) {
        Map<String, Boolean> ret = new HashMap<>();
        keys.forEach(key -> {
            ret.put(key, null != cache.get(key));
        });
        return ret;
    }

    /**
     * guava 中不提供清除某个key, 不能设置 value 为 null
     * @param key
     */
    @Override
    public void remove(String key) {
        cache.remove(key);
    }

    /**
     * 遍历读取出所有的 key, value 返回结果
     *
     * @param keys
     * @return
     */
    @Override
    public Map<String, String> multiGet(Collection<String> keys) {
        Map<String, String> ret = new HashMap<>(keys.size());
        keys.forEach(key -> {
            ret.put(key, cache.get(key));
        });
        return ret;
    }

    /**
     * 遍历写入到缓存
     *
     * @param tuples
     */
    @Override
    public void multiSet(Collection<Tuple3> tuples) {
        tuples.forEach(tuple -> {
            cache.set(tuple.getKey(), tuple.getValue());
        });
    }

    @Override
    public void close() {
        // Nothing to do
    }

}
