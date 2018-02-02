package com.itcast.po;

import com.itcast.handler.DownloadHandler;
import com.itcast.handler.ProccessHandler;
import com.itcast.handler.StormHandler;
import com.itcast.handler.impl.HtmlCleanerProccessHandlerImpl;
import com.itcast.handler.impl.HttpClientDownloadHandlerImpl;
import com.itcast.handler.impl.MysqlStormHandlerImpl;
import org.dom4j.DocumentException;
import org.htmlcleaner.XPatherException;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Leo_Chan on 2018/1/25.
 * haha 用git提交的
 */
public class Spider {

    private DownloadHandler downloadHandler = new HttpClientDownloadHandlerImpl();

    private ProccessHandler proccessHandler = new HtmlCleanerProccessHandlerImpl();

    private StormHandler stormHandler = new MysqlStormHandlerImpl();

    public void start(String url) throws IOException, XPatherException, DocumentException, SQLException {
        Page page = downloadHandler.download(url);
        // 解析页面 -> list -> nextPage

        // 如果nextPage 不为空 继续解析

        // 如果list 不为空 存队列


        page = proccessHandler.proccessDetail(page);
        stormHandler.saveDB(page);
    }

    public void setDownloadHandler(DownloadHandler downloadHandler) {
        this.downloadHandler = downloadHandler;
    }

    public void setProccessHandler(ProccessHandler proccessHandler) {
        this.proccessHandler = proccessHandler;
    }
}
