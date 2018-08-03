package com.itcast.controller;

import com.alibaba.fastjson.JSONObject;
import com.itcast.bean.JDSkuDetailInfo;
import com.itcast.bean.JDSkuSearchInfo;
import com.itcast.service.IEsService;
import com.itcast.service.IHBaseService;
import com.itcast.utils.FreeMarkerUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/6/26
 * Time: 15:55
 * Work contact: Astion_Leo@163.com
 */


@RestController
@EnableAutoConfiguration
public class SkuSearchController {

    @Resource(name = "esService")
    private IEsService esService;

    @Resource(name = "hBaseService")
    private IHBaseService hBaseService;


    @RequestMapping("/search")
    ModelAndView search() {
        return new ModelAndView("search");
    }

    @RequestMapping("/list")
    List<JDSkuSearchInfo> list(@RequestParam Map<String, Object> params) {

        String searchContent = params.get("searchContent").toString();
        String sort = StringUtils.isNotBlank(params.get("sort").toString()) ? params.get("sort").toString() : null;

        List<JDSkuSearchInfo> jdSkuSearchInfos = esService.esSearch(searchContent, sort, 0, 100);
        return jdSkuSearchInfos;
    }

    @RequestMapping("/detail")
    ModelAndView detail(String id) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        return new ModelAndView("detail", params);
    }

    @RequestMapping("/getById")
    JDSkuDetailInfo getById(String id) {
        JDSkuDetailInfo detail = hBaseService.getById(id);
        detail.setCouponList(converter(detail.getCoupon()));
        detail.setSalesList(converter(detail.getSales()));
        detail.setParamsList(converter(detail.getParams()));
        return detail;
    }

    @RequestMapping("/getPriceHistory")
    Map<String, Object> getPriceHistory(String id) {
        Map<String, Object> priceHistory = hBaseService.getPriceHistory(id);
        return priceHistory;
    }



    private List<Map<String, String>> converter(String json) {
        List<Map<String, String>> result = new ArrayList<>();

        Map<String, String> map = JSONObject.parseObject(json, Map.class);
        for (Map.Entry<String, String> entry : map.entrySet()){
            Map<String, String> temp = new HashMap<>();
            temp.put("name", entry.getKey());
            temp.put("content", entry.getValue());
            result.add(temp);
        }
        return result;
    }
}
