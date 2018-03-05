package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillFieldConfigEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_field_config 表
*/
@Repository("waybillFieldConfigDao")
public interface IWaybillFieldConfigDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillFieldConfigEntity  POJO类
     */
    WaybillFieldConfigEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillFieldConfigEntity>  POJO类 集合
     */
    List<WaybillFieldConfigEntity> select(WaybillFieldConfigEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillFieldConfigEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillFieldConfigEntity entity);

}