package com.cfy.trait.impl;

import com.cfy.entity.Column;
import com.cfy.trait.DBReader;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/3/31
 * Time: 15:26
 * Work contact: Astion_Leo@163.com
 */


public class MysqlDBReader implements DBReader {

    protected Connection connection;

    public MysqlDBReader(Connection connection){
        this.connection = connection;
    }


    @Override
    public List<String> readTable() {
        ResultSet resultSet = null;
        List<String> tableNames = null;
        try {
            resultSet = connection.getMetaData().getTables(connection.getCatalog(), "root", null, new String[]{"TABLE"});

            tableNames = new ArrayList<String>(resultSet.getFetchSize());
            while (resultSet.next()){
                tableNames.add(resultSet.getString("TABLE_NAME"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tableNames;
    }

    @Override
    public Map<String, Object> readTableColumn(List<String> tableNames) {
        Map<String, Object> map = new HashMap<>();

        for (String tableName : tableNames){
            try {
                //获取表字段内容
                ResultSet resultSet = connection.getMetaData().getColumns(connection.getCatalog(), connection.getMetaData().getUserName(), tableName, null);
                List<Column> list = new ArrayList<Column>(resultSet.getFetchSize());
                while (resultSet.next()){
                    Column column = new Column();
                    String columnName = resultSet.getString("COLUMN_NAME"); //字段名称
                    String columnType = resultSet.getString("TYPE_NAME"); //字段类型
                    String remark = resultSet.getString("REMARKS"); //字段注释

                    column.setTableName(tableName);
                    column.setColumnName(columnName);
                    column.setColumnType(columnType);
                    column.setRemark(remark);

                    list.add(column);
                }

                map.put(tableName, list);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return map;
    }
}
