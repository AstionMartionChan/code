package com.rltx.waybill.dao;

import com.rltx.waybill.po.WaybillUpstreamGoodsPackingListEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* waybill_upstream_goods_packing_list 表
*/
@Repository("waybillUpstreamGoodsPackingListDao")
public interface IWaybillUpstreamGoodsPackingListDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return WaybillUpstreamGoodsPackingListEntity  POJO类
     */
    WaybillUpstreamGoodsPackingListEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<WaybillUpstreamGoodsPackingListEntity>  POJO类 集合
     */
    List<WaybillUpstreamGoodsPackingListEntity> select(WaybillUpstreamGoodsPackingListEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(WaybillUpstreamGoodsPackingListEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(WaybillUpstreamGoodsPackingListEntity entity);

}