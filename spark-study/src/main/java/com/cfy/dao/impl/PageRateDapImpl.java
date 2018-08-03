package com.cfy.dao.impl;

import com.cfy.bean.PageRateEntity;
import com.cfy.dao.IPageRateDao;
import com.cfy.utils.JDBCHelper;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/7/10
 * Time: 16:44
 * Work contact: Astion_Leo@163.com
 */


public class PageRateDapImpl implements IPageRateDao {


    @Override
    public void insert(PageRateEntity entity) {
        JDBCHelper jdbcHelper = JDBCHelper.getJdbcHelper();
        Object[] params = new Object[]{entity.getTaskId(), entity.getRate()};
        String sql = " INSERT INTO page_rate (\n" +
                "            task_id,\n" +
                "            rate\n" +
                "        )\n" +
                "        VALUES (\n" +
                "            ?,\n" +
                "            ?\n" +
                "        )";
        jdbcHelper.prepareUpdate(sql, params);
    }
}
