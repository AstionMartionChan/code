package com.itcast.handler.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.itcast.handler.ProccessHandler;
import com.itcast.po.Page;
import com.itcast.utils.HttpClientUtil;
import com.itcast.utils.ProccessUtil;
import org.dom4j.DocumentException;
import org.dom4j.tree.DefaultAttribute;
import org.dom4j.tree.DefaultElement;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Leo_Chan on 2018/1/25.
 */
public class HtmlCleanerProccessHandlerImpl implements ProccessHandler {


    @Override
    public Map<String, Object> proccessList(String content) throws XPatherException, DocumentException, IOException {
        Map<String, Object> result = new HashMap<>();

        // 获取商品详情url
        Map<String, Object> skuUrlList = ProccessUtil.proccessElement(content, "//*[@id=\"plist\"]/ul/li/div/div[4]/a/@href");
        // 获取下一页url
        Map<String, Object> nextPageUrl = ProccessUtil.proccessElement(content, "//*[@id=\"J_topPage\"]/a[2]/@href");

        // 封装商品详情url
        List<String> resultSkuUrlList = null;
        if (skuUrlList.size() > 0){
            List<DefaultAttribute> attributeList = (List<DefaultAttribute>) skuUrlList.get("attributeList");
            resultSkuUrlList = new ArrayList<>(attributeList.size());

            for (DefaultAttribute defaultAttribute : attributeList){
                resultSkuUrlList.add("https:" + defaultAttribute.getText());
            }
        }


        // 封装下一页url
        String resultNextPageUrl = nextPageUrl.size() > 0
                ? "https://list.jd.com" + nextPageUrl.get("elementAttribute").toString()
                : null;
        result.put("skuUrlList", resultSkuUrlList);
        result.put("nextPageUrl", resultNextPageUrl);

        return result;
    }

    @Override
    public Page proccessDetail(Page page) throws XPatherException, IOException, DocumentException {
        String content = page.getContext();
        // 商品标题
        Map<String, Object> title = ProccessUtil.proccessElement(content, "//div[@class='sku-name']");

        // 商品图片
        Map<String, Object> imgUrl = ProccessUtil.proccessElement(content, "//div[@id='spec-n1']/img[1]/@data-origin");

        // 价格
        String url = page.getUrl().replace("https://item.jd.com/", "").replace(".html", "");
//        String json = HttpClientUtil.sendGet("https://p.3.cn/prices/mgets?skuIds=J_" + url);
        String json = HttpClientUtil.sendGet("https://p.3.cn/prices/mgets?callback=jQuery5516057&type=1&area=1_72_4137_0&pdtk=&pduid=1821570292&pdpin=&pin=null&pdbp=0&skuIds=J_" + url + "&ext=11000000&source=item-pc");
        json = json.replace("\n", "").replace("jQuery5516057(", "").replace(");", "");
        JSONArray jsonArray = JSONObject.parseArray(json);
        String price = jsonArray.size() != 0 ? jsonArray.getJSONObject(0).get("op").toString() : "";

        // 参数
        Map<String, Object> map = ProccessUtil.proccessElement(content, "//div[@class='Ptable-item']/dl/*[not(@class)]");
        List<DefaultElement> elementList = map.get("elementList") != null ? (ArrayList<DefaultElement>) map.get("elementList") : new ArrayList<DefaultElement>();
        Map<String, Object> params = new HashMap<>();
        if (elementList.size() != 0){
            String temp = null;
            for (int x = 0; x < elementList.size(); x++){
                if (isOdd(x)){ //偶数
                    params.put(elementList.get(x).getText(), "");
                    temp = elementList.get(x).getText();
                } else {      //奇数
                    params.put(temp, elementList.get(x).getText());
                }
            }
        }

        // 封装参数
        page.addParams("title", trim(title.get("elementText") != null ? title.get("elementText").toString() : ""));
        page.addParams("imgUrl", imgUrl.get("elementAttribute") != null ? imgUrl.get("elementAttribute").toString() : "");
        page.addParams("price", price);
        page.addParams("commodityParams", JSONObject.toJSONString(params));

        return page;
    }


    private String trim(String textContent) {
        textContent = textContent.trim();
        while (textContent.startsWith("　")) {
            textContent = textContent.substring(1, textContent.length()).trim();
        }
        while (textContent.endsWith("　")) {
            textContent = textContent.substring(0, textContent.length() - 1).trim();
        }
        return textContent;
    }

    private static boolean isOdd(int a){
        if((a & 1) != 1){   //是奇数
            return true;
        }
        return false;
    }
}
