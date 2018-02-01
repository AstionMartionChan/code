package com.itcast.po;

import com.itcast.handler.DownloadHandler;
import com.itcast.handler.ProccessHandler;
import com.itcast.handler.impl.HtmlCleanerProccessHandlerImpl;
import com.itcast.handler.impl.HttpClientDownloadHandlerImpl;
import org.dom4j.DocumentException;
import org.htmlcleaner.XPatherException;

import java.io.IOException;

/**
 * Created by Leo_Chan on 2018/1/25.
 * haha 用git提交的
 */
public class Spider {

    private DownloadHandler downloadHandler = new HttpClientDownloadHandlerImpl();

    private ProccessHandler proccessHandler = new HtmlCleanerProccessHandlerImpl();


    public void storm() {

    }

    public void start(String url) throws IOException, XPatherException, DocumentException {
        Page page = downloadHandler.download(url);
        page = proccessHandler.proccess(page);
    }

    public void setDownloadHandler(DownloadHandler downloadHandler) {
        this.downloadHandler = downloadHandler;
    }

    public void setProccessHandler(ProccessHandler proccessHandler) {
        this.proccessHandler = proccessHandler;
    }
}
