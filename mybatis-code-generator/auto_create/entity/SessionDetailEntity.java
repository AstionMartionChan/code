package com.cfy.po;

import java.util.Date;

/**
 * session_detail 表 Entity
 */
public class SessionDetailEntity {

    // 
    private Long id;

    // 
    private Integer taskId;

    // 
    private Integer userId;

    // 
    private String sessionId;

    // 
    private Integer pageId;

    // 
    private String actionTime;

    // 
    private String searchKeyword;

    // 
    private Integer clickCategoryId;

    // 
    private Integer clickProductId;

    // 
    private String orderCategoryIds;

    // 
    private String orderProductIds;

    // 
    private String payCategoryIds;

    // 
    private String payProductIds;



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
     * @param userId 
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * get 
     * @return Integer 
     */
    public Integer getUserId() {
        return userId;
    }


    /**
     * set 
     * @param sessionId 
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * get 
     * @return String 
     */
    public String getSessionId() {
        return sessionId;
    }


    /**
     * set 
     * @param pageId 
     */
    public void setPageId(Integer pageId) {
        this.pageId = pageId;
    }

    /**
     * get 
     * @return Integer 
     */
    public Integer getPageId() {
        return pageId;
    }


    /**
     * set 
     * @param actionTime 
     */
    public void setActionTime(String actionTime) {
        this.actionTime = actionTime;
    }

    /**
     * get 
     * @return String 
     */
    public String getActionTime() {
        return actionTime;
    }


    /**
     * set 
     * @param searchKeyword 
     */
    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }

    /**
     * get 
     * @return String 
     */
    public String getSearchKeyword() {
        return searchKeyword;
    }


    /**
     * set 
     * @param clickCategoryId 
     */
    public void setClickCategoryId(Integer clickCategoryId) {
        this.clickCategoryId = clickCategoryId;
    }

    /**
     * get 
     * @return Integer 
     */
    public Integer getClickCategoryId() {
        return clickCategoryId;
    }


    /**
     * set 
     * @param clickProductId 
     */
    public void setClickProductId(Integer clickProductId) {
        this.clickProductId = clickProductId;
    }

    /**
     * get 
     * @return Integer 
     */
    public Integer getClickProductId() {
        return clickProductId;
    }


    /**
     * set 
     * @param orderCategoryIds 
     */
    public void setOrderCategoryIds(String orderCategoryIds) {
        this.orderCategoryIds = orderCategoryIds;
    }

    /**
     * get 
     * @return String 
     */
    public String getOrderCategoryIds() {
        return orderCategoryIds;
    }


    /**
     * set 
     * @param orderProductIds 
     */
    public void setOrderProductIds(String orderProductIds) {
        this.orderProductIds = orderProductIds;
    }

    /**
     * get 
     * @return String 
     */
    public String getOrderProductIds() {
        return orderProductIds;
    }


    /**
     * set 
     * @param payCategoryIds 
     */
    public void setPayCategoryIds(String payCategoryIds) {
        this.payCategoryIds = payCategoryIds;
    }

    /**
     * get 
     * @return String 
     */
    public String getPayCategoryIds() {
        return payCategoryIds;
    }


    /**
     * set 
     * @param payProductIds 
     */
    public void setPayProductIds(String payProductIds) {
        this.payProductIds = payProductIds;
    }

    /**
     * get 
     * @return String 
     */
    public String getPayProductIds() {
        return payProductIds;
    }


}
