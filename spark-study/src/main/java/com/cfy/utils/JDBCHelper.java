package com.cfy.utils;

import com.cfy.constants.Constant;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/6/30
 * Time: 22:12
 * Work contact: Astion_Leo@163.com
 */


public class JDBCHelper {

    static {
        try {
            Class.forName(ConfigurationManager.getString(Constant.JDBC_DRIVER));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static JDBCHelper jdbcHelper = null;
    private static LinkedList<Connection> connectionPool = new LinkedList<>();

    private JDBCHelper() {
        Integer connectionNum = ConfigurationManager.getInteger(Constant.CONNECTION_NUM);
        try {
            String url = ConfigurationManager.getString(Constant.JDBC_URL);
            String username = ConfigurationManager.getString(Constant.JDBC_USERNAME);
            String password = ConfigurationManager.getString(Constant.JDBC_PASSWORD);

            for (int x=0; x<connectionNum; x++){
                Connection connection = DriverManager.getConnection(url, username, password);
                connectionPool.push(connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static JDBCHelper getJdbcHelper() {

        if (null == jdbcHelper){
            synchronized (JDBCHelper.class) {
                if (null == jdbcHelper){
                    jdbcHelper = new JDBCHelper();
                }
            }
        }
        return jdbcHelper;
    }

    private static synchronized Connection getConnection() {
        while (connectionPool.size() == 0){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return connectionPool.poll();
    }


    public static Integer prepareUpdate(String sql, Object[] params) {
        Connection connection = null;
        PreparedStatement statement = null;
        Integer rst = 0;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            for (int x=0; x < params.length; x++){
                statement.setObject(x + 1, params[x]);
            }

            rst = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (null != connection) {
                connectionPool.push(connection);
            }
        }
        return rst;
    }


    public static void prepareQuery(String sql, Object[] params, ResultProccess resultProccess) {

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            for (int x=0; x < params.length; x++){
                statement.setObject(x + 1, params[x]);
            }

            resultSet = statement.executeQuery();
            resultProccess.proccess(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (null != connection) {
                connectionPool.push(connection);
            }
        }
    }



    public static int[] prepareBult(String sql, List<Object[]> paramsList) {

        Connection connection = null;
        PreparedStatement statement = null;
        int[] rst = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            connection.setAutoCommit(false);

            for (Object[] params : paramsList){
                for (int x=0; x<params.length; x++){
                    statement.setObject(x + 1, params[x]);
                }
                statement.addBatch();
            }

            rst = statement.executeBatch();
            connection.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (null != connection) {
                connectionPool.push(connection);
            }
        }

        return rst;
    }


    public static interface ResultProccess {

        void proccess(ResultSet resultSet) throws SQLException;
    }

}
