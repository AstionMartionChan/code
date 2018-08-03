package com.cfy.spark.ad;

import com.cfy.utils.DateUtils;
import kafka.serializer.StringDecoder;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Duration;
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
 * Date: 2018/7/12
 * Time: 17:38
 * Work contact: Astion_Leo@163.com
 */


public class AdRealTimeStatSpark {

    public static void main(String[] args) {
        // 设置spark conf
        SparkConf conf = new SparkConf()
                .setMaster("local")
                .setAppName("adRealTimeStatJob");
        // 设置sparkStreaming 上下文
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(5));

        // 设置kafka集群broker位置 也就是zookeeper的位置
        Map<String, String> kafkaConf = new HashMap<>();
        kafkaConf.put("kafka.metadata.broker.list", "leochan1:9092,leochan2:9092,leochan3:9092");

        // 设置kafka topic
        Set<String> topicConf = new HashSet<>();
        topicConf.add("spark");

        // 获取从kafka拿出的Dstream
        JavaPairInputDStream<String, String> DStream = KafkaUtils.createDirectStream(
                jssc,
                String.class,
                String.class,
                StringDecoder.class,
                StringDecoder.class,
                kafkaConf,
                topicConf);

        // 将Batch（5秒内的批数据）映射成<date_userId_adId, 1L>形式
        JavaPairDStream<String, Long> mappedRDD = DStream.mapToPair(new PairFunction<Tuple2<String, String>, String, Long>() {
            @Override
            public Tuple2<String, Long> call(Tuple2<String, String> tuple) throws Exception {
                String log = tuple._2;
                String[] logSplited = log.split(" ");
                String timestamp = logSplited[0];
                Date date = new Date(Long.valueOf(timestamp));

                String dateString = DateUtils.formatDate(date);
                String userId = logSplited[3];
                String adId = logSplited[4];
                String result = dateString + "_" + userId + "_" + adId;


                return new Tuple2<String, Long>(result, 1L);
            }
        });

        // 将Batch批数据聚合，得出当天每个用户点击每个广告的次数
        JavaPairDStream<String, Long> reducedRDD = mappedRDD.reduceByKey(new Function2<Long, Long, Long>() {
            @Override
            public Long call(Long v1, Long v2) throws Exception {
                return v1 + v2;
            }
        });


        reducedRDD.foreachRDD(new Function<JavaPairRDD<String, Long>, Void>() {
            @Override
            public Void call(JavaPairRDD<String, Long> rdd) throws Exception {

                rdd.foreachPartition(new VoidFunction<Iterator<Tuple2<String, Long>>>() {
                    @Override
                    public void call(Iterator<Tuple2<String, Long>> iterator) throws Exception {
                        while (iterator.hasNext()){
                            Tuple2<String, Long> userAdCount = iterator.next();

                        }
                    }
                });

                return null;
            }
        });



        // 构建完spark streaming上下文之后，记得要进行上下文的启动、等待执行结束、关闭
        jssc.start();
        jssc.awaitTermination();
        jssc.close();
    }
}
