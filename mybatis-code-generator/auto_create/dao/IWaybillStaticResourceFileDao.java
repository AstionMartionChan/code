package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillStaticResourceFileEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_static_resource_file 表
*/
@Repository("waybillStaticResourceFileDao")
public interface IWaybillStaticResourceFileDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillStaticResourceFileEntity  POJO类
     */
    WaybillStaticResourceFileEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillStaticResourceFileEntity>  POJO类 集合
     */
    List<WaybillStaticResourceFileEntity> select(WaybillStaticResourceFileEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillStaticResourceFileEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillStaticResourceFileEntity entity);

}