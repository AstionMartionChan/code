package com.cfy.trait.impl;

import com.cfy.entity.Column;
import com.cfy.trait.Processable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/4/1
 * Time: 15:14
 * Work contact: Astion_Leo@163.com
 */


public class DefaultProcessJavaType implements Processable {

    @Override
    public Map<String, String> process(Column column) {
        Map<String, String> map = new HashMap<>();
        String columnType = column.getColumnType().toUpperCase();
        String javaType = null;
        switch (columnType){
            case "VARCHAR":
            case "VARCHAR2":
            case "CHAR":
                javaType = "java.lang.String";
                break;
            case "TINYINT":
            case "BIT":
                javaType = "java.lang.Boolean";
                break;
            case "INT":
            case "SMALLINT":
            case "INTEGER":
                javaType = "java.lang.Integer";
                break;
            case "BIGINT":
                javaType = "java.lang.Long";
                break;
            case "DATETIME":
            case "TIMESTAMP":
            case "DATE":
                javaType = "java.util.Date";
                break;
            case "DOUBLE":
            case "DECIMAL":
                javaType = "java.lang.Double";
                break;
            default:
                javaType = "java.lang.String";
                break;
        }

        String fieldType = javaType.substring(javaType.lastIndexOf(".") + 1, javaType.length());
        map.put("javaType", javaType);
        map.put("fieldType", fieldType);

        return map;
    }

}
