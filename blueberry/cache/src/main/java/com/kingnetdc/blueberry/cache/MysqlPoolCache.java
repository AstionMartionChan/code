package com.kingnetdc.blueberry.cache;

import com.kingnetdc.blueberry.cache.base.Constants;
import com.kingnetdc.blueberry.cache.base.Tuple3;
import com.kingnetdc.blueberry.mysql.KdcMysqlConnectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kingnetdc.blueberry.cache.base.Constants.*;

/**
 * @author zhoujiongyu@kingnet.com <zhoujiongyu@kingnet.com>
 */
public class MysqlPoolCache extends BaseMysqlCache implements ICache {

    private final static Logger logger = LoggerFactory.getLogger(MysqlPoolCache.class);

    /**
     * 连接池的最大连接数，默认是3
     */
    private int poolNum;

    /**
     * 数据库连接池
     */
    KdcMysqlConnectPool kdcMysqlConnectPool;

    public MysqlPoolCache(Map<String, String> configs) {
        super(configs);
        this.poolNum = null != configs.get(MYSQL_POOL_NUM) ?
                Integer.valueOf(configs.get(MYSQL_POOL_NUM)) : Constants.DEFAULT_MYSQL_POOL_NUM;

        kdcMysqlConnectPool = new KdcMysqlConnectPool(configs.get(MYSQL_DB_URL), configs.get(MYSQL_USERNAME),
                configs.get(MYSQL_PASSWORD), poolNum, "set names \"utf8\"", null);
        if (autoCreateTable) {
            checkIfTableExist();
        }
    }

    private void checkIfTableExist() {
        try {
            Connection conn = kdcMysqlConnectPool.getConnection();
            createTableIfNotExists(conn);
            conn.close();
        } catch (Exception e) {
            logger.error("check table error.", e);
        }
    }

    @Override
    public String get(String key) throws Exception {
        Connection conn = kdcMysqlConnectPool.getConnection();
        String result = get(key, conn);
        conn.close();
        return result;
    }

    @Override
    public void set(String key, String value, int expire) throws Exception {
        if (writeBack) {
            Connection conn = kdcMysqlConnectPool.getConnection();
            set(key, value, expire, conn);
            conn.close();
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
        Set<String> keySet = multiValue(keyArray, kdcMysqlConnectPool.getConnection());
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
        Set<String> keySet = multiValue(keys, kdcMysqlConnectPool.getConnection());
        keys.forEach(itemKey -> {
            ret.put(itemKey, keySet.contains(itemKey));
        });
        return ret;
    }

    @Override
    public void remove(String key) throws Exception {
        if (writeBack) {
            Connection conn = kdcMysqlConnectPool.getConnection();
            remove(key, conn);
            conn.close();
        }
    }

    @Override
    public Map<String, String> multiGet(Collection<String> keys) throws Exception {
        Connection conn = kdcMysqlConnectPool.getConnection();
        Map<String, String> keyValueResult = multiGet(keys, conn);
        conn.close();
        return keyValueResult;
    }

    @Override
    public void multiSet(Collection<Tuple3> tuples) throws Exception {
        if (writeBack) {
            Connection conn = kdcMysqlConnectPool.getConnection();
            multiSet(tuples, conn);
            conn.close();
        }
    }

    @Override
    public void close() {
        try {
            kdcMysqlConnectPool.close();
        } catch (Exception e) {
            logger.error("Failed to close HikariDataSource", e);
        }
    }

}
