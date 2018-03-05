package com.rltx.org.config.dao;

import com.rltx.org.config.po.OrgConfigDrivingItemDefineEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* org_config_driving_item_define 表
*/
@Repository("orgConfigDrivingItemDefineDao")
public interface IOrgConfigDrivingItemDefineDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return OrgConfigDrivingItemDefineEntity  POJO类
     */
    OrgConfigDrivingItemDefineEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<OrgConfigDrivingItemDefineEntity>  POJO类 集合
     */
    List<OrgConfigDrivingItemDefineEntity> select(OrgConfigDrivingItemDefineEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(OrgConfigDrivingItemDefineEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(OrgConfigDrivingItemDefineEntity entity);

}