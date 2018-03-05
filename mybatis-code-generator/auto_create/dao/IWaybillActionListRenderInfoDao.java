package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillActionListRenderInfoEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_action_list_render_info 表
*/
@Repository("waybillActionListRenderInfoDao")
public interface IWaybillActionListRenderInfoDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillActionListRenderInfoEntity  POJO类
     */
    WaybillActionListRenderInfoEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillActionListRenderInfoEntity>  POJO类 集合
     */
    List<WaybillActionListRenderInfoEntity> select(WaybillActionListRenderInfoEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillActionListRenderInfoEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillActionListRenderInfoEntity entity);

}