package com.rltx.ipatio.dao;

import com.rltx.ipatio.po.TYesterdayStatEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* t_yesterday_stat 表
*/
@Repository("tYesterdayStatDao")
public interface ITYesterdayStatDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return TYesterdayStatEntity  POJO类
     */
    TYesterdayStatEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<TYesterdayStatEntity>  POJO类 集合
     */
    List<TYesterdayStatEntity> select(TYesterdayStatEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(TYesterdayStatEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(TYesterdayStatEntity entity);

}