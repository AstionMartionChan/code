package com.itcast.utils;

import com.itcast.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/6/25
 * Time: 10:23
 * Work contact: Astion_Leo@163.com
 */


public class ReflectUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(ReflectUtil.class);

    public static<T> T newObj(Map<String, Object> source, Class<T> target) {
        T t = null;
        try {
            t = target.newInstance();
            Field[] declaredFields = target.getDeclaredFields();
            Method[] methods = target.getMethods();

            for (Field field : declaredFields){
                String fieldName = field.getName();
                String setMethodName = setMethodName(fieldName);
                if (!checkMethodExist(methods, setMethodName)){
                    LOGGER.error("field {} not found setting method ......", fieldName);
                    continue;
                }

                Method method = target.getMethod(setMethodName, field.getType());
                Object value = source.get(fieldName);
                if (null != value){
                    String fieldType = field.getType().getSimpleName();
                    if ("String".equals(fieldType)){
                        method.invoke(t, value.toString());
                    } else if ("Date".equals(fieldType)){
                        Date date = parseDate(value.toString());
                        method.invoke(t, date);
                    } else if ("Integer".equalsIgnoreCase(fieldType)){
                        method.invoke(t, Integer.valueOf(value.toString()));
                    } else if ("Double".equalsIgnoreCase(fieldType)){
                        method.invoke(t, Double.valueOf(value.toString()));
                    } else if ("Boolean".equalsIgnoreCase(fieldType)){
                        method.invoke(t, Boolean.valueOf(value.toString()));
                    } else if ("Long".equalsIgnoreCase(fieldType)){
                        method.invoke(t, Long.valueOf(value.toString()));
                    } else {
                        LOGGER.error("not supper type {} ......", fieldType);
                    }
                }


            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


        return t;
    }


    private static String setMethodName(String fieldName) {
        return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    private static Boolean checkMethodExist(Method[] methods, String methodName) {
        for (Method method : methods){
            if (method.getName().equals(methodName)){
                return true;
            }
        }
        return false;
    }


    private static Date parseDate(String datestr) {
        if (null == datestr || "".equals(datestr)) {
            return null;
        }
        try {
            String fmtstr = null;
            if (datestr.indexOf(':') > 0) {
                fmtstr = "yyyy-MM-dd HH:mm:ss";
            } else {
                fmtstr = "yyyy-MM-dd";
            }
            SimpleDateFormat sdf = new SimpleDateFormat(fmtstr, Locale.UK);
            return sdf.parse(datestr);
        } catch (Exception e) {
            return null;
        }
    }
}
