package com.kingnetdc.blueberry.kafka;


import com.kingnetdc.blueberry.kafka.base.Constants;


import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 * kdc kafka producer pool
 *
 * @author jake.zhang <zhangxj@kingnet.com>
 */
public class KdcProducerPool<K, V> {

    private List<KdcProducer<K, V>> producerList = new ArrayList<>();

    private int poolNum;

    private static final Random RANDOM = new Random();

    public KdcProducerPool(Properties properties) {
        this(properties, Constants.DEFAULT_KAFKA_PRODUCER_POOL_NUM);
    }

    public KdcProducerPool(Properties properties, int poolNum) {
        this.poolNum = poolNum;
        for (int i = 0; i < poolNum; i ++) {
            producerList.add(i, new KdcProducer<K, V>(properties));
        }
    }

    public KdcProducer<K, V> getProducer() {
        return producerList.get(RANDOM.nextInt(10000) % poolNum);
    }

    public void close() {
        producerList.forEach(item -> item.close());
    }
}
