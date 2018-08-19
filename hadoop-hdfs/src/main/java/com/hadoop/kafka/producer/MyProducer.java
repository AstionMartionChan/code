package com.hadoop.kafka.producer;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/6/5
 * Time: 15:23
 * Work contact: Astion_Leo@163.com
 */


public class MyProducer {

    private static final String TOPIC = "activation";

    public static void main(String[] args) throws IOException {
        Properties prop = new Properties();
        prop.load(MyProducer.class.getClassLoader().getResourceAsStream("producer.properties"));

        Producer producer = new Producer<String, String>(new ProducerConfig(prop));
        List<KeyedMessage<String, String>> list = new ArrayList<>();

        Random random = new Random();
        Integer num = 0;

        while (true){
            KeyedMessage<String, String> keyedMessage1 = new KeyedMessage<String, String>(TOPIC, "{\"name\":\"cfy\",\"age\":25,\"properties\":{\"tokenId\":\"ji29012jidsal9f\",\"deviceId\":\"qwiods9jnvmb\"}}");
            KeyedMessage<String, String> keyedMessage2 = new KeyedMessage<String, String>(TOPIC, "{\"name\":\"lym\",\"age\":26,\"properties\":{\"tokenId\":\"yuwqnjodis9jio\",\"deviceId\":\"09jl0sdi9sdas\"}}");
            list.add(keyedMessage1);
            list.add(keyedMessage2);
            producer.send(list);
            num++;

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

}
