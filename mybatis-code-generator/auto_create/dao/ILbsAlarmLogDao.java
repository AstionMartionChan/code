package com.rltx.lbs.dao;

import com.rltx.lbs.po.LbsAlarmLogEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* lbs_alarm_log 表
*/
@Repository("lbsAlarmLogDao")
public interface ILbsAlarmLogDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return LbsAlarmLogEntity  POJO类
     */
    LbsAlarmLogEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<LbsAlarmLogEntity>  POJO类 集合
     */
    List<LbsAlarmLogEntity> select(LbsAlarmLogEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(LbsAlarmLogEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(LbsAlarmLogEntity entity);

}