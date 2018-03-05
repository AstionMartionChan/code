package com.rltx.lbs.dao;

import com.rltx.lbs.po.LbsDeviceTruckRelationEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* lbs_device_truck_relation 表
*/
@Repository("lbsDeviceTruckRelationDao")
public interface ILbsDeviceTruckRelationDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return LbsDeviceTruckRelationEntity  POJO类
     */
    LbsDeviceTruckRelationEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<LbsDeviceTruckRelationEntity>  POJO类 集合
     */
    List<LbsDeviceTruckRelationEntity> select(LbsDeviceTruckRelationEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(LbsDeviceTruckRelationEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(LbsDeviceTruckRelationEntity entity);

}