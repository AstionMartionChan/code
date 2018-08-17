package com.cfy.dao.impl;

import com.cfy.bean.SessionDetailEntity;
import com.cfy.dao.ISesstionDetailDao;
import com.cfy.utils.JDBCHelper;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/7/3
 * Time: 16:46
 * Work contact: Astion_Leo@163.com
 */


public class SessionDetailDaoImpl implements ISesstionDetailDao {
    @Override
    public void insert(SessionDetailEntity entity) {
        JDBCHelper jdbcHelper = JDBCHelper.getJdbcHelper();
        Object[] params = new Object[]{entity.getTaskId(), entity.getUserId(), entity.getSessionId(), entity.getPageId(), entity.getActionTime(), entity.getSearchKeyword(), entity.getClickCategoryId(), entity.getClickProductId(), entity.getOrderCategoryIds(), entity.getOrderProductIds(), entity.getPayCategoryIds(), entity.getPayProductIds()};
        String sql = "INSERT INTO session_detail (\n" +
                "            task_id,\n" +
                "            user_id,\n" +
                "            session_id,\n" +
                "            page_id,\n" +
                "            action_time,\n" +
                "            search_keyword,\n" +
                "            click_category_id,\n" +
                "            click_product_id,\n" +
                "            order_category_ids,\n" +
                "            order_product_ids,\n" +
                "            pay_category_ids,\n" +
                "            pay_product_ids\n" +
                "        )\n" +
                "        VALUES (\n" +
                "            ?,\n" +
                "            ?,\n" +
                "            ?,\n" +
                "            ?,\n" +
                "            ?,\n" +
                "            ?,\n" +
                "            ?,\n" +
                "            ?,\n" +
                "            ?,\n" +
                "            ?,\n" +
                "            ?,\n" +
                "            ?\n" +
                "        )";
        jdbcHelper.prepareUpdate(sql, params);
    }
}
