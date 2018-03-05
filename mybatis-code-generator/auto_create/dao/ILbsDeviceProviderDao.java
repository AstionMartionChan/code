package com.rltx.lbs.dao;

import com.rltx.lbs.po.LbsDeviceProviderEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* lbs_device_provider 表
*/
@Repository("lbsDeviceProviderDao")
public interface ILbsDeviceProviderDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return LbsDeviceProviderEntity  POJO类
     */
    LbsDeviceProviderEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<LbsDeviceProviderEntity>  POJO类 集合
     */
    List<LbsDeviceProviderEntity> select(LbsDeviceProviderEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(LbsDeviceProviderEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(LbsDeviceProviderEntity entity);

}