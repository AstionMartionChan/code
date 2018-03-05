package com.rltx.org.config.dao;

import com.rltx.org.config.po.OrgConfigActionTemplateDetailEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* org_config_action_template_detail 表
*/
@Repository("orgConfigActionTemplateDetailDao")
public interface IOrgConfigActionTemplateDetailDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return OrgConfigActionTemplateDetailEntity  POJO类
     */
    OrgConfigActionTemplateDetailEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<OrgConfigActionTemplateDetailEntity>  POJO类 集合
     */
    List<OrgConfigActionTemplateDetailEntity> select(OrgConfigActionTemplateDetailEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(OrgConfigActionTemplateDetailEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(OrgConfigActionTemplateDetailEntity entity);

}