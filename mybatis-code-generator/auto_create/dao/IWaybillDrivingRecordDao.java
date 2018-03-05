package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillDrivingRecordEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_driving_record 表
*/
@Repository("waybillDrivingRecordDao")
public interface IWaybillDrivingRecordDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillDrivingRecordEntity  POJO类
     */
    WaybillDrivingRecordEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillDrivingRecordEntity>  POJO类 集合
     */
    List<WaybillDrivingRecordEntity> select(WaybillDrivingRecordEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillDrivingRecordEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillDrivingRecordEntity entity);

}