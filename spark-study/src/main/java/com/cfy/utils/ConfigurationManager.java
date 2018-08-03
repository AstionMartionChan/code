package com.cfy.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/6/30
 * Time: 22:06
 * Work contact: Astion_Leo@163.com
 */


public class ConfigurationManager {


    private static Properties properties = new Properties();

    static {
        try {
            InputStream inputStream = ConfigurationManager.class.getClassLoader().getResourceAsStream("my.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String getString(String key) {
        String value = properties.getProperty(key);
        return value;
    }


    public static Integer getInteger(String key) {

        try {
            String value = properties.getProperty(key);
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Boolean getBoolean(String key) {
        try {
            String value = properties.getProperty(key);
            return Boolean.valueOf(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
