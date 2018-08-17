package com.itcast.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/5/15
 * Time: 17:55
 * Work contact: Astion_Leo@163.com
 */


public class HBaseUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(HBaseUtil.class);

    public static Connection getConnection() throws IOException {
        Configuration configuration = HBaseConfiguration.create();
        return ConnectionFactory.createConnection(configuration);
    }

    public static void putMutation2HBase(Connection connection, String tableNam, List<Mutation> putList) throws IOException {
        long start = System.currentTimeMillis();
        BufferedMutatorParams params = new BufferedMutatorParams(TableName.valueOf(Bytes.toBytes(tableNam)));
        BufferedMutator mutator = connection.getBufferedMutator(params);
        mutator.mutate(putList);
        mutator.close();
        long end = System.currentTimeMillis();
        LOGGER.warn("插入了{}条数据，耗时{}毫秒", putList.size(), (end - start));
    }


    public static void put2HBase(Connection connection, String tableNam, List<Put> putList) throws IOException {
        long start = System.currentTimeMillis();
        TableName tableName = TableName.valueOf(Bytes.toBytes(tableNam));
        Table table = connection.getTable(tableName);
        table.put(putList);
        table.close();
        long end = System.currentTimeMillis();
        LOGGER.warn("插入了{}条数据，耗时{}毫秒", putList.size(), (end - start));
    }


    public static void put2HBase(String tableNam, List<Put> putList) throws IOException {
        long start = System.currentTimeMillis();
        Connection connection = getConnection();
        TableName tableName = TableName.valueOf(Bytes.toBytes(tableNam));
        Table table = connection.getTable(tableName);
        table.put(putList);
        table.close();
        connection.close();
        long end = System.currentTimeMillis();
        LOGGER.info("插入了{}条数据，耗时{}毫秒", putList.size(), (end - start));
    }

    public static void put2HBase(String tableNam, Put put) throws IOException {
        long start = System.currentTimeMillis();
        Connection connection = getConnection();
        TableName tableName = TableName.valueOf(Bytes.toBytes(tableNam));
        Table table = connection.getTable(tableName);
        table.put(put);
        table.close();
        connection.close();
        long end = System.currentTimeMillis();
        LOGGER.info("插入了1条数据，耗时{}毫秒", (end - start));
    }


    public static Put put(String rowKey, String family, String qualifier, Object value) {
        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value.toString()));
        return put;
    }


    public static ResultScanner scan(String tableNam) throws IOException {
        Connection connection = getConnection();
        TableName tableName = TableName.valueOf(Bytes.toBytes(tableNam));
        Table table = connection.getTable(tableName);
        Scan scan = new Scan();
        ResultScanner scanner = table.getScanner(scan);
        return scanner;
    }

    public static void printScanResult(Result result){
        // 获取行键
        String rowKey = Bytes.toString(result.getRow());

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
                    LOGGER.warn("{} {}:{} {} {}", rowKey, family, qualifier, timestamp, value);
                }
            }
        }
    }
}
