package com.cfy.dao;

import com.cfy.po.Top10CategoryEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* top10_category 表
*/
@Repository("top10CategoryDao")
public interface ITop10CategoryDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return Top10CategoryEntity  POJO类
     */
    Top10CategoryEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<Top10CategoryEntity>  POJO类 集合
     */
    List<Top10CategoryEntity> select(Top10CategoryEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(Top10CategoryEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(Top10CategoryEntity entity);

}