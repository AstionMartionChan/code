package com.rltx.lbs.dao;

import com.rltx.lbs.po.LbsProviderInterfaceParameterValueEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* lbs_provider_interface_parameter_value 表
*/
@Repository("lbsProviderInterfaceParameterValueDao")
public interface ILbsProviderInterfaceParameterValueDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return LbsProviderInterfaceParameterValueEntity  POJO类
     */
    LbsProviderInterfaceParameterValueEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<LbsProviderInterfaceParameterValueEntity>  POJO类 集合
     */
    List<LbsProviderInterfaceParameterValueEntity> select(LbsProviderInterfaceParameterValueEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(LbsProviderInterfaceParameterValueEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(LbsProviderInterfaceParameterValueEntity entity);

}