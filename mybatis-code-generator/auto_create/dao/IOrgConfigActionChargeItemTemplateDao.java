package com.rltx.org.config.dao;

import com.rltx.org.config.po.OrgConfigActionChargeItemTemplateEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* org_config_action_charge_item_template 表
*/
@Repository("orgConfigActionChargeItemTemplateDao")
public interface IOrgConfigActionChargeItemTemplateDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return OrgConfigActionChargeItemTemplateEntity  POJO类
     */
    OrgConfigActionChargeItemTemplateEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<OrgConfigActionChargeItemTemplateEntity>  POJO类 集合
     */
    List<OrgConfigActionChargeItemTemplateEntity> select(OrgConfigActionChargeItemTemplateEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(OrgConfigActionChargeItemTemplateEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(OrgConfigActionChargeItemTemplateEntity entity);

}