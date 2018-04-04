package com.cfy.trait.impl;

import com.cfy.entity.Column;
import com.cfy.trait.HumpProcess;
import com.cfy.trait.Processable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/4/2
 * Time: 11:23
 * Work contact: Astion_Leo@163.com
 */


public class DefaultProcessField extends HumpProcess implements Processable {

    @Override
    public Map<String, String> process(Column column) {
        Map<String, String> map = new HashMap<>();
        String fieldName = super.toHumpName(column.getColumnName());
        String upperFieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        map.put("fieldName", fieldName);
        map.put("upperFieldName", upperFieldName);

        return map;
    }

}
