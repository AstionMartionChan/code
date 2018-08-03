package com.itcast.service.impl;

import com.itcast.bean.JDSkuDetailInfo;
import com.itcast.service.IHBaseService;
import com.itcast.utils.HBaseUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/6/27
 * Time: 14:14
 * Work contact: Astion_Leo@163.com
 */


@Service("hBaseService")
public class HBaseServiceImpl implements IHBaseService {



    @Override
    public JDSkuDetailInfo getById(String id) {
        JDSkuDetailInfo detail = HBaseUtil.get("t_jd_spider_sku_info", id, JDSkuDetailInfo.class);
        return detail;
    }

    @Override
    public Map<String, Object> getPriceHistory(String id) {
        List<String> dateList = new ArrayList<>();
        for (int x=9; x >= 0; x--){
            dateList.add(getPastDate(x));
        }

        List<Map<String, Object>> opList = HBaseUtil.getVersion("t_jd_spider_sku_info", id, "op_price", 10);
        List<Map<String, Object>> pList = HBaseUtil.getVersion("t_jd_spider_sku_info", id, "p_price", 10);

        List<String> opResultList = getInitList(10);
        List<String> pResultList = getInitList(10);


        for (Map<String, Object> map : opList){
            String time = dateConverter(Long.valueOf(map.get("timestamp").toString()));
            if (dateList.contains(time)){
                int i = dateList.indexOf(time);
                opResultList.remove(i);
                opResultList.add(i, map.get("value").toString());
            }
        }

        for (Map<String, Object> map : pList){
            String time = dateConverter(Long.valueOf(map.get("timestamp").toString()));
            if (dateList.contains(time)){
                int i = dateList.indexOf(time);
                pResultList.remove(i);
                pResultList.add(i, map.get("value").toString());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("opList", opResultList);
        result.put("pList", pResultList);
        result.put("dateList", dateList);


        return result;
    }


    private String getPastDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("MM-dd");
        String result = format.format(today);
        return result;
    }

    private String dateConverter(Long timestamp) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd");
        Date date = new Date();
        date.setTime(timestamp);
        String result = format.format(timestamp);
        return result;
    }

    private List<String> getInitList(Integer size) {
        List<String> list = new ArrayList<>(size);
        for (int x=0; x<size; x++){
            list.add("0");
        }
        return list;
    }
}
