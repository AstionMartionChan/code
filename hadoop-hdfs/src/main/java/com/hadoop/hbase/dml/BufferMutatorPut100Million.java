package com.hadoop.hbase.dml;

import com.hadoop.hbase.utils.HBaseUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/5/15
 * Time: 20:51
 * Work contact: Astion_Leo@163.com
 */


public class BufferMutatorPut100Million {

    public static void main(String[] args) throws IOException {
        Configuration configuration = HBaseConfiguration.create();
        Connection connection = ConnectionFactory.createConnection(configuration);

        ExecutorService threadPool = Executors.newFixedThreadPool(1);

        for (int y=0; y<10000; y++){
            final int basix = y;
            threadPool.submit(new Runnable() {
                @Override
                public void run() {
                    List<Mutation> putList = new ArrayList<>();
                    for (int x=0; x<=9999; x++){
                        putList.add(HBaseUtil.put(basix + "&"+ x , "cf1", "q1", x));
                    }
                    try {
                        HBaseUtil.putMutation2HBase(connection, "t_100million", putList);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        
        threadPool.shutdown();
        connection.close();
    }

}
