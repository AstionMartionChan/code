package com.kingnetdc.blueberry.core.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 最小的大小是100
 * @author jake.zhang <zhangxj@kingnet.com>
 * @param <K>
 * @param <V>
 */
public class KdcMemCache<K, V> {

    private static final Logger logger = LoggerFactory.getLogger(KdcMemCache.class);

    /**
     * 默认最大容量
     */
    private int maxCapacity = 100;

    /**
     * 默认最大超时时间
     */
    private int expireTime = 24 * 3600;

    /**
     * 过期顺序
     */
    private boolean accessOrder = true;

    private Cache<K, V> cache;

    public KdcMemCache(int maxCapacity) {
        this(maxCapacity, 24 * 3600, true);
    }

    public KdcMemCache(int maxCapacity, int expireTime) {
        this(maxCapacity, expireTime, true);
    }

    public KdcMemCache(int maxCapacity, int expireTime, boolean accessOrder) {
        if (maxCapacity > 100) {
            this.maxCapacity = maxCapacity;
        }
        if (expireTime > 0) {
            this.expireTime = expireTime;
        }
        this.accessOrder = accessOrder;
        initial();
    }

    private void initial() {
        if (accessOrder) {
            cache = CacheBuilder.newBuilder().maximumSize(maxCapacity).expireAfterAccess(expireTime, TimeUnit.SECONDS).build();
            logger.info("cache build from maxsize: " + maxCapacity + ", expireAfterAccess:" + expireTime + "(" + TimeUnit.SECONDS + ")");
        } else {
            cache = CacheBuilder.newBuilder().maximumSize(maxCapacity).expireAfterWrite(expireTime, TimeUnit.SECONDS).build();
            logger.info("cache build from maxsize: " + maxCapacity + ", expireAfterWrite:" + expireTime + "(" + TimeUnit.SECONDS + ")");
        }
    }

    /**
     * 从cache中指定key的值，如果没找到返回null
     * @param key
     * @return
     */
    public V get(K key) {
        return cache.getIfPresent(key);
    }

    /**
     * 将key对应的value值放入缓存中，如果原来缓存中存在key对应的value，则用新value覆盖旧value
     * @param key
     * @param value
     */
    public void set(K key, V value) {
        cache.put(key, value);
    }

    /**
     * 从cache中删除指定key
     * @param key
     */
    public void remove(K key) {
        cache.invalidate(key);
    }

    /**
     * 删除cache中所有缓存
     */
    public void removeAll() {
        cache.invalidateAll();
    }
}
