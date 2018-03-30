package com.rltx.truck.broker.vo;

/**
 * Created by Leo_Chan on 2017/9/14.
 */
public class VehicleReportDocumentVo {

    /*
    name: 报文参考号
    */
    private String messageReferenceNumber;

    /*
    name: 单证名称
     */
    private String documentName;

    /*
    name: 报文版本号
    */
    private String documentVersionNumber;

    /*
    name: 发送方代码
     */
    private String senderCode;

    /*
    name: 接收方代码
     */
    private String recipientCode;

    /*
    name: 发送日期时间
     */
    private String messageSendingDateTime;

    // 车牌号
    private String vehicleNumber;

    // 牌照类型代码
    private String licensePlateTypeCode;

    // 所有人
    private String owner;

    public String getMessageReferenceNumber() {
        return messageReferenceNumber;
    }

    public void setMessageReferenceNumber(String messageReferenceNumber) {
        this.messageReferenceNumber = messageReferenceNumber;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentVersionNumber() {
        return documentVersionNumber;
    }

    public void setDocumentVersionNumber(String documentVersionNumber) {
        this.documentVersionNumber = documentVersionNumber;
    }

    public String getSenderCode() {
        return senderCode;
    }

    public void setSenderCode(String senderCode) {
        this.senderCode = senderCode;
    }

    public String getRecipientCode() {
        return recipientCode;
    }

    public void setRecipientCode(String recipientCode) {
        this.recipientCode = recipientCode;
    }

    public String getMessageSendingDateTime() {
        return messageSendingDateTime;
    }

    public void setMessageSendingDateTime(String messageSendingDateTime) {
        this.messageSendingDateTime = messageSendingDateTime;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getLicensePlateTypeCode() {
        return licensePlateTypeCode;
    }

    public void setLicensePlateTypeCode(String licensePlateTypeCode) {
        this.licensePlateTypeCode = licensePlateTypeCode;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
