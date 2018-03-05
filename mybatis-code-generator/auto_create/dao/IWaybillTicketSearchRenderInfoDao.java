package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillTicketSearchRenderInfoEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_ticket_search_render_info 表
*/
@Repository("waybillTicketSearchRenderInfoDao")
public interface IWaybillTicketSearchRenderInfoDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillTicketSearchRenderInfoEntity  POJO类
     */
    WaybillTicketSearchRenderInfoEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillTicketSearchRenderInfoEntity>  POJO类 集合
     */
    List<WaybillTicketSearchRenderInfoEntity> select(WaybillTicketSearchRenderInfoEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillTicketSearchRenderInfoEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillTicketSearchRenderInfoEntity entity);

}