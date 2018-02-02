package com.itcast.test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.itcast.handler.ProccessHandler;
import com.itcast.handler.impl.HtmlCleanerProccessHandlerImpl;
import com.itcast.handler.impl.HttpClientDownloadHandlerImpl;
import com.itcast.po.Spider;
import com.itcast.utils.HttpClientUtil;
import com.itcast.utils.ProccessUtil;
import org.dom4j.DocumentException;
import org.htmlcleaner.XPatherException;
import org.junit.Test;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

/**
 * Created by Leo_Chan on 2018/1/25.
 */
public class SpiderTest {


    @Test
    public void spiderTest() throws IOException, XPatherException, DocumentException, SQLException {
        Spider spider = new Spider();
        spider.start("https://item.jd.com/5089253.html");

    }


    @Test
    public void getContentTest() throws IOException, XPatherException, DocumentException {
        // 标题
//        String content = HttpClientUtil.sendGet("https://list.jd.com/list.html?cat=9987,653,655");
//        write(content);
//        Map<String, Object> title = ProccessUtil.proccessElement(content, "//*[@id=\"plist\"]/ul/li/div/div[4]/a/@href");
//        Map<String, Object> nextPageUrl = ProccessUtil.proccessElement(content, "//*[@id=\"J_topPage\"]/a[2]/@href");

//        System.out.println(title.get("elementText").toString());

        String content = HttpClientUtil.sendGet("https://list.jd.com/list.html?cat=9987,653,655");
        ProccessHandler proccessHandler = new HtmlCleanerProccessHandlerImpl();
        Map<String, Object> map = proccessHandler.proccessList(content);


        return;
    }



    private void write(String content) {
        try {

            FileOutputStream outputStream = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\a.txt");
            outputStream.write(content.getBytes("utf-8"));
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
