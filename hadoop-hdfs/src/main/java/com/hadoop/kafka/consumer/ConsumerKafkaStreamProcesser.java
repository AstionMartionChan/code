package com.hadoop.kafka.consumer;

import com.alibaba.fastjson.JSONObject;
import com.hadoop.kafka.utils.HttpClientUtil;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/8/18
 * Time: 18:00
 * Work contact: Astion_Leo@163.com
 */


public class ConsumerKafkaStreamProcesser implements Runnable {

    private KafkaStream<String, String> stream;
    private Integer threadNum;
    private KafkaMessageParseable kafkaMessageParseable;

    public ConsumerKafkaStreamProcesser(KafkaStream<String, String> stream, KafkaMessageParseable kafkaMessageParseable, Integer threadNum) {
        this.stream = stream;
        this.threadNum = threadNum;
        this.kafkaMessageParseable = kafkaMessageParseable;
    }

    @Override
    public void run() {
        ConsumerIterator<String, String> iterator = this.stream.iterator();

        while (iterator.hasNext()){

            MessageAndMetadata<String, String> value = iterator.next();
            System.out.println("threadNum: " + this.threadNum + " partition: " + value.partition() + " offset: " + value.offset() + " value: " + value.message());
            JSONObject jsonObject = kafkaMessageParseable.parse(value.message());
            System.out.println(jsonObject.toJSONString());

            JSONObject resultJSONObject = HttpClientUtil.doPost("", jsonObject.toJSONString());


        }
    }
}
