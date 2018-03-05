package com.rltx.lbs.dao;

import com.rltx.lbs.po.LbsAlarmConfigEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* lbs_alarm_config 表
*/
@Repository("lbsAlarmConfigDao")
public interface ILbsAlarmConfigDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return LbsAlarmConfigEntity  POJO类
     */
    LbsAlarmConfigEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<LbsAlarmConfigEntity>  POJO类 集合
     */
    List<LbsAlarmConfigEntity> select(LbsAlarmConfigEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(LbsAlarmConfigEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(LbsAlarmConfigEntity entity);

}