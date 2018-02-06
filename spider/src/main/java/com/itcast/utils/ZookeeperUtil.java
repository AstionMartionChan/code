package com.itcast.utils;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.List;
import java.util.Properties;

/**
 * Created by Leo_Chan on 2018/2/6.
 */
public class ZookeeperUtil {

    static {
        Properties prop = new Properties();
        InputStream resourceAsStream = MySqlUtil.class.getResourceAsStream("/systemConfig.properties");

        try {
            prop.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        address = prop.getProperty("zookeeper.address").trim();
        connectionTime = Integer.valueOf(prop.getProperty("zookeeper.connection.time").trim());
        reconnectionCount = Integer.valueOf(prop.getProperty("zookeeper.reconnection.count").trim());
        sesstionTimeOut = Integer.valueOf(prop.getProperty("zookeeper.sesstion.timeout").trim());
        connectionTimeOut = Integer.valueOf(prop.getProperty("zookeeper.connection.timeout").trim());


    }

    private static String address;
    private static Integer connectionTime;
    private static Integer reconnectionCount;
    private static Integer sesstionTimeOut;
    private static Integer connectionTimeOut;

    public static CuratorFramework start(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(connectionTime, reconnectionCount);
        CuratorFramework client = CuratorFrameworkFactory.newClient(address, sesstionTimeOut, connectionTimeOut, retryPolicy);
        return client;
    }

    public static void createEphemeral(String parentModeName){
        CuratorFramework client = start();
        try{
            InetAddress localHost = InetAddress.getLocalHost();
            String ip = localHost.getHostAddress();
            client.start();
            client.create()
                    .creatingParentsIfNeeded()      //如果父节点不存在则创建
                    .withMode(CreateMode.EPHEMERAL) //创建临时节点
                    .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)   //指点节点权限
                    .forPath(parentModeName != null ? ("/" + parentModeName + "/" + ip) : "/" + ip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static List<String> getModeList(Watcher watcher, String modeName){
        CuratorFramework client = start();
        try {
            List<String> modeList = client.getChildren().usingWatcher(watcher).forPath(modeName != null ? ("/" + modeName) : "/");
            return modeList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
