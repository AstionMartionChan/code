package com.rltx.org.config.dao;

import com.rltx.org.config.po.OrgConfigActionFieldDefineEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* org_config_action_field_define 表
*/
@Repository("orgConfigActionFieldDefineDao")
public interface IOrgConfigActionFieldDefineDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return OrgConfigActionFieldDefineEntity  POJO类
     */
    OrgConfigActionFieldDefineEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<OrgConfigActionFieldDefineEntity>  POJO类 集合
     */
    List<OrgConfigActionFieldDefineEntity> select(OrgConfigActionFieldDefineEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(OrgConfigActionFieldDefineEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(OrgConfigActionFieldDefineEntity entity);

}