package com.cfy.utils;


import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by Leo_Chan on 2018/2/2.
 */
public class MySqlUtil {
    /**
     * 连接数据库
     */
    static {

        String url = PropertiesUtil.getProperty("jdbc.url");
        String username = PropertiesUtil.getProperty("jdbc.username");
        String password = PropertiesUtil.getProperty("jdbc.password");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /* 数据库连接 */
    private static Connection connection;

    public static Connection getConnection() {
        return connection;
    }

    public static void close() {
        if (connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
