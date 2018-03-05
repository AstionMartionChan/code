package com.rltx.org.config.dao;

import com.rltx.org.config.po.OrgConfigMeasureUnitDefineEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* org_config_measure_unit_define 表
*/
@Repository("orgConfigMeasureUnitDefineDao")
public interface IOrgConfigMeasureUnitDefineDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return OrgConfigMeasureUnitDefineEntity  POJO类
     */
    OrgConfigMeasureUnitDefineEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<OrgConfigMeasureUnitDefineEntity>  POJO类 集合
     */
    List<OrgConfigMeasureUnitDefineEntity> select(OrgConfigMeasureUnitDefineEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(OrgConfigMeasureUnitDefineEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(OrgConfigMeasureUnitDefineEntity entity);

}