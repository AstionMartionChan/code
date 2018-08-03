package com.cfy.constants;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/6/30
 * Time: 22:18
 * Work contact: Astion_Leo@163.com
 */


public interface Constant {

    /**
     * 环境配置相关
     */
    String JDBC_DRIVER = "jdbc_driver";
    String JDBC_URL = "jdbc_url";
    String JDBC_USERNAME = "jdbc_username";
    String JDBC_PASSWORD = "jdbc_password";
    String CONNECTION_NUM = "connection_num";
    String SPARK_LOCAL = "spark.local";


    /**
     * spark作业相关
     */
    String SPARK_APP_NAME_SESSTION = "usr_visit_sesstion_analyze_job";
    String SPARK_APP_NAME_PAGE = "page_convert_rate_analyze_job";
    String SPARK_APP_NAME_PRODUCT = "area_product_top3_analyze_job";
    String FIELD_SESSTION_ID = "sesstionId";
    String FIELD_CLICK_CATALOGS = "clickCatalogs";
    String FIELD_SEARCH_KEYWORDS = "searchKeywords";
    String FIELD_AGE = "age";
    String FIELD_PROFESSIONAL = "professional";
    String FIELD_CITY = "city";
    String FIELD_SEX = "sex";
    String FIELD_TIME_PERIOD = "timePeriod";
    String FIELD_STEP_PERIOD = "stepPeriod";
    String FIELD_START_TIME = "startTime";
    String FIELD_CLICK_CATAGORY_COUNT = "clickCatagoryCount";
    String FIELD_ORDER_CATAGORY_COUNT = "clickOrderCount";
    String FIELD_PAY_CATAGORY_COUNT = "clickPayCount";


    String SESSION_COUNT = "sesstionCount";
    String TIME_PERIOD_1s_3s = "1s_3s";
    String TIME_PERIOD_4s_6s = "4s_6s";
    String TIME_PERIOD_7s_9s = "7s_9s";
    String TIME_PERIOD_10s_30s = "10s_30s";
    String TIME_PERIOD_30s_60s = "30s_60s";
    String TIME_PERIOD_1m_3m = "1m_3m";
    String TIME_PERIOD_3m_10m = "3m_10m";
    String TIME_PERIOD_10m_30m = "10m_30m";
    String TIME_PERIOD_30m = "30m";
    String STEP_PERIOD_1_3 = "1_3";
    String STEP_PERIOD_4_6 = "4_6";
    String STEP_PERIOD_7_9 = "7_9";
    String STEP_PERIOD_10_30 = "10_30";
    String STEP_PERIOD_30_60 = "30_60";
    String STEP_PERIOD_60 = "60";

    String PARAM_START_DATE = "startDate";
    String PARAM_END_DATE = "endDate";
    String PARAM_START_AGE = "startAge";
    String PARAM_END_AGE = "endAge";
    String PARAM_PROFESSIONALS = "professionals";
    String PARAM_CITIES = "city";
    String PARAM_SEX = "sex";
    String PARAM_KEYWORDS = "searchKeywords";
    String PARAM_CATEGORY_IDS = "catagoryIds";
    String PARAM_PAGE_LIST = "pageList";
}
