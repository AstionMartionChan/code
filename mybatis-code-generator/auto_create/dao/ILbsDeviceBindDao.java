package com.rltx.lbs.dao;

import com.rltx.lbs.po.LbsDeviceBindEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* lbs_device_bind 表
*/
@Repository("lbsDeviceBindDao")
public interface ILbsDeviceBindDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return LbsDeviceBindEntity  POJO类
     */
    LbsDeviceBindEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<LbsDeviceBindEntity>  POJO类 集合
     */
    List<LbsDeviceBindEntity> select(LbsDeviceBindEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(LbsDeviceBindEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(LbsDeviceBindEntity entity);

}