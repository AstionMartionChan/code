package com.cfy.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/4/3
 * Time: 17:46
 * Work contact: Astion_Leo@163.com
 */


public class PropertiesUtil {

    private static Properties prop;

    static {
        prop = new Properties();
        InputStream inputStream = PropertiesUtil.class.getResourceAsStream("/systemConfig.properties");
        try {
            prop.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String getProperty(String key) {
        String value = prop.getProperty(key);
        if (null != value){
            value.trim();
        }
        return value;
    }

}
