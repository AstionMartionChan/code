package com.itcast.handler.impl;

import com.alibaba.fastjson.JSONObject;
import com.itcast.handler.StoreHandler;
import com.itcast.po.JdSkuInfo;
import com.itcast.po.JdSkuNewInfo;
import com.itcast.po.Page;
import com.itcast.utils.HBaseUtil;
import com.itcast.utils.MD5Utils;
import com.itcast.utils.RedisUtil;
import org.apache.hadoop.hbase.client.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/6/15
 * Time: 14:29
 * Work contact: Astion_Leo@163.com
 */


public class HbaseStoreHandlerImpl implements StoreHandler {

    private static final String HBASE_TABLE_NAME = "t_jd_spider_sku_info";
    private static final String HBASE_CONLUME_FAMILY = "c1";
    private static final String HBASE_CONLUME_TITLE = "title";
    private static final String HBASE_CONLUME_OP_PRICE = "op_price";
    private static final String HBASE_CONLUME_P_PRICE = "p_price";
    private static final String HBASE_CONLUME_COUPON = "coupon";
    private static final String HBASE_CONLUME_SALES = "sales";
    private static final String HBASE_CONLUME_PARAMS = "params";
    private static final String HBASE_CONLUME_URL = "url";
    private static final String HBASE_CONLUME_IMAGE_URL = "img_url";
    private static final String HBASE_CONLUME_SKU_ID = "sku_id";
    private static final Logger LOGGER = LoggerFactory.getLogger(HbaseStoreHandlerImpl.class);


    @Override
    public void saveDB(Page page) throws SQLException, IOException, NoSuchAlgorithmException {

        JdSkuNewInfo jdSkuInfo = (JdSkuNewInfo) page.getJdSkuInfo();
        // rowkey 进行哈希散列
        String rowkey = MD5Utils.md5(jdSkuInfo.getSkuId()).substring(0, 8);
        List<Put> putList = new ArrayList<>();

        LOGGER.info("------ {}", JSONObject.toJSONString(jdSkuInfo));
        if (null != jdSkuInfo.getTitle() && null != jdSkuInfo.getOpPrice()){
            putList.add(HBaseUtil.put(rowkey, HBASE_CONLUME_FAMILY, HBASE_CONLUME_TITLE, jdSkuInfo.getTitle()));
            putList.add(HBaseUtil.put(rowkey, HBASE_CONLUME_FAMILY, HBASE_CONLUME_OP_PRICE, jdSkuInfo.getOpPrice()));
            putList.add(HBaseUtil.put(rowkey, HBASE_CONLUME_FAMILY, HBASE_CONLUME_P_PRICE, jdSkuInfo.getpPrice()));
            putList.add(HBaseUtil.put(rowkey, HBASE_CONLUME_FAMILY, HBASE_CONLUME_URL, jdSkuInfo.getUrl()));
            putList.add(HBaseUtil.put(rowkey, HBASE_CONLUME_FAMILY, HBASE_CONLUME_COUPON, jdSkuInfo.getCoupon()));
            putList.add(HBaseUtil.put(rowkey, HBASE_CONLUME_FAMILY, HBASE_CONLUME_SALES, jdSkuInfo.getSales()));
            putList.add(HBaseUtil.put(rowkey, HBASE_CONLUME_FAMILY, HBASE_CONLUME_PARAMS, jdSkuInfo.getParam()));
            putList.add(HBaseUtil.put(rowkey, HBASE_CONLUME_FAMILY, HBASE_CONLUME_IMAGE_URL, jdSkuInfo.getImgUrl()));
            putList.add(HBaseUtil.put(rowkey, HBASE_CONLUME_FAMILY, HBASE_CONLUME_SKU_ID, jdSkuInfo.getSkuId()));
            // 向hbase 插入一行数据
            HBaseUtil.put2HBase(HBASE_TABLE_NAME, putList);
            LOGGER.info("向Hbase插入一行数据");

            // 向redis 插入rowkey 建立es索引
            RedisUtil.lpush("rowkey_es_index", rowkey);
        }

    }
}
