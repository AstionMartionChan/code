package com.itcast.utils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by Leo_Chan on 2018/1/30.
 */
public class HttpClientUtil {


    public static String sendGet(String url) throws IOException {
        HttpGet request = new HttpGet(url);
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        HttpResponse response = defaultHttpClient.execute(request);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
            return EntityUtils.toString(response.getEntity(), "utf-8");
        } else {
            return null;
        }
    }
}
