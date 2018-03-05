package com.rltx.org.config.dao;

import com.rltx.org.config.po.OrgConfigActionConditionDefineEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* org_config_action_condition_define 表
*/
@Repository("orgConfigActionConditionDefineDao")
public interface IOrgConfigActionConditionDefineDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return OrgConfigActionConditionDefineEntity  POJO类
     */
    OrgConfigActionConditionDefineEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<OrgConfigActionConditionDefineEntity>  POJO类 集合
     */
    List<OrgConfigActionConditionDefineEntity> select(OrgConfigActionConditionDefineEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(OrgConfigActionConditionDefineEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(OrgConfigActionConditionDefineEntity entity);

}