package com.rltx.org.config.dao;

import com.rltx.org.config.po.OrgConfigElementTypeInfoRelationEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* org_config_element_type_info_relation 表
*/
@Repository("orgConfigElementTypeInfoRelationDao")
public interface IOrgConfigElementTypeInfoRelationDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return OrgConfigElementTypeInfoRelationEntity  POJO类
     */
    OrgConfigElementTypeInfoRelationEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<OrgConfigElementTypeInfoRelationEntity>  POJO类 集合
     */
    List<OrgConfigElementTypeInfoRelationEntity> select(OrgConfigElementTypeInfoRelationEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(OrgConfigElementTypeInfoRelationEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(OrgConfigElementTypeInfoRelationEntity entity);

}