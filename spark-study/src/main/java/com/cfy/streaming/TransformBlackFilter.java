package com.cfy.streaming;

import com.google.common.base.Optional;
import kafka.serializer.StringDecoder;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import scala.Tuple2;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/7/22
 * Time: 16:41
 * Work contact: Astion_Leo@163.com
 */


public class TransformBlackFilter {

    public static void main(String[] args) {
        // 创建sparkContext上下文
        SparkConf conf = new SparkConf()
                .setMaster("local[2]")
                .setAppName("TransformBlackFilter");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(5));

        // 手动生成黑名单RDD
        List<Tuple2<String, String>> blackList = new ArrayList<>();
        blackList.add(new Tuple2<String, String>("alice", "1"));
        JavaPairRDD<String, String> blackListRDD = jssc.sc().parallelizePairs(blackList);

        // 使用kafka direct方式从kafka里获取batch数据
        Map<String, String> kafkaParams = new HashMap<>();
        kafkaParams.put("metadata.broker.list", "leochan1:9092,leochan2:9092,leochan3:9092");
        Set<String> topics = new HashSet<>();
        topics.add("sparkstreaming");
        JavaPairInputDStream<String, String> DStreamRDD = KafkaUtils.createDirectStream(jssc,
                String.class,
                String.class,
                StringDecoder.class,
                StringDecoder.class,
                kafkaParams,
                topics);

        // 将数据映射成 name, hello name 格式
        JavaPairDStream<String, String> mappedDstreamRDD = DStreamRDD.mapToPair(new PairFunction<Tuple2<String, String>, String, String>() {
            @Override
            public Tuple2<String, String> call(Tuple2<String, String> tuple) throws Exception {
                return new Tuple2<String, String>(tuple._2.split(" ")[1], tuple._2);
            }
        });

        // 使用transform将DStream转换成普通的JavaRDD形式
        JavaDStream<String> transformedRDD = mappedDstreamRDD.transform(new Function<JavaPairRDD<String, String>, JavaRDD<String>>() {
            @Override
            public JavaRDD<String> call(JavaPairRDD<String, String> mappedRDD) throws Exception {

                // 将转换后普通的RDD与黑名单RDD进行Join
                JavaPairRDD<String, Tuple2<String, Optional<String>>> joinedRDD = mappedRDD.leftOuterJoin(blackListRDD);

                // 过滤将连接上黑名单RDD的数据剔除
                JavaPairRDD<String, Tuple2<String, Optional<String>>> filteredRDD = joinedRDD.filter(new Function<Tuple2<String, Tuple2<String, Optional<String>>>, Boolean>() {
                    @Override
                    public Boolean call(Tuple2<String, Tuple2<String, Optional<String>>> tuple) throws Exception {
                        if (tuple._2._2.isPresent() && tuple._2._2.get().equals("1")) {
                            return false;
                        }
                        return true;
                    }
                });

                // 将过滤后的数据重新映射并返回
                return filteredRDD.map(new Function<Tuple2<String, Tuple2<String, Optional<String>>>, String>() {
                    @Override
                    public String call(Tuple2<String, Tuple2<String, Optional<String>>> tuple) throws Exception {
                        return tuple._2._1;
                    }
                });

            }
        });

        transformedRDD.print();

        jssc.start();
        jssc.awaitTermination();
        jssc.close();

    }

}
