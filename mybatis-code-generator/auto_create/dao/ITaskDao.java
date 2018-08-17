package com.cfy.dao;

import com.cfy.po.TaskEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* task 表
*/
@Repository("taskDao")
public interface ITaskDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return TaskEntity  POJO类
     */
    TaskEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<TaskEntity>  POJO类 集合
     */
    List<TaskEntity> select(TaskEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(TaskEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(TaskEntity entity);

}