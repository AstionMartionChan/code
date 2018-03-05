package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillThumbnailResourceFileEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_thumbnail_resource_file 表
*/
@Repository("waybillThumbnailResourceFileDao")
public interface IWaybillThumbnailResourceFileDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillThumbnailResourceFileEntity  POJO类
     */
    WaybillThumbnailResourceFileEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillThumbnailResourceFileEntity>  POJO类 集合
     */
    List<WaybillThumbnailResourceFileEntity> select(WaybillThumbnailResourceFileEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillThumbnailResourceFileEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillThumbnailResourceFileEntity entity);

}