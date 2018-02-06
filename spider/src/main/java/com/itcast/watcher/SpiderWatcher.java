package com.itcast.watcher;

import com.itcast.utils.ZookeeperUtil;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.List;


/**
 * Created by Leo_Chan on 2018/2/6.
 */
public class SpiderWatcher implements Watcher {

    private List<String> modeList;

    private CuratorFramework client;

    public SpiderWatcher(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        String zookeeperConnectionString = "47.98.46.234:2181";
        client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, 5000, 3000, retryPolicy);
        client.start();

        try {
            modeList = client.getChildren().usingWatcher(this).forPath("/spider");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void process(WatchedEvent event) {
        List<String> nowModeList = null;
        try {
            nowModeList = client.getChildren().usingWatcher(this).forPath("/spider");
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String mode : modeList){
            if (!nowModeList.contains(mode)){
                System.out.println("------销毁了爬虫节点" + mode);
            }
        }

        for (String mode : nowModeList){
            if (!modeList.contains(mode)){
                System.out.println("------新增了爬虫节点" + mode);
            }
        }

        modeList = nowModeList;

    }


    public static void main(String[] args){
        SpiderWatcher spiderWatcher = new SpiderWatcher();

        while (true){

        }
    }
}
