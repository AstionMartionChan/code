package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillSettleListRenderInfoEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_settle_list_render_info 表
*/
@Repository("waybillSettleListRenderInfoDao")
public interface IWaybillSettleListRenderInfoDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillSettleListRenderInfoEntity  POJO类
     */
    WaybillSettleListRenderInfoEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillSettleListRenderInfoEntity>  POJO类 集合
     */
    List<WaybillSettleListRenderInfoEntity> select(WaybillSettleListRenderInfoEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillSettleListRenderInfoEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillSettleListRenderInfoEntity entity);

}