package com.hadoop.kafka.producer;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/6/5
 * Time: 15:23
 * Work contact: Astion_Leo@163.com
 */


public class MyProducer {

    private static final String TOPIC = "leochan_partition";

    public static void main(String[] args) throws IOException {
        Properties prop = new Properties();
        prop.load(MyProducer.class.getClassLoader().getResourceAsStream("producer.properties"));

        Producer producer = new Producer<String, String>(new ProducerConfig(prop));
        List<KeyedMessage<String, String>> list = new ArrayList<>();
        KeyedMessage<String, String> keyedMessage1 = new KeyedMessage<String, String>(TOPIC, "key1", "cfy");
        KeyedMessage<String, String> keyedMessage2 = new KeyedMessage<String, String>(TOPIC, "key1", "lym");
        KeyedMessage<String, String> keyedMessage3 = new KeyedMessage<String, String>(TOPIC, "key2", "chq");
        KeyedMessage<String, String> keyedMessage4 = new KeyedMessage<String, String>(TOPIC, "key3", "zem");
        list.add(keyedMessage1);
        list.add(keyedMessage2);
        list.add(keyedMessage3);
        list.add(keyedMessage4);

        producer.send(list);
        producer.close();
    }

}
