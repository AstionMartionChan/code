package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillDetailRenderInfoEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_detail_render_info 表
*/
@Repository("waybillDetailRenderInfoDao")
public interface IWaybillDetailRenderInfoDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillDetailRenderInfoEntity  POJO类
     */
    WaybillDetailRenderInfoEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillDetailRenderInfoEntity>  POJO类 集合
     */
    List<WaybillDetailRenderInfoEntity> select(WaybillDetailRenderInfoEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillDetailRenderInfoEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillDetailRenderInfoEntity entity);

}