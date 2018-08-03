package com.cfy.dao.impl;

import com.cfy.bean.Top10CategorySessionEntity;
import com.cfy.dao.ITop10CategorySessionDao;
import com.cfy.utils.JDBCHelper;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/7/4
 * Time: 22:15
 * Work contact: Astion_Leo@163.com
 */


public class Top10CategorySessionDaoImpl implements ITop10CategorySessionDao {
    @Override
    public void insert(Top10CategorySessionEntity entity) {
        JDBCHelper jdbcHelper = JDBCHelper.getJdbcHelper();
        Object[] params = new Object[]{entity.getTaskId(), entity.getCategoryId(), entity.getSessionId(), entity.getClickCount()};
        String sql = "INSERT INTO top10_category_session (\n" +
                "            task_id,\n" +
                "            category_id,\n" +
                "            session_id,\n" +
                "            click_count\n" +
                "        )\n" +
                "        VALUES (\n" +
                "            ?,\n" +
                "            ?,\n" +
                "            ?,\n" +
                "            ?\n" +
                "        )";
        jdbcHelper.prepareUpdate(sql, params);
    }
}
