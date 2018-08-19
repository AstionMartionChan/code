package com.hadoop.kafka.consumer;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/8/19
 * Time: 10:56
 * Work contact: Astion_Leo@163.com
 */


public class ActivationMessageParse implements KafkaMessageParseable {
    @Override
    public JSONObject parse(String json) {

        JSONObject jsonObject = JSONObject.parseObject(json);
        boolean flag = jsonObject.containsKey("properties");

        if (flag){
            JSONObject resultJSONObject = new JSONObject();
            JSONObject properties = jsonObject.getJSONObject("properties");
            String tokenId = properties.getString("tokenId");
            String deviceId = properties.getString("deviceId");
            resultJSONObject.put("tokenId", tokenId);
            resultJSONObject.put("deviceId", deviceId);
            return resultJSONObject;
        }

        return null;
    }
}
