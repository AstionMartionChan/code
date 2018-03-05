package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillUpstreamBaseInfoEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_upstream_base_info 表
*/
@Repository("waybillUpstreamBaseInfoDao")
public interface IWaybillUpstreamBaseInfoDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillUpstreamBaseInfoEntity  POJO类
     */
    WaybillUpstreamBaseInfoEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillUpstreamBaseInfoEntity>  POJO类 集合
     */
    List<WaybillUpstreamBaseInfoEntity> select(WaybillUpstreamBaseInfoEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillUpstreamBaseInfoEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillUpstreamBaseInfoEntity entity);

}