package com.rltx.org.config.dao;

import com.rltx.org.config.po.OrgConfigAccountingChargeItemDefineEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* org_config_accounting_charge_item_define 表
*/
@Repository("orgConfigAccountingChargeItemDefineDao")
public interface IOrgConfigAccountingChargeItemDefineDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return OrgConfigAccountingChargeItemDefineEntity  POJO类
     */
    OrgConfigAccountingChargeItemDefineEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<OrgConfigAccountingChargeItemDefineEntity>  POJO类 集合
     */
    List<OrgConfigAccountingChargeItemDefineEntity> select(OrgConfigAccountingChargeItemDefineEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(OrgConfigAccountingChargeItemDefineEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(OrgConfigAccountingChargeItemDefineEntity entity);

}