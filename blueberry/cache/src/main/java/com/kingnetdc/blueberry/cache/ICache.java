package com.kingnetdc.blueberry.cache;

import com.kingnetdc.blueberry.cache.base.Tuple3;

import java.util.Collection;
import java.util.Map;

/**
 * @author jake.zhang <zhangxj@kingnet.com>
 */
public interface ICache {


    /**
     * 从缓存中获取字符串, null 说明字符串不存在
     *
     * @param key
     * @return
     * @throws Exception
     */
    String get(String key) throws Exception;


    /**
     * 以字符串方式写入数据
     * @param key
     * @param value
     * @param expire
     * @throws Exception
     */
    void set(String key, String value, int expire) throws Exception;

    /**
     * 检查 Key 是否存在
     * @param key
     * @return
     * @throws Exception
     */
    boolean exists(String key) throws Exception;

    /**
     * 一次检查多条多个 key 是否存在
     * @param keys
     * @return
     * @throws Exception
     */
    Map<String, Boolean> multiExists(Collection<String> keys) throws Exception;

    /**
     * 删除某个缓存的key
     * @param key
     * @throws Exception
     */
    void remove(String key) throws Exception;


    /**
     * 一次获取多个值
     *
     * @param keys
     * @return
     * @throws Exception
     */
    Map<String, String> multiGet(Collection<String> keys) throws Exception;


    /**
     * 一次写入多个值
     *
     * @param tuples
     * @throws Exception
     */
    void multiSet(Collection<Tuple3> tuples) throws Exception;


    /**
     * 手动关闭连接
     * @throws Exception
     */
    void close() throws Exception;

}
