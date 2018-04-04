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
 * Time: 16:49
 * Work contact: Astion_Leo@163.com
 */


public class DefaultProcessResult extends HumpProcess implements Processable {

    @Override
    public Map<String, String> process(Column column) {
        Map<String, String> map = new HashMap<>();
        map.put("resultName", super.toHumpName(column.getColumnName()) + "Result");
        return map;
    }

}
