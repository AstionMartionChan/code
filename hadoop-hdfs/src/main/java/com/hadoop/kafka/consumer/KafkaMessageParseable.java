package com.hadoop.kafka.consumer;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/8/19
 * Time: 10:42
 * Work contact: Astion_Leo@163.com
 */


public interface KafkaMessageParseable {

    JSONObject parse(String json);
}
