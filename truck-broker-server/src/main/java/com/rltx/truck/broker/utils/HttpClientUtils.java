package com.rltx.truck.broker.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Created by Leo_Chan on 2017/9/12.
 */
public class HttpClientUtils {

    /**
     * 发送HTTP GET请求
     * @param url
     */
    public static String sendHttpGetRequest(String url) {
        HttpGet httpGet = null;
        String result = null;
        try {
            httpGet = new HttpGet(url);
            HttpClient httpClient = HttpClients.createDefault();
            RequestConfig config = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();
            httpGet.setConfig(config);
            HttpResponse response = null;

            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200){
                result= EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("连接超时");
        } finally {
            if (httpGet != null){
                httpGet.releaseConnection();
            }
        }

        return result;
    }
}
