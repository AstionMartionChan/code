package com.itcast.handler.impl;

import com.itcast.handler.DownloadHandler;
import com.itcast.po.Page;
import com.itcast.utils.HttpClientUtil;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import sun.net.www.http.HttpClient;

import java.io.IOException;

/**
 * Created by Leo_Chan on 2018/1/25.
 */
public class HttpClientDownloadHandlerImpl implements DownloadHandler {


    @Override
    public Page download(String url) throws IOException {
        Page result = new Page();
        result.setContext(HttpClientUtil.sendGet(url));
        return result;
    }
}
