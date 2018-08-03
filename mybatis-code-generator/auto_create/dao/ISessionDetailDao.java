package com.cfy.dao;

import com.cfy.po.SessionDetailEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* session_detail 表
*/
@Repository("sessionDetailDao")
public interface ISessionDetailDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return SessionDetailEntity  POJO类
     */
    SessionDetailEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<SessionDetailEntity>  POJO类 集合
     */
    List<SessionDetailEntity> select(SessionDetailEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(SessionDetailEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(SessionDetailEntity entity);

}