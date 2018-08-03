package com.hadoop.hbase.dml;

import com.hadoop.hbase.utils.HBaseUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/5/15
 * Time: 20:32
 * Work contact: Astion_Leo@163.com
 */


public class Put100Million {

    /**
     * 结论：每次执行插入获取连接 平均耗时250毫秒 需要6小时左右插完1亿条数据
     *      直接获取连接插入，平均耗时60毫秒 需要1小时左右插完1亿条数据
     * @param args
     * @throws IOException
     */

    public static void main(String[] args) throws IOException {
        Configuration configuration = HBaseConfiguration.create();
        Connection connection = ConnectionFactory.createConnection(configuration);

        List<Put> putList = new ArrayList<>();
        for (int x=0; x<=99999999; x++){
            if (x % 1000 == 0){
                HBaseUtil.put2HBase(connection, "t_100million", putList);
                putList.clear();
            }
            putList.add(HBaseUtil.put(x + "", "cf1", "q1", x));
        }

        connection.close();
    }

}
