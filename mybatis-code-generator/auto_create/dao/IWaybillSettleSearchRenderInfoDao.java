package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillSettleSearchRenderInfoEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_settle_search_render_info 表
*/
@Repository("waybillSettleSearchRenderInfoDao")
public interface IWaybillSettleSearchRenderInfoDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillSettleSearchRenderInfoEntity  POJO类
     */
    WaybillSettleSearchRenderInfoEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillSettleSearchRenderInfoEntity>  POJO类 集合
     */
    List<WaybillSettleSearchRenderInfoEntity> select(WaybillSettleSearchRenderInfoEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillSettleSearchRenderInfoEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillSettleSearchRenderInfoEntity entity);

}