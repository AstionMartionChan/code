package com.kingnetdc.blueberry.cache;


import com.kingnetdc.blueberry.cache.base.Tuple3;
import com.kingnetdc.blueberry.core.util.Common;
import com.kingnetdc.blueberry.core.yml.YamlConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.*;

/**
 * @author jake.zhang <zhangxj@kingnet.com>
 */
public class KdcCache implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(KdcCache.class);

    private CacheConfig cacheConfig;
    private List<ICache> caches;

    KdcCache(CacheConfig cacheConfig) {
        this(cacheConfig, cacheConfig.getItemCache());
    }

    KdcCache(CacheConfig cacheConfig, List<ICache> caches) {
        this.cacheConfig = cacheConfig;
        this.caches = caches;
    }

    public static KdcCache builder(InputStream is) {
        CacheConfig cacheConfig = YamlConfig.loadAs(is, CacheConfig.class);
        return new KdcCache(cacheConfig);
    }

    private String buildKey(String key) {
        if (cacheConfig.isNeedHash()) {
            return Common.fastHashing(cacheConfig.getKeyPrefix() + key);
        } else {
            return key;
        }
    }


    /**
     * 默认支持回写功能
     * @param key
     * @return
     */
    public String get(String key) {
        return get(key, true);
    }

    /**
     * 按照顺序遍历缓存结果, 根据重试次数判断重试，2次之间默认时间是1s
     * 可以单独设置当次是否屏蔽回写功能
     * @param key
     * @param isWriteBack
     * @return
     */
    public String get(String key, boolean isWriteBack) {
        boolean retry;
        String ret = null;
        String itemBuildKey = buildKey(key);
        int current = 0;
        for (ICache cache : caches) {
            retry = false;
            for (int i = 1; i <= cacheConfig.getRetryNum(); i++) {
                try {
                    ret = cache.get(itemBuildKey);
                    if (null != ret) {
                        break;
                    }
                } catch (Throwable e) {
                    logger.error("get error", e);
                    retry = true;
                }
                // 在获取的时候不出错的情况下，是不重试的，只有出错的时候需要重试
                if (!retry) {
                    break;
                }
                if (i < cacheConfig.getRetryNum()) {
                    Common.sleep(cacheConfig.getRetryIdle());
                }
                if (i > 1) {
                    logger.info("retry " + i + " times for get key : " + key);
                }
            }

            // 开启回写且不是第一级获取的，则执行回写
            if (null != ret) {
                if (isWriteBack && cacheConfig.isGetWriteBack() && current > 0) {
                    // 回写不支持最后一层的数据回写，主要是越往后的层级基本都是持久化层，需要手动确认数据
                    if (current >= caches.size() - 1) {
                        set(key, ret, cacheConfig.getGetWriteBackTime(), caches.size() - 2);
                    } else {
                        set(key, ret, cacheConfig.getGetWriteBackTime(), current);
                    }
                }
                break;
            }
            current ++;
        }
        return ret;
    }

    /**
     * 根据顺序设置缓存，设置写入的层级
     *
     * @param key
     * @param value
     * @param expire
     */
    public void set(String key, String value, int expire, int currentSize) {
        boolean retry;
        int current = 0;
        String itemBuildKey = buildKey(key);
        for (ICache cache : caches) {
            retry = false;
            for (int i = 0; i < cacheConfig.getRetryNum(); i++) {
                try {
                    cache.set(itemBuildKey, value, expire);
                } catch (Throwable e) {
                    logger.error("set error.", e);
                    retry = true;
                }
                if (!retry) {
                    break;
                }
                if (i < cacheConfig.getRetryNum()) {
                    Common.sleep(cacheConfig.getRetryIdle());
                }
            }
            current ++;
            // 设置的时候，设置最大写入层级，默认是当前的写入层级
            if (current > currentSize) {
                break;
            }
        }
    }

    /**
     * 默认写入功能
     * @param key
     * @param value
     * @param expire
     */
    public void set(String key, String value, int expire) {
        set(key, value, expire, caches.size() - 1);
    }


    /**
     * 判断缓存key是否存在，如果存在。判断的逻辑是需要遍历缓存层确认是否存在
     */
    public boolean exists(String key) {
        boolean retry;
        String itemBuildKey = buildKey(key);
        boolean ret = false;
        for (ICache cache : caches) {
            retry = false;
            for (int i = 0; i < cacheConfig.getRetryNum(); i++) {
                try {
                    ret = cache.exists(itemBuildKey);
                } catch (Throwable e) {
                    logger.error("remove error.", e);
                    retry = true;
                }
                if (!retry) {
                    break;
                }
                if (i < cacheConfig.getRetryNum()) {
                    Common.sleep(cacheConfig.getRetryIdle());
                }
            }
            if (ret) {
                return true;
            }
        }
        return ret;
    }

    /**
     * 判断多个缓存值是否存在，需要做下key的变换
     * @param keys
     * @return
     */
    public Map<String, Boolean> multiExists(Collection<String> keys) {
        Map<String, Boolean> cacheRet = new HashMap<>();
        boolean retry;
        Map<String, String> keyMap = new HashMap<String, String>(keys.size());
        Map<String, String> valueMap = new HashMap<String, String>(keys.size());
        Collection<Tuple3> getWriteBackList = new ArrayList<>();
        keys.forEach(key -> {
            String itemBuildKey = buildKey(key);
            keyMap.put(itemBuildKey, key);
            valueMap.put(key, itemBuildKey);
        });
        for (ICache cache : caches) {
            // 获取上一级没有获取到的key的值
            if (keyMap.size() > 0) {
                retry = false;
                for (int i = 1; i <= cacheConfig.getRetryNum(); i++) {
                    try {
                        Map<String, Boolean> itemRet = cache.multiExists(keyMap.keySet());
                        itemRet.keySet().forEach(itemKey -> {
                            keyMap.remove(itemKey);
                        });
                        cacheRet.putAll(itemRet);
                    } catch (Throwable e) {
                        logger.error("multiExists error.", e);
                        retry = true;
                    }
                    if (!retry) {
                        break;
                    }
                    if (i < cacheConfig.getRetryNum()) {
                        Common.sleep(cacheConfig.getRetryIdle());
                    }
                }
            } else {
                break;
            }
        }

        Map<String, Boolean> ret = new HashMap<>(keys.size());
        keys.forEach(key -> {
            ret.put(key, cacheRet.getOrDefault(valueMap.get(key), false));
        });
        return ret;
    }


    /**
     * 根据顺序删除缓存key
     *
     * @param key
     */
    public void remove(String key) {
        boolean retry;
        String itemBuildKey = buildKey(key);
        for (ICache cache : caches) {
            retry = false;
            for (int i = 0; i < cacheConfig.getRetryNum(); i++) {
                try {
                    cache.remove(itemBuildKey);
                } catch (Throwable e) {
                    logger.error("remove error.", e);
                    retry = true;
                }
                if (!retry) {
                    break;
                }
                if (i < cacheConfig.getRetryNum()) {
                    Common.sleep(cacheConfig.getRetryIdle());
                }
            }
        }
    }

    /**
     * 默认支持回写功能，也可以手动设置不支持回写
     * @param keys
     * @return
     */
    public Map<String, String> multiGet(Collection<String> keys) {
        return multiGet(keys, true);
    }

    /**
     * 处理流程，需要先处理keys，然后转换回结果的Key
     * 第一次检查一级缓存，如果没有获取全部，则从二级缓存继续获取，然后尝试三级缓存，直到结束
     * 不返回null，只返回 Map 结构
     * 回写的策略：从第二级缓存开始，获取的到数据，一直写入到最后一级的前一级缓存。
     * 二级缓存：1级是内存或者redis，2级是持久化存储
     * 根据回写策略，数据从2级获取的数据都会写到第一级里
     * 三级缓存：1级是内存，2级是redis，3级是持久化存储
     * 根据回写策略：数据从2级和3级获取到的数据会写到1级和2级，不再详细区分细分2级或者的数据重复写的逻辑
     *
     * @param keys
     * @return
     */
    public Map<String, String> multiGet(Collection<String> keys, boolean isWriteBack) {
        Map<String, String> cacheRet = new HashMap<>();
        boolean retry;
        int current = 0;
        Map<String, String> keyMap = new HashMap<String, String>(keys.size());
        Map<String, String> valueMap = new HashMap<String, String>(keys.size());
        Collection<Tuple3> getWriteBackList = new ArrayList<>();
        keys.forEach(key -> {
            String itemBuildKey = buildKey(key);
            keyMap.put(itemBuildKey, key);
            valueMap.put(key, itemBuildKey);
        });
        for (ICache cache : caches) {
            // 确保退出的时候，记录当前层级
            current ++;
            // 获取上一级没有获取到的key的值
            if (keyMap.size() > 0) {
                retry = false;
                for (int i = 1; i <= cacheConfig.getRetryNum(); i++) {
                    try {
                        Map<String, String> itemRet = cache.multiGet(keyMap.keySet());
                        // 单独处理结果，如果 cacheRet 的数量和 keys的数量一致，则是获取完成
                        for (Map.Entry<String, String> entry : itemRet.entrySet()) {
                            String itemKey = entry.getKey();
                            String itemValue = entry.getValue();
                            if (null != itemValue) {
                                // 开启了回写功能，且不是第一次获取到的，则开始回写功能
                                if (isWriteBack && cacheConfig.isGetWriteBack() && current > 0) {
                                    // 在set的时候需要一个原始的key，不能转换过
                                    getWriteBackList.add(new Tuple3(keyMap.get(itemKey), itemValue, cacheConfig.getGetWriteBackTime()));
                                }
                                // 去掉当前这一层获取的数据key
                                // 缓存当前这一层的结果数据
                                keyMap.remove(itemKey);
                                cacheRet.put(itemKey, itemValue);
                            }
                        }
                    } catch (Throwable e) {
                        logger.error("multiGet error.", e);
                        retry = true;
                    }
                    if (!retry) {
                        break;
                    }
                    if (i < cacheConfig.getRetryNum()) {
                        Common.sleep(cacheConfig.getRetryIdle());
                    }
                }
            } else {
                break;
            }
        }

        // 在获取数据完成后，如果需要回写，则且回写的大小 > 0
        if (isWriteBack && cacheConfig.isGetWriteBack() && getWriteBackList.size() > 0) {
            // current 会再最后一次加 1，需要减掉
            // current 的值需要减去1，作为当前获取数据的层级，如果当前数据获取的层级为最大层级是，需要减去一个
            // 通过get的方式回写数据不支持最后一个数据回写，可以通过set的方式来实现数据写入
            if ((current - 1) <= caches.size() - 1) {
                multiSet(getWriteBackList, caches.size() - 2);
            } else {
                multiSet(getWriteBackList, current - 1);
            }
        }

        Map<String, String> ret = new HashMap<>(keys.size());
        keys.forEach(key -> {
            ret.put(key, cacheRet.getOrDefault(valueMap.get(key), null));
        });
        return ret;
    }

    /**
     * 重新生成 tuples 缓存key
     *
     * @param tuples
     */
    public void multiSet(Collection<Tuple3> tuples, int currentSize) {
        boolean retry;
        int current = 0;
        Collection<Tuple3> cacheTuples = new ArrayList<Tuple3>();
        tuples.forEach(tuple3 -> {
            cacheTuples.add(new Tuple3(buildKey(tuple3.getKey()), tuple3.getValue(), tuple3.getExpire()));
        });
        for (ICache cache : caches) {
            retry = false;
            for (int i = 1; i <= cacheConfig.getRetryNum(); i++) {
                try {
                    cache.multiSet(cacheTuples);
                } catch (Throwable e) {
                    logger.error("multiSet error, " + cache, e);
                    retry = true;
                }
                if (!retry) {
                    break;
                }
                if (i < cacheConfig.getRetryNum()) {
                    Common.sleep(cacheConfig.getRetryIdle());
                }
            }
            current ++;
            // 设置的时候，设置最大写入层级，默认是当前的写入层级
            if (current > currentSize) {
                break;
            }
        }
    }

    /**
     * 多个数据写入的问题
     * @param tuples
     */
    public void multiSet(Collection<Tuple3> tuples) {
        multiSet(tuples, caches.size() - 1);
    }

    /**
     * 手动关闭连接不考虑重试
     */
    @Override
    public void close() {
        for (ICache cache : caches) {
            try {
                cache.close();
            } catch (Throwable e) {
                logger.error("close item error, " + cache, e);
            }
        }
    }

}
