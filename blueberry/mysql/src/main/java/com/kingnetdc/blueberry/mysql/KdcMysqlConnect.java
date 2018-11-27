package com.kingnetdc.blueberry.mysql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * 根据需求获取数据库连接
 * @author jake.zhang <zhangxj@kingnet.com>
 */
public class KdcMysqlConnect extends BaseMysqlConnect {

    private static final Logger logger = LoggerFactory.getLogger(KdcMysqlConnect.class);

    private Connection conn = null;

    private String url;
    private String username;
    private String password;

    public KdcMysqlConnect(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        initConnection();
    }

    private void initConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, username, password);
        } catch (Throwable e) {
            logger.error("init connection error.", e);
        }
    }

    @Override
    public Connection getConnection() {
        return conn;
    }

    @Override
    public void close() {
        close(conn);
    }

}
