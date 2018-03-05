package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillActionRecordEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_action_record 表
*/
@Repository("waybillActionRecordDao")
public interface IWaybillActionRecordDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillActionRecordEntity  POJO类
     */
    WaybillActionRecordEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillActionRecordEntity>  POJO类 集合
     */
    List<WaybillActionRecordEntity> select(WaybillActionRecordEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillActionRecordEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillActionRecordEntity entity);

}