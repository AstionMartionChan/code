package com.cfy.bean;

import java.util.Date;

/**
 * session_random_extract 表 Entity
 */
public class SessionRandomExtractEntity {

    // 
    private Long id;

    // 
    private Integer taskId;

    // 
    private String sessionId;

    // 
    private String startTime;

    // 
    private String searchKeywords;

    // 
    private String catagoryIds;



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
     * @param startTime 
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * get 
     * @return String 
     */
    public String getStartTime() {
        return startTime;
    }


    /**
     * set 
     * @param searchKeywords 
     */
    public void setSearchKeywords(String searchKeywords) {
        this.searchKeywords = searchKeywords;
    }

    /**
     * get 
     * @return String 
     */
    public String getSearchKeywords() {
        return searchKeywords;
    }


    /**
     * set 
     * @param catagoryIds 
     */
    public void setCatagoryIds(String catagoryIds) {
        this.catagoryIds = catagoryIds;
    }

    /**
     * get 
     * @return String 
     */
    public String getCatagoryIds() {
        return catagoryIds;
    }


}
