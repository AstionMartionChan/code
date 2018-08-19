package com.hadoop.kafka.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/8/19
 * Time: 17:41
 * Work contact: Astion_Leo@163.com
 */


public class HttpClientUtil {

    public static JSONObject doPost(String url, String json) {

        JSONObject jsonObject = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost = setHeader(httpPost);
            StringEntity entity = new StringEntity(json, "UTF-8");
            httpPost.setEntity(entity);

            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                String result = EntityUtils.toString(response.getEntity());
                jsonObject = JSONObject.parseObject(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    private static HttpPost setHeader(HttpPost httpPost) {
        httpPost.setHeader("Accept", MediaType.APPLICATION_JSON);
        httpPost.setHeader("Content-Type", MediaType.APPLICATION_JSON);
        return httpPost;
    }
}
