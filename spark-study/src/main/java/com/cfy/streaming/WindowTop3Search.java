package com.cfy.streaming;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/7/22
 * Time: 21:02
 * Work contact: Astion_Leo@163.com
 */


public class WindowTop3Search {

    public static void main(String[] args) {
        // 创建sparkstreaming上下文
        SparkConf conf = new SparkConf()
                .setMaster("local[2]")
                .setAppName("WindowTop3Search");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(5));

        // 获取输入DStream
        JavaReceiverInputDStream<String> inputDStream = jssc.socketTextStream("leochan", 9999);

        // 将输入数据映射成 <word 1>
        JavaPairDStream<String, Integer> mappedDStream = inputDStream.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String str) throws Exception {
                return new Tuple2<String, Integer>(str.split(" ")[1], 1);
            }
        });

        // 使用滑动窗口统计，每隔十秒统计前六十秒所有的数据
        JavaPairDStream<String, Integer> reducedDStream = mappedDStream.reduceByKeyAndWindow(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer v1, Integer v2) throws Exception {
                return v1 + v2;
            }
        }, Durations.seconds(60), Durations.seconds(10));

        // 使用transform操作将DStream转换成RDD
        JavaPairDStream<String, Integer> finalDStream = reducedDStream.transformToPair(new Function<JavaPairRDD<String, Integer>, JavaPairRDD<String, Integer>>() {
            @Override
            public JavaPairRDD<String, Integer> call(JavaPairRDD<String, Integer> reducedRDD) throws Exception {

                // 将统计后的数据反转映射，然后按照key倒序排序，然后再次映射，
                JavaPairRDD<String, Integer> sortedRDD = reducedRDD.mapToPair(new PairFunction<Tuple2<String, Integer>, Integer, String>() {
                    @Override
                    public Tuple2<Integer, String> call(Tuple2<String, Integer> tuple) throws Exception {
                        return new Tuple2<Integer, String>(tuple._2, tuple._1);
                    }
                })
                .sortByKey(false)
                .mapToPair(new PairFunction<Tuple2<Integer, String>, String, Integer>() {
                    @Override
                    public Tuple2<String, Integer> call(Tuple2<Integer, String> tuple) throws Exception {
                        return new Tuple2<String, Integer>(tuple._2, tuple._1);
                    }
                });

                // 使用take取top3
                List<Tuple2<String, Integer>> top3Data = sortedRDD.take(3);

                for (Tuple2<String, Integer> tuple : top3Data) {
                    System.out.println(tuple._1 + ": " + tuple._2);
                }

                return sortedRDD;
            }
        });


        finalDStream.print();

        jssc.start();
        jssc.awaitTermination();
        jssc.close();

    }

}
