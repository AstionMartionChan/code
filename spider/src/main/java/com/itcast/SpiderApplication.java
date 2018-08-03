package com.itcast;

import com.itcast.handler.DownloadHandler;
import com.itcast.handler.impl.*;
import com.itcast.po.Spider;
import com.itcast.utils.MySqlUtil;
import com.itcast.utils.PhantomJSUtil;
import com.itcast.utils.ProccessUtil;
import com.itcast.utils.RedisUtil;
import org.dom4j.tree.DefaultElement;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/6/3
 * Time: 19:35
 * Work contact: Astion_Leo@163.com
 */


public class SpiderApplication {

    public static void main(String[] args) {
        Spider spider = new Spider();
        spider.setDownloadHandler(new HttpClientDownloadHandlerImpl());
        spider.setProccessListHandler(new HtmlCleanerProccessHandlerImpl());
        spider.setProccessDetailHandler(new HtmlCleanerProccessDetailNewHandlerImpl());
        spider.setQueueHandler(new RedisQueueHandlerImpl());
        spider.setStormHandler(new HbaseStoreHandlerImpl());

        spider.start(args);

    }
}
