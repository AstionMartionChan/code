package com.rltx.org.config.dao;

import com.rltx.org.config.po.OrgConfigElementTypeInfoEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* org_config_element_type_info 表
*/
@Repository("orgConfigElementTypeInfoDao")
public interface IOrgConfigElementTypeInfoDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return OrgConfigElementTypeInfoEntity  POJO类
     */
    OrgConfigElementTypeInfoEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<OrgConfigElementTypeInfoEntity>  POJO类 集合
     */
    List<OrgConfigElementTypeInfoEntity> select(OrgConfigElementTypeInfoEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(OrgConfigElementTypeInfoEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(OrgConfigElementTypeInfoEntity entity);

}