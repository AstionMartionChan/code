package com.wutiao.realtime.application.storm.model;

import java.io.Serializable;

/**
 * @author zhouml on 14/09/2018.
 */
public class WtLog implements Serializable {

    private String phone;

    private String ouid;

    private String did;

    private String event;

    private String riskLevel;

    /**
     * 是否是数美日志
     */
    private Boolean shumei_flag;

    private String log;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOuid() {
        return ouid;
    }

    public void setOuid(String ouid) {
        this.ouid = ouid;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Boolean getShumei_flag() {
        return shumei_flag;
    }

    public void setShumei_flag(Boolean shumei_flag) {
        this.shumei_flag = shumei_flag;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    @Override
    public String toString() {
        return "WtLog{" +
                "phone='" + phone + '\'' +
                ", ouid='" + ouid + '\'' +
                ", did='" + did + '\'' +
                ", event='" + event + '\'' +
                ", riskLevel='" + riskLevel + '\'' +
                ", shumei_flag=" + shumei_flag +
                ", log='" + log + '\'' +
                '}';
    }

}
