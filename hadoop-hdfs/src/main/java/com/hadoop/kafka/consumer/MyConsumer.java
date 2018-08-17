package com.hadoop.kafka.consumer;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

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

    private static final String TOPIC = "uv";

    public static void main(String[] args) throws IOException {
        Properties prop = new Properties();
        prop.load(MyConsumer.class.getClassLoader().getResourceAsStream("consumer.properties"));
        ConsumerConnector consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(prop));

        Map<String, Integer> topicMap = new HashMap<>();
        topicMap.put(TOPIC, 2);
        Map<String, List<KafkaStream<byte[], byte[]>>> messageStreams = consumer.createMessageStreams(topicMap);

        List<KafkaStream<byte[], byte[]>> kafkaStreams = messageStreams.get(TOPIC);

        for (KafkaStream<byte[], byte[]> kafkaStream : kafkaStreams){
            new Thread(new Worker(kafkaStream)).start();
        }

    }

    static class Worker implements Runnable {

        private KafkaStream<byte[], byte[]> kafkaStream;

        Worker (KafkaStream<byte[], byte[]> kafkaStream){
            this.kafkaStream = kafkaStream;
        }

        @Override
        public void run() {
            ConsumerIterator<byte[], byte[]> iterator = kafkaStream.iterator();
            while (iterator.hasNext()){
                MessageAndMetadata<byte[], byte[]> next = iterator.next();
                /*System.out.println("key: " + next.key()==null?"":new String(next.key()) + " " +
                        "msg: " + next.message()==null?"":new String(next.message()) + " " +
                        "partition: " + next.partition() + " " +
                        "offset: " + next.offset());*/
                System.out.println("----------------------------");
            }
        }
    }
}
