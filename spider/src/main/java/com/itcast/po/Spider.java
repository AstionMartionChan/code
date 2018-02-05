package com.itcast.po;

import com.itcast.handler.DownloadHandler;
import com.itcast.handler.ProccessHandler;
import com.itcast.handler.QueueHandler;
import com.itcast.handler.StormHandler;
import com.itcast.handler.impl.*;
import com.itcast.utils.MySqlUtil;
import com.itcast.utils.RedisUtil;
import org.dom4j.DocumentException;
import org.htmlcleaner.XPatherException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Leo_Chan on 2018/1/25.
 * haha 用git提交的
 */
public class Spider {

    private DownloadHandler downloadHandler = new HttpClientDownloadHandlerImpl();

    private ProccessHandler proccessHandler = new HtmlCleanerProccessHandlerImpl();

    private StormHandler stormHandler = new MysqlStormHandlerImpl();

    private QueueHandler queueHandler = new RedisQueueHandlerImpl();

    public void start(String url) throws IOException, XPatherException, DocumentException, SQLException, InterruptedException {

        while (url != null){
            Page page = downloadHandler.download(url);
            // 解析页面 -> list -> nextPage
            Map<String, Object> result = proccessHandler.proccessList(page.getContext());
            url = result.get("nextPageUrl") != null ? result.get("nextPageUrl").toString() : null;
            List<String> detailUrls = result.get("skuUrlList") != null
                    ? (ArrayList<String>) result.get("skuUrlList")
                    : null;

            if (detailUrls != null){
                for (String detailUrl : detailUrls){
                    System.out.println("存入队列" + detailUrl);
                    queueHandler.add(detailUrl);
                }
            }

        }

       while (RedisUtil.llen("jdDetaillUrl") != 0){
           Page detailData = downloadHandler.download(queueHandler.poll());
           Page data = proccessHandler.proccessDetail(detailData);
           stormHandler.saveDB(data);
       }


        MySqlUtil.close();
    }

    public void spider() {
        while (RedisUtil.llen("jdDetaillUrl") != 0){
            try {
                Page detailData = downloadHandler.download(queueHandler.poll());
                Page data = proccessHandler.proccessDetail(detailData);
                stormHandler.saveDB(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        MySqlUtil.close();
    }

    public void setDownloadHandler(DownloadHandler downloadHandler) {
        this.downloadHandler = downloadHandler;
    }

    public void setProccessHandler(ProccessHandler proccessHandler) {
        this.proccessHandler = proccessHandler;
    }
}
