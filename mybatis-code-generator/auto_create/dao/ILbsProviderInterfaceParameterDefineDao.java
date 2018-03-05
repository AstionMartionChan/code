package com.rltx.lbs.dao;

import com.rltx.lbs.po.LbsProviderInterfaceParameterDefineEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* lbs_provider_interface_parameter_define 表
*/
@Repository("lbsProviderInterfaceParameterDefineDao")
public interface ILbsProviderInterfaceParameterDefineDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return LbsProviderInterfaceParameterDefineEntity  POJO类
     */
    LbsProviderInterfaceParameterDefineEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<LbsProviderInterfaceParameterDefineEntity>  POJO类 集合
     */
    List<LbsProviderInterfaceParameterDefineEntity> select(LbsProviderInterfaceParameterDefineEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(LbsProviderInterfaceParameterDefineEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(LbsProviderInterfaceParameterDefineEntity entity);

}