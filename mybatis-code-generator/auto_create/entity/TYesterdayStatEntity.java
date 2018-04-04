package com.rltx.ipatio.po;

import java.util.Date;

/**
 * t_yesterday_stat 表 Entity
 */
public class TYesterdayStatEntity {

    // 自增id
    private String id;

    // 访问后台接口url
    private String url;

    // 次日留存的uid
    private String userId;

    // 次日留存的登录账号
    private String account;

    // 创建时间
    private String createTime;



    /**
     * set 自增id
     * @param id 自增id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * get 自增id
     * @return String 自增id
     */
    public String getId() {
        return id;
    }


    /**
     * set 访问后台接口url
     * @param url 访问后台接口url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * get 访问后台接口url
     * @return String 访问后台接口url
     */
    public String getUrl() {
        return url;
    }


    /**
     * set 次日留存的uid
     * @param userId 次日留存的uid
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * get 次日留存的uid
     * @return String 次日留存的uid
     */
    public String getUserId() {
        return userId;
    }


    /**
     * set 次日留存的登录账号
     * @param account 次日留存的登录账号
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * get 次日留存的登录账号
     * @return String 次日留存的登录账号
     */
    public String getAccount() {
        return account;
    }


    /**
     * set 创建时间
     * @param createTime 创建时间
     */
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    /**
     * get 创建时间
     * @return String 创建时间
     */
    public String getCreateTime() {
        return createTime;
    }


}
