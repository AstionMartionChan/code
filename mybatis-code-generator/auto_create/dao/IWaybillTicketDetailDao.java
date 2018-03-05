package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillTicketDetailEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_ticket_detail 表
*/
@Repository("waybillTicketDetailDao")
public interface IWaybillTicketDetailDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillTicketDetailEntity  POJO类
     */
    WaybillTicketDetailEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillTicketDetailEntity>  POJO类 集合
     */
    List<WaybillTicketDetailEntity> select(WaybillTicketDetailEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillTicketDetailEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillTicketDetailEntity entity);

}