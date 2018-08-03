package com.cfy.dao.impl;

import com.cfy.bean.Top10CategoryEntity;
import com.cfy.dao.ITop10categoryDao;
import com.cfy.utils.JDBCHelper;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/7/4
 * Time: 15:34
 * Work contact: Astion_Leo@163.com
 */


public class Top10categoryDaoImpl implements ITop10categoryDao {
    @Override
    public void insert(Top10CategoryEntity entity) {
        JDBCHelper jdbcHelper = JDBCHelper.getJdbcHelper();
        String sql = "INSERT INTO top10_category (\n" +
                "            task_id,\n" +
                "            category_id,\n" +
                "            click_count,\n" +
                "            order_count,\n" +
                "            pay_count\n" +
                "        )\n" +
                "        VALUES (\n" +
                "            ?,\n" +
                "            ?,\n" +
                "            ?,\n" +
                "            ?,\n" +
                "            ?\n" +
                "        )";
        Object[] params = new Object[]{entity.getTaskId(), entity.getCategoryId(), entity.getClickCount(), entity.getOrderCount(), entity.getPayCount()};
        jdbcHelper.prepareUpdate(sql, params);
    }
}
