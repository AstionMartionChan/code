package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillTicketListRenderInfoEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_ticket_list_render_info 表
*/
@Repository("waybillTicketListRenderInfoDao")
public interface IWaybillTicketListRenderInfoDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillTicketListRenderInfoEntity  POJO类
     */
    WaybillTicketListRenderInfoEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillTicketListRenderInfoEntity>  POJO类 集合
     */
    List<WaybillTicketListRenderInfoEntity> select(WaybillTicketListRenderInfoEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillTicketListRenderInfoEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillTicketListRenderInfoEntity entity);

}