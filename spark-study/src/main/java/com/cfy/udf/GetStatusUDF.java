package com.cfy.udf;

import com.alibaba.fastjson.JSONObject;
import org.apache.spark.sql.api.java.UDF1;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/7/11
 * Time: 22:09
 * Work contact: Astion_Leo@163.com
 */


public class GetStatusUDF implements UDF1<String, String> {
    @Override
    public String call(String s) throws Exception {
        JSONObject jsonObject = JSONObject.parseObject(s);
        return jsonObject.getString("product_status");
    }
}
