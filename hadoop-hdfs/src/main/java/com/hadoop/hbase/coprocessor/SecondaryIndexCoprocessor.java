package com.hadoop.hbase.coprocessor;

import org.apache.commons.collections.CollectionUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.coprocessor.BaseRegionObserver;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.regionserver.wal.WALEdit;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/5/21
 * Time: 21:14
 * Work contact: Astion_Leo@163.com
 */


public class SecondaryIndexCoprocessor extends BaseRegionObserver {

    @Override
    public void postPut(ObserverContext<RegionCoprocessorEnvironment> e, Put put, WALEdit edit, Durability durability) throws IOException {
        Configuration configuration = HBaseConfiguration.create();
        Connection connection = ConnectionFactory.createConnection(configuration);
        Table table = connection.getTable(TableName.valueOf(Bytes.toBytes("t_member_secondary_index")));

        List<Cell> cells = put.get(Bytes.toBytes("cf1"), Bytes.toBytes("name"));
        if (CollectionUtils.isNotEmpty(cells)){
            Iterator<Cell> iterator = cells.iterator();
            while (iterator.hasNext()){
                Cell cell = iterator.next();
                byte[] value = cell.getValue();
                byte[] rowkey = cell.getRow();
                Put newPut = new Put(value);
                newPut.addColumn(Bytes.toBytes("cf1"), Bytes.toBytes("rowkey"), rowkey);
                table.put(put);
            }
        }
        table.close();
        connection.close();
    }
}
