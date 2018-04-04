package com.cfy.trait;

import com.cfy.entity.Column;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/4/1
 * Time: 10:43
 * Work contact: Astion_Leo@163.com
 */


public class Converter {

    List<Processable> processList = new ArrayList<>();

    public Map<String, Object> converterToMap(Map<String, Object> tableInfoMap){
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry entry : tableInfoMap.entrySet()){
            String tableName = entry.getKey().toString();
            List<Column> columnList = (List<Column>) entry.getValue();
            List<Map<String, String>> list = new ArrayList<>(columnList.size());
            for (Column column : columnList){
                Map<String, String> proccessed = new HashMap<>();

                // 转换处理
                for (Processable expandProcess : processList){
                    proccessed.putAll(expandProcess.process(column));
                }

                list.add(proccessed);
            }
            result.put(tableName, list);
        }
        return result;
    }


    public void addProcess(Processable processable) {
        this.processList.add(processable);
    }
}
