package com.cfy.dao;

import com.cfy.bean.AreaTop3ProductEntity;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/7/12
 * Time: 14:15
 * Work contact: Astion_Leo@163.com
 */


public interface IAreaTop3ProductDao {


    void batchInsert(List<AreaTop3ProductEntity> entityList);
}
