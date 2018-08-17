package com.cfy.wordcount;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/8/5
 * Time: 21:03
 * Work contact: Astion_Leo@163.com
 */


public class WordCountTest {

    public static void main(String[] args) {

        SparkConf sparkConf = new SparkConf()
                .setAppName("wordCountTest")
                .setMaster("yarn-client");

        JavaSparkContext sc = new JavaSparkContext(sparkConf);
        JavaRDD<String> javaRDD = sc.textFile("hdfs://leochan:9000/source/spark/wordcount.txt");
        JavaPairRDD<String, Long> flatMapedRDD = javaRDD.flatMapToPair(new PairFlatMapFunction<String, String, Long>() {
            @Override
            public Iterable<Tuple2<String, Long>> call(String line) throws Exception {

                List<Tuple2<String, Long>> result = new ArrayList<>();
                String[] splited = line.split("\t");
                result.add(new Tuple2<String, Long>(splited[0], 1L));
                result.add(new Tuple2<String, Long>(splited[1], 1L));

                return result;
            }
        });

        JavaPairRDD<String, Long> reducedRDD = flatMapedRDD.reduceByKey(new Function2<Long, Long, Long>() {
            @Override
            public Long call(Long v1, Long v2) throws Exception {
                return v1 + v2;
            }
        });

        reducedRDD.saveAsTextFile("hdfs:/leochan:9000/out/spark/wordcountResult");

        sc.close();
    }
}
