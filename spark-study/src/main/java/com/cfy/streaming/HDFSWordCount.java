package com.cfy.streaming;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/7/21
 * Time: 17:46
 * Work contact: Astion_Leo@163.com
 */


public class HDFSWordCount {

    public static void main(String[] args) {

        // 设置spark配置
        SparkConf conf = new SparkConf()
                .setMaster("local[2]")
                .setAppName("HDFSWordCount");
        // 设置spark streaming上下文，并配置多少时间获取一个batch
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(5));

        // 从hdfs目录下获取数据
        JavaDStream<String> lines = jssc.textFileStream("hdfs://leochan:9000/spark_streaming_wordcount_dir");


        JavaPairDStream<String, Integer> mapped = lines.flatMapToPair(new PairFlatMapFunction<String, String, Integer>() {
            @Override
            public Iterable<Tuple2<String, Integer>> call(String line) throws Exception {
                List<Tuple2<String, Integer>> resultList = new ArrayList<Tuple2<String, Integer>>();
                String[] splited = line.split(" ");

                for (String word : splited) {
                    resultList.add(new Tuple2(word, 1));
                }
                return resultList;
            }
        });

        JavaPairDStream<String, Integer> reduced = mapped.reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer v1, Integer v2) throws Exception {
                return v1 + v2;
            }
        });
        reduced.print();

        // 默认一定要设置的开始，等待接受，还有关闭方法
        jssc.start();
        jssc.awaitTermination();
        jssc.close();

    }
}
