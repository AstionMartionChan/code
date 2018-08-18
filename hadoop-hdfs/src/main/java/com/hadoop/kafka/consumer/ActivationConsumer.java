package com.hadoop.kafka.consumer;

import com.hadoop.kafka.constants.KafkaConstant;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/8/18
 * Time: 17:41
 * Work contact: Astion_Leo@163.com
 */


public class ActivationConsumer {

    private ConsumerConnector consumer;

    private String topic;

    private Integer numThreads;

    private ExecutorService executorPool;


    public ActivationConsumer(Properties prop) {
        this.consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(prop));
        this.topic = prop.getProperty(KafkaConstant.TOPIC);
        // consumer消费数量与partition数量对应
        this.numThreads = Integer.parseInt(prop.getProperty(KafkaConstant.NUM_PARTITION));
    }

    public void consumer() {
        // 指定topic
        Map<String, Integer> topicMap = new HashMap<>();
        topicMap.put(topic, numThreads);

        // 指定数据的解码器
        StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
        StringDecoder valueDecoder = new StringDecoder(new VerifiableProperties());

        // 获取topic数据集
        Map<String, List<KafkaStream<String, String>>> messageStreams = consumer.createMessageStreams(topicMap, keyDecoder, valueDecoder);

        // 获取指定topic数据
        List<KafkaStream<String, String>> kafkaStreams = messageStreams.get(topic);

        // 创建连接池
        this.executorPool = Executors.newFixedThreadPool(this.numThreads);

        for (KafkaStream<String, String> stream : kafkaStreams){
            this.executorPool.submit(new ConsumerKafkaStreamProcesser(stream));
        }
    }

}
