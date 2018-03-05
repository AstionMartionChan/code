package com.rltx.org.config.dao;

import com.rltx.org.config.po.OrgConfigExpressionDefineEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* org_config_expression_define 表
*/
@Repository("orgConfigExpressionDefineDao")
public interface IOrgConfigExpressionDefineDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return OrgConfigExpressionDefineEntity  POJO类
     */
    OrgConfigExpressionDefineEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<OrgConfigExpressionDefineEntity>  POJO类 集合
     */
    List<OrgConfigExpressionDefineEntity> select(OrgConfigExpressionDefineEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(OrgConfigExpressionDefineEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(OrgConfigExpressionDefineEntity entity);

}