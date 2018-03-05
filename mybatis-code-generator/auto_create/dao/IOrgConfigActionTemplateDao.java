package com.rltx.org.config.dao;

import com.rltx.org.config.po.OrgConfigActionTemplateEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* org_config_action_template 表
*/
@Repository("orgConfigActionTemplateDao")
public interface IOrgConfigActionTemplateDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return OrgConfigActionTemplateEntity  POJO类
     */
    OrgConfigActionTemplateEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<OrgConfigActionTemplateEntity>  POJO类 集合
     */
    List<OrgConfigActionTemplateEntity> select(OrgConfigActionTemplateEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(OrgConfigActionTemplateEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(OrgConfigActionTemplateEntity entity);

}