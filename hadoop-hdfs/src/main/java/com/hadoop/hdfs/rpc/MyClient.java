package com.hadoop.hdfs.rpc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by leochan on 2018/2/11.
 */
public class MyClient {


    public static void main(String[] args) throws IOException, InterruptedException {
        InetSocketAddress addr = new InetSocketAddress("localhost", 6666);
        Configuration conf = new Configuration();
        Bizable proxy = RPC.getProxy(Bizable.class, Bizable.versionID, addr, conf);

        while(true){
            String s = proxy.sendHeart("发送心跳 --~><~><~>>~" + System.currentTimeMillis());
            if (s.equals("继续干活-----")){
                System.out.println("understand");
            }
            Thread.sleep(3000);
        }
    }
}
