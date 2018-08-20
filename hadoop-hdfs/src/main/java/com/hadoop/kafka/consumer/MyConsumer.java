package com.hadoop.kafka.consumer;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;

import java.io.IOException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/6/5
 * Time: 16:08
 * Work contact: Astion_Leo@163.com
 */


public class MyConsumer {

    private static final String TOPIC = "activation";

    public static void main(String[] args) throws IOException {
        Properties prop = new Properties();
        prop.load(MyConsumer.class.getClassLoader().getResourceAsStream("consumer.properties"));
        ConsumerConnector consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(prop));

        Map<String, Integer> topicMap = new HashMap<>();
        topicMap.put(TOPIC, 2);
        StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
        StringDecoder valueDecoder = new StringDecoder(new VerifiableProperties());
        Map<String, List<KafkaStream<String, String>>> messageStreams = consumer.createMessageStreams(topicMap, keyDecoder, valueDecoder);

        List<KafkaStream<String, String>> kafkaStreams = messageStreams.get(TOPIC);

        for (KafkaStream<String, String> kafkaStream : kafkaStreams){
            new Thread(new Worker(kafkaStream)).start();
        }

    }

    static class Worker implements Runnable {

        private KafkaStream<String, String> kafkaStream;

        Worker (KafkaStream<String, String> kafkaStream){
            this.kafkaStream = kafkaStream;
        }

        @Override
        public void run() {
            ConsumerIterator<String, String> iterator = kafkaStream.iterator();
            while (iterator.hasNext()){
                MessageAndMetadata<String, String> next = iterator.next();
                System.out.println("key: " + next.key()==null?"":new String(next.key()) + " " +
                        "msg: " + next.message()==null?"":new String(next.message()) + " " +
                        "partition: " + next.partition() + " " +
                        "offset: " + next.offset());
                System.out.println("----------------------------");
            }
        }
    }
}
