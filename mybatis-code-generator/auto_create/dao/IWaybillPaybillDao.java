package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillPaybillEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_paybill 表
*/
@Repository("waybillPaybillDao")
public interface IWaybillPaybillDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillPaybillEntity  POJO类
     */
    WaybillPaybillEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillPaybillEntity>  POJO类 集合
     */
    List<WaybillPaybillEntity> select(WaybillPaybillEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillPaybillEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillPaybillEntity entity);

}