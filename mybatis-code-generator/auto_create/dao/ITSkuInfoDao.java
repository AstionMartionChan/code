package com.rltx.ipatio.dao;

import com.rltx.ipatio.po.TSkuInfoEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* t_sku_info 表
*/
@Repository("tSkuInfoDao")
public interface ITSkuInfoDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return TSkuInfoEntity  POJO类
     */
    TSkuInfoEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<TSkuInfoEntity>  POJO类 集合
     */
    List<TSkuInfoEntity> select(TSkuInfoEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(TSkuInfoEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(TSkuInfoEntity entity);

}