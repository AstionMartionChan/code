package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillActionConditionEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_action_condition 表
*/
@Repository("waybillActionConditionDao")
public interface IWaybillActionConditionDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillActionConditionEntity  POJO类
     */
    WaybillActionConditionEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillActionConditionEntity>  POJO类 集合
     */
    List<WaybillActionConditionEntity> select(WaybillActionConditionEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillActionConditionEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillActionConditionEntity entity);

}