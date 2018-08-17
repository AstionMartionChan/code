package com.cfy.dao;

import com.cfy.po.Top10CategorySessionEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* top10_category_session 表
*/
@Repository("top10CategorySessionDao")
public interface ITop10CategorySessionDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return Top10CategorySessionEntity  POJO类
     */
    Top10CategorySessionEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<Top10CategorySessionEntity>  POJO类 集合
     */
    List<Top10CategorySessionEntity> select(Top10CategorySessionEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(Top10CategorySessionEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(Top10CategorySessionEntity entity);

}