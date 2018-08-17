package com.cfy.dao;

import com.cfy.po.AreaTop3ProductEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* area_top3_product 表
*/
@Repository("areaTop3ProductDao")
public interface IAreaTop3ProductDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return AreaTop3ProductEntity  POJO类
     */
    AreaTop3ProductEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<AreaTop3ProductEntity>  POJO类 集合
     */
    List<AreaTop3ProductEntity> select(AreaTop3ProductEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(AreaTop3ProductEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(AreaTop3ProductEntity entity);

}