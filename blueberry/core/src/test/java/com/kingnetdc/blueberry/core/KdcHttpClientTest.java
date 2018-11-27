package com.kingnetdc.blueberry.core;


import com.kingnetdc.blueberry.core.http.KdcHttpClient;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class KdcHttpClientTest {

    private String url = "http://shservice.kingnetdc.com/dana";

    @Test
    public void doGetTest() {
        String ret = null;
        try {
            ret = KdcHttpClient.doGet(url);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println("url:" + url + ", get, response: " + ret);
        assertTrue("17".equals(ret));
    }

    @Test
    public void doPostTest() {
        String ret = null;
        try {
            ret = KdcHttpClient.doPost(url, "[]");
            System.out.println(KdcHttpClient.doGet("http://www.baidu.com"));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println("url:" + url + ", post, response: " + ret);
        assertTrue("10".equals(ret));
    }

}
