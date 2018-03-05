package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillProducerEventEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_producer_event 表
*/
@Repository("waybillProducerEventDao")
public interface IWaybillProducerEventDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillProducerEventEntity  POJO类
     */
    WaybillProducerEventEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillProducerEventEntity>  POJO类 集合
     */
    List<WaybillProducerEventEntity> select(WaybillProducerEventEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillProducerEventEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillProducerEventEntity entity);

}