package com.kingnetdc.blueberry.cache;


import com.kingnetdc.blueberry.cache.base.CacheItem;
import com.kingnetdc.blueberry.cache.base.CacheType;
import com.kingnetdc.blueberry.cache.base.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.kingnetdc.blueberry.cache.base.Constants.MEM_CACHE_CAPACITY;

/**
 * @author jake.zhang <zhangxj@kingnet.com>
 */
public class CacheConfig {

    private String keyPrefix;

    private int retryNum = Constants.DEFAULT_CONFIG_RETRY_NUM;

    private int retryIdle = Constants.DEFAULT_CONFIG_RETRY_IDLE;

    private boolean needHash = Constants.DEFAULT_NEED_HASH;

    private boolean getWriteBack = Constants.DEFAULT_GET_WRITE_BACK;

    private int getWriteBackTime = Constants.DEFAULT_GET_WRITE_BACK_TIME;


    public boolean isGetWriteBack() {
        return getWriteBack;
    }

    public void setGetWriteBack(boolean getWriteBack) {
        this.getWriteBack = getWriteBack;
    }

    public int getGetWriteBackTime() {
        return getWriteBackTime;
    }

    public void setGetWriteBackTime(int getWriteBackTime) {
        this.getWriteBackTime = getWriteBackTime;
    }

    public boolean isNeedHash() {
        return needHash;
    }

    public void setNeedHash(boolean needHash) {
        this.needHash = needHash;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public int getRetryNum() {
        return (retryNum >= 1 && retryNum <= 3) ? retryNum : Constants.DEFAULT_CONFIG_RETRY_NUM;
    }

    public int getRetryIdle() {
        return (retryIdle >= 1 && retryIdle <= 10000) ? retryIdle : Constants.DEFAULT_CONFIG_RETRY_IDLE;
    }

    public void setRetryNum(int retryNum) {
        this.retryNum = retryNum;
    }

    public void setRetryIdle(int retryIdle) {
        this.retryIdle = retryIdle;
    }

    private List<CacheItem> items;

    public String getKeyPrefix() {
        return null != keyPrefix ? keyPrefix : Constants.DEFAULT_CONFIG_PREFIX;
    }

    public List<CacheItem> getItems() {
        return items;
    }

    public void setItems(List<CacheItem> items) {
        this.items = items;
    }

    public List<ICache> getItemCache() {
        List<ICache> caches = new ArrayList<>();
        List<CacheItem> cacheItems = getItems();
        Collections.sort(cacheItems);
        for (CacheItem cacheItem : cacheItems) {
            String cacheType = cacheItem.getCacheType();
            Map<String, String> props = cacheItem.getProps();
            if (cacheType.equals(CacheType.MEMORY.getName())) {
                String capacity = props.get(MEM_CACHE_CAPACITY);
                MemCache cache = capacity != null ? new MemCache(Integer.valueOf(capacity)) : new MemCache();
                caches.add(cache);
            }
            if (cacheType.equals(CacheType.MYSQL.getName())) {
                caches.add(new MysqlCache(props));
            }
            if (cacheType.equals(CacheType.REDIS.getName())) {
                caches.add(new RedisCache(props));
            }
            if (cacheType.equals(CacheType.REDIS_CLUSTER.getName())) {
                RedisClusterCache cluster = new RedisClusterCache(props);
                caches.add(cluster);
            }
            if (cacheType.equals(CacheType.MYSQL_DATASOURCE.getName())) {
                MysqlPoolCache dataSource = new MysqlPoolCache(props);
                caches.add(dataSource);
            }
        }
        return caches;
    }

    @Override
    public String toString() {
        return "CacheConfig{" +
                "keyPrefix='" + keyPrefix + '\'' +
                ", retryNum=" + retryNum +
                ", retryIdle=" + retryIdle +
                ", items=" + items +
                '}';
    }
}
