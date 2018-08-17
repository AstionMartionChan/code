package com.cfy.dao;

import com.cfy.po.PageRateEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* page_rate 表
*/
@Repository("pageRateDao")
public interface IPageRateDao {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return PageRateEntity  POJO类
     */
    PageRateEntity selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<PageRateEntity>  POJO类 集合
     */
    List<PageRateEntity> select(PageRateEntity entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(PageRateEntity entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(PageRateEntity entity);

}