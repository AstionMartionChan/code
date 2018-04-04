package com.cfy.trait.impl;

import com.cfy.entity.Column;
import com.cfy.trait.Processable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/4/2
 * Time: 11:26
 * Work contact: Astion_Leo@163.com
 */


public class DefaultProcessRemark implements Processable {

    @Override
    public Map<String, String> process(Column column) {
        Map<String, String> map = new HashMap<>();
        map.put("remark", column.getRemark());
        return map;
    }
}
