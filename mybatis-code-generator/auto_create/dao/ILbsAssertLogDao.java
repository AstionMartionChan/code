package com.rltx.lbs.dao;

import com.rltx.lbs.po.LbsAssertLogEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* lbs_assert_log 表
*/
@Repository("lbsAssertLogDao")
public interface ILbsAssertLogDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return LbsAssertLogEntity  POJO类
     */
    LbsAssertLogEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<LbsAssertLogEntity>  POJO类 集合
     */
    List<LbsAssertLogEntity> select(LbsAssertLogEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(LbsAssertLogEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(LbsAssertLogEntity entity);

}