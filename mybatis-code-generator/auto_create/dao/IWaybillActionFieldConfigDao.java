package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillActionFieldConfigEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_action_field_config 表
*/
@Repository("waybillActionFieldConfigDao")
public interface IWaybillActionFieldConfigDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillActionFieldConfigEntity  POJO类
     */
    WaybillActionFieldConfigEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillActionFieldConfigEntity>  POJO类 集合
     */
    List<WaybillActionFieldConfigEntity> select(WaybillActionFieldConfigEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillActionFieldConfigEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillActionFieldConfigEntity entity);

}