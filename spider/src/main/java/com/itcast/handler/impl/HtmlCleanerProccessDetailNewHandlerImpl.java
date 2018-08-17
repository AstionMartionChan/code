package com.itcast.handler.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.JSONToken;
import com.itcast.exception.PriceGetFailException;
import com.itcast.handler.ProccessHandler;
import com.itcast.po.JdSkuNewInfo;
import com.itcast.po.Page;
import com.itcast.utils.HttpClientUtil;
import com.itcast.utils.ProccessUtil;
import org.dom4j.DocumentException;
import org.htmlcleaner.XPatherException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Leo_Chan on 2018/1/25.
 */
public class HtmlCleanerProccessDetailNewHandlerImpl implements ProccessHandler {

    private static final String GET_PRICE_URL = "https://p.3.cn/prices/mgets?callback=jQuery<>&type=1&area=1_72_4137_0&pdtk=&pduid=1821570292&pdpin=&pin=null&pdbp=0&skuIds=J_{}&ext=11000000&source=item-pc";
    private static final String GET_PRICE_URL_2 = "https://c0.3.cn/stock?skuId={}&area=1_72_2799_0&venderId=742889&cat=9987,653,655&buyNum=1&choseSuitSkuIds=&extraParam=%7B%22originid%22:%221%22%7D&ch=1&fqsp=0&pduid=1527989069105382738614&pdpin=&detailedAdd=null&callback=jQuery861981";
    private static final String GET_SALER_URL = "https://cd.jd.com/promotion/v2?callback=jQuery<>&skuId={}&area=1_72_2799_0&shopId=313133&venderId=22089&cat=9987%2C653%2C655&isCanUseDQ=isCanUseDQ-1&isCanUseJQ=isCanUseJQ-1&platform=0&orgType=2&jdPrice=4578.00&appid=1&_=1530086723211";
    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlCleanerProccessDetailNewHandlerImpl.class);


    public Page proccessDetail(Page page) throws XPatherException, IOException, DocumentException {
        String content = page.getContext();
        JdSkuNewInfo result = new JdSkuNewInfo();

        // 标题
        Map<String, List<String>> stringObjectMap2 = ProccessUtil.proccessElement2(content, "//div[@class=\"sku-name\"]");
        if (stringObjectMap2.size() > 0){
            List<String> elementList = stringObjectMap2.get("elementText");
            result.setTitle(elementList.get(0));
        }

        // 价格
        Integer randomNum = getRandomNum();
        String skuId = getSkuId(page.getUrl());
//        String json = HttpClientUtil.sendGet(GET_PRICE_URL.replace("{}", skuId).replace("<>", randomNum.toString()));
//        json = json.replace("\n", "").replace("jQuery"+ randomNum +"(", "").replace(");", "");
//        String substring = json.substring(0, 1);
//        if (substring.equals("{")) {
//            LOGGER.info("价格原始接口获取失败，尝试用备用接口获取价格 ...");
            String json = HttpClientUtil.sendGet(GET_PRICE_URL_2.replace("{}", skuId));
            json = json.replace("\n", "").replace("jQuery861981(", "").replace(")", "");
            JSONObject jsonObject = JSONObject.parseObject(json);
            if (jsonObject.containsKey("stock")){
                JSONObject stock = jsonObject.getJSONObject("stock");
                if (stock.containsKey("jdPrice")){
                    JSONObject jdPrice = stock.getJSONObject("jdPrice");
                    String op_price = jdPrice.get("op").toString();
                    String p_price = jdPrice.get("p").toString();
                    result.setOpPrice(Double.valueOf(op_price));
                    result.setpPrice(Double.valueOf(p_price));
                } else {
                    throw new PriceGetFailException(json);
                }
            } else {
                throw new PriceGetFailException(json);
            }
//        } else {
//            JSONArray jsonArray = JSONObject.parseArray(json);
//            if (jsonArray.size() != 0){
//                String op_price = jsonArray.getJSONObject(0).get("op").toString();
//                String p_price = jsonArray.getJSONObject(0).get("p").toString();
//                result.setOpPrice(Double.valueOf(op_price));
//                result.setpPrice(Double.valueOf(p_price));
//            }
//        }

        // 商品图片
        String imgUrl = "";
        Map<String, List<String>> stringListMap = ProccessUtil.proccessElement2(content, "//img[@id='spec-img']/@data-origin");
        List<String> urlList = stringListMap.get("elementAttribute");
        if (null == urlList || urlList.size() == 0){
            stringListMap = ProccessUtil.proccessElement2(content, "//div[@id='spec-n1']/img/@src");
            urlList = stringListMap.get("elementAttribute");
            if (null != urlList && urlList.size() > 0){
                imgUrl = urlList.get(0);
                result.setImgUrl(imgUrl);
            }
        } else {
            imgUrl = urlList.get(0);
            result.setImgUrl(imgUrl);
        }


        Map<String, String> saleMap = new HashMap<>();
        Map<String, String> couponMap = new HashMap<>();
        Map<String, String> paramMap = new HashMap<>();
        String key = "";
        String value = "";

        // 促销
        String salerJson = HttpClientUtil.sendGet2(GET_SALER_URL.replace("{}", skuId).replace("<>", randomNum.toString()));
        salerJson = salerJson.replace("\n", "").replace("jQuery"+ randomNum +"(", "").replace(")", "");
        JSONObject jsonObject1 = JSONObject.parseObject(salerJson);
        if (jsonObject1.containsKey("prom")){
            JSONObject prom = jsonObject1.getJSONObject("prom");
            if (prom.containsKey("pickOneTag")){
                JSONArray pickOneTag = prom.getJSONArray("pickOneTag");
                if (null != pickOneTag){
                    for (int x=0; x < pickOneTag.size(); x++){
                        JSONObject obj = pickOneTag.getJSONObject(x);
                        if (obj.containsKey("name") && obj.containsKey("content")){
                            saleMap.put(obj.get("name").toString(), obj.get("content").toString());
                        }
                    }
                }
            }
            if (prom.containsKey("tags")){
                JSONArray tags = prom.getJSONArray("tags");
                if (null != tags){
                    for (int x=0; x < tags.size(); x++){
                        JSONObject obj = tags.getJSONObject(x);
                        if (obj.containsKey("name") && obj.containsKey("content")){
                            saleMap.put(obj.get("name").toString(), obj.get("content").toString());
                        }
                    }
                }
            }
        }
        result.setSales(JSONObject.toJSONString(saleMap));

        // 优惠券
        if (jsonObject1.containsKey("skuCoupon")){
            JSONArray skuCoupon = jsonObject1.getJSONArray("skuCoupon");
            if (null != skuCoupon){
                for (int x=0; x < skuCoupon.size(); x++){
                    JSONObject obj = skuCoupon.getJSONObject(x);
                    if (obj.containsKey("quota") && obj.containsKey("discount")){
                        couponMap.put(obj.get("quota").toString(), obj.get("discount").toString());
                    }
                }
            }

        }
        result.setCoupon(JSONObject.toJSONString(couponMap));

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


    private Integer getRandomNum() {
        Random random = new Random();
        return random.nextInt(9999999);
    }

    private static boolean isOdd(int a){
        if((a & 1) != 1){   //是奇数
            return true;
        }
        return false;
    }
}
