package com.rltx.lbs.dao;

import com.rltx.lbs.po.LbsPolygonAccessLogEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* lbs_polygon_access_log 表
*/
@Repository("lbsPolygonAccessLogDao")
public interface ILbsPolygonAccessLogDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return LbsPolygonAccessLogEntity  POJO类
     */
    LbsPolygonAccessLogEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<LbsPolygonAccessLogEntity>  POJO类 集合
     */
    List<LbsPolygonAccessLogEntity> select(LbsPolygonAccessLogEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(LbsPolygonAccessLogEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(LbsPolygonAccessLogEntity entity);

}