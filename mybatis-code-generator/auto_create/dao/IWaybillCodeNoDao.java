package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillCodeNoEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_code_no 表
*/
@Repository("waybillCodeNoDao")
public interface IWaybillCodeNoDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillCodeNoEntity  POJO类
     */
    WaybillCodeNoEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillCodeNoEntity>  POJO类 集合
     */
    List<WaybillCodeNoEntity> select(WaybillCodeNoEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillCodeNoEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillCodeNoEntity entity);

}