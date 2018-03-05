package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillActionFieldEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_action_field 表
*/
@Repository("waybillActionFieldDao")
public interface IWaybillActionFieldDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillActionFieldEntity  POJO类
     */
    WaybillActionFieldEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillActionFieldEntity>  POJO类 集合
     */
    List<WaybillActionFieldEntity> select(WaybillActionFieldEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillActionFieldEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillActionFieldEntity entity);

}