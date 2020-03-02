package com.cfy.trait.impl;

import com.cfy.entity.Column;
import com.cfy.trait.Processable;

import java.util.HashMap;
import java.util.Map;

public class DefaultProcessFlinkUDFName implements Processable {
    @Override
    public Map<String, String> process(Column column) {
        Map<String, String> map = new HashMap<>();
        String columnType = column.getColumnType().toUpperCase();
        String udfName = null;
        switch (columnType){
            case "VARCHAR":
            case "VARCHAR2":
            case "CHAR":
            case "DATETIME":
            case "DATE":
                udfName = "blcsExtractVarcharValue";
                break;
            case "TINYINT":
            case "INT":
            case "SMALLINT":
            case "INTEGER":
                udfName = "blcsExtractIntValue";
                break;
            case "BIGINT":
            case "TIMESTAMP":
                udfName = "blcsExtractBigintValue";
                break;
            case "DOUBLE":
            case "DECIMAL":
                udfName = "blcsExtractDoubleValue";
                break;
            default:
                udfName = "blcsExtractVarcharValue";
                break;
        }

        map.put("BLCSUdfName", udfName);

        return map;
    }
}
