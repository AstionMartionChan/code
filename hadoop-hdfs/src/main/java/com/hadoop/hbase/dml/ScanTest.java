package com.hadoop.hbase.dml;

import com.hadoop.hbase.utils.HBaseUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/5/18
 * Time: 19:55
 * Work contact: Astion_Leo@163.com
 */


public class ScanTest {

    public static void main(String[] args) throws IOException {
        query(null, null, "上海", "2003-04-11", "2014-11-18", null);
    }


    private static void query(String id, String name, String area, String startDate, String endDate, String lastDaysLogin) throws IOException {
        Connection connection = HBaseUtil.getConnection();
        TableName tableName = TableName.valueOf(Bytes.toBytes("t_jd_spider_sku_info"));
        Table table = connection.getTable(tableName);
        Scan scan = new Scan();
//        FilterList filterList = new FilterList();
//        if (null != id){
//            filterList.addFilter(new RowFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(id))));
//        }
//        if (null != name){
//            filterList.addFilter(new SingleColumnValueFilter(Bytes.toBytes("cf1"), Bytes.toBytes("name"), CompareFilter.CompareOp.EQUAL, new SubstringComparator(name)));
//        }
//        if (null != area){
//            filterList.addFilter(new SingleColumnValueFilter(Bytes.toBytes("cf1"), Bytes.toBytes("address"), CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(area))));
//        }
//        if (null != startDate){
//            filterList.addFilter(new SingleColumnValueFilter(Bytes.toBytes("cf1"), Bytes.toBytes("startDate"), CompareFilter.CompareOp.GREATER_OR_EQUAL, new BinaryComparator(Bytes.toBytes(startDate))));
//        }
//        if (null != endDate){
//            filterList.addFilter(new SingleColumnValueFilter(Bytes.toBytes("cf1"), Bytes.toBytes("endDate"), CompareFilter.CompareOp.LESS_OR_EQUAL, new BinaryComparator(Bytes.toBytes(endDate))));
//        }
//
//        scan.setFilter(filterList);
        ResultScanner scanner = table.getScanner(scan);
        for (Result result : scanner){
            HBaseUtil.printScanResult(result);
        }
    }
}
