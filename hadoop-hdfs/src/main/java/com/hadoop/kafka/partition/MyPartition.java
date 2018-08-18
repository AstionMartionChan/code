package com.hadoop.kafka.partition;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/6/5
 * Time: 15:44
 * Work contact: Astion_Leo@163.com
 */


public class MyPartition implements Partitioner {

    public MyPartition(VerifiableProperties verifiableProperties) {

    }

    @Override
    public int partition(Object key, int numPartitions) {
        String str = key.toString();
        if (str.contains("0")){
            return 0;
        } else if (str.contains("1")){
            return 1;
        } else if (str.contains("2")){
            return 2;
        }
        return 0;
    }
}
