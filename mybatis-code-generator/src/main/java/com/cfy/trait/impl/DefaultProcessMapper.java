package com.cfy.trait.impl;

import com.cfy.entity.Column;
import com.cfy.trait.Processable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/4/1
 * Time: 15:16
 * Work contact: Astion_Leo@163.com
 */


public class DefaultProcessMapper implements Processable {

    @Override
    public Map<String, String> process(Column column) {
        Map<String, String> map = new HashMap<>();
        String xmlName = column.getTableName().replaceAll("_", ".") + ".mapping.xml";
        map.put("xmlName", xmlName);
        map.put("columnName", column.getColumnName());
        map.put("tableName", column.getTableName());
        return map;
    }

}
