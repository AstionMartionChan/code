package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillSettleFieldConfigEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_settle_field_config 表
*/
@Repository("waybillSettleFieldConfigDao")
public interface IWaybillSettleFieldConfigDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillSettleFieldConfigEntity  POJO类
     */
    WaybillSettleFieldConfigEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillSettleFieldConfigEntity>  POJO类 集合
     */
    List<WaybillSettleFieldConfigEntity> select(WaybillSettleFieldConfigEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillSettleFieldConfigEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillSettleFieldConfigEntity entity);

}