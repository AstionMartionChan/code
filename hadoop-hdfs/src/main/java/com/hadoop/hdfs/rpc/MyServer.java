package com.hadoop.hdfs.rpc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

import java.io.IOException;

/**
 * Created by leochan on 2018/2/11.
 */
public class MyServer {


    public static void main(String[] args) throws IOException {
        Configuration conf = new Configuration();
        RPC.Builder builder = new RPC.Builder(conf);
        builder.setBindAddress("localhost")
                .setPort(6666)
                .setProtocol(Bizable.class)
                .setInstance(new Biz());

        RPC.Server server = builder.build();
        server.start();
        System.out.print("服务器启动了");

    }
}
