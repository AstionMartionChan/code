package com.kingnetdc.blueberry.mysql;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Properties;

/**
 * 根据需求获取数据库连接池
 * @author jake.zhang <zhangxj@kingnet.com>
 */
public class KdcMysqlConnectPool extends BaseMysqlConnect {

    private static final Logger logger = LoggerFactory.getLogger(KdcMysqlConnect.class);

    private String url;
    private String username;
    private String password;
    private int poolSize;
    private String initSql = "";

    private Properties dataSourceProps = null;
    private HikariDataSource ds;

    public KdcMysqlConnectPool(String url, String username, String password) {
        this(url, username, password, 3, "", null);
    }

    public KdcMysqlConnectPool(String url, String username, String password, int poolSize, String initSql, Properties dataSourceProps) {
        this.url = url;
        this.username = username;
        this.password = password;
        if (poolSize > 1) {
            this.poolSize = poolSize;
        }
        if (null != initSql) {
            this.initSql = initSql;
        }
        this.dataSourceProps = dataSourceProps;
        initConnection();
    }

    private void initConnection() {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(username);
            config.setPassword(password);
            config.setMaximumPoolSize(poolSize);
            config.setConnectionInitSql(initSql);
            if (null != dataSourceProps) {
                dataSourceProps.forEach((key, value) -> {
                    config.addDataSourceProperty(String.valueOf(key), value);
                });
            }
            ds = new HikariDataSource(config);
        } catch (Throwable e) {
            logger.error("init connection error.", e);
        }
    }

    @Override
    public Connection getConnection() {
        try {
            return ds.getConnection();
        } catch (Throwable e) {
            logger.error("get connection error.", e);
        }
        return null;
    }

    /**
     * 默认关闭数据库连接
     */
    @Override
    public void close() {
        closeDataSource();
    }

    /**
     * 关闭连接池
     */
    public void closeDataSource() {
        if (null != ds) {
            try {
                ds.close();
            } catch (Throwable e) {
                logger.error("hikari data source close error.", e);
            }
        }
    }
}
