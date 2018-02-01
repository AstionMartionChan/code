package com.itcast.handler.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.itcast.handler.ProccessHandler;
import com.itcast.po.Page;
import com.itcast.utils.HttpClientUtil;
import com.itcast.utils.ProccessUtil;
import org.dom4j.DocumentException;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Created by Leo_Chan on 2018/1/25.
 */
public class HtmlCleanerProccessHandlerImpl implements ProccessHandler {


    @Override
    public Page proccess(Page page) throws XPatherException, IOException, DocumentException {
        String content = page.getContext();
        // 商品标题
        Map<String, Object> title = ProccessUtil.proccessElement(content, "//div[@class='sku-name']");

        // 商品图片
        Map<String, Object> imgUrl = ProccessUtil.proccessElement(content, "//div[@id='spec-n1']/img[1]/@data-origin");

        // 价格
        String json = HttpClientUtil.sendGet("https://p.3.cn/prices/mgets?skuIds=J_5089253");
        json = json.replace("\n", "");
        JSONArray jsonArray = JSONObject.parseArray(json);
        String price = jsonArray.getJSONObject(0).get("op").toString();

        // 参数
        Map<String, Object> map = ProccessUtil.proccessElement(content, "//div[@class='Ptable-item']/dl/*[not(@class)]");

        // 封装参数
        page.addParams("title", title.get("elementText").toString());
        page.addParams("imgUrl", imgUrl.get("elementAttribute").toString());
        page.addParams("price", price);
        page.addParams("commodityParams", JSONObject.toJSONString(map));

        return page;
    }
}
