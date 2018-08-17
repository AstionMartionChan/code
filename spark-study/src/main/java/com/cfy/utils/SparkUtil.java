package com.cfy.utils;

import com.alibaba.fastjson.JSONObject;
import com.cfy.bean.TaskEntity;
import com.cfy.dao.ITaskDao;
import com.cfy.dao.factory.DaoFactory;
import com.cfy.test.MockData;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.hive.HiveContext;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/7/10
 * Time: 15:08
 * Work contact: Astion_Leo@163.com
 */


public class SparkUtil {

    public static void setMaster(SparkConf sparkConf) {
        Boolean isLocal = ConfigurationManager.getBoolean("spark.local");
        if (isLocal){
            sparkConf.setMaster("local");
        } else {

        }
    }

    public static SQLContext getSQLContext(SparkContext sc) {
        Boolean isLocal = ConfigurationManager.getBoolean("spark.local");
        if (isLocal){
            return new SQLContext(sc);
        } else {
            return new HiveContext(sc);
        }
    }

    public static void mockData(JavaSparkContext sc, SQLContext sqlContext) {
        Boolean isLocal = ConfigurationManager.getBoolean("spark.local");
        if (isLocal) {
            MockData.mock(sc, sqlContext);
        }
    }

    public static JSONObject getParams(String args[]) {
        Long taskId = ParamUtils.getTaskIdFromArgs(args);
        ITaskDao taskDao = DaoFactory.getTaskDao();
        TaskEntity taskEntity = taskDao.findById(Integer.valueOf(taskId.toString()));
        return JSONObject.parseObject(taskEntity.getTaskParam());
    }
}
