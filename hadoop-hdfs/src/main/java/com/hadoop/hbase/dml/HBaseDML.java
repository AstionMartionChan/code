package com.hadoop.hbase.dml;



import com.hadoop.hbase.utils.HBaseUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Put;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/5/15
 * Time: 20:05
 * Work contact: Astion_Leo@163.com
 */


public class HBaseDML {

    public static void main(String[] args) throws IOException {
        InputStream resourceAsStream = HBaseDML.class.getResourceAsStream("/members.data");
        List<String> lines = IOUtils.readLines(resourceAsStream);
        List<Put> putList = new ArrayList<>();
        for (String line : lines){
            String[] splited = StringUtils.split(line, "|");
            String id = splited[0];
            String name = splited[1];
            String address = splited[2];
            String startDate = splited[3];
            String endDate = splited[4];

            putList.add(HBaseUtil.put(id, "cf1", "name", name));
            putList.add(HBaseUtil.put(id, "cf1", "address", address));
            putList.add(HBaseUtil.put(id, "cf1", "startDate", startDate));
            putList.add(HBaseUtil.put(id, "cf1", "endDate", endDate));
        }

        HBaseUtil.put2HBase("t_member", putList);
    }



}
