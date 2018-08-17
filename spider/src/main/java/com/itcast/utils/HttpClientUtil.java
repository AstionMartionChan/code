package com.itcast.utils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Leo_Chan on 2018/1/30.
 */
public class HttpClientUtil {

    public static final String[] BROWSER = {"Mozilla/5.0 (Windows NT 6.2; Win64; x64) Gecko/20100101 Firefox/50.0", "Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.87 Safari/537.36"};

    public static String sendGet(String url) throws IOException {

        HttpGet request = new HttpGet(url);
        setHeader(request);
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        HttpResponse response = defaultHttpClient.execute(request);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
            return EntityUtils.toString(response.getEntity(), "utf-8");
        } else {
            return "";
        }

    }


    public static String sendGet2(String url) throws IOException {

        HttpGet request = new HttpGet(url);
        setHeader(request);
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        HttpResponse response = defaultHttpClient.execute(request);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
            return EntityUtils.toString(response.getEntity(), "gbk");
        } else {
            return "";
        }

    }


    public static String sendPost(String url, Map<String, Object> paramMap) throws URISyntaxException, IOException {
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost();
        request.setURI(new URI(url));

        //设置参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>(paramMap.size());
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));

            System.out.println(entry.getKey() +  ": " + entry.getValue().toString());
        }

        request.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        HttpResponse response = defaultHttpClient.execute(request);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
            return EntityUtils.toString(response.getEntity(), "utf-8");
        } else {
            return "";
        }
    }

    private static void setHeader(HttpGet request) {
        Random random = new Random();
        int index = random.nextInt(2);
        request.setHeader("User-Agent", BROWSER[index]);
    }

}
