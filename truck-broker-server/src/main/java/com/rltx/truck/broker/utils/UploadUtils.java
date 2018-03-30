package com.rltx.truck.broker.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Leo_Chan on 2017/3/10.
 */
public class UploadUtils {

    public static String getFormatTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date()).toString();
    }

    public static String getFormatTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(date).toString();
    }

    /**
     * 格式化日期 按照yyyyMMddHHmmss格式输出
     * @param   obj 日期
     * @return  yyyyMMddHHmmss格式字符串
     */
    public static String getFormatTime(Object obj) {

        if (obj instanceof Date){
            Date date = (Date) obj;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            return sdf.format(date).toString();
        }

        if (obj instanceof String){
            return obj.toString().replaceAll("-", "").replaceAll(":", "").trim() + "000000";
        }

        return null;
    }


    public static String getUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 格式化数字 默认格式：0.000
     * @param number 数字
     * @return 数字字符串
     */
    public static String formatNumber(Double number) {
       return UploadUtils.formatNumber(number, "0.000");
    }

    /**
     * 格式化数字
     * @param number 数字
     * @param pattern 格式
     * @return 数字字符串
     */
    public static String formatNumber(Double number, String pattern) {
        if (number == null) {
            return null;
        }

        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.applyPattern(pattern);
        return decimalFormat.format(number);
    }
}
