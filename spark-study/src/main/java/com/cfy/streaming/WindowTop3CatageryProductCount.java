package com.cfy.streaming;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.hive.HiveContext;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/7/23
 * Time: 11:36
 * Work contact: Astion_Leo@163.com
 */


public class WindowTop3CatageryProductCount {

    public static void main(String[] args) {
        SparkConf conf = new SparkConf()
                .setMaster("local[2]")
                .setAppName("WindowTop3CatageryProductCount");

        // 每间隔1秒从数据源获取数据
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(1));
        JavaReceiverInputDStream<String> DStream = jssc.socketTextStream("leochan", 9999);

        // 将数据映射成<categery_product 1>格式
        JavaPairDStream<String, Integer> mappedDStream = DStream.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String str) throws Exception {

                String[] splited = str.split(" ");
                String catageryId = splited[1];
                String productId = splited[2];

                return new Tuple2<String, Integer>(catageryId + "_" + productId, 1);
            }
        });

        // 使用滑动窗口对数据进行统计 窗口长度60秒 窗口间隔10秒
        JavaPairDStream<String, Integer> reducedDStream = mappedDStream.reduceByKeyAndWindow(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer v1, Integer v2) throws Exception {
                return v1 + v2;
            }
        }, Durations.seconds(60), Durations.seconds(10));

        // 遍历统计好的数据
        reducedDStream.foreachRDD(new Function<JavaPairRDD<String, Integer>, Void>() {
            @Override
            public Void call(JavaPairRDD<String, Integer> catageryProductCountRDD) throws Exception {

                // 将数据映射成<Row>的格式，方便后面进行DataFrame的转换
                JavaRDD<Row> catageryProductCountRowRDD = catageryProductCountRDD.map(new Function<Tuple2<String, Integer>, Row>() {
                    @Override
                    public Row call(Tuple2<String, Integer> tuple) throws Exception {

                        String[] splited = tuple._1.split("_");
                        String catageryId = splited[1];
                        String productId = splited[2];
                        Integer click_count = tuple._2;

                        return new RowFactory().create(catageryId, productId, click_count);
                    }
                });

                // 定义DataFrame的字段类型
                StructType structType = DataTypes.createStructType(Arrays.asList(
                        DataTypes.createStructField("catageryId", DataTypes.StringType, true),
                        DataTypes.createStructField("productId", DataTypes.StringType, true),
                        DataTypes.createStructField("click_count", DataTypes.IntegerType, true)
                ));

                // 转换成DataFrame,并注册成为临时表，方便后面使用spark sql
                SQLContext hiveContext = new HiveContext(catageryProductCountRowRDD.context());
                DataFrame dataFrame = hiveContext.createDataFrame(catageryProductCountRowRDD, structType);
                dataFrame.registerTempTable("tmp_categery_product_count");

                // 使用spark sql开窗函数统计每个分组下点击次数排名前三的商品
                DataFrame resultDF = hiveContext.sql("" +
                        "SELECT " +
                            "tb1.catageryId, " +
                            "tb1.productId, " +
                            "tb1.click_count " +
                        "FROM (" +
                                "SELECT " +
                                    "catageryId, " +
                                    "productId, " +
                                    "click_count, " +
                                    "row_number() OVER (PARTITION BY catageryId ORDER BY click_count DESC) rank " +
                                "FROM " +
                                    "tmp_categery_product_count" +
                        ") tb1 " +
                        "WHERE " +
                            "tb1.rank <= 3");

                resultDF.show();

                return null;
            }
        });
    }

}
