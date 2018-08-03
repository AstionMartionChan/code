package com.cfy.dao;

import com.cfy.po.SessionRandomExtractEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* session_random_extract 表
*/
@Repository("sessionRandomExtractDao")
public interface ISessionRandomExtractDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return SessionRandomExtractEntity  POJO类
     */
    SessionRandomExtractEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<SessionRandomExtractEntity>  POJO类 集合
     */
    List<SessionRandomExtractEntity> select(SessionRandomExtractEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(SessionRandomExtractEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(SessionRandomExtractEntity entity);

}