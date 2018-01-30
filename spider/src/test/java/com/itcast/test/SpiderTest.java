package com.itcast.test;

import com.itcast.handler.impl.HtmlCleanerProccessHandlerImpl;
import com.itcast.handler.impl.HttpClientDownloadHandlerImpl;
import com.itcast.po.Spider;
import org.htmlcleaner.XPatherException;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by Leo_Chan on 2018/1/25.
 */
public class SpiderTest {


    @Test
    public void spiderTest() throws IOException, XPatherException {
        Spider spider = new Spider();

        spider.setDownloadHandler(new HttpClientDownloadHandlerImpl());
        spider.setProccessHandler(new HtmlCleanerProccessHandlerImpl());
        spider.start("http://www.mvnrepository.com/");


    }
}
