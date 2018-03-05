package com.rltx.org.config.dao;

import com.rltx.org.config.po.OrgConfigElementTypeEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* org_config_element_type 表
*/
@Repository("orgConfigElementTypeDao")
public interface IOrgConfigElementTypeDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return OrgConfigElementTypeEntity  POJO类
     */
    OrgConfigElementTypeEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<OrgConfigElementTypeEntity>  POJO类 集合
     */
    List<OrgConfigElementTypeEntity> select(OrgConfigElementTypeEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(OrgConfigElementTypeEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(OrgConfigElementTypeEntity entity);

}