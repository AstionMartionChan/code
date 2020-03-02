package com.cfy.application;

import com.cfy.observer.Observable;
import com.cfy.observer.impl.DaoFileWriter;
import com.cfy.observer.impl.EntityFileWriter;
import com.cfy.observer.impl.FlinkSQLFileWriter;
import com.cfy.observer.impl.MapperFileWriter;
import com.cfy.trait.Converter;
import com.cfy.trait.DBReader;
import com.cfy.trait.impl.*;
import com.cfy.utils.MySqlUtil;
import com.cfy.utils.FileUtil;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/3/31
 * Time: 15:05
 * Work contact: Astion_Leo@163.com
 */


public class Generator {


    public static void main(String[] args) {

        // 读数据库表及其字段
        DBReader dbReader = new MysqlDBReader(MySqlUtil.getConnection());
        Map<String, Object> tableInfoMap = dbReader.readTableColumn(dbReader.readTable());


        // 转换处理
        Converter converter = new Converter();
        converter.addProcess(new DefaultProcessEntity());
        converter.addProcess(new DefaultProcessDao());
        converter.addProcess(new DefaultProcessRepositoryDao());
        converter.addProcess(new DefaultProcessField());
        converter.addProcess(new DefaultProcessJavaType());
        converter.addProcess(new DefaultProcessMapper());
        converter.addProcess(new DefaultProcessRemark());
        converter.addProcess(new DefaultProcessPackage());
        converter.addProcess(new DefaultProcessColumnType());
        converter.addProcess(new DefaultProcessFlinkUDFName());
        Map<String, Object> processedTableInfoMap = converter.converterToMap(tableInfoMap);


        // 写文件
        Observable observable = new Observable();
        new DaoFileWriter(observable);
        new EntityFileWriter(observable);
        new MapperFileWriter(observable);
        new FlinkSQLFileWriter(observable);

        for (Map.Entry<String, Object> entry : processedTableInfoMap.entrySet()){
            List<Map<String, String>> listData = (List<Map<String, String>>)entry.getValue();
            observable.write(listData);
        }

    }


}
