package com.itcast.po;

import com.itcast.handler.DownloadHandler;
import com.itcast.handler.ProccessHandler;
import org.htmlcleaner.XPatherException;

import java.io.IOException;

/**
 * Created by Leo_Chan on 2018/1/25.
 */
public class Spider {

    private DownloadHandler downloadHandler;

    private ProccessHandler proccessHandler;


    public void storm() {

    }

    public void start(String url) throws IOException, XPatherException {
        Page page = downloadHandler.download(url);
        page = proccessHandler.proccess(page);
        System.out.print(page.getTitle());
    }

    public void setDownloadHandler(DownloadHandler downloadHandler) {
        this.downloadHandler = downloadHandler;
    }

    public void setProccessHandler(ProccessHandler proccessHandler) {
        this.proccessHandler = proccessHandler;
    }
}
