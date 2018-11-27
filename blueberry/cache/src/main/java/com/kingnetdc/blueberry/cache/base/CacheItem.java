package com.kingnetdc.blueberry.cache.base;

import java.util.Map;

/**
 * @author zhouml <zhouml@kingnet.com>
 */
public class CacheItem implements Comparable<CacheItem> {

    /**
     * 缓存等级
     */
    private int cacheLevel;

    /**
     * 缓存类型
     */
    private String cacheType;

    /**
     * 缓存配置项
     */
    private Map<String, String> props;


    public int getCacheLevel() {
        return cacheLevel;
    }

    public String getCacheType() {
        return cacheType;
    }

    public Map<String, String> getProps() {
        return props;
    }

    public void setCacheLevel(int cacheLevel) {
        this.cacheLevel = cacheLevel;
    }

    public void setCacheType(String cacheType) {
        this.cacheType = cacheType;
    }

    public void setProps(Map<String, String> props) {
        this.props = props;
    }

    @Override
    public String toString() {
        return "CacheItem{" +
                "cacheLevel=" + cacheLevel +
                ", cacheType='" + cacheType + '\'' +
                ", props=" + props +
                '}';
    }


    @Override
    public int compareTo(CacheItem other) {
        return this.cacheLevel - other.cacheLevel;
    }

}
