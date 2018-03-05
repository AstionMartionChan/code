package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillActionRelationEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_action_relation 表
*/
@Repository("waybillActionRelationDao")
public interface IWaybillActionRelationDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillActionRelationEntity  POJO类
     */
    WaybillActionRelationEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillActionRelationEntity>  POJO类 集合
     */
    List<WaybillActionRelationEntity> select(WaybillActionRelationEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillActionRelationEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillActionRelationEntity entity);

}