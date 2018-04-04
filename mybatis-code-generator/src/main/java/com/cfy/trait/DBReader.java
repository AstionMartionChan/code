package com.cfy.trait;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/3/31
 * Time: 15:10
 * Work contact: Astion_Leo@163.com
 */


public interface DBReader {

    List<String> readTable();

    Map<String, Object> readTableColumn(List<String> tableNames);

}
