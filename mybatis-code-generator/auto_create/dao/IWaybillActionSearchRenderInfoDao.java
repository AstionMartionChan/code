package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillActionSearchRenderInfoEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_action_search_render_info 表
*/
@Repository("waybillActionSearchRenderInfoDao")
public interface IWaybillActionSearchRenderInfoDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillActionSearchRenderInfoEntity  POJO类
     */
    WaybillActionSearchRenderInfoEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillActionSearchRenderInfoEntity>  POJO类 集合
     */
    List<WaybillActionSearchRenderInfoEntity> select(WaybillActionSearchRenderInfoEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillActionSearchRenderInfoEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillActionSearchRenderInfoEntity entity);

}