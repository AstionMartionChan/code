package com.rltx.org.config.dao;

import com.rltx.org.config.po.OrgConfigActionDefineEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* org_config_action_define 表
*/
@Repository("orgConfigActionDefineDao")
public interface IOrgConfigActionDefineDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return OrgConfigActionDefineEntity  POJO类
     */
    OrgConfigActionDefineEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<OrgConfigActionDefineEntity>  POJO类 集合
     */
    List<OrgConfigActionDefineEntity> select(OrgConfigActionDefineEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(OrgConfigActionDefineEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(OrgConfigActionDefineEntity entity);

}