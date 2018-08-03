package com.itcast;

import com.itcast.bean.JDSkuInfo;
import com.itcast.utils.EsUtil;
import com.itcast.utils.HBaseUtil;
import com.itcast.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/6/25
 * Time: 11:16
 * Work contact: Astion_Leo@163.com
 */


public class Application {

    private static final String INDEX = "spider";
    private static final String DOCUMENT = "jd_sku_info";
    private static Logger LOGGER = LoggerFactory.getLogger(Application.class);


    public static void main(String[] args) {

//        while (true){
//            String rowkey = RedisUtil.rpop("rowkey_es_index");
//            if (null != rowkey){
//                JDSkuInfo jdSkuInfo = HBaseUtil.get("t_jd_spider_sku_info", rowkey, JDSkuInfo.class);
//                jdSkuInfo.setTime(new Date());
//
//                EsUtil.addIndex(INDEX, DOCUMENT, rowkey, jdSkuInfo);
//                LOGGER.info("成功向ES插入一条索引数据 ...");
//            } else {
//                try {
//                    Thread.sleep(5000);
//                    LOGGER.info("没有rowkey了 休息一会 ...");
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }




    }
}
