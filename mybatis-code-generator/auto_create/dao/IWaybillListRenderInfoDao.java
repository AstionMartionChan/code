package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillListRenderInfoEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_list_render_info 表
*/
@Repository("waybillListRenderInfoDao")
public interface IWaybillListRenderInfoDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillListRenderInfoEntity  POJO类
     */
    WaybillListRenderInfoEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillListRenderInfoEntity>  POJO类 集合
     */
    List<WaybillListRenderInfoEntity> select(WaybillListRenderInfoEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillListRenderInfoEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillListRenderInfoEntity entity);

}