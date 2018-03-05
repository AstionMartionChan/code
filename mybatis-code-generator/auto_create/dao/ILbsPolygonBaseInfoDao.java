package com.rltx.lbs.dao;

import com.rltx.lbs.po.LbsPolygonBaseInfoEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* lbs_polygon_base_info 表
*/
@Repository("lbsPolygonBaseInfoDao")
public interface ILbsPolygonBaseInfoDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return LbsPolygonBaseInfoEntity  POJO类
     */
    LbsPolygonBaseInfoEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<LbsPolygonBaseInfoEntity>  POJO类 集合
     */
    List<LbsPolygonBaseInfoEntity> select(LbsPolygonBaseInfoEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(LbsPolygonBaseInfoEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(LbsPolygonBaseInfoEntity entity);

}