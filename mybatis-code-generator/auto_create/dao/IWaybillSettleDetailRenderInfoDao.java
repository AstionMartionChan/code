package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillSettleDetailRenderInfoEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_settle_detail_render_info 表
*/
@Repository("waybillSettleDetailRenderInfoDao")
public interface IWaybillSettleDetailRenderInfoDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillSettleDetailRenderInfoEntity  POJO类
     */
    WaybillSettleDetailRenderInfoEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillSettleDetailRenderInfoEntity>  POJO类 集合
     */
    List<WaybillSettleDetailRenderInfoEntity> select(WaybillSettleDetailRenderInfoEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillSettleDetailRenderInfoEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillSettleDetailRenderInfoEntity entity);

}