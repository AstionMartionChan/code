package com.kingnetdc.blueberry.cache.base;

/**
 * @author jake.zhang <zhangxj@kingnet.com>
 */
public class Tuple3 {

    private String key;

    private String value;

    private int expire;

    public Tuple3(String key, String value, int expire) {
        this.key = key;
        this.value = value;
        this.expire = expire;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public int getExpire() {
        return expire;
    }

    @Override
    public String toString() {
        return "{key:" + key + ", value:" + value + ", expire:" + expire + "}";
    }

}
