package com.hadoop.kafka.consumer;

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

    public ConsumerKafkaStreamProcesser(KafkaStream<String, String> stream) {
        this.stream = stream;
    }

    @Override
    public void run() {
        ConsumerIterator<String, String> iterator = this.stream.iterator();

        while (iterator.hasNext()){

            MessageAndMetadata<String, String> value = iterator.next();
            System.out.println("partition: " + value.partition() + " offset: " + value.offset() + " value: " + value.message());
        }
    }
}
