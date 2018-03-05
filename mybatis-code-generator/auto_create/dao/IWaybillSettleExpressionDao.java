package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillSettleExpressionEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_settle_expression 表
*/
@Repository("waybillSettleExpressionDao")
public interface IWaybillSettleExpressionDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillSettleExpressionEntity  POJO类
     */
    WaybillSettleExpressionEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillSettleExpressionEntity>  POJO类 集合
     */
    List<WaybillSettleExpressionEntity> select(WaybillSettleExpressionEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillSettleExpressionEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillSettleExpressionEntity entity);

}