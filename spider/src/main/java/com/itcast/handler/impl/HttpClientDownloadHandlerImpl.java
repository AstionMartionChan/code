package com.itcast.handler.impl;

import com.itcast.handler.DownloadHandler;
import com.itcast.po.Page;
import com.itcast.po.Spider;
import com.itcast.utils.HttpClientUtil;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.net.www.http.HttpClient;

import java.io.IOException;

/**
 * Created by Leo_Chan on 2018/1/25.
 */
public class HttpClientDownloadHandlerImpl implements DownloadHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientDownloadHandlerImpl.class);

    public Page download(String url) throws IOException {
        Page result = new Page();
        result.setContext(HttpClientUtil.sendGet(url));
        result.setUrl(url);
        LOGGER.info("正在下载列表页： {}", url);
        return result;
    }
}
