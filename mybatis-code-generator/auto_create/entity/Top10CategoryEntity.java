package com.cfy.po;

import java.util.Date;

/**
 * top10_category 表 Entity
 */
public class Top10CategoryEntity {

    // 
    private Long id;

    // 
    private Integer taskId;

    // 
    private Integer categoryId;

    // 
    private Integer clickCount;

    // 
    private Integer orderCount;

    // 
    private Integer payCount;



    /**
     * set 
     * @param id 
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * get 
     * @return Long 
     */
    public Long getId() {
        return id;
    }


    /**
     * set 
     * @param taskId 
     */
    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    /**
     * get 
     * @return Integer 
     */
    public Integer getTaskId() {
        return taskId;
    }


    /**
     * set 
     * @param categoryId 
     */
    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * get 
     * @return Integer 
     */
    public Integer getCategoryId() {
        return categoryId;
    }


    /**
     * set 
     * @param clickCount 
     */
    public void setClickCount(Integer clickCount) {
        this.clickCount = clickCount;
    }

    /**
     * get 
     * @return Integer 
     */
    public Integer getClickCount() {
        return clickCount;
    }


    /**
     * set 
     * @param orderCount 
     */
    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }

    /**
     * get 
     * @return Integer 
     */
    public Integer getOrderCount() {
        return orderCount;
    }


    /**
     * set 
     * @param payCount 
     */
    public void setPayCount(Integer payCount) {
        this.payCount = payCount;
    }

    /**
     * get 
     * @return Integer 
     */
    public Integer getPayCount() {
        return payCount;
    }


}
