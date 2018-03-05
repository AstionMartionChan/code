package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillSearchRenderInfoEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_search_render_info 表
*/
@Repository("waybillSearchRenderInfoDao")
public interface IWaybillSearchRenderInfoDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillSearchRenderInfoEntity  POJO类
     */
    WaybillSearchRenderInfoEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillSearchRenderInfoEntity>  POJO类 集合
     */
    List<WaybillSearchRenderInfoEntity> select(WaybillSearchRenderInfoEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillSearchRenderInfoEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillSearchRenderInfoEntity entity);

}