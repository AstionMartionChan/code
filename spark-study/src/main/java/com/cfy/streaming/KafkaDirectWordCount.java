package com.cfy.streaming;

import kafka.serializer.StringDecoder;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import scala.Tuple2;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/7/21
 * Time: 20:02
 * Work contact: Astion_Leo@163.com
 */


public class KafkaDirectWordCount {

    public static void main(String[] args) {
        // 获取spark streaming上下文对象
        SparkConf conf = new SparkConf()
                .setMaster("local[2]")
                .setAppName("kafkaDirectWordCount");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(5));

        // 设置kafka的broker list
        Map<String, String> kafkaParams = new HashMap<>();
        kafkaParams.put("metadata.broker.list", "leochan1:9092,leochan2:9092,leochan3:9092");

        // 设置kafka的topic
        Set<String> topics = new HashSet<>();
        topics.add("sparkstreaming");

        // 从kafka数据源中获取batch数据 -> DStream
        JavaPairInputDStream<String, String> Dstream = KafkaUtils.createDirectStream(jssc,
                String.class,
                String.class,
                StringDecoder.class,
                StringDecoder.class,
                kafkaParams,
                topics);

        JavaPairDStream<String, Long> mappedDstream = Dstream.flatMapToPair(new PairFlatMapFunction<Tuple2<String, String>, String, Long>() {
            @Override
            public Iterable<Tuple2<String, Long>> call(Tuple2<String, String> tuple) throws Exception {
                List<Tuple2<String, Long>> result = new ArrayList<>();
                String[] splited = tuple._2.split(" ");
                for (String word : splited) {
                    result.add(new Tuple2<>(word, 1L));
                }
                return result;
            }
        });

        JavaPairDStream<String, Long> reducedDstream = mappedDstream.reduceByKey(new Function2<Long, Long, Long>() {
            @Override
            public Long call(Long v1, Long v2) throws Exception {
                return v1 + v2;
            }
        });

        reducedDstream.print();

        // 默认参数
        jssc.start();
        jssc.awaitTermination();
        jssc.close();
    }
}
