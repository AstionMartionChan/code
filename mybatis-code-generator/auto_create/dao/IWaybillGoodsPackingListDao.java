package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillGoodsPackingListEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_goods_packing_list 表
*/
@Repository("waybillGoodsPackingListDao")
public interface IWaybillGoodsPackingListDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillGoodsPackingListEntity  POJO类
     */
    WaybillGoodsPackingListEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillGoodsPackingListEntity>  POJO类 集合
     */
    List<WaybillGoodsPackingListEntity> select(WaybillGoodsPackingListEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillGoodsPackingListEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillGoodsPackingListEntity entity);

}