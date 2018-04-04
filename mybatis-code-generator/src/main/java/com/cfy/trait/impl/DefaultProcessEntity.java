package com.cfy.trait.impl;

import com.cfy.entity.Column;
import com.cfy.trait.HumpProcess;
import com.cfy.trait.Processable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/4/1
 * Time: 14:59
 * Work contact: Astion_Leo@163.com
 */


public class DefaultProcessEntity extends HumpProcess implements Processable {

    @Override
    public Map<String, String> process(Column column) {
        Map<String, String> map = new HashMap<>();
        String humpName = super.toHumpName(column.getTableName());
        String entityName = humpName.substring(0, 1).toUpperCase() + humpName.substring(1) + "Entity";
        map.put("entityName", entityName);
        map.put("entityClassName", entityName + ".java");

        return map;
    }

}
