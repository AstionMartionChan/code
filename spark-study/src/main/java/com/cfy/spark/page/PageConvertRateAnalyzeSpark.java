package com.cfy.spark.page;

import com.alibaba.fastjson.JSONObject;
import com.cfy.bean.PageRateEntity;
import com.cfy.constants.Constant;
import com.cfy.dao.IPageRateDao;
import com.cfy.dao.factory.DaoFactory;
import com.cfy.utils.DateUtils;
import com.cfy.utils.NumberUtils;
import com.cfy.utils.ParamUtils;
import com.cfy.utils.SparkUtil;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.storage.StorageLevel;
import scala.Tuple2;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/7/10
 * Time: 15:10
 * Work contact: Astion_Leo@163.com
 */


public class PageConvertRateAnalyzeSpark {

    public static void main(String[] args) {

        // 设置spark作业名称
        SparkConf conf = new SparkConf()
                .setAppName(Constant.SPARK_APP_NAME_PAGE);
        // 设置sparkMaster
        SparkUtil.setMaster(conf);
        // 获取Spark上下文对象
        JavaSparkContext sc = new JavaSparkContext(conf);
        // 获取SQLContext
        SQLContext sqlContext = SparkUtil.getSQLContext(sc.sc());

        // 生成模拟数据
        SparkUtil.mockData(sc, sqlContext);

        // 获取用户指定的查询参数
        JSONObject params = SparkUtil.getParams(args);

        // 根绝用户咨询的参数查询出原始数据
        JavaRDD<Row> javaRDDByDate = getJavaRDDByDate(sqlContext, params);

        // 映射为<session, row>格式的RDD
        JavaPairRDD<String, Row> mappedOriginalRDD = javaRDDByDate.mapToPair(new PairFunction<Row, String, Row>() {
            @Override
            public Tuple2<String, Row> call(Row row) throws Exception {
                return new Tuple2<String, Row>(row.getString(2), row);
            }
        });
        
        // 根据session粒度，聚合数据
        JavaPairRDD<String, Iterable<Row>> sessionGranularityRDD = mappedOriginalRDD.groupByKey();
        // 复用的RDD 进行缓存避免重复计算
        sessionGranularityRDD = sessionGranularityRDD.persist(StorageLevel.MEMORY_ONLY());

        // 取出用户指定的页面List 作为广播变量
        String pageList = ParamUtils.getParam(params, Constant.PARAM_PAGE_LIST);
        Broadcast<String> pageListToBroadcast = sc.broadcast(pageList);

        // 计算出每个页面切片的访问数量
        JavaPairRDD<String, Long> pageSplitNumRDD = computePageSplitNum(sessionGranularityRDD, pageListToBroadcast);
        Map<String, Object> pageSplitCount = pageSplitNumRDD.countByKey();

        // 计算出初始页面的访问数量
        Long firstPageCount = computeFirstPageNum(sessionGranularityRDD, pageListToBroadcast);

        // 将转化率存入mysql
        saveMysql(pageListToBroadcast, pageSplitCount, firstPageCount, Long.valueOf(args[0]));


    }

    private static void saveMysql(Broadcast<String> pageListToBroadcast, Map<String, Object> pageSplitCount, Long firstPageCount, Long taskId) {

        String[] pageArray = pageListToBroadcast.value().split(",");
        String rateStr = "";
        for (int x=1; x<pageArray.length; x++){
            String currentPageSplit = pageArray[x - 1] + "_" + pageArray[x];
            Long currentPageSplitCount = Long.valueOf(pageSplitCount.get(currentPageSplit).toString());
            double rate = 0;
            if (x == 1){
                rate = NumberUtils.formatDouble((double) currentPageSplitCount / (double) firstPageCount, 2);
                rateStr += currentPageSplit + "=" + rate;
            } else {
                if (pageArray.length - x > 1){
                    String nextPageSplit = pageArray[x] + "_" + pageArray[x + 1];
                    Long nextPageSplitCount = Long.valueOf(pageSplitCount.get(nextPageSplit).toString());
                    rate = NumberUtils.formatDouble((double) nextPageSplitCount / (double) currentPageSplitCount, 2);
                    rateStr += "|" + currentPageSplit + "=" + rate;
                }
            }
        }

        IPageRateDao pageRateDao = DaoFactory.getPageRateDao();
        PageRateEntity entity = new PageRateEntity();
        entity.setTaskId(taskId);
        entity.setRate(rateStr);
        pageRateDao.insert(entity);

    }

    private static Long computeFirstPageNum(
            JavaPairRDD<String, Iterable<Row>> sessionGranularityRDD,
            Broadcast<String> pageListToBroadcast) {

        String pageList = pageListToBroadcast.value();
        Long firstPage = Long.valueOf(pageList.split(",")[0].toString());
        return sessionGranularityRDD.flatMap(new FlatMapFunction<Tuple2<String,Iterable<Row>>, Long>() {
            @Override
            public Iterable<Long> call(Tuple2<String, Iterable<Row>> tuple) throws Exception {
                Iterator<Row> rows = tuple._2.iterator();
                List<Long> countList = new ArrayList<Long>();
                while (rows.hasNext()){
                    Row row = rows.next();
                    Long currentPage = row.getLong(3);
                    if (currentPage.equals(firstPage)){
                        countList.add(1L);
                    }
                }

                return countList;
            }
        }).count();
    }


    private static JavaPairRDD<String, Long> computePageSplitNum(
            JavaPairRDD<String, Iterable<Row>> sessionGranularityRDD,
            Broadcast<String> pageListToBroadcast) {

        String pageList = pageListToBroadcast.value();
        String[] pageArray = pageList.split(",");


        return sessionGranularityRDD.flatMapToPair(new PairFlatMapFunction<Tuple2<String,Iterable<Row>>, String, Long>() {
            @Override
            public Iterable<Tuple2<String, Long>> call(Tuple2<String, Iterable<Row>> tuple) throws Exception {
                // 对session聚合里的数据先进行时间排序
                Iterator<Row> rows = tuple._2.iterator();
                List<Row> sortRowList = new ArrayList<>();

                while (rows.hasNext()){
                    sortRowList.add(rows.next());
                }

                Collections.sort(sortRowList, new Comparator<Row>() {
                    @Override
                    public int compare(Row row1, Row row2) {
                        Date actionTimeForRow1 = DateUtils.parseString(row1.getString(4));
                        Date actionTimeForRow2 = DateUtils.parseString(row2.getString(4));

                        return (int) (actionTimeForRow1.getTime() - actionTimeForRow2.getTime());
                    }
                });

                // 循环取出两两相依的page并去用户指定的pageList里判断，如果有的话就返回
                List<Tuple2<String, Long>> resultList = new ArrayList<Tuple2<String, Long>>();
                Long lastPage = null;
                for (Row row : sortRowList){
                    Long currentPage = row.getLong(3);

                    if (lastPage == null){
                        lastPage = currentPage;
                        continue;
                    }

                    String pageSplit = currentPage + "_" + lastPage;

                    for (int x=1; x<pageArray.length; x++){
                        String currentPageSplit = pageArray[x - 1] + "_" + pageArray[x];
                        if (pageSplit.equals(currentPageSplit)){
                            resultList.add(new Tuple2<String, Long>(pageSplit, 1L));
                        }
                    }

                    lastPage = currentPage;
                }

                return resultList;
            }
        });
    }


    private static JavaRDD<Row> getJavaRDDByDate(SQLContext sqlContext, JSONObject params) {
        String startDate = ParamUtils.getParam(params, Constant.PARAM_START_DATE);
        String endDate = ParamUtils.getParam(params, Constant.PARAM_END_DATE);
        String sql ="select " +
                "* " +
                " from " +
                "   user_visit_action" +
                " where" +
                " date >= '" + startDate +
                "' and" +
                " date <='" + endDate + "'";
        DataFrame dataFrame = sqlContext.sql(sql);
        JavaRDD<Row> javaRDD = dataFrame.javaRDD();
        System.out.println(javaRDD.count());
        return javaRDD;
    }
}
