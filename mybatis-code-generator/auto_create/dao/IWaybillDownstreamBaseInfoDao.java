package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillDownstreamBaseInfoEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_downstream_base_info 表
*/
@Repository("waybillDownstreamBaseInfoDao")
public interface IWaybillDownstreamBaseInfoDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillDownstreamBaseInfoEntity  POJO类
     */
    WaybillDownstreamBaseInfoEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillDownstreamBaseInfoEntity>  POJO类 集合
     */
    List<WaybillDownstreamBaseInfoEntity> select(WaybillDownstreamBaseInfoEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillDownstreamBaseInfoEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillDownstreamBaseInfoEntity entity);

}