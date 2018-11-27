package com.kingnetdc.blueberry.cache;

import com.kingnetdc.blueberry.cache.base.Tuple3;
import com.kingnetdc.blueberry.mysql.KdcMysqlConnect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kingnetdc.blueberry.cache.base.Constants.MYSQL_PASSWORD;
import static com.kingnetdc.blueberry.cache.base.Constants.MYSQL_USERNAME;


/**
 * @author zhouml <zhouml@kingnet.com>
 */
public class MysqlCache extends BaseMysqlCache implements ICache {

    private final static Logger logger = LoggerFactory.getLogger(MysqlCache.class);

    /**
     * 获取单个数据库连接
     */
    private KdcMysqlConnect kdcMysqlConnect;

    public MysqlCache(Map<String, String> configs) {
        super(configs);
        kdcMysqlConnect = new KdcMysqlConnect(dbUrl, configs.get(MYSQL_USERNAME), configs.get(MYSQL_PASSWORD));
        if (autoCreateTable) {
            checkIfTableExist();
        }
    }

    public void checkIfTableExist() {
        createTableIfNotExists(kdcMysqlConnect.getConnection());
    }

    @Override
    public String get(String key) throws Exception {
        return get(key, kdcMysqlConnect.getConnection());
    }

    @Override
    public void set(String key, String value, int expire) throws Exception {
        if (writeBack) {
            set(key, value, expire, kdcMysqlConnect.getConnection());
        }
    }

    /**
     * 判断单个 key 是否存在
     * @param key
     * @return
     */
    @Override
    public boolean exists(String key) throws Exception {
        List<String> keyArray = new ArrayList<String>();
        Set<String> keySet = multiValue(keyArray, kdcMysqlConnect.getConnection());
        return keySet.contains(key);
    }

    /**
     * 判断多个 key 是否存在
     * @param keys
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Boolean> multiExists(Collection<String> keys) throws Exception {
        Map<String, Boolean> ret = new HashMap<>();
        Set<String> keySet = multiValue(keys, kdcMysqlConnect.getConnection());
        keys.forEach(itemKey -> {
            ret.put(itemKey, keySet.contains(itemKey));
        });
        return ret;
    }

    @Override
    public void remove(String key) throws Exception {
        if (writeBack) {
            remove(key, kdcMysqlConnect.getConnection());
        }
    }

    @Override
    public Map<String, String> multiGet(Collection<String> keys) throws Exception {
        Map<String, String> keyValueMap = multiGet(keys, kdcMysqlConnect.getConnection());
        return keyValueMap;
    }

    @Override
    public void multiSet(Collection<Tuple3> tuples) throws Exception {
        if (writeBack) {
            multiSet(tuples, kdcMysqlConnect.getConnection());
        }
    }

    @Override
    public void close() {
        try {
            kdcMysqlConnect.close();
        } catch (Throwable e) {
            logger.error("Failed to close mysql connection", e);
        }
    }

}
