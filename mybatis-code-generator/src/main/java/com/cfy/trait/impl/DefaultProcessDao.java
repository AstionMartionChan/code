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
 * Time: 15:08
 * Work contact: Astion_Leo@163.com
 */


public class DefaultProcessDao extends HumpProcess implements Processable {

    @Override
    public Map<String, String> process(Column column) {
        Map<String, String> map = new HashMap<>();
        String humpName = super.toHumpName(column.getTableName());
        String daoName = "I" + humpName.substring(0, 1).toUpperCase() + humpName.substring(1) + "Dao";
        map.put("daoName", daoName);
        map.put("daoClassName", daoName + ".java");

        return map;
    }

}
