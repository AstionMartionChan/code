package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillActionChargeItemRelationEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_action_charge_item_relation 表
*/
@Repository("waybillActionChargeItemRelationDao")
public interface IWaybillActionChargeItemRelationDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillActionChargeItemRelationEntity  POJO类
     */
    WaybillActionChargeItemRelationEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillActionChargeItemRelationEntity>  POJO类 集合
     */
    List<WaybillActionChargeItemRelationEntity> select(WaybillActionChargeItemRelationEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillActionChargeItemRelationEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillActionChargeItemRelationEntity entity);

}