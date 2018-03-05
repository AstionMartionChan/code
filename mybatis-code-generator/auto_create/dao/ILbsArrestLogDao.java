package com.rltx.lbs.dao;

import com.rltx.lbs.po.LbsArrestLogEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* lbs_arrest_log 表
*/
@Repository("lbsArrestLogDao")
public interface ILbsArrestLogDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return LbsArrestLogEntity  POJO类
     */
    LbsArrestLogEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<LbsArrestLogEntity>  POJO类 集合
     */
    List<LbsArrestLogEntity> select(LbsArrestLogEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(LbsArrestLogEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(LbsArrestLogEntity entity);

}