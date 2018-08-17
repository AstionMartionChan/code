package com.itcast.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/6/24
 * Time: 21:56
 * Work contact: Astion_Leo@163.com
 */


public class HBaseUtil {

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Configuration configuration = HBaseConfiguration.create();
            connection = ConnectionFactory.createConnection(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static<T> T get(String tableNam, String rowkey, Class<T> clz) {
        T t = null;
        Connection connection = null;
        try {
            connection = getConnection();
            TableName tableName = TableName.valueOf(Bytes.toBytes(tableNam));
            Table table = connection.getTable(tableName);
            Get get = new Get(Bytes.toBytes(rowkey));
            Result result = table.get(get);
            t = proccessResult(result, clz);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != connection){
                try {
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return t;
    }


    public static List<Map<String, Object>> getVersion(String tableNam, String rowkey, String qualifier, Integer version) {
        Connection connection = null;
        List<Map<String, Object>> resultList = null;
        try {
            connection = getConnection();
            TableName tableName = TableName.valueOf(Bytes.toBytes(tableNam));
            Table table = connection.getTable(tableName);
            Get get = new Get(Bytes.toBytes(rowkey));
            get.setMaxVersions(version);
            get.addColumn(Bytes.toBytes("c1"), Bytes.toBytes(qualifier));
            Result result = table.get(get);
            List<Cell> cells = result.listCells();
            resultList = new ArrayList<>(cells.size());
            if (null != cells && cells.size() > 0){
                for (Cell cell : cells){
                    Map<String, Object> map = new HashMap<>();
                    long timestamp = cell.getTimestamp();
                    String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                    map.put("value", value);
                    map.put("timestamp", timestamp);
                    resultList.add(map);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != connection){
                try {
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return resultList;
    }


    public static<T> T proccessResult(Result result, Class<T> clz) {
        // 获取行键
        String rowKey = Bytes.toString(result.getRow());
        Map<String, Object> hbaseResultMap = new HashMap<>();

        NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> map = result.getMap();
        for (Map.Entry<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> map1: map.entrySet()){
            // 获取列簇
            String family = Bytes.toString(map1.getKey());
            for (Map.Entry<byte[], NavigableMap<Long, byte[]>> map2 : map1.getValue().entrySet()){
                // 获取列名
                String qualifier = Bytes.toString(map2.getKey());

                for (Map.Entry<Long, byte[]> map3 : map2.getValue().entrySet()){
                    // 获取时间戳
                    Long timestamp = map3.getKey();
                    // 获取value值
                    String value = Bytes.toString(map3.getValue());
                    hbaseResultMap.put(qualifier, value);
                }
            }
        }

        T t = ReflectUtil.newObj(hbaseResultMap, clz);

        return t;
    }
}
