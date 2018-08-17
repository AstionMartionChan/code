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
        if (str.contains("1")){
            System.out.println("1");
            return 0;
        } else if (str.contains("2")){
            System.out.println("2");
            return 1;
        } else {
            System.out.println("3");
            return 2;
        }
    }
}
