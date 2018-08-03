package com.cfy.spark.product;

import com.alibaba.fastjson.JSONObject;
import com.cfy.bean.AreaTop3ProductEntity;
import com.cfy.constants.Constant;
import com.cfy.dao.IAreaTop3ProductDao;
import com.cfy.dao.factory.DaoFactory;
import com.cfy.udaf.GroupConcatUDAF;
import com.cfy.udf.ConcatUDF;
import com.cfy.udf.GetStatusUDF;
import com.cfy.utils.ConfigurationManager;
import com.cfy.utils.ParamUtils;
import com.cfy.utils.SparkUtil;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/7/11
 * Time: 18:06
 * Work contact: Astion_Leo@163.com
 */


public class AreaProductTop3AnalyzeSpark {

    public static void main(String[] args) {
        // 设置spark作业名称
        SparkConf conf = new SparkConf()
                .setAppName(Constant.SPARK_APP_NAME_PRODUCT);
        // 设置sparkMaster
        SparkUtil.setMaster(conf);
        // 获取Spark上下文对象
        JavaSparkContext sc = new JavaSparkContext(conf);
        // 获取SQLContext
        SQLContext sqlContext = SparkUtil.getSQLContext(sc.sc());
        // 注册自定义的UDF UDAF函数
        sqlContext.udf().register("concat", new ConcatUDF(), DataTypes.StringType);
        sqlContext.udf().register("getStatus", new GetStatusUDF(), DataTypes.StringType);
        sqlContext.udf().register("group_concat", new GroupConcatUDAF());

        // 生成模拟数据
        SparkUtil.mockData(sc, sqlContext);


        // 获取用户指定的查询参数和taskId
        Long taskId = ParamUtils.getTaskIdFromArgs(args);
        JSONObject params = SparkUtil.getParams(args);

        // 获取点击商品的原始数据并映射成<cityId, Row>
        JavaPairRDD<Long, Row> cityProductInfoRDD = getCityProductInfoRDD(sqlContext, params);

        // 查询Mysql将数据映射成<cityId, Row>
        JavaPairRDD<Long, Row> cityInfoRDD = getCityInfoRDD(sqlContext);

        // join商品点击表和城市表并生成临时表
        generatorAreaProductTempTable(sqlContext, cityProductInfoRDD, cityInfoRDD);

//        // 按照地区为粒度分组计算出每个地区下各个商品被点击的数量并生成临时表
        generatorAreaProductGroupTempTable(sqlContext);
//
//        // 使用开窗函数统计出各个地区分组里商品点击次数top3并生成临时表
        generatorTop3AreaProductClickCountTempTable(sqlContext);

        // 按照地区为粒度分组计算出每个地区下各个商品被点击的数量
        JavaPairRDD<String, Row> areaProductGroupRDD = getAreaProductGroupRDD(sqlContext);

        // 按照地区为粒度分组统计出每个地区下top3点击数量商品并生产临时表
        generatorTop3AreaProductClickCountTempTable(sc, sqlContext, areaProductGroupRDD);


        // join商品详情表关联商品状态信息，并根据地区划分地区等级
        JavaRDD<Row> resultRDD = getFullTop3AreaProductClickCountRDD(sqlContext);

        // 将数据保存进Mysql
        saveMysql(taskId, resultRDD);

    }


    private static JavaPairRDD<Long, Row> getCityProductInfoRDD(SQLContext sqlContext, JSONObject params) {
        String startDate = ParamUtils.getParam(params, Constant.PARAM_START_DATE);
        String endDate = ParamUtils.getParam(params, Constant.PARAM_END_DATE);
        String sql = "SELECT " +
                        "city_id," +
                        "click_product_id " +
                     "FROM " +
                        "user_visit_action " +
                     "WHERE " +
                        "click_product_id IS NOT NULL " +
//                     "AND " +
//                        "click_product_id != 'null' " +
//                     "AND " +
//                        "click_product_id != 'NULL' " +
                     "AND " +
                        "date >= '" + startDate + "'" +
                     "AND " +
                        "date <= '" + endDate + "'";
        DataFrame dataFrame = sqlContext.sql(sql);
        return dataFrame.javaRDD().mapToPair(new PairFunction<Row, Long, Row>() {
            @Override
            public Tuple2<Long, Row> call(Row row) throws Exception {
                return new Tuple2<Long, Row>(row.getLong(0), row);
            }
        });
    }


    private static JavaPairRDD<Long,Row> getCityInfoRDD(SQLContext sqlContext) {

        Map<String, String> options = new HashMap<>();
        options.put("url", ConfigurationManager.getString("jdbc_url"));
        options.put("user", ConfigurationManager.getString("jdbc_username"));
        options.put("password", ConfigurationManager.getString("jdbc_password"));
        options.put("dbtable", "city_info");

        DataFrame cityInfoDataFrame = sqlContext.read().format("jdbc").options(options).load();
        return cityInfoDataFrame.javaRDD().mapToPair(new PairFunction<Row, Long, Row>() {
            @Override
            public Tuple2<Long, Row> call(Row row) throws Exception {
                return new Tuple2<Long, Row>((long) row.getInt(0), row);
            }
        });
    }


    private static void generatorAreaProductTempTable(SQLContext sqlContext, JavaPairRDD<Long, Row> cityProductInfoRDD, JavaPairRDD<Long, Row> cityInfoRDD) {
        // 关联两张表
        JavaPairRDD<Long, Tuple2<Row, Row>> joinedRDD = cityProductInfoRDD.join(cityInfoRDD);
        // 将关联后的数据重新映射
        JavaRDD<Row> mappedRDD = joinedRDD.map(new Function<Tuple2<Long, Tuple2<Row, Row>>, Row>() {
            @Override
            public Row call(Tuple2<Long, Tuple2<Row, Row>> tuple) throws Exception {
                Long cityId = tuple._1;
                Row productInfoRow = tuple._2._1;
                Row cityInfoRow = tuple._2._2;

                return RowFactory.create((long)cityInfoRow.getInt(0), cityInfoRow.getString(1), cityInfoRow.getString(2), productInfoRow.getLong(1));
            }
        });
        // 定义DataFrame字段信息
        List<StructField> structFields = new ArrayList<>();
        structFields.add(DataTypes.createStructField("city_id", DataTypes.LongType, true));
        structFields.add(DataTypes.createStructField("city_name", DataTypes.StringType, true));
        structFields.add(DataTypes.createStructField("area", DataTypes.StringType, true));
        structFields.add(DataTypes.createStructField("product_id", DataTypes.LongType, true));
        StructType structType = DataTypes.createStructType(structFields);
        // 根据RDD和定义的字段信息 创建dataFrame
        DataFrame dataFrame = sqlContext.createDataFrame(mappedRDD, structType);
        // 将dataFrame注册成临时表
        dataFrame.registerTempTable("tmp_click_product_basic");
    }



    private static void generatorAreaProductGroupTempTable(SQLContext sqlContext) {

        String sql = "SELECT " +
                        "area, " +
                        "product_id, " +
                        "count(*) AS click_count, " +
                        "group_concat(concat(city_id, city_name, ':')) AS city_info " +
                     "FROM " +
                        "tmp_click_product_basic " +
                     "GROUP BY " +
                        "area, " +
                        "product_id";

        DataFrame dataFrame = sqlContext.sql(sql);
        // 生成每个地区下商品被点击次数统计的临时表
        dataFrame.registerTempTable("tmp_area_product_click_count");
    }


    private static JavaPairRDD<String, Row> getAreaProductGroupRDD(SQLContext sqlContext) {

        String sql = "SELECT " +
                "area, " +
                "product_id, " +
                "count(*) AS click_count, " +
                "group_concat(concat(city_id, city_name, ':')) AS city_info " +
                "FROM " +
                "tmp_click_product_basic " +
                "GROUP BY " +
                "area, " +
                "product_id";

        DataFrame dataFrame = sqlContext.sql(sql);
        return dataFrame.javaRDD().mapToPair(new PairFunction<Row, String, Row>() {
            @Override
            public Tuple2<String, Row> call(Row row) throws Exception {
                return new Tuple2<String, Row>(row.getString(0), row);
            }
        });
    }


    private static void generatorTop3AreaProductClickCountTempTable(JavaSparkContext sc, SQLContext sqlContext, JavaPairRDD<String, Row> areaProductGroupRDD) {
        // 根据地区为粒度分组
        JavaPairRDD<String, Iterable<Row>> groupByAreaRDD = areaProductGroupRDD.groupByKey();

        // 创建广播变统计top3变量
        Map<String, Row[]> top3Map = new HashMap<>();
        Broadcast<Map<String, Row[]>> broadcastTop3Map = sc.broadcast(top3Map);

        // 遍历每个地区分组 统计每个地区分组下top3商品点击次数
        groupByAreaRDD.foreach(new VoidFunction<Tuple2<String, Iterable<Row>>>() {
            @Override
            public void call(Tuple2<String, Iterable<Row>> tuple) throws Exception {
                String area = tuple._1;
                Iterator<Row> iterator = tuple._2.iterator();

                Map<String, Row[]> top3Map = broadcastTop3Map.value();
                Row[] rows = top3Map.get(area);
                if (rows == null){
                    rows = new Row[3];
                }

                // 遍历分组下每个商品的信息
                while (iterator.hasNext()){
                    // 取出当前商品信息
                    Row currentRow = iterator.next();

                    // 遍历top3数组
                    for (int x=0;x<rows.length;x++){

                        // 如果位置为空的话设置为当前商品信息，并跳出循环 -> 遍历下一个商品信息
                        if (rows[x] == null){
                            rows[x] = currentRow;
                            break;
                        }

                        // 位置不为空且位置的点击次数比当前商品点击次数少的话则将当前商品信息放入位置
                        if (currentRow.getLong(2) > rows[x].getLong(2)){
                            // 将原先数组位置都往后移一位
                            for (int y=rows.length-1; y>x; y--){
                                rows[y] = rows[y - 1];
                            }
                            // 设置位置为当前商品信息，并跳出循环 -> 遍历下一个商品信息
                            rows[x] = currentRow;
                            break;
                        }
                    }
                }
                // 将统计信息重新放入map中
                top3Map.put(area, rows);
            }
        });

        Map<String, Row[]> value = broadcastTop3Map.value();
        List<Row> rowList = new ArrayList<>(value.size() * 3);
        for (Map.Entry<String, Row[]> entry : value.entrySet()){
            for (Row row : entry.getValue()){
                rowList.add(row);
            }
        }

        JavaRDD<Row> top3AreaProductClickCountRDD = sc.parallelize(rowList);
        StructType schema = DataTypes.createStructType(Arrays.asList(
                DataTypes.createStructField("area", DataTypes.StringType, true),
                DataTypes.createStructField("product_id", DataTypes.LongType, true),
                DataTypes.createStructField("click_count", DataTypes.LongType, true),
                DataTypes.createStructField("city_info", DataTypes.StringType, true)));

        DataFrame dataFrame = sqlContext.createDataFrame(top3AreaProductClickCountRDD, schema);
        dataFrame.registerTempTable("tmp_top3_area_product_click_count");

    }


    private static void generatorTop3AreaProductClickCountTempTable(SQLContext sqlContext) {
        String sql = "SELECT " +
                        "tb1.area," +
                        "tb1.product_id," +
                        "tb1.click_count," +
                        "tb1.city_info" +
                     "FROM (" +
                        "SELECT " +
                            "area," +
                            "product_id," +
                            "click_count," +
                            "city_info," +
                            "row_number() OVER (PARTITION BY area ORDER BY click_count DESC) rank " +
                        "FROM " +
                            "tmp_area_product_click_count" +
                           ") tb1 " +
                     "WHERE " +
                        "tb1.rank <= 3";

        DataFrame dataFrame = sqlContext.sql(sql);
        // 生成每个地区下商品被点击次数统计的临时表
        dataFrame.registerTempTable("tmp_top3_area_product_click_count");
    }



    private static JavaRDD<Row> getFullTop3AreaProductClickCountRDD(SQLContext sqlContext) {

        String sql = "SELECT " +
                        "tb1.area," +
                        "CASE " +
                            "WHEN tb1.area='华东' OR tb1.area='华北' THEN '一线城市' " +
                            "WHEN tb1.area='华南' OR tb1.area='华中' THEN '二线城市' " +
                            "WHEN tb1.area='西北' OR tb1.area='西南' THEN '三线城市' " +
                            "WHEN tb1.area='东北' THEN '四线城市' " +
                        "END AS area_level," +
                        "tb1.product_id," +
                        "tb2.product_name," +
                        "if(getStatus(tb2.extend_info)=0,'自营商品','第三方商品') AS product_status," +
                        "tb1.click_count," +
                        "tb1.city_info " +
                    "FROM " +
                        "tmp_top3_area_product_click_count tb1 " +
                    "JOIN " +
                        "product_info tb2 " +
                    "ON " +
                        "tb1.product_id = tb2.product_id";

        DataFrame dataFrame = sqlContext.sql(sql);
        return dataFrame.javaRDD();
    }


    private static void saveMysql(Long taskId, JavaRDD<Row> resultRDD) {
        List<Row> collect = resultRDD.collect();
        IAreaTop3ProductDao areaTop3ProductDao = DaoFactory.getAreaTop3ProductDao();
        List<AreaTop3ProductEntity> entityList = new ArrayList<>();
        for (Row row : collect){
            AreaTop3ProductEntity entity = new AreaTop3ProductEntity();
            entity.setTaskId(taskId);
            entity.setArea(row.getString(0));
            entity.setAreaLevel(row.getString(1));
            entity.setProductId(row.getLong(2));
            entity.setCityNames(row.getString(6));
            entity.setClickCount((int) row.getLong(5));
            entity.setProductName(row.getString(3));
            entity.setProductStatus(row.getString(4));
            entityList.add(entity);
        }
        areaTop3ProductDao.batchInsert(entityList);
    }

}
