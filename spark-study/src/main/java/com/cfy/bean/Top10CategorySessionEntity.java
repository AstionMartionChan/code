package com.cfy.bean;

import java.util.Date;

/**
 * top10_category_session 表 Entity
 */
public class Top10CategorySessionEntity {

    // 
    private Long id;

    // 
    private Integer taskId;

    // 
    private Integer categoryId;

    // 
    private String sessionId;

    // 
    private Integer clickCount;



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


}
