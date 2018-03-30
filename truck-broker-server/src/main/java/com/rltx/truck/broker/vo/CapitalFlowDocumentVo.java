package com.rltx.truck.broker.vo;

import java.util.List;

/**
 * Created by Leo_Chan on 2017/9/14.
 */
public class CapitalFlowDocumentVo {

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

    /*
    name: 报文功能代码
    desc: 9（新增） 5（更改） 1（删除）  详见代码集4.2.1
     */
    private String messageFunctionCode;

    /*
    name: 单证号
     */
    private String documentNumber;

    /*
    name: 承运人
     */
    private String carrier;

    /*
    name: 车辆牌照号
     */
    private String vehicleNumber;

    /*
    name: 牌照类型代码
     */
    private String licensePlateTypeCode;

    /*
    name: 运单列表
     */
    private List<ShippingNoteListVo> shippingNoteList;

    /*
    name: 财务列表
     */
    private List<FinancialListVo> financialList;

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

    public String getMessageFunctionCode() {
        return messageFunctionCode;
    }

    public void setMessageFunctionCode(String messageFunctionCode) {
        this.messageFunctionCode = messageFunctionCode;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
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

    public List<ShippingNoteListVo> getShippingNoteList() {
        return shippingNoteList;
    }

    public void setShippingNoteList(List<ShippingNoteListVo> shippingNoteList) {
        this.shippingNoteList = shippingNoteList;
    }

    public List<FinancialListVo> getFinancialList() {
        return financialList;
    }

    public void setFinancialList(List<FinancialListVo> financialList) {
        this.financialList = financialList;
    }
}
