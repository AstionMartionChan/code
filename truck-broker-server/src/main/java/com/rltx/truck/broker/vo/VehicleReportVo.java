package com.rltx.truck.broker.vo;

/**
 * Created by Leo_Chan on 2017/9/14.
 */
public class VehicleReportVo {

    // 车牌号
    private String vehicleNumber;

    // 牌照类型代码
    private String licensePlateTypeCode;

    // 所有人
    private String owner;

    // 物流交换代码
    private String senderCode;

    // 物流交换密码
    private String senderPassword;


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
