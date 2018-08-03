package com.cfy.dao.impl;

import com.cfy.bean.AreaTop3ProductEntity;
import com.cfy.dao.IAreaTop3ProductDao;
import com.cfy.utils.JDBCHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/7/12
 * Time: 14:16
 * Work contact: Astion_Leo@163.com
 */


public class AreaTop3ProductDaoImpl implements IAreaTop3ProductDao {


    @Override
    public void batchInsert(List<AreaTop3ProductEntity> entityList) {
        JDBCHelper jdbcHelper = JDBCHelper.getJdbcHelper();
        List<Object[]> objs = new ArrayList<>();
        String sql = "INSERT INTO area_top3_product (\n" +
                "            task_id,\n" +
                "            area,\n" +
                "            area_level,\n" +
                "            product_id,\n" +
                "            city_names,\n" +
                "            click_count,\n" +
                "            product_name,\n" +
                "            product_status\n" +
                "        )\n" +
                "        VALUES (\n" +
                "            ?,\n" +
                "            ?,\n" +
                "            ?,\n" +
                "            ?,\n" +
                "            ?,\n" +
                "            ?,\n" +
                "            ?,\n" +
                "            ?\n" +
                "        )";

        for (AreaTop3ProductEntity entity : entityList){
            Object[] obj = new Object[]{entity.getTaskId(), entity.getArea(), entity.getAreaLevel(),
                    entity.getProductId(), entity.getCityNames(), entity.getClickCount(), entity.getProductName(), entity.getProductStatus()};
            objs.add(obj);
        }

        jdbcHelper.prepareBult(sql, objs);
    }
}
