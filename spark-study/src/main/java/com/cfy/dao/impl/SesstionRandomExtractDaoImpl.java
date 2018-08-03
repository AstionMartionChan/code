package com.cfy.dao.impl;

import com.cfy.bean.SessionRandomExtractEntity;
import com.cfy.dao.ISesstionRandomExtractDao;
import com.cfy.utils.JDBCHelper;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/7/3
 * Time: 16:17
 * Work contact: Astion_Leo@163.com
 */


public class SesstionRandomExtractDaoImpl implements ISesstionRandomExtractDao {


    @Override
    public void insert(SessionRandomExtractEntity entity) {
        JDBCHelper jdbcHelper = JDBCHelper.getJdbcHelper();

        String sql = "INSERT INTO session_random_extract (\n" +
                "            task_id,\n" +
                "            session_id,\n" +
                "            start_time,\n" +
                "            search_keywords,\n" +
                "            catagory_ids\n" +
                "        )\n" +
                "        VALUES (\n" +
                "            ?,\n" +
                "            ?,\n" +
                "            ?,\n" +
                "            ?,\n" +
                "            ?\n" +
                "        )";
        Object[] params = new Object[]{entity.getTaskId(), entity.getSessionId(), entity.getStartTime(), entity.getSearchKeywords(), entity.getCatagoryIds()};
        jdbcHelper.prepareUpdate(sql, params);
    }


}
