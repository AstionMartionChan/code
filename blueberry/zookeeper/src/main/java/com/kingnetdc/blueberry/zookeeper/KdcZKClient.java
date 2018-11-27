package com.kingnetdc.blueberry.zookeeper;


import com.kingnetdc.blueberry.zookeeper.base.Constants;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * kdc zookeeper client for java
 * 提供的功能 zk 的访问和选举功能
 * 读取、写入、选举客户端
 * @author jake.zhang <zhangxj@kingnet.com>
 */
public class KdcZKClient {

    private static final Logger logger = LoggerFactory.getLogger(KdcZKClient.class);

    private CuratorFramework client;

    public KdcZKClient(String connector) {
        this(connector, Constants.DEFAULT_ZK_CONNECT_RETRY_TIME);
    }

    public KdcZKClient(String connector, int retry) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, retry);
        client = CuratorFrameworkFactory.newClient(connector, retryPolicy);
        client.start();
    }

    public byte[] get(String path) {
        try {
            return client.getData().forPath(path);
        } catch (Throwable e) {
            logger.error("get zk data from path: " + path + " error.", e);
        }
        return null;
    }

    public void set(String path) {
        try {
            client.setData().forPath(path);
        } catch (Throwable e) {
            logger.error("set zk data for path: " + path +" error.", e);
        }
    }

    public void set(String path, byte[] data) {
        try {
            client.setData().forPath(path, data);
        } catch (Throwable e) {
            logger.error("set zk data for path: " + path +" error.", e);
        }
    }

    public boolean exists(String path) {
        return null != stat(path);
    }

    public boolean remove(String path) {
        try {
            client.delete().guaranteed().deletingChildrenIfNeeded().forPath(path);
            return true;
        } catch (Throwable e) {
            logger.error("remove zk path: " + path + " error.", e);
        }
        return false;
    }

    public List<String> getChildren(String path) {
        try {
            return client.getChildren().forPath(path);
        } catch (Throwable e) {
            logger.error("get children for path: " + path + " error.", e);
        }
        return null;
    }

    public Stat stat(String path) {
        try {
            return client.checkExists().forPath(path);
        } catch (Throwable e) {
            logger.error("get zk stat path: " + path + " error.", e);
        }
        return null;
    }

    public void close() {
        try {
            client.close();
        } catch (Throwable e) {
            logger.error("close zookeeper error.", e);
        }
    }
}
