package com.kingnetdc.blueberry.core.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author jake.zhang <zhangxj@kingnet.com>
 */
public class DateTime {

    private static final Logger logger = LoggerFactory.getLogger(DateTime.class);

    /**
     * 根据时间获取时间字符串
     * @param timestamp
     * @return
     */
    public static String dateTime(long timestamp) {
        return dateTime(timestamp, "yyyy-MM-dd");
    }

    public static String dateTime(long timestamp, String formatter) {
        return dateTime(timestamp, formatter, ZoneId.systemDefault());
    }

    public static String dateTime(long timestamp, String formatter, ZoneId zoneId) {
        try {
            return Instant.ofEpochSecond(timestamp).atZone(zoneId).format(DateTimeFormatter.ofPattern(formatter));
        } catch (Throwable e) {
            logger.error("date time change error.", e);
        }
        return null;
    }

    public static String dateDiff(String dateTime, int days) {
        return dateDiff(dateTime, days, "yyyy-MM-dd");
    }

    public static String dateDiff(String dateTime, int days, String formatter) {
        try {
            LocalDate localDate = LocalDate.parse(dateTime, DateTimeFormatter.ofPattern(formatter));
            return localDate.plusDays(days).toString();
        } catch (Throwable e) {
            logger.error("date diff error.", e);
        }
        return null;
    }

    public static Long timestamp(String dateTime) {
        return timestamp(dateTime, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 依赖系统的 时间 Zone ID
     * @param dateTime
     * @param formatter
     * @return
     */
    public static Long timestamp(String dateTime, String formatter) {
        return timestamp(dateTime, formatter, ZoneId.systemDefault());
    }

    /**
     * 可以自定义设置时区
     * @param dateTime
     * @param formatter
     * @param zoneId
     * @return
     */
    public static Long timestamp(String dateTime, String formatter, ZoneId zoneId) {
        try {
            LocalDateTime local = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(formatter));
            return local.atZone(zoneId).toEpochSecond();
        } catch (Throwable e) {
            logger.error("parse datetime to timestamp error.", e);
        }
        return null;
    }

}
