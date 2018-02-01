package com.itcast.test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import java.util.Map;
import java.util.Set;

/**
 * Created by Leo_Chan on 2018/1/25.
 */
public class SpiderTest {


    @Test
    public void spiderTest() throws IOException, XPatherException, DocumentException {
        Spider spider = new Spider();

        spider.setDownloadHandler(new HttpClientDownloadHandlerImpl());
        spider.setProccessHandler(new HtmlCleanerProccessHandlerImpl());
        spider.start("http://www.mvnrepository.com/");


    }


    @Test
    public void getContentTest() throws IOException, XPatherException, DocumentException {
        // 标题
        String content = HttpClientUtil.sendGet("https://item.jd.com/5089253.html");
//        write(content);
        Map<String, Object> title = ProccessUtil.proccessElement(content, "//div[@class='sku-name']");
        System.out.println(title.get("elementText").toString());

        // 商品图片
        Map<String, Object> imgUrl = ProccessUtil.proccessElement(content, "//div[@id='spec-n1']/img[1]/@data-origin");
        System.out.println(imgUrl.get("elementAttribute").toString());

        // 价格
        String json = HttpClientUtil.sendGet("https://p.3.cn/prices/mgets?skuIds=J_5089253");
        json = json.replace("\n", "");
        JSONArray jsonArray = JSONObject.parseArray(json);
        String price = jsonArray.getJSONObject(0).get("op").toString();
        System.out.println(price);

        // 参数
        Map<String, Object> map = ProccessUtil.proccessElement(content, "//div[@class='Ptable-item']/dl/*[not(@class)]");
        Set<String> strings = map.keySet();

        for (String key : map.keySet()){
            String value = map.get(key).toString();
            System.out.println(key + ": " + value);
        }



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
