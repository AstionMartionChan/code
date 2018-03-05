package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillDrivingItemDefineEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_driving_item_define 表
*/
@Repository("waybillDrivingItemDefineDao")
public interface IWaybillDrivingItemDefineDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillDrivingItemDefineEntity  POJO类
     */
    WaybillDrivingItemDefineEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillDrivingItemDefineEntity>  POJO类 集合
     */
    List<WaybillDrivingItemDefineEntity> select(WaybillDrivingItemDefineEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillDrivingItemDefineEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillDrivingItemDefineEntity entity);

}