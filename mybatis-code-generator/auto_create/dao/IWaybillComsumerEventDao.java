package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillComsumerEventEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_comsumer_event 表
*/
@Repository("waybillComsumerEventDao")
public interface IWaybillComsumerEventDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillComsumerEventEntity  POJO类
     */
    WaybillComsumerEventEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillComsumerEventEntity>  POJO类 集合
     */
    List<WaybillComsumerEventEntity> select(WaybillComsumerEventEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillComsumerEventEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillComsumerEventEntity entity);

}