package com.cfy.spark.sesstion;

import com.alibaba.fastjson.JSONObject;
import com.cfy.accumulator.SesstionAccumulator;
import com.cfy.bean.*;
import com.cfy.constants.Constant;
import com.cfy.dao.*;
import com.cfy.dao.factory.DaoFactory;
import com.cfy.key.TOP10CustomSortKey;
import com.cfy.test.MockData;
import com.cfy.utils.*;
import com.google.common.base.Optional;
import org.apache.spark.Accumulator;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.hive.HiveContext;
import scala.Tuple2;

import java.util.*;


/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/7/1
 * Time: 13:43
 * Work contact: Astion_Leo@163.com
 */


public class UserVisitSeestionAnalyzeSpark {

    public static void main(String[] args) {



        // 构建spark上下文
        SparkConf conf = new SparkConf()
                .setAppName(Constant.SPARK_APP_NAME_SESSTION)
                .setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);
        SQLContext sqlContext = getSQLContext(sc.sc());

        // 生成模拟数据
        mockData(sc, sqlContext);

        // 获取根据传入的args参数获取taskId
        Long taskId = ParamUtils.getTaskIdFromArgs(args);
        ITaskDao taskDao = DaoFactory.getTaskDao();
        TaskEntity taskEntity = taskDao.findById(Integer.valueOf(taskId.toString()));
        JSONObject params = JSONObject.parseObject(taskEntity.getTaskParam());

        // 根据spark sql获取原始的JavaRDD
        JavaRDD<Row> javaRDD = getJavaRDDByDate(sqlContext, params);

        // 按照sesstionId为粒度进行数据聚合
        JavaPairRDD<String, String> sesstionFullAggrRDD = aggregateBySesstionId(sqlContext, javaRDD);
        System.out.println(sesstionFullAggrRDD.count());

        // 创建自定义accumulator
        Accumulator<String> accumulator = sc.accumulator("", new SesstionAccumulator());

        // 按照用户传入的查询参数进行sesstion过滤
        JavaPairRDD<String, String> filterSesstionFullAggrRDD = filterAggregateByParams(sesstionFullAggrRDD, params, accumulator);
        System.out.println(filterSesstionFullAggrRDD.count());


        // 需求1： 计算sesstion的访问时长 步长
//        saveForMysql(accumulator.value(), taskId);

        // 需求2： 对sesstion数据按照时间比例随机抽取100个样本
//        randomExtractSesstion(filterSesstionFullAggrRDD, javaRDD, taskId);

        // 需求3： 对热门品类取TOP10 排序方式自定义 按照 点击 下单 支付
        List<Tuple2<TOP10CustomSortKey, Long>> top10Category = getTop10Catagory(filterSesstionFullAggrRDD, javaRDD, taskId);

        // 需求4： 对热门品类TOP10 取每个品类点击次数最多的十个sesstion
        getTop10CategoryClickSesstion(sc, top10Category, filterSesstionFullAggrRDD, javaRDD, taskId);


        // 关闭spark上下文
        sc.close();
    }


    private static SQLContext getSQLContext(SparkContext sc) {
        Boolean local = ConfigurationManager.getBoolean(Constant.SPARK_LOCAL);

        if (local){
            return new SQLContext(sc);
        } else {
            return new HiveContext(sc);
        }
    }

    private static void mockData(JavaSparkContext sc, SQLContext sqlContext) {
        Boolean local = ConfigurationManager.getBoolean(Constant.SPARK_LOCAL);

        if (local){
            MockData.mock(sc, sqlContext);
        }
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

    /**
     *  按照sesstionId粒度进行数据聚合
     *  第一步抽取sesstionId作为key                       row -> (sesstionId, row)
     *  第二步进行分组聚合                                (sesstionId, row) -> (sesstionId, iterable<row>)
     *  第三步抽取userId作为key,
     *  并拼接sesstionId,统计搜索商品名称,点击分类数据总和   (sesstionId, iterable<row>) -> (userId, partAggrInfo)
     *  第四步spark sql获取user_info数据
     *  并按照userId粒度进行数据聚合                       row -> (userId, row)
     *  第五步使用join 对两个RDD进行聚合                   (userId, partAggrInfo) (userId, row) -> userId, (partAggrInfo, row)
     *  第六步对join后数据根绝sesstionId粒度进行数据聚合    userId, (partAggrInfo, row) -> (sesstionId, fullPartAggrInfo)
     * @param sqlContext
     * @param javaRDD
     */
    private static JavaPairRDD<String, String> aggregateBySesstionId(SQLContext sqlContext, JavaRDD<Row> javaRDD) {

        // 1. 将row数据映射成 <String, Row>
        JavaPairRDD<String, Row> sesstionIdRDD = javaRDD.mapToPair(new PairFunction<Row, String, Row>() {

            private static final long serialVersionUID = 1L;

            @Override
            public Tuple2<String, Row> call(Row row) throws Exception {
                return new Tuple2<String, Row>(row.getString(2), row);
            }
        });

        // 2. 对数据按照sesstionId为粒度进行分组
        JavaPairRDD<String, Iterable<Row>> sesstionIdGroupRDD = sesstionIdRDD.groupByKey();

        // 3. 对每一组sesstionId里的数据进行聚合，分别把搜索商品，点击类目都聚合起来
        JavaPairRDD<Long, String> partAggrInfoRDD = sesstionIdGroupRDD.mapToPair(new PairFunction<Tuple2<String, Iterable<Row>>, Long, String>() {

            private static final long serialVersionUID = 1L;

            @Override
            public Tuple2<Long, String> call(Tuple2<String, Iterable<Row>> tuple2) throws Exception {

                StringBuffer catalogs = new StringBuffer("");
                StringBuffer searchContents = new StringBuffer("");

                Long userId = null;
                Integer stepPeriod = 0;
                Date startTime = null;
                Date endTime = null;
                
                String sesstionId = tuple2._1;
                Iterator<Row> iterator = tuple2._2.iterator();
                while (iterator.hasNext()) {
                    Row row = iterator.next();

                    if (null == userId) {
                        userId = row.getLong(1);
                    }
                    String searchContent = row.getString(5);
                    Long catalog = row.getLong(6);
                    if (StringUtils.isNotEmpty(searchContent)) {
                        searchContents.append(searchContent + ",");
                    }
                    if (null != catalog) {
                        catalogs.append(catalog + ",");
                    }
                    
                    
                    // 计算sesstion的时长
                    Date actionTime = DateUtils.parseString(row.getString(4));

                    if (startTime == null){
                        startTime = actionTime;
                    }

                    if (endTime == null){
                        endTime = actionTime;
                    }

                    if (actionTime.before(startTime)){
                        startTime = actionTime;
                    }

                    if (actionTime.after(endTime)){
                        endTime = actionTime;
                    }

                    // 累加sesstion的步长
                    stepPeriod++;
                }

                Long timePeriod = (endTime.getTime() - startTime.getTime()) / 1000;


                String clickCatalogs = StringUtils.trimComma(catalogs.toString());
                String searchKeywords = StringUtils.trimComma(searchContents.toString());
                String partAggrInfo = Constant.FIELD_SESSTION_ID + "=" + sesstionId + "|"
                        + Constant.FIELD_CLICK_CATALOGS + "=" + clickCatalogs + "|"
                        + Constant.FIELD_SEARCH_KEYWORDS + "=" + searchKeywords + "|"
                        + Constant.FIELD_TIME_PERIOD + "=" + timePeriod + "|"
                        + Constant.FIELD_STEP_PERIOD + "=" + stepPeriod + "|"
                        + Constant.FIELD_START_TIME + "=" + DateUtils.formatTime(startTime);

                return new Tuple2<Long, String>(userId, partAggrInfo);
            }
        });

        //4. 查询user_info数据 并映射成<String, Row>
        String sql ="select" +
                        " *" +
                    " from" +
                        " user_info";
        JavaRDD<Row> userInfoJavaRDD = sqlContext.sql(sql).javaRDD();
        JavaPairRDD<Long, Row> userInfoPairRDD = userInfoJavaRDD.mapToPair(new PairFunction<Row, Long, Row>() {
            @Override
            public Tuple2<Long, Row> call(Row row) throws Exception {
                return new Tuple2<Long, Row>(row.getLong(0), row);
            }
        });

        //5. 对两个RDD 进行join
        JavaPairRDD<Long, Tuple2<String, Row>> joinedRDD = partAggrInfoRDD.join(userInfoPairRDD);

        //6. 对join后的RDD根据sesstionId粒度进行聚合
        JavaPairRDD<String, String> fullPartAggrInfoRDD = joinedRDD.mapToPair(new PairFunction<Tuple2<Long, Tuple2<String, Row>>, String, String>() {

            private static final long serialVersionUID = 1L;

            @Override
            public Tuple2<String, String> call(Tuple2<Long, Tuple2<String, Row>> tuple2) throws Exception {
                Tuple2<String, Row> fullPartAggrInfo = tuple2._2;
                String partAggrInfo = fullPartAggrInfo._1;
                Row userInfo = fullPartAggrInfo._2;

                String sesstionId = StringUtils.getFieldFromConcatString(partAggrInfo, "\\|", Constant.FIELD_SESSTION_ID);

                Integer age = userInfo.getInt(3);
                String professional = userInfo.getString(4);
                String city = userInfo.getString(5);
                String sex = userInfo.getString(6);

                String fullInfo = partAggrInfo + "|"
                        + Constant.FIELD_AGE + "=" + age + "|"
                        + Constant.FIELD_PROFESSIONAL + "=" + professional + "|"
                        + Constant.FIELD_CITY + "=" + city + "|"
                        + Constant.FIELD_SEX + "=" + sex;


                return new Tuple2<String, String>(sesstionId, fullInfo);
            }
        });

        return fullPartAggrInfoRDD;

    }


    private static JavaPairRDD<String,String> filterAggregateByParams(JavaPairRDD<String, String> sesstionFullAggrRDD, JSONObject taskParam, Accumulator<String> accumuLator) {
        String startAge = ParamUtils.getParam(taskParam, Constant.PARAM_START_AGE);
        String endAge = ParamUtils.getParam(taskParam, Constant.PARAM_END_AGE);
        String professionals = ParamUtils.getParam(taskParam, Constant.PARAM_PROFESSIONALS);
        String cities = ParamUtils.getParam(taskParam, Constant.PARAM_CITIES);
        String sex = ParamUtils.getParam(taskParam, Constant.PARAM_SEX);
        String keywords = ParamUtils.getParam(taskParam, Constant.PARAM_KEYWORDS);
        String categoryIds = ParamUtils.getParam(taskParam, Constant.PARAM_CATEGORY_IDS);

        String _parameter = (startAge != null ? Constant.PARAM_START_AGE + "=" + startAge + "|" : "")
                + (endAge != null ? Constant.PARAM_END_AGE + "=" + endAge + "|" : "")
                + (professionals != null ? Constant.PARAM_PROFESSIONALS + "=" + professionals + "|" : "")
                + (cities != null ? Constant.PARAM_CITIES + "=" + cities + "|" : "")
                + (sex != null ? Constant.PARAM_SEX + "=" + sex + "|" : "")
                + (keywords != null ? Constant.PARAM_KEYWORDS + "=" + keywords + "|" : "")
                + (categoryIds != null ? Constant.PARAM_CATEGORY_IDS + "=" + categoryIds: "");

        if(_parameter.endsWith("\\|")) {
            _parameter = _parameter.substring(0, _parameter.length() - 1);
        }

        final String parameter = _parameter;

        JavaPairRDD<String, String> filteredRDD = sesstionFullAggrRDD.filter(new Function<Tuple2<String, String>, Boolean>() {

            private static final long serialVersionUID = 4124121L;

            @Override
            public Boolean call(Tuple2<String, String> tuple2) throws Exception {

                String fullAggrInfo = tuple2._2;

                // 根据用户传入的条件参数做过滤
                if (!ValidUtils.between(fullAggrInfo, Constant.FIELD_AGE, parameter, Constant.PARAM_START_AGE, Constant.PARAM_END_AGE)) {
                    return false;
                }

                if (!ValidUtils.in(fullAggrInfo, Constant.FIELD_PROFESSIONAL, parameter, Constant.PARAM_PROFESSIONALS)) {
                    return false;
                }

                if (!ValidUtils.in(fullAggrInfo, Constant.FIELD_CITY, parameter, Constant.PARAM_CITIES)) {
                    return false;
                }

                if (!ValidUtils.equal(fullAggrInfo, Constant.FIELD_SEX, parameter, Constant.PARAM_SEX)) {
                    return false;
                }

                if (!ValidUtils.in(fullAggrInfo, Constant.FIELD_SEARCH_KEYWORDS, parameter, Constant.PARAM_KEYWORDS)) {
                    return false;
                }

                if (!ValidUtils.in(fullAggrInfo, Constant.FIELD_CLICK_CATALOGS, parameter, Constant.PARAM_CATEGORY_IDS)) {
                    return false;
                }

                // 对每一个sesstion进行计数
                accumuLator.add(Constant.SESSION_COUNT);

                // 对时长和步长进行统计
                statTimePeriod(fullAggrInfo);
                statStepPeriod(fullAggrInfo);

                return true;
            }

            private void statTimePeriod(String fullAggrInfo) {
                Long timePeriod = Long.valueOf(StringUtils.getFieldFromConcatString(fullAggrInfo, "\\|", Constant.FIELD_TIME_PERIOD));

                if (timePeriod >= 1 && timePeriod <= 3){
                    accumuLator.add(Constant.TIME_PERIOD_1s_3s);
                } else if (timePeriod >= 4 && timePeriod <= 6){
                    accumuLator.add(Constant.TIME_PERIOD_4s_6s);
                } else if (timePeriod >= 7 && timePeriod <= 9){
                    accumuLator.add(Constant.TIME_PERIOD_7s_9s);
                } else if (timePeriod >= 10 && timePeriod <= 30){
                    accumuLator.add(Constant.TIME_PERIOD_10s_30s);
                } else if (timePeriod > 30 && timePeriod <= 60){
                    accumuLator.add(Constant.TIME_PERIOD_30s_60s);
                } else if (timePeriod > 60 && timePeriod <= 180){
                    accumuLator.add(Constant.TIME_PERIOD_1m_3m);
                } else if (timePeriod > 180 && timePeriod <= 600){
                    accumuLator.add(Constant.TIME_PERIOD_3m_10m);
                } else if (timePeriod > 600 && timePeriod <= 1800){
                    accumuLator.add(Constant.TIME_PERIOD_10m_30m);
                } else if (timePeriod > 1800){
                    accumuLator.add(Constant.TIME_PERIOD_30m);
                }

            }

            private void statStepPeriod(String fullAggrInfo) {
                Integer stepPeriod = Integer.valueOf(StringUtils.getFieldFromConcatString(fullAggrInfo, "\\|", Constant.FIELD_STEP_PERIOD));
                if (stepPeriod >= 1 && stepPeriod <= 3){
                    accumuLator.add(Constant.STEP_PERIOD_1_3);
                } else if (stepPeriod >= 4 && stepPeriod <= 6){
                    accumuLator.add(Constant.STEP_PERIOD_4_6);
                } else if (stepPeriod >= 7 && stepPeriod <= 9){
                    accumuLator.add(Constant.STEP_PERIOD_7_9);
                } else if (stepPeriod >= 10 && stepPeriod <= 30){
                    accumuLator.add(Constant.STEP_PERIOD_10_30);
                } else if (stepPeriod > 30 && stepPeriod <= 60){
                    accumuLator.add(Constant.STEP_PERIOD_30_60);
                } else if (stepPeriod > 60){
                    accumuLator.add(Constant.STEP_PERIOD_60);
                }
            }
        });
        return filteredRDD;

    }


    private static void saveForMysql(String value, Long taskId) {
        String sesstion_count = StringUtils.getFieldFromConcatString(value, "\\|", Constant.SESSION_COUNT);
        String time_1s_3s = StringUtils.getFieldFromConcatString(value, "\\|", Constant.TIME_PERIOD_1s_3s);
        String time_4s_6s = StringUtils.getFieldFromConcatString(value, "\\|", Constant.TIME_PERIOD_4s_6s);
        String time_7s_9s = StringUtils.getFieldFromConcatString(value, "\\|", Constant.TIME_PERIOD_7s_9s);
        String time_10s_30s = StringUtils.getFieldFromConcatString(value, "\\|", Constant.TIME_PERIOD_10s_30s);
        String time_30s_60s = StringUtils.getFieldFromConcatString(value, "\\|", Constant.TIME_PERIOD_30s_60s);
        String time_1m_3m = StringUtils.getFieldFromConcatString(value, "\\|", Constant.TIME_PERIOD_1m_3m);
        String time_3m_10m = StringUtils.getFieldFromConcatString(value, "\\|", Constant.TIME_PERIOD_3m_10m);
        String time_10m_30m = StringUtils.getFieldFromConcatString(value, "\\|", Constant.TIME_PERIOD_10m_30m);
        String time_30m = StringUtils.getFieldFromConcatString(value, "\\|", Constant.TIME_PERIOD_30m);
        String step_1_3 = StringUtils.getFieldFromConcatString(value, "\\|", Constant.STEP_PERIOD_1_3);
        String step_4_6 = StringUtils.getFieldFromConcatString(value, "\\|", Constant.STEP_PERIOD_4_6);
        String step_7_9 = StringUtils.getFieldFromConcatString(value, "\\|", Constant.STEP_PERIOD_7_9);
        String step_10_30 = StringUtils.getFieldFromConcatString(value, "\\|", Constant.STEP_PERIOD_10_30);
        String step_30_60 = StringUtils.getFieldFromConcatString(value, "\\|", Constant.STEP_PERIOD_30_60);
        String step_60 = StringUtils.getFieldFromConcatString(value, "\\|", Constant.STEP_PERIOD_60);

        double precent_1s_3s = NumberUtils.formatDouble((Double.valueOf(time_1s_3s) / Double.valueOf(sesstion_count)), 2);
        double precent_4s_6s = NumberUtils.formatDouble((Double.valueOf(time_4s_6s) / Double.valueOf(sesstion_count)), 2);
        double precent_7s_9s = NumberUtils.formatDouble((Double.valueOf(time_7s_9s) / Double.valueOf(sesstion_count)), 2);
        double precent_10s_30s = NumberUtils.formatDouble((Double.valueOf(time_10s_30s) / Double.valueOf(sesstion_count)), 2);
        double precent_30s_60s = NumberUtils.formatDouble((Double.valueOf(time_30s_60s) / Double.valueOf(sesstion_count)), 2);
        double precent_1m_3m = NumberUtils.formatDouble((Double.valueOf(time_1m_3m) / Double.valueOf(sesstion_count)), 2);
        double precent_3m_10m = NumberUtils.formatDouble((Double.valueOf(time_3m_10m) / Double.valueOf(sesstion_count)), 2);
        double precent_10m_30m = NumberUtils.formatDouble((Double.valueOf(time_10m_30m) / Double.valueOf(sesstion_count)), 2);
        double precent_30m = NumberUtils.formatDouble((Double.valueOf(time_30m) / Double.valueOf(sesstion_count)), 2);
        double precent_1_3 = NumberUtils.formatDouble((Double.valueOf(step_1_3) / Double.valueOf(sesstion_count)), 2);
        double precent_4_6 = NumberUtils.formatDouble((Double.valueOf(step_4_6) / Double.valueOf(sesstion_count)), 2);
        double precent_7_9 = NumberUtils.formatDouble((Double.valueOf(step_7_9) / Double.valueOf(sesstion_count)), 2);
        double precent_10_30 = NumberUtils.formatDouble((Double.valueOf(step_10_30) / Double.valueOf(sesstion_count)), 2);
        double precent_30_60 = NumberUtils.formatDouble((Double.valueOf(step_30_60) / Double.valueOf(sesstion_count)), 2);
        double precent_60 = NumberUtils.formatDouble((Double.valueOf(step_60) / Double.valueOf(sesstion_count)), 2);


        SessionAggrStatEntity entity = new SessionAggrStatEntity();
        entity.setTaskId(Integer.valueOf(taskId.toString()));
        entity.setSessionCount(Integer.valueOf(sesstion_count));
        entity.setTime_1s3s(precent_1s_3s);
        entity.setTime_4s6s(precent_4s_6s);
        entity.setTime_7s9s(precent_7s_9s);
        entity.setTime_10s30s(precent_10s_30s);
        entity.setTime_30s60s(precent_30s_60s);
        entity.setTime_1m3m(precent_1m_3m);
        entity.setTime_3m10m(precent_3m_10m);
        entity.setTime_10m30m(precent_10m_30m);
        entity.setTime_30m(precent_30m);
        entity.setStep_13(precent_1_3);
        entity.setStep_46(precent_4_6);
        entity.setStep_79(precent_7_9);
        entity.setStep_1030(precent_10_30);
        entity.setStep_3060(precent_30_60);
        entity.setStep_60(precent_60);

        ISesstionAggrStatDao sesstionAggrStatDao = DaoFactory.getSesstionAggrStatDao();
        sesstionAggrStatDao.insert(entity);
    }




    private static void randomExtractSesstion(JavaPairRDD<String, String> filteredSesstionAggrInfo,
                                              JavaRDD<Row> sesstionBaseInfo,
                                              Long taskId) {
        // 1. 将sesstion的操作时间作为key 映射
        JavaPairRDD<String, String> mappedStartTimeSesstionAggrInfo = filteredSesstionAggrInfo.mapToPair(new PairFunction<Tuple2<String, String>, String, String>() {
            @Override
            public Tuple2<String, String> call(Tuple2<String, String> tuple2) throws Exception {
                String sesstionAggrInfo = tuple2._2;
                String startTime = StringUtils.getFieldFromConcatString(sesstionAggrInfo, "\\|", Constant.FIELD_START_TIME);
                String dateHour = DateUtils.getDateHour(startTime);
                return new Tuple2<String, String>(dateHour, sesstionAggrInfo);
            }
        });

        // 2. 使用countByKey 求出每天每小时的sesstion总数
        Map<String, Object> startTimeCountMap = mappedStartTimeSesstionAggrInfo.countByKey();

        // 3. 拆分每天和每小时维度 封装进新的map 并按照一样的每天和没小时维度创建索引map
        Map<String, Map<String, Object>> dateHourCountMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : startTimeCountMap.entrySet()){
            String dateTime = entry.getKey();
            Integer dateHourCount = Integer.valueOf(entry.getValue().toString());
            String[] splited = dateTime.split("_");
            if (splited.length == 2){
                String date = splited[0];
                String hour = splited[1];

                Map<String, Object> hourCountMap = dateHourCountMap.get(date);
                if (hourCountMap == null){
                    hourCountMap = new HashMap<>();
                    dateHourCountMap.put(date, hourCountMap);
                }

                hourCountMap.put(hour, dateHourCount);
            }
        }

        Map<String, Map<String, List<Integer>>> dateHourIndexMap = new HashMap<>();

        // 4. 计算出每天平均需要抽取多少样本
        int extractDateNum = 100 / dateHourCountMap.size();

        // 5. 计算每个小时的sesstion总数占当天sesstion总数的百分比，并按这个比例得出每个小时抽取多少样本
        Random random = new Random();
        for (Map.Entry<String, Map<String, Object>> entry : dateHourCountMap.entrySet()){
            String date = entry.getKey();
            Map<String, Object> hourCountMap = entry.getValue();
            
            // 每天的sesstion总数
            Integer sesstionDateCount = 0;
            for (Object count : hourCountMap.values()){
                sesstionDateCount += Integer.valueOf(count.toString());
            }

            Map<String, List<Integer>> hourIndexMap = dateHourIndexMap.get(date);
            if (hourIndexMap == null){
                hourIndexMap = new HashMap<String, List<Integer>>();
                dateHourIndexMap.put(date, hourIndexMap);
            }

            for (Map.Entry<String, Object> hourEntry : hourCountMap.entrySet()){
                String hour = hourEntry.getKey();
                Integer sesstionHourCount = Integer.valueOf(hourEntry.getValue().toString());

                Integer extractHourNum = (int) (((double)sesstionHourCount / (double)sesstionDateCount) * (double)extractDateNum);

                // 如果每小时随机抽取的数量大于每小时sesstion的总数量
                // 则让随机抽取数量等于每小时sesstion总数量，否则再后面取值的时候会出现问题。
                if (extractHourNum > sesstionHourCount){
                    extractHourNum = sesstionHourCount;
                }

                List<Integer> hourIndex = dateHourIndexMap.get(date).get(hour);
                if (hourIndex == null){
                    hourIndex = new ArrayList<>();
                }

                for (int x=0; x< extractHourNum; x++){
                    int extractIndex = random.nextInt(sesstionHourCount);
                    while (hourIndex.contains(extractIndex)){
                        extractIndex = random.nextInt(sesstionHourCount);
                    }
                    hourIndex.add(extractIndex);
                }
                hourIndexMap.put(hour, hourIndex);
            }
        }
        
        // 6. 根据算出的随机抽取样本索引，取出数据并存入Mysql
        JavaPairRDD<String, Iterable<String>> mappedStartTimeSesstionsAggrInfo = mappedStartTimeSesstionAggrInfo.groupByKey();

        JavaPairRDD<String, String> sesstionFlatMapRDD = mappedStartTimeSesstionsAggrInfo.flatMapToPair(new PairFlatMapFunction<Tuple2<String, Iterable<String>>, String, String>() {
            @Override
            public Iterable<Tuple2<String, String>> call(Tuple2<String, Iterable<String>> tuple2) throws Exception {
                String dateHour = tuple2._1;
                Iterator<String> sesstionAggrInfo = tuple2._2.iterator();
                String[] splited = dateHour.split("_");
                String date = splited[0];
                String hour = splited[1];

                List<Integer> hourIndex = dateHourIndexMap.get(date).get(hour);
                List<Tuple2<String, String>> resultTuple = new ArrayList<>();

                int index = 0;
                while (sesstionAggrInfo.hasNext()) {
                    String info = sesstionAggrInfo.next();
                    if (hourIndex.contains(index)) {
                        ISesstionRandomExtractDao sesstionRandomExtractDao = DaoFactory.getSesstionRandomExtractDao();
                        SessionRandomExtractEntity entity = new SessionRandomExtractEntity();

                        String sessionId = StringUtils.getFieldFromConcatString(info, "\\|", Constant.FIELD_SESSTION_ID);
                        entity.setTaskId(Integer.valueOf(taskId.toString()));
                        entity.setSessionId(sessionId);
                        entity.setStartTime(StringUtils.getFieldFromConcatString(info, "\\|", Constant.FIELD_START_TIME));
                        entity.setSearchKeywords(StringUtils.getFieldFromConcatString(info, "\\|", Constant.FIELD_SEARCH_KEYWORDS));
                        entity.setCatagoryIds(StringUtils.getFieldFromConcatString(info, "\\|", Constant.FIELD_CLICK_CATALOGS));

                        sesstionRandomExtractDao.insert(entity);

                        resultTuple.add(new Tuple2<>(sessionId, sessionId));
                    }

                    index++;
                }

                return resultTuple;
            }
        });

        // 7. 根据抽取数据的sesstionId 关联原始sesstion数据并插入mysql表
        JavaPairRDD<String, Row> mappedSesstionBaseInfoRDD = sesstionBaseInfo.mapToPair(new PairFunction<Row, String, Row>() {
            @Override
            public Tuple2<String, Row> call(Row row) throws Exception {
                return new Tuple2<String, Row>(row.getString(2), row);
            }
        });

        JavaPairRDD<String, Tuple2<String, Row>> joinedRDD = sesstionFlatMapRDD.join(mappedSesstionBaseInfoRDD);
        joinedRDD.foreach(new VoidFunction<Tuple2<String, Tuple2<String, Row>>>() {
            @Override
            public void call(Tuple2<String, Tuple2<String, Row>> tuple2) throws Exception {
                Row row = tuple2._2._2;

                ISesstionDetailDao sessionDetailDao = DaoFactory.getSessionDetailDao();
                SessionDetailEntity entity = new SessionDetailEntity();

                entity.setTaskId(Integer.valueOf(taskId.toString()));
                entity.setUserId((int) row.getLong(1));
                entity.setSessionId(row.getString(2));
                entity.setPageId((int) row.getLong(3));
                entity.setActionTime(row.getString(4));
                entity.setSearchKeyword(row.getString(5));
                entity.setClickCategoryId((int) row.getLong(6));
                entity.setClickProductId((int) row.getLong(7));
                entity.setOrderCategoryIds(row.getString(8));
                entity.setOrderProductIds(row.getString(9));
                entity.setPayCategoryIds(row.getString(10));
                entity.setPayProductIds(row.getString(11));

                sessionDetailDao.insert(entity);
            }
        });
    }


    private static List<Tuple2<TOP10CustomSortKey, Long>> getTop10Catagory(JavaPairRDD<String, String> filterSesstionFullAggrRDD, JavaRDD<Row> javaRDD, Long taskId) {

        JavaPairRDD<String, Row> filterSesstionDetailRDD = javaRDD.mapToPair(new PairFunction<Row, String, Row>() {
            @Override
            public Tuple2<String, Row> call(Row row) throws Exception {
                return new Tuple2<String, Row>(row.getString(2), row);
            }
        })
        .join(filterSesstionFullAggrRDD)
        .mapToPair(new PairFunction<Tuple2<String, Tuple2<Row, String>>, String, Row>() {
            @Override
            public Tuple2<String, Row> call(Tuple2<String, Tuple2<Row, String>> tuple2) throws Exception {
                return new Tuple2<String, Row>(tuple2._1, tuple2._2._1);
            }
        });

        System.out.println(filterSesstionDetailRDD.count());

        JavaPairRDD<Long, Long> catagoryIdsRDD = filterSesstionDetailRDD.flatMapToPair(new PairFlatMapFunction<Tuple2<String, Row>, Long, Long>() {
            @Override
            public Iterable<Tuple2<Long, Long>> call(Tuple2<String, Row> tuple2) throws Exception {

                Row row = tuple2._2;
                List<Tuple2<Long, Long>> result = new ArrayList<Tuple2<Long, Long>>();

                Long clickCatagoryId = row.getLong(6);
                if (clickCatagoryId != null) {
                    result.add(new Tuple2<>(clickCatagoryId, clickCatagoryId));
                }

                String orderCatagoryIds = row.getString(8);
                if (StringUtils.isNotEmpty(orderCatagoryIds)) {
                    String[] splited = orderCatagoryIds.split(",");
                    for (String orderCatagoryId : splited) {
                        result.add(new Tuple2<>(Long.valueOf(orderCatagoryId), Long.valueOf(orderCatagoryId)));
                    }
                }

                String payCatagoryIds = row.getString(10);
                if (StringUtils.isNotEmpty(payCatagoryIds)) {
                    String[] splited = payCatagoryIds.split(",");
                    for (String payCatagoryId : splited) {
                        result.add(new Tuple2<>(Long.valueOf(payCatagoryId), Long.valueOf(payCatagoryId)));
                    }
                }

                return result;
            }
        }).distinct();


        JavaPairRDD<Long, Long> clickCatagoryIdCountRDD = getClickCatagoryIdCountRDD(filterSesstionDetailRDD);
        JavaPairRDD<Long, Long> orderCatagoryIdCountRDD = getOrderCatagoryIdCountRDD(filterSesstionDetailRDD);
        JavaPairRDD<Long, Long> payCatagoryIdCountRDD = getPayCatagoryIdCountRDD(filterSesstionDetailRDD);


        JavaPairRDD<Long, String> joinedCatagoryIdCountRDD = catagoryIdsRDD.leftOuterJoin(clickCatagoryIdCountRDD).mapToPair(new PairFunction<Tuple2<Long, Tuple2<Long, Optional<Long>>>, Long, String>() {
            @Override
            public Tuple2<Long, String> call(Tuple2<Long, Tuple2<Long, Optional<Long>>> tuple2) throws Exception {

                Long catagoryId = tuple2._1;
                Optional<Long> clickCatagoryCountOptional = tuple2._2._2;

                String countStr = null;
                if (clickCatagoryCountOptional.isPresent()) {
                    countStr = Constant.FIELD_CLICK_CATAGORY_COUNT + "=" + clickCatagoryCountOptional.get();
                } else {
                    countStr = Constant.FIELD_CLICK_CATAGORY_COUNT + "=" + 0;
                }

                return new Tuple2<Long, String>(catagoryId, countStr);
            }
        });

        joinedCatagoryIdCountRDD = joinedCatagoryIdCountRDD.leftOuterJoin(orderCatagoryIdCountRDD).mapToPair(new PairFunction<Tuple2<Long, Tuple2<String, Optional<Long>>>, Long, String>() {
            @Override
            public Tuple2<Long, String> call(Tuple2<Long, Tuple2<String, Optional<Long>>> tuple2) throws Exception {
                Long catagoryId = tuple2._1;
                String countStr = tuple2._2._1;
                Optional<Long> orderCatagoryCountOptional = tuple2._2._2;

                if (orderCatagoryCountOptional.isPresent()){
                    countStr += "|" + Constant.FIELD_ORDER_CATAGORY_COUNT + "=" + orderCatagoryCountOptional.get();
                } else {
                    countStr += "|" + Constant.FIELD_ORDER_CATAGORY_COUNT + "=" + 0;
                }

                return new Tuple2<Long, String>(catagoryId, countStr);
            }
        });

        joinedCatagoryIdCountRDD = joinedCatagoryIdCountRDD.leftOuterJoin(payCatagoryIdCountRDD).mapToPair(new PairFunction<Tuple2<Long, Tuple2<String, Optional<Long>>>, Long, String>() {
            @Override
            public Tuple2<Long, String> call(Tuple2<Long, Tuple2<String, Optional<Long>>> tuple2) throws Exception {
                Long catagoryId = tuple2._1;
                String countStr = tuple2._2._1;
                Optional<Long> payCatagoryCountOptional = tuple2._2._2;

                if (payCatagoryCountOptional.isPresent()){
                    countStr += "|" + Constant.FIELD_PAY_CATAGORY_COUNT + "=" + payCatagoryCountOptional.get();
                } else {
                    countStr += "|" + Constant.FIELD_PAY_CATAGORY_COUNT + "=" + 0;
                }

                return new Tuple2<Long, String>(catagoryId, countStr);
            }
        });

        JavaPairRDD<TOP10CustomSortKey, Long> top10CustomSortKeyRDD = joinedCatagoryIdCountRDD.mapToPair(new PairFunction<Tuple2<Long, String>, TOP10CustomSortKey, Long>() {
            @Override
            public Tuple2<TOP10CustomSortKey, Long> call(Tuple2<Long, String> tuple2) throws Exception {
                Long catagoryId = tuple2._1;
                String countStr = tuple2._2;
                TOP10CustomSortKey key = new TOP10CustomSortKey();
                key.setClickCount(Long.valueOf(StringUtils.getFieldFromConcatString(countStr, "\\|", Constant.FIELD_CLICK_CATAGORY_COUNT)));
                key.setOrderCount(Long.valueOf(StringUtils.getFieldFromConcatString(countStr, "\\|", Constant.FIELD_ORDER_CATAGORY_COUNT)));
                key.setPayCount(Long.valueOf(StringUtils.getFieldFromConcatString(countStr, "\\|", Constant.FIELD_PAY_CATAGORY_COUNT)));

                return new Tuple2<TOP10CustomSortKey, Long>(key, catagoryId);
            }
        });

        JavaPairRDD<TOP10CustomSortKey, Long> sortedRDD = top10CustomSortKeyRDD.sortByKey(false);

        List<Tuple2<TOP10CustomSortKey, Long>> top10CatagoryId = sortedRDD.take(10);
        for (Tuple2<TOP10CustomSortKey, Long> tuple2 : top10CatagoryId){
            TOP10CustomSortKey top10CustomSortKey = tuple2._1;
            Long catagoryId = tuple2._2;
            ITop10categoryDao top10categoryDao = DaoFactory.getTop10categoryDao();
            Top10CategoryEntity entity = new Top10CategoryEntity();
            entity.setTaskId(taskId.intValue());
            entity.setCategoryId(catagoryId.intValue());
            entity.setClickCount(top10CustomSortKey.getClickCount().intValue());
            entity.setOrderCount(top10CustomSortKey.getOrderCount().intValue());
            entity.setPayCount(top10CustomSortKey.getPayCount().intValue());
            top10categoryDao.insert(entity);
        }
        
        return top10CatagoryId;

    }

    private static JavaPairRDD<Long, Long> getClickCatagoryIdCountRDD(JavaPairRDD<String, Row> filterSesstionDetailRDD) {
        JavaPairRDD<Long, Long> clickCatagoryIdCountRDD = filterSesstionDetailRDD.filter(new Function<Tuple2<String, Row>, Boolean>() {
            @Override
            public Boolean call(Tuple2<String, Row> tuple2) throws Exception {
                Row row = tuple2._2;
                Long clickCatagoryId = row.getLong(6);
                if (clickCatagoryId == null || clickCatagoryId == 0) {
                    return false;
                } else {
                    return true;
                }
            }
        }).mapToPair(new PairFunction<Tuple2<String, Row>, Long, Long>() {
            @Override
            public Tuple2<Long, Long> call(Tuple2<String, Row> tuple2) throws Exception {
                Row row = tuple2._2;
                Long catagoryId = row.getLong(6);
                return new Tuple2<Long, Long>(catagoryId, 1L);
            }
        }).reduceByKey(new Function2<Long, Long, Long>() {
            @Override
            public Long call(Long v1, Long v2) throws Exception {
                return v1 + v2;
            }
        });

        return clickCatagoryIdCountRDD;
    }

    private static JavaPairRDD<Long, Long> getOrderCatagoryIdCountRDD(JavaPairRDD<String, Row> filterSesstionDetailRDD) {
        JavaPairRDD<Long, Long> orderCatagoryIdCountRDD = filterSesstionDetailRDD.flatMapToPair(new PairFlatMapFunction<Tuple2<String, Row>, Long, Long>() {
            @Override
            public Iterable<Tuple2<Long, Long>> call(Tuple2<String, Row> tuple2) throws Exception {
                List<Tuple2<Long, Long>> result = new ArrayList<Tuple2<Long, Long>>();

                Row row = tuple2._2;
                String orderCatagoryIds = row.getString(8);
                if (StringUtils.isNotEmpty(orderCatagoryIds)) {
                    String[] splited = orderCatagoryIds.split(",");
                    for (String orderCatagoryId : splited) {
                        result.add(new Tuple2<Long, Long>(Long.valueOf(orderCatagoryId), 1L));
                    }
                }

                return result;
            }
        }).reduceByKey(new Function2<Long, Long, Long>() {
            @Override
            public Long call(Long v1, Long v2) throws Exception {
                return v1 + v2;
            }
        });

        return orderCatagoryIdCountRDD;
    }


    private static JavaPairRDD<Long, Long> getPayCatagoryIdCountRDD(JavaPairRDD<String, Row> filterSesstionDetailRDD) {
        JavaPairRDD<Long, Long> payCatagoryIdCountRDD = filterSesstionDetailRDD.flatMapToPair(new PairFlatMapFunction<Tuple2<String, Row>, Long, Long>() {
            @Override
            public Iterable<Tuple2<Long, Long>> call(Tuple2<String, Row> tuple2) throws Exception {
                List<Tuple2<Long, Long>> result = new ArrayList<Tuple2<Long, Long>>();

                Row row = tuple2._2;
                String payCatagoryIds = row.getString(10);
                if (StringUtils.isNotEmpty(payCatagoryIds)) {
                    String[] splited = payCatagoryIds.split(",");
                    for (String payCatagoryId : splited) {
                        result.add(new Tuple2<Long, Long>(Long.valueOf(payCatagoryId), 1L));
                    }
                }

                return result;
            }
        }).reduceByKey(new Function2<Long, Long, Long>() {
            @Override
            public Long call(Long v1, Long v2) throws Exception {
                return v1 + v2;
            }
        });

        return payCatagoryIdCountRDD;
    }


    private static void getTop10CategoryClickSesstion(JavaSparkContext sc, List<Tuple2<TOP10CustomSortKey, Long>> top10Category, JavaPairRDD<String, String> filterSesstionFullAggrRDD, JavaRDD<Row> javaRDD, Long taskId) {
    
        // 1. 得到过滤后的session原始数据
        JavaPairRDD<String, Row> sessionDetailRDD = javaRDD.mapToPair(new PairFunction<Row, String, Row>() {
            @Override
            public Tuple2<String, Row> call(Row row) throws Exception {
                return new Tuple2<String, Row>(row.getString(2), row);
            }
        });
        JavaPairRDD<String, Row> filteredSessionRDD = filterSesstionFullAggrRDD.join(sessionDetailRDD).mapToPair(new PairFunction<Tuple2<String, Tuple2<String, Row>>, String, Row>() {
            @Override
            public Tuple2<String, Row> call(Tuple2<String, Tuple2<String, Row>> tuple2) throws Exception {
                return new Tuple2<String, Row>(tuple2._1, tuple2._2._2);
            }
        });

        // 2. 计算每个session对不同品类的点击次数
        JavaPairRDD<Long, String> sesstionClickCountRDD = filteredSessionRDD.groupByKey().flatMapToPair(new PairFlatMapFunction<Tuple2<String, Iterable<Row>>, Long, String>() {
            @Override
            public Iterable<Tuple2<Long, String>> call(Tuple2<String, Iterable<Row>> tuple2) throws Exception {
                String sessionId = tuple2._1;
                Iterator<Row> iterator = tuple2._2.iterator();

                Map<Long, Long> clickCountMap = new HashMap<>();
                while (iterator.hasNext()) {
                    Row row = iterator.next();
                    if (row.get(6) != null) {
                        Long clickCategoryId = row.getLong(6);

                        Long clickCount = clickCountMap.get(clickCategoryId);
                        if (clickCount == null) {
                            clickCount = 0L;
                        }

                        clickCount++;
                        clickCountMap.put(clickCategoryId, clickCount);
                    }
                }

                List<Tuple2<Long, String>> resultList = new ArrayList<Tuple2<Long, String>>();
                for (Map.Entry<Long, Long> entry : clickCountMap.entrySet()) {
                    Long categoryId = entry.getKey();
                    Long clickCount = entry.getValue();

                    resultList.add(new Tuple2<>(categoryId, sessionId + ":" + clickCount));
                }

                return resultList;
            }
        });

        sesstionClickCountRDD.foreach(new VoidFunction<Tuple2<Long, String>>() {
            @Override
            public void call(Tuple2<Long, String> tuple2) throws Exception {
                System.out.println(tuple2._1 + ": " + tuple2._2);
            }
        });
        
        // 3. 将统计出的session对不同品类点击次数的RDD 与 TOP10品类RDD join 得出TOP10品类下所有sesstion的点击次数
        JavaPairRDD<Long, Long> top10CategoryRDD = sc.parallelizePairs(top10Category).mapToPair(new PairFunction<Tuple2<TOP10CustomSortKey, Long>, Long, Long>() {
            @Override
            public Tuple2<Long, Long> call(Tuple2<TOP10CustomSortKey, Long> tuple2) throws Exception {
                return new Tuple2<Long, Long>(tuple2._2, tuple2._2);
            }
        });

        // 4. 统计每个品类下top10的session点击次数，并返回sessionId
        JavaPairRDD<String, String> top10CategorySesstionIdsRDD = top10CategoryRDD.join(sesstionClickCountRDD).groupByKey().flatMapToPair(new PairFlatMapFunction<Tuple2<Long, Iterable<Tuple2<Long, String>>>, String, String>() {
            @Override
            public Iterable<Tuple2<String, String>> call(Tuple2<Long, Iterable<Tuple2<Long, String>>> tuple2) throws Exception {
                Long categoryId = tuple2._1;
                Iterator<Tuple2<Long, String>> iterator = tuple2._2.iterator();
                String[] top10Array = new String[10];

                while (iterator.hasNext()) {
                    Tuple2<Long, String> tuple = iterator.next();
                    String sessionClickCount = tuple._2;
                    if (StringUtils.isNotEmpty(sessionClickCount)) {
                        String[] splited = sessionClickCount.split(":");
                        String clickCount = splited[1];

                        for (int x = 0; x < top10Array.length; x++) {

                            if (top10Array[x] == null) {
                                top10Array[x] = sessionClickCount;
                                break;
                            } else {
                                String thatClickCount = top10Array[x].split(":")[1];
                                if (Long.valueOf(clickCount) > Long.valueOf(thatClickCount)) {
                                    for (int y = 9; y > x; y--) {
                                        top10Array[y] = top10Array[y - 1];
                                    }
                                    top10Array[x] = sessionClickCount;
                                    break;
                                }
                            }
                        }
                    }
                }

                List<Tuple2<String, String>> resultList = new ArrayList<Tuple2<String, String>>();

                for (String top10SessionClickCount : top10Array) {
                    ITop10CategorySessionDao top10CategorySesstionDao = DaoFactory.getTop10CategorySesstionDao();
                    Top10CategorySessionEntity entity = new Top10CategorySessionEntity();
                    String[] splited = top10SessionClickCount.split(":");
                    String sessionId = splited[0];
                    String clickCount = splited[1];

                    entity.setTaskId(taskId.intValue());
                    entity.setCategoryId(categoryId.intValue());
                    entity.setSessionId(sessionId);
                    entity.setClickCount(Integer.valueOf(clickCount));
                    top10CategorySesstionDao.insert(entity);

                    resultList.add(new Tuple2<>(sessionId, sessionId));
                }
                return resultList;
            }
        });

        // 5.根据返回的sessionIds join 明细数据 插入mysql
        top10CategorySesstionIdsRDD.join(sessionDetailRDD).foreach(new VoidFunction<Tuple2<String, Tuple2<String, Row>>>() {
            @Override
            public void call(Tuple2<String, Tuple2<String, Row>> tuple2) throws Exception {
                Row row = tuple2._2._2;

                ISesstionDetailDao sessionDetailDao = DaoFactory.getSessionDetailDao();
                SessionDetailEntity entity = new SessionDetailEntity();

                entity.setTaskId(taskId.intValue());
                entity.setUserId((int) row.getLong(1));
                entity.setSessionId(row.getString(2));
                entity.setPageId((int) row.getLong(3));
                entity.setActionTime(row.getString(4));
                entity.setSearchKeyword(row.getString(5));
                entity.setClickCategoryId((int) row.getLong(6));
                entity.setClickProductId((int) row.getLong(7));
                entity.setOrderCategoryIds(row.getString(8));
                entity.setOrderProductIds(row.getString(9));
                entity.setPayCategoryIds(row.getString(10));
                entity.setPayProductIds(row.getString(11));

                sessionDetailDao.insert(entity);
            }
        });

    }
    
}
