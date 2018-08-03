package com.cfy.po;

import java.util.Date;

/**
 * page_rate 表 Entity
 */
public class PageRateEntity {

    // 
    private Long id;

    // 
    private Long taskId;

    // 
    private String rate;



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
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    /**
     * get 
     * @return Long 
     */
    public Long getTaskId() {
        return taskId;
    }


    /**
     * set 
     * @param rate 
     */
    public void setRate(String rate) {
        this.rate = rate;
    }

    /**
     * get 
     * @return String 
     */
    public String getRate() {
        return rate;
    }


}
