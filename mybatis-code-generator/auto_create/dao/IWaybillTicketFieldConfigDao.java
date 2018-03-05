package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillTicketFieldConfigEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_ticket_field_config 表
*/
@Repository("waybillTicketFieldConfigDao")
public interface IWaybillTicketFieldConfigDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillTicketFieldConfigEntity  POJO类
     */
    WaybillTicketFieldConfigEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillTicketFieldConfigEntity>  POJO类 集合
     */
    List<WaybillTicketFieldConfigEntity> select(WaybillTicketFieldConfigEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillTicketFieldConfigEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillTicketFieldConfigEntity entity);

}