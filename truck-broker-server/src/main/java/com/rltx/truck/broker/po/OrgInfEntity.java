package com.rltx.truck.broker.po;

/**
 *  组织信息po
 */
public class OrgInfEntity {

    // 自增id
    private Long id;

    // 物流交换代码
    private String senderCode;

    // 物流交换密码
    private String senderPassword;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenderCode() {
        return senderCode;
    }

    public void setSenderCode(String senderCode) {
        this.senderCode = senderCode;
    }

    public String getSenderPassword() {
        return senderPassword;
    }

    public void setSenderPassword(String senderPassword) {
        this.senderPassword = senderPassword;
    }
}
