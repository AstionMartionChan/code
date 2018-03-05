package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillAccountingChargeRecordEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_accounting_charge_record 表
*/
@Repository("waybillAccountingChargeRecordDao")
public interface IWaybillAccountingChargeRecordDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillAccountingChargeRecordEntity  POJO类
     */
    WaybillAccountingChargeRecordEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillAccountingChargeRecordEntity>  POJO类 集合
     */
    List<WaybillAccountingChargeRecordEntity> select(WaybillAccountingChargeRecordEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillAccountingChargeRecordEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillAccountingChargeRecordEntity entity);

}