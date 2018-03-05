package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillTicketDetailRenderInfoEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_ticket_detail_render_info 表
*/
@Repository("waybillTicketDetailRenderInfoDao")
public interface IWaybillTicketDetailRenderInfoDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillTicketDetailRenderInfoEntity  POJO类
     */
    WaybillTicketDetailRenderInfoEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillTicketDetailRenderInfoEntity>  POJO类 集合
     */
    List<WaybillTicketDetailRenderInfoEntity> select(WaybillTicketDetailRenderInfoEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillTicketDetailRenderInfoEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillTicketDetailRenderInfoEntity entity);

}