package com.cfy.udf;

import org.apache.spark.sql.api.java.UDF3;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/7/11
 * Time: 21:10
 * Work contact: Astion_Leo@163.com
 */


/**
 * 自定义UDF函数
 */
public class ConcatUDF implements UDF3<Long, String, String, String> {

    /**
     * 返回拼接的内容
     * @param value1    UDF函数第一个参数
     * @param value2    UDF函数第二个参数
     * @param value3    UDF函数第三个参数
     * @return
     * @throws Exception
     */
    @Override
    public String call(Long value1, String value2, String value3) throws Exception {
        return value1 + value3 + value2;
    }
}
