package com.hadoop.hdfs.datastat;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Created by leochan on 2018/3/17.
 */
public class DataBean {

    private String accessDate;
    private String accessTime;
    private String currentUrl;
    private String httpMethod;
    private String ip;
    private String referer;
    private String synchronousId;
    private String userAgent;
    private String userId;
    private String username;


    public String getAccessDate() {
        return accessDate;
    }

    public void setAccessDate(String accessDate) {
        this.accessDate = accessDate;
    }

    public String getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(String accessTime) {
        this.accessTime = accessTime;
    }

    public String getCurrentUrl() {
        return currentUrl;
    }

    public void setCurrentUrl(String currentUrl) {
        this.currentUrl = currentUrl;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getSynchronousId() {
        return synchronousId;
    }

    public void setSynchronousId(String synchronousId) {
        this.synchronousId = synchronousId;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return accessDate + "\t"
                + accessTime + "\t"
                + currentUrl + "\t"
                + httpMethod + "\t"
                + ip + "\t"
                + referer + "\t"
                + synchronousId + "\t"
                + userAgent + "\t"
                + userId + "\t"
                + username;
    }
}
