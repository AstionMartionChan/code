package com.cfy.trait.impl;

import com.cfy.entity.Column;
import com.cfy.trait.Processable;

import java.util.HashMap;
import java.util.Map;

public class DefaultProcessColumnType implements Processable {
    @Override
    public Map<String, String> process(Column column) {
        Map<String, String> map = new HashMap<>();
        String columnType = column.getColumnType().toUpperCase();
        switch (columnType){
            case "DECIMAL":
                columnType = "DOUBLE";
                break;
            case "DATETIME":
                columnType = "VARCHAR";
                break;
            case "CHAR":
                columnType = "VARCHAR";
                break;
            default:
                columnType = columnType;
                break;
        }

        map.put("columnType", columnType);
        return map;
    }
}
