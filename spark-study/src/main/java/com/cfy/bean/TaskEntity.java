package com.cfy.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * task 表 Entity
 */
public class TaskEntity implements Serializable {

    private static final long serialVersionUID = 3214L;

    // 
    private Integer taskId;

    // 
    private String taskName;

    // 
    private String createTime;

    // 
    private String startTime;

    // 
    private String finishTime;

    // 
    private String taskType;

    // 
    private String taskStatus;

    // 
    private String taskParam;



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
     * @param taskName 
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    /**
     * get 
     * @return String 
     */
    public String getTaskName() {
        return taskName;
    }


    /**
     * set 
     * @param createTime 
     */
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    /**
     * get 
     * @return String 
     */
    public String getCreateTime() {
        return createTime;
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
     * @param finishTime 
     */
    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    /**
     * get 
     * @return String 
     */
    public String getFinishTime() {
        return finishTime;
    }


    /**
     * set 
     * @param taskType 
     */
    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    /**
     * get 
     * @return String 
     */
    public String getTaskType() {
        return taskType;
    }


    /**
     * set 
     * @param taskStatus 
     */
    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    /**
     * get 
     * @return String 
     */
    public String getTaskStatus() {
        return taskStatus;
    }


    /**
     * set 
     * @param taskParam 
     */
    public void setTaskParam(String taskParam) {
        this.taskParam = taskParam;
    }

    /**
     * get 
     * @return String 
     */
    public String getTaskParam() {
        return taskParam;
    }


}
