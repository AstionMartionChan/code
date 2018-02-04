package com.itcast.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Leo_Chan on 2018/2/2.
 */
public class MySqlUtil {
    /**
     * 连接数据库
     */
    static {
        try {
            Properties prop = new Properties();
            InputStream resourceAsStream = MySqlUtil.class.getResourceAsStream("/systemConfig.properties");

            prop.load(resourceAsStream);
            String url = prop.getProperty("jdbc.url").trim();
            String username = prop.getProperty("jdbc.username").trim();
            String password = prop.getProperty("jdbc.password").trim();
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* 数据库连接 */
    private static Connection connection;

    public static void insert(Map<String, Object> params) throws SQLException {
        String sql = "insert into t_sku_info (title,image_url,price,specifications) values(?,?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, params.get("title").toString());
        preparedStatement.setString(2, params.get("imgUrl").toString());
        preparedStatement.setString(3, params.get("price").toString());
        preparedStatement.setString(4, params.get("commodityParams").toString());
        preparedStatement.execute();
        preparedStatement.close();
        connection.close();
    }
}
