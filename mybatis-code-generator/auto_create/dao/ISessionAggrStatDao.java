package com.cfy.dao;

import com.cfy.po.SessionAggrStatEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* session_aggr_stat 表
*/
@Repository("sessionAggrStatDao")
public interface ISessionAggrStatDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return SessionAggrStatEntity  POJO类
     */
    SessionAggrStatEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<SessionAggrStatEntity>  POJO类 集合
     */
    List<SessionAggrStatEntity> select(SessionAggrStatEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(SessionAggrStatEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(SessionAggrStatEntity entity);

}