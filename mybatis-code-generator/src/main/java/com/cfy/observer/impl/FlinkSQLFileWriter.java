package com.cfy.observer.impl;

import com.cfy.observer.FileWriter;
import com.cfy.observer.Observable;
import com.cfy.trait.FreeMarkerBuilder;
import com.cfy.utils.FileUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlinkSQLFileWriter extends FreeMarkerBuilder implements FileWriter {

    public FlinkSQLFileWriter(Observable observable){
        observable.registerObserver(this);
    }

    @Override
    public void write(Object data) {
        // 封装参数
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("entityList", data);

        // 构建获取数据
        String dataString = super.build(paramMap, "flinksql.ftl");

        // 写文件
        String rootPath=getClass().getResource("/").getFile().toString();
        String filePath = rootPath.substring(0, rootPath.indexOf(PROJECT_NAME) + PROJECT_NAME.length()) + File.separator + "auto_create" + File.separator + "flinksql";
        List<Map<String, String>> list = (List<Map<String, String>>) data;
        String fileName =list.get(0).get("tableName");
        FileUtil.write(filePath, fileName, dataString.getBytes());
    }
}
