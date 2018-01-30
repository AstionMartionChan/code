package com.itcast.handler;

import com.itcast.po.Page;

import java.io.IOException;

/**
 * Created by Leo_Chan on 2018/1/25.
 */
public interface DownloadHandler {

    Page download(String url) throws IOException;

}
