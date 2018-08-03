package com.cfy.streaming;

import com.google.common.base.Optional;
import kafka.serializer.StringDecoder;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import scala.Tuple2;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/7/21
 * Time: 20:26
 * Work contact: Astion_Leo@163.com
 */


public class UpdateStateByKeyWordCount {

    public static void main(String[] args) {
        SparkConf conf = new SparkConf()
                .setMaster("local[2]")
                .setAppName("UpdateStateByKeyWordCount");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(5));
        jssc.checkpoint("hdfs://leochan:9000/sparkstreaming_updateStateByKey_checkpoint");

        Map<String, String> kafkaParams = new HashMap<>();
        kafkaParams.put("metadata.broker.list", "leochan1:9092,leochan2:9092,leochan3:9092");

        Set<String> topics = new HashSet<>();
        topics.add("sparkstreaming");

        JavaPairInputDStream<String, String> DStream = KafkaUtils.createDirectStream(jssc,
                String.class,
                String.class,
                StringDecoder.class,
                StringDecoder.class,
                kafkaParams,
                topics);

//        JavaReceiverInputDStream<String> DStream = jssc.socketTextStream("leochan", 44444);

        JavaPairDStream<String, Long> mappedDStream = DStream.flatMapToPair(new PairFlatMapFunction<Tuple2<String, String>, String, Long>() {
            @Override
            public Iterable<Tuple2<String, Long>> call(Tuple2<String, String> tuple2) throws Exception {
                List<Tuple2<String, Long>> result = new ArrayList<>();

                for (String word : tuple2._2.split(" ")) {
                    result.add(new Tuple2<>(word, 1L));
                }
                return result;
            }
        });

        // 使用updateStateByKey对每个batch来的数据进行全局的计数统计
        JavaPairDStream<String, Long> wordCountsDStream = mappedDStream.updateStateByKey(new Function2<List<Long>, Optional<Long>, Optional<Long>>() {

            // 第一个参数是当前这个batch里key对应的values，可能有多个所以是list
            // 第二个参数是key之前的数值，可能有可能没有，可以用optional.isPresent()判断
            @Override
            public Optional<Long> call(List<Long> pre, Optional<Long> optional) throws Exception {

                Long newValue = 0L;

                if (optional.isPresent()) {
                    newValue = optional.get();
                }

                for (Long value : pre) {
                    newValue += value;
                }

                return Optional.of(newValue);
            }
        });


        // foreachRDD 是output操作，相当于action操作，触发job。
        wordCountsDStream.foreachRDD(new Function<JavaPairRDD<String, Long>, Void>() {
            @Override
            public Void call(JavaPairRDD<String, Long> wordCountsRDD) throws Exception {

                // 使用foreachPartition ，然后在里面创建connection，针对每个partition创建connection
                // 这样能尽可能减少性能消耗
                wordCountsRDD.foreachPartition(new VoidFunction<Iterator<Tuple2<String, Long>>>() {
                    @Override
                    public void call(Iterator<Tuple2<String, Long>> tuple2Iterator) throws Exception {
                        // save db
                    }
                });

                return null;
            }
        });


        jssc.start();
        jssc.awaitTermination();
        jssc.close();
    }
}
