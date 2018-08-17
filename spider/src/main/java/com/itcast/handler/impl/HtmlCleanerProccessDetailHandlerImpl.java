package com.itcast.handler.impl;

import com.alibaba.fastjson.JSONObject;
import com.itcast.handler.ProccessHandler;
import com.itcast.po.JdSkuInfo;
import com.itcast.po.Page;
import com.itcast.utils.ProccessUtil;
import org.dom4j.DocumentException;
import org.htmlcleaner.XPatherException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Leo_Chan on 2018/1/25.
 */
public class HtmlCleanerProccessDetailHandlerImpl implements ProccessHandler {


    public Page proccessDetail(Page page) throws XPatherException, IOException, DocumentException {
        String content = page.getContext();
        JdSkuInfo result = new JdSkuInfo();

        // 标题
        Map<String, List<String>> stringObjectMap2 = ProccessUtil.proccessElement2(content, "//div[@class=\"sku-name\"]");
        if (stringObjectMap2.size() > 0){
            List<String> elementList = stringObjectMap2.get("elementText");
            result.setTitle(elementList.get(0));
        }

        Map<String, List<String>> stringObjectMap = ProccessUtil.proccessElement2(content, "//div[@class=\"dd\"]/span[@class=\"p-price\"]/span[2]");
        if (stringObjectMap.size() > 0){
            List<String> elementList = stringObjectMap.get("elementText");
            result.setPrice(Double.valueOf(elementList.get(0)));
        }

        Map<String, String> couponMap = new HashMap<>();
        Map<String, String> saleMap = new HashMap<>();
        Map<String, String> paramMap = new HashMap<>();
        String key = "";
        String value = "";
        // 优惠券
        Map<String, List<String>> stringObjectMap3 = ProccessUtil.proccessElement2(content, "//a[@class=\"J-open-tb\"]/span[@class=\"quan-item\"]/span");
        if (stringObjectMap3.size() > 0){
            List<String> elementList = null;
            if (stringObjectMap3.containsKey("elementList")){
                elementList = stringObjectMap3.get("elementList");
            } else if (stringObjectMap3.containsKey("elementText")) {
                elementList = stringObjectMap3.get("elementText");
            }


            for (int x=0; x < elementList.size(); x++){
                if (isOdd(x)){
                    key = elementList.get(x);
                } else {
                    value = elementList.get(x);
                }
                couponMap.put(key, value);
            }

        }
        result.setCoupon(JSONObject.toJSONString(couponMap));

        // 促销 （换购，满减）
        Map<String, List<String>> stringObjectMap4 = ProccessUtil.proccessElement2(content, "//ins[@id=\"prom-one\"]/div[@class=\"J-prom\"]/div/em");
        if (stringObjectMap4.size() > 0){
            List<String> elementList = null;
            if (stringObjectMap4.containsKey("elementList")){
                elementList = stringObjectMap4.get("elementList");
            } else if (stringObjectMap4.containsKey("elementText")) {
                elementList = stringObjectMap4.get("elementText");
            }

            for (int x=0; x < elementList.size(); x++){
                if (isOdd(x)){
                    key = elementList.get(x);
                } else {
                    value = elementList.get(x);
                }
                saleMap.put(key, value);
            }

        }

        // 促销 （限制）
        Map<String, List<String>> stringObjectMap5 = ProccessUtil.proccessElement2(content, "//ins[@id=\"prom\"]/div[@class=\"J-prom\"]/div/em");
        if (stringObjectMap5.size() > 0){
            List<String> elementList = null;
            if (stringObjectMap5.containsKey("elementList")){
                elementList = stringObjectMap5.get("elementList");
            } else if (stringObjectMap5.containsKey("elementText")) {
                elementList = stringObjectMap5.get("elementText");
            }

            for (int x=0; x < elementList.size(); x++){
                if (isOdd(x)){
                    key = elementList.get(x);
                } else {
                    value = elementList.get(x);
                }
                saleMap.put(key, value);
            }

        }
        // 促销 （满额返券）
        Map<String, List<String>> stringObjectMap6 = ProccessUtil.proccessElement2(content, "//ins[@id=\"prom-quan\"]/div[@class=\"J-prom-quan prom-quan\"]/em");
        if (stringObjectMap6.size() > 0){
            List<String> elementList = null;
            if (stringObjectMap6.containsKey("elementList")){
                elementList = stringObjectMap6.get("elementList");
            } else if (stringObjectMap6.containsKey("elementText")) {
                elementList = stringObjectMap6.get("elementText");
            }

            for (int x=0; x < elementList.size(); x++){
                if (isOdd(x)){
                    key = elementList.get(x);
                } else {
                    value = elementList.get(x);
                }
                saleMap.put(key, value);
            }

        }

        result.setSales(JSONObject.toJSONString(saleMap));

        // 商品参数
        Map<String, List<String>> stringObjectMap7 = ProccessUtil.proccessElement2(content, "//div[@class='Ptable-item']/dl/*[not(@class)]");
        if (stringObjectMap7.size() > 0){
            List<String> elementList = null;
            if (stringObjectMap7.containsKey("elementList")){
                elementList = stringObjectMap7.get("elementList");
            } else if (stringObjectMap7.containsKey("elementText")) {
                elementList = stringObjectMap7.get("elementText");
            }

            for (int x=0; x < elementList.size(); x++){
                if (isOdd(x)){
                    key = elementList.get(x);
                } else {
                    value = elementList.get(x);
                }
                paramMap.put(key, value);
            }

        }
        result.setParam(JSONObject.toJSONString(paramMap));
        result.setUrl(page.getUrl());
        result.setSkuId(getSkuId(page.getUrl()));



        page.setJdSkuInfo(result);

        return page;
    }

    @Override
    public Map<String, Object> proccessList(String content) throws XPatherException, DocumentException, IOException {
        return null;
    }



    private String getSkuId(String url){
        return url.substring(url.lastIndexOf("/") + 1).replace(".html", "");
    }


    private static boolean isOdd(int a){
        if((a & 1) != 1){   //是奇数
            return true;
        }
        return false;
    }
}
