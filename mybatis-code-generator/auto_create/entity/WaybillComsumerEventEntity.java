package com.rltx.waybill.po;

import java.util.Date;

/**
 * waybill_comsumer_event 表 Entity
 */
public class WaybillComsumerEventEntity {

    // 自增id
    private Long id;

    // 事件code
    private String eventCode;

    // 消费者名称
    private String consumerName;

    // 主题
    private String topic;

    // 消息体
    private String message;

    // 状态
    private String status;

    // 失败原因
    private String failedReason;

    // 创建时间
    private Date createTime;

    // 更新时间
    private Date updateTime;



    /**
     * set 自增id
     * @param id 自增id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * get 自增id
     * @return Long 自增id
     */
    public Long getId() {
        return id;
    }


    /**
     * set 事件code
     * @param eventCode 事件code
     */
    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    /**
     * get 事件code
     * @return String 事件code
     */
    public String getEventCode() {
        return eventCode;
    }


    /**
     * set 消费者名称
     * @param consumerName 消费者名称
     */
    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }

    /**
     * get 消费者名称
     * @return String 消费者名称
     */
    public String getConsumerName() {
        return consumerName;
    }


    /**
     * set 主题
     * @param topic 主题
     */
    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * get 主题
     * @return String 主题
     */
    public String getTopic() {
        return topic;
    }


    /**
     * set 消息体
     * @param message 消息体
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * get 消息体
     * @return String 消息体
     */
    public String getMessage() {
        return message;
    }


    /**
     * set 状态
     * @param status 状态
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * get 状态
     * @return String 状态
     */
    public String getStatus() {
        return status;
    }


    /**
     * set 失败原因
     * @param failedReason 失败原因
     */
    public void setFailedReason(String failedReason) {
        this.failedReason = failedReason;
    }

    /**
     * get 失败原因
     * @return String 失败原因
     */
    public String getFailedReason() {
        return failedReason;
    }


    /**
     * set 创建时间
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * get 创建时间
     * @return Date 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }


    /**
     * set 更新时间
     * @param updateTime 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * get 更新时间
     * @return Date 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }


}
