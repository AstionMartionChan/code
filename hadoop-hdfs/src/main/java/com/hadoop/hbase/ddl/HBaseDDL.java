package com.hadoop.hbase.ddl;

import com.hadoop.hbase.utils.HBaseUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/5/14
 * Time: 17:34
 * Work contact: Astion_Leo@163.com
 */


public class HBaseDDL {

    private Configuration conf;

    public HBaseDDL() {
        this.conf = HBaseConfiguration.create();
    }

    private Connection getConnection() throws IOException {
        return ConnectionFactory.createConnection(conf);
    }

    private void closeConnection(Connection connection) throws IOException {
        connection.close();
    }

    public void createTable(String tableName, String... columeFamilys) throws IOException {
        Connection connection = getConnection();
        Admin admin = connection.getAdmin();
        HTableDescriptor table = new HTableDescriptor(tableName);
        for (String columeFamily : columeFamilys){
            HColumnDescriptor cf = new HColumnDescriptor(columeFamily);
            table.addFamily(cf);
        }
        admin.createTable(table);
        admin.close();
        closeConnection(connection);
    }


    public static void main(String[] args) throws IOException {
//        HBaseDDL hBaseDDL = new HBaseDDL();
//        hBaseDDL.createTable("table5", "cf1", "cf2");

//        HBaseUtil.put2HBase();
    }
}
