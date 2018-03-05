package com.rltx.lbs.dao;

import com.rltx.lbs.po.LbsProviderInterfaceDefineEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* lbs_provider_interface_define 表
*/
@Repository("lbsProviderInterfaceDefineDao")
public interface ILbsProviderInterfaceDefineDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return LbsProviderInterfaceDefineEntity  POJO类
     */
    LbsProviderInterfaceDefineEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<LbsProviderInterfaceDefineEntity>  POJO类 集合
     */
    List<LbsProviderInterfaceDefineEntity> select(LbsProviderInterfaceDefineEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(LbsProviderInterfaceDefineEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(LbsProviderInterfaceDefineEntity entity);

}