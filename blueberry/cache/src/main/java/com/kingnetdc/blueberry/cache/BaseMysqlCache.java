package com.kingnetdc.blueberry.cache;

import com.google.common.collect.Lists;
import com.kingnetdc.blueberry.cache.base.Constants;
import com.kingnetdc.blueberry.cache.base.Tuple3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.kingnetdc.blueberry.cache.base.Constants.*;
import static com.kingnetdc.blueberry.cache.base.Constants.MYSQL_KEY_PARTITION;

/**
 * @author zhouml <zhouml@kingnet.com>
 */
public abstract class BaseMysqlCache {

    private final static Logger logger = LoggerFactory.getLogger(BaseMysqlCache.class);

    protected String dbUrl;
    protected String table;
    protected String keyColumnName;
    protected String valueColumnName;
    protected int batchSize;
    protected int keyPartition;
    protected boolean writeBack;
    protected boolean autoCreateTable;

    public BaseMysqlCache(Map<String, String> configs) {
        // 主要参数不能为空
        checkNotNull(configs.get(MYSQL_DB_URL), "mysql dbUrl can't null.");
        checkNotNull(configs.get(MYSQL_USERNAME), "mysql username can't null.");
        checkNotNull(configs.get(MYSQL_PASSWORD), "mysql password can't null.");
        checkNotNull(configs.get(MYSQL_CACHE_TABLE), "mysql cache table can't null.");

        this.dbUrl = configs.get(MYSQL_DB_URL);
        this.table = configs.get(MYSQL_CACHE_TABLE);

        this.keyColumnName = null != configs.get(MYSQL_KEY_NAME) ?
                configs.get(MYSQL_KEY_NAME) : Constants.DEFAULT_MYSQL_KEY_NAME;
        this.valueColumnName = null != configs.get(MYSQL_VALUE_NAME) ?
                configs.get(MYSQL_VALUE_NAME) : Constants.DEFAULT_MYSQL_VALUE_NAME;
        this.batchSize = null != configs.get(MYSQL_BATCH_SIZE) ?
                Integer.valueOf(configs.get(MYSQL_BATCH_SIZE)) : Constants.DEFAULT_MYSQL_BATCH_SIZE;
        this.writeBack = null != configs.get(MYSQL_WRITE_BACK) ?
                Boolean.valueOf(configs.get(MYSQL_WRITE_BACK)) : Constants.DEFAULT_MYSQL_WRITE_BACK;
        this.keyPartition = null != configs.get(MYSQL_KEY_PARTITION) ?
                Integer.valueOf(configs.get(MYSQL_KEY_PARTITION)) : Constants.DEFAULT_MYSQL_KEY_PARTITION;
        this.autoCreateTable = null != configs.get(MYSQL_AUTO_CREATE_TABLE) ?
                Boolean.valueOf(configs.get(MYSQL_AUTO_CREATE_TABLE)) : Constants.DEFAULT_MYSQL_AUTO_CREATE_TABLE;
    }

    /**
     * 执行没有返回的SQL语句，比如DDL
     * @param connection
     * @param sql
     * @return
     */
    public int executeUpdate(Connection connection, String sql) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            return statement.executeUpdate(sql);
        } catch (Throwable e) {
            logger.error("Failed to execute sql: " + sql, e);
        } finally {
            closeStatement(statement);
        }
        return -1;
    }

    /**
     * 在初始化连接的时候，检查表是否存在
     * @param connection
     */
    protected void createTableIfNotExists(Connection connection) {
        String sql =
                String.format(
                        "create table if not exists %s (" +
                                "`%s` VARCHAR(64) NOT NULL COMMENT '缓存Key'," +
                                "`%s` LONGTEXT NOT NULL COMMENT '缓存Value'," +
                                "`last_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间'," +
                                "PRIMARY KEY (`%s`)" +
                                ")  PARTITION BY KEY (`%s`) PARTITIONS %d",
                        table, keyColumnName, valueColumnName, keyColumnName, keyColumnName, keyPartition
                );
        logger.info("create table sql: " + sql);
        executeUpdate(connection, sql);
    }

    public String get(String key, Connection conn) throws Exception {
        Statement stmt = conn.createStatement();
        String sql = String.format("select `%s` from %s where `%s` = '%s' limit 1",
                valueColumnName, table, keyColumnName, key);
        ResultSet rs = stmt.executeQuery(sql);
        String result = rs.next() ? rs.getString(valueColumnName) : null;
        closeStatement(stmt);
        return result;
    }

    /**
     * 持久化存储不考虑超时时间
     * @param key
     * @param value
     * @param expire
     * @param conn
     * @throws Exception
     */
    public void set(String key, String value, int expire, Connection conn) throws Exception {
        String sql = String.format(
                "insert into %s (`%s`, `%s`) values (?, ?) on duplicate key update `%s` = ?",
                table, keyColumnName, valueColumnName, valueColumnName
        );
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, key);
        stmt.setString(2, value);
        stmt.setString(3, value);
        stmt.execute();
        closeStatement(stmt);
    }



    /**
     * 删除持久化存储中的数据
     * @param key
     * @param conn
     * @throws Exception
     */
    public void remove(String key, Connection conn) throws Exception {
        String sql = String.format( "delete from %s where `%s` = ?", table, keyColumnName);
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, key);
        stmt.execute();
        closeStatement(stmt);
    }

    /**
     * 一次获取多个Key, 判断key是否在数据库中
     * @param keys
     * @param conn
     * @return
     * @throws Exception
     */
    public Set<String> multiValue(Collection<String> keys, Connection conn) throws Exception {
        Set<String> ret = new HashSet<>();
        Statement stmt = conn.createStatement();
        for (List<String> batch : partition(keys, batchSize)) {
            String inClauses =
                    batch.stream()
                            .map(key -> String.format("'%s'", key))
                            .collect(Collectors.joining(","));
            String sql = String.format("select `%s` from %s where `%s` in (%s)",
                    keyColumnName, table, keyColumnName, inClauses);

            if (logger.isDebugEnabled()) {
                logger.debug("Send one multiValue batch of " + batch.size());
            }
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                String key = resultSet.getString(1);
                ret.add(key);
            }
        }
        closeStatement(stmt);
        return ret;
    }

    /**
     * 一次获取多个Key的结果
     * @param keys
     * @param conn
     * @return
     * @throws Exception
     */
    public Map<String, String> multiGet(Collection<String> keys, Connection conn) throws Exception {
        Map<String, String> keyValueResult = new HashMap<>();
        Statement stmt = conn.createStatement();
        for (List<String> batch : partition(keys, batchSize)) {
            String inClauses =
                    batch.stream()
                            .map(key -> String.format("'%s'", key))
                            .collect(Collectors.joining(","));
            String sql = String.format("select `%s`, `%s` from %s where `%s` in (%s)",
                    keyColumnName, valueColumnName, table, keyColumnName, inClauses);


            if (logger.isDebugEnabled()) {
                logger.debug("Send one multiGet batch of " + batch.size());
            }
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                String key = resultSet.getString(1);
                String value = resultSet.getString(2);
                keyValueResult.put(key, value);
            }
        }
        closeStatement(stmt);
        return keyValueResult;
    }

    /**
     * 一次获取多个key的结果
     * @param tuples
     * @param conn
     * @throws Exception
     */
    public void multiSet(Collection<Tuple3> tuples, Connection conn) throws Exception {
        String sql = String.format(
                "insert into %s (`%s`, `%s`) values (?, ?) on duplicate key update `%s` = ?",
                table, keyColumnName, valueColumnName, valueColumnName
        );
        PreparedStatement stmt = conn.prepareStatement(sql);
        for (List<Tuple3> batch : partition(tuples, batchSize)) {
            for (Tuple3 tuple3 : batch) {
                stmt.setString(1, tuple3.getKey());
                stmt.setString(2, tuple3.getValue());
                stmt.setString(3, tuple3.getValue());
                stmt.addBatch();
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Send one multiSet batch of " + batch.size());
            }
            stmt.executeBatch();
        }
        closeStatement(stmt);
    }

    /**
     * 关闭sql 查询结果
     * @param statement
     */
    private void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                logger.error("Failed to close statement", e);
            }
        }
    }

    /**
     * List(1,2,3,4,5,6) 2 ==> List(1,2,3), List(4,5,6)
     * @param collection
     * @param sizeInGroup
     * @param <T>
     * @return
     */
    public static <T> Collection<List<T>> partition(Collection<T> collection, int sizeInGroup) {
        List<List<T>> lists = Lists.newArrayList();
        AtomicInteger counter = new AtomicInteger(0);

        List<T> buffer = Lists.newArrayList();
        for (T elem : collection) {
            buffer.add(elem);
            counter.getAndIncrement();
            if (counter.get() % sizeInGroup == 0) {
                lists.add(Lists.newArrayList(buffer));
                buffer.clear();
            }
        }
        if (buffer.size() > 0) {
            lists.add(buffer);
        }
        return lists;
    }

}
