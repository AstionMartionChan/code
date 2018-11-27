package com.kingnetdc.blueberry.kafka;


import com.kingnetdc.blueberry.kafka.base.Constants;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.PartitionInfo;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author jake.zhang <zhangxj@kingnet.com>
 */
public class KdcProducer<K, V> {

    private KafkaProducer<K, V> kafkaProducer;

    public KdcProducer(Properties properties) {
        Properties props = new Properties();
        props.put("bootstrap.servers", properties.get("bootstrap.servers"));
        props.put("key.serializer", properties.getOrDefault("key.serializer", Constants.KAFKA_KEY_SERIALIZER));
        props.put("value.serializer", properties.getOrDefault("value.serializer", Constants.KAFKA_VALUE_SERIALIZER));
        props.put("acks", properties.getOrDefault("acks", Constants.KAFKA_ACKS));
        props.put("buffer.memory", properties.getOrDefault("buffer.memory", Constants.KAFKA_BUFFER_MEMORY));
        props.put("compression.type", properties.getOrDefault("compression.type", Constants.KAFKA_COMPRESSION_TYPE));
        props.put("retries", properties.getOrDefault("retries", Constants.KAFKA_RETRIES));
        props.put("batch.size", properties.getOrDefault("batch.size", Constants.KAFKA_BATCH_SIZE));
        props.put("linger.ms", properties.getOrDefault("linger.ms", Constants.KAFKA_LINGER_MS));
        props.put("max.block.ms", properties.getOrDefault("max.block.ms", Constants.KAFKA_MAX_BLOCK_MS));
        props.put("max.request.size", properties.getOrDefault("max.request.size", Constants.KAFKA_MAX_REQUEST_SIZE));
        props.put("partitioner.class", properties.getOrDefault("partitioner.class", Constants.KAFKA_PARTITIONER_CLASS));
        props.put("request.timeout.ms", properties.getOrDefault("request.timeout.ms", Constants.KAFKA_REQUEST_TIMEOUT_MS));
        props.put("retry.backoff.ms", properties.getOrDefault("retry.backoff.ms", Constants.KAFKA_RETRY_BACKOFF_MS));
        kafkaProducer = new KafkaProducer<K, V>(props);
    }

    public Future<RecordMetadata> send(ProducerRecord<K, V> record) {
        return kafkaProducer.send(record);
    }

    public Future<RecordMetadata> send(ProducerRecord<K, V> record, Callback callback) {
        return kafkaProducer.send(record, callback);
    }

    public KafkaProducer getKafkaProducer() {
        return kafkaProducer;
    }

    public void flush() {
        kafkaProducer.flush();
    }

    public List<PartitionInfo> partitionsFor(String topic) {
        return kafkaProducer.partitionsFor(topic);
    }

    public void close() {
        kafkaProducer.close();
    }

    public void close(long timeout, TimeUnit timeUnit) {
        kafkaProducer.close(timeout, timeUnit);
    }
}
