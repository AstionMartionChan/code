package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillDownstreamGoodsPackingListEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_downstream_goods_packing_list 表
*/
@Repository("waybillDownstreamGoodsPackingListDao")
public interface IWaybillDownstreamGoodsPackingListDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillDownstreamGoodsPackingListEntity  POJO类
     */
    WaybillDownstreamGoodsPackingListEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillDownstreamGoodsPackingListEntity>  POJO类 集合
     */
    List<WaybillDownstreamGoodsPackingListEntity> select(WaybillDownstreamGoodsPackingListEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillDownstreamGoodsPackingListEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillDownstreamGoodsPackingListEntity entity);

}