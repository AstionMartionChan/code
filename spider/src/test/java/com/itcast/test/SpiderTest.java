package com.itcast.test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.itcast.handler.impl.HtmlCleanerProccessHandlerImpl;
import com.itcast.handler.impl.HttpClientDownloadHandlerImpl;
import com.itcast.po.Spider;
import com.itcast.utils.HttpClientUtil;
import com.itcast.utils.ProccessUtil;
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


    @Test
    public void getContentTest() throws IOException, XPatherException {
        String content = HttpClientUtil.sendGet("https://item.jd.com/5089253.html");
        String title = ProccessUtil.proccessTextContent(content, "//div[@class='sku-name']");
        System.out.println(title);

        String imgUrl = ProccessUtil.proccessAttributeContent(content, "//div[@id='spec-n1']/img[1]", "data-origin");
        System.out.println(imgUrl);


        String json = HttpClientUtil.sendGet("https://p.3.cn/prices/mgets?skuIds=J_5089253");
        json = json.replace("\n", "");
        JSONArray jsonArray = JSONObject.parseArray(json);
        String price = jsonArray.getJSONObject(0).get("op").toString();
        System.out.println(price);


//        ProccessUtil.proccessTextContentMore(content, "//div[@class='Ptable-item']/h3");

        ProccessUtil.proccessTextContentMore(content, "//div[@class='Ptable-item']/dl/dd[not(@class)]");
    }
}
