package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillActionDetailRenderInfoEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_action_detail_render_info 表
*/
@Repository("waybillActionDetailRenderInfoDao")
public interface IWaybillActionDetailRenderInfoDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillActionDetailRenderInfoEntity  POJO类
     */
    WaybillActionDetailRenderInfoEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillActionDetailRenderInfoEntity>  POJO类 集合
     */
    List<WaybillActionDetailRenderInfoEntity> select(WaybillActionDetailRenderInfoEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillActionDetailRenderInfoEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillActionDetailRenderInfoEntity entity);

}