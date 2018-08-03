package com.cfy.dao;

import com.cfy.po.CityInfoEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* city_info 表
*/
@Repository("cityInfoDao")
public interface ICityInfoDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return CityInfoEntity  POJO类
     */
    CityInfoEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<CityInfoEntity>  POJO类 集合
     */
    List<CityInfoEntity> select(CityInfoEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(CityInfoEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(CityInfoEntity entity);

}