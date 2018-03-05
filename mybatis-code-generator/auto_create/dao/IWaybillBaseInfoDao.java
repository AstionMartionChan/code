package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillBaseInfoEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_base_info 表
*/
@Repository("waybillBaseInfoDao")
public interface IWaybillBaseInfoDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillBaseInfoEntity  POJO类
     */
    WaybillBaseInfoEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillBaseInfoEntity>  POJO类 集合
     */
    List<WaybillBaseInfoEntity> select(WaybillBaseInfoEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillBaseInfoEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillBaseInfoEntity entity);

}