package com.kingnetdc.blueberry.mysql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.Statement;

/**
 * 管理数据库连接
 * @author jake.zhang <zhangxj@kingnet.com>
 */
public abstract class BaseMysqlConnect {

    private static final Logger logger = LoggerFactory.getLogger(BaseMysqlConnect.class);

    /**
     * 获取数据库连接
     * @return
     */
    public abstract Connection getConnection();

    /**
     * 默认关闭连接
     */
    public abstract void close();

    public void close(Connection conn) {
        if (null != conn) {
            try {
                conn.close();
            } catch (Throwable e) {
                logger.error("close Connection conn error.", e);
            }
        }
    }

    public void close(Statement stmt) {
        if (null != stmt) {
            try {
                stmt.close();
            } catch (Throwable e) {
                logger.error("close Connection conn error.", e);
            }
        }
    }

}
