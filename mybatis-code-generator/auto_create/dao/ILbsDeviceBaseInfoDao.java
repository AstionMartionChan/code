package com.rltx.lbs.dao;

import com.rltx.lbs.po.LbsDeviceBaseInfoEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* lbs_device_base_info 表
*/
@Repository("lbsDeviceBaseInfoDao")
public interface ILbsDeviceBaseInfoDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return LbsDeviceBaseInfoEntity  POJO类
     */
    LbsDeviceBaseInfoEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<LbsDeviceBaseInfoEntity>  POJO类 集合
     */
    List<LbsDeviceBaseInfoEntity> select(LbsDeviceBaseInfoEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(LbsDeviceBaseInfoEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(LbsDeviceBaseInfoEntity entity);

}