package com.itcast.po;

import com.itcast.exception.PriceGetFailException;
import com.itcast.handler.DownloadHandler;
import com.itcast.handler.ProccessHandler;
import com.itcast.handler.QueueHandler;
import com.itcast.handler.StoreHandler;
import com.itcast.utils.PhantomJSUtil;
import com.itcast.utils.RedisUtil;
import org.dom4j.DocumentException;
import org.htmlcleaner.XPatherException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Leo_Chan on 2018/1/25.
 * haha 用git提交的
 */
public class Spider {

    private DownloadHandler downloadHandler;

    private ProccessHandler proccessListHandler;

    private ProccessHandler proccessDetailHandler;

    private StoreHandler stormHandler;

    private QueueHandler queueHandler;

    private static final Logger LOGGER = LoggerFactory.getLogger(Spider.class);

    public void check() {
/*
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        String zookeeperConnectionString = "47.98.46.234:2181";
        CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, 5000, 3000, retryPolicy);
        client.start();

        InetAddress localHost = InetAddress.getLocalHost();
        String ip = localHost.getHostAddress();
        try {
            client.create()
                    .creatingParentsIfNeeded()      //如果父节点不存在则创建
                    .withMode(CreateMode.EPHEMERAL) //创建临时节点
                    .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)   //指点节点权限
                    .forPath("/spider/" + ip);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    public void start(String[] args){
        while (true) {
            String url = queueHandler.poll();
            if (null != url){
                // 列表页
                if (url.contains("list.jd.com")){
                    spiderList(url);
                } else if (url.contains("item.jd.com")){ // 详情页
                    spiderDetailAndStore(url, args);
                }

            } else {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                LOGGER.info("没有Url了，休息一会");
            }
        }

    }



    public void spiderList(String url){
        try {
            Page page = downloadHandler.download(url);
            // 解析列表页面
            Map<String, Object> result = proccessListHandler.proccessList(page.getContext());
            // 获取下一个页面
            String nextPageUrl = result.get("nextPageUrl") != null
                    ? result.get("nextPageUrl").toString()
                    : null;
            // 获取详情页url
            List<String> detailUrls = result.get("skuUrlList") != null
                    ? (ArrayList<String>) result.get("skuUrlList")
                    : null;

            // 存入redis队列
            if (nextPageUrl != null){
                // 取前100页数据
                if (!nextPageUrl.contains("page=101")){
                    queueHandler.add(nextPageUrl);
                }
            }
            if (detailUrls != null){
                for (String detailUrl : detailUrls){
                    queueHandler.add(detailUrl);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            failProccess(url);
            if (isRehandler(url)){
                RedisUtil.rpush("jd_detail_url", url);
            }
            LOGGER.info("解析列表页面出错： {}", url);
        }
    }

    public void spiderDetailAndStore(String url, String[] args) {

        try {
            // 下载页面
            Page page = downloadHandler.download(url);
            // 解析页面
            proccessDetailHandler.proccessDetail(page);
            // 存入hbase
            stormHandler.saveDB(page);

            LOGGER.info("页面爬取成功");
//            LOGGER.info("休息2秒......");
//            Thread.sleep(2000);
        } catch (PriceGetFailException e) {
            e.printStackTrace();
            LOGGER.info("页面爬取失败 url: {}", url);
//            RedisUtil.lpush("jd_detail_url", url);
        } catch (Exception e) {
            e.printStackTrace();
//            failProccess(url);
//            if (isRehandler(url)){
//                RedisUtil.rpush("jd_detail_url", url);
//            }
            LOGGER.info("页面爬取失败 url: {}", url);
        }

    }


    private Map<String, Integer> failCountMap = new HashMap<>();
    public Boolean isRehandler(String url) {

        Integer count = failCountMap.get(url);
        if (count != null && count > 3){
            return false;
        } else {
            return true;
        }
    }


    public void failProccess(String url) {
        Integer count = 0;
        if (failCountMap.containsKey(url)){
            count = failCountMap.get(url);
        }
        count++;

        failCountMap.put(url, count);
    }

    public DownloadHandler getDownloadHandler() {
        return downloadHandler;
    }

    public void setDownloadHandler(DownloadHandler downloadHandler) {
        this.downloadHandler = downloadHandler;
    }

    public ProccessHandler getProccessListHandler() {
        return proccessListHandler;
    }

    public void setProccessListHandler(ProccessHandler proccessListHandler) {
        this.proccessListHandler = proccessListHandler;
    }

    public ProccessHandler getProccessDetailHandler() {
        return proccessDetailHandler;
    }

    public void setProccessDetailHandler(ProccessHandler proccessDetailHandler) {
        this.proccessDetailHandler = proccessDetailHandler;
    }

    public StoreHandler getStormHandler() {
        return stormHandler;
    }

    public void setStormHandler(StoreHandler stormHandler) {
        this.stormHandler = stormHandler;
    }

    public QueueHandler getQueueHandler() {
        return queueHandler;
    }

    public void setQueueHandler(QueueHandler queueHandler) {
        this.queueHandler = queueHandler;
    }
}
