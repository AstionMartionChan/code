package com.cfy.trait.impl;

import com.cfy.entity.Column;
import com.cfy.trait.Processable;
import com.cfy.utils.PropertiesUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/4/3
 * Time: 17:57
 * Work contact: Astion_Leo@163.com
 */


public class DefaultProcessPackage implements Processable {


    @Override
    public Map<String, String> process(Column column) {
        Map<String, String> map = new HashMap<>();
        String packagePath = PropertiesUtil.getProperty("package.path");
        map.put("packagePath", packagePath);
        return map;
    }
}
