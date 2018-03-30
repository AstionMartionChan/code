package com.rltx.truck.broker.result;

/**
 * Created by Leo_Chan on 2017/10/9.
 */
public class VehicleQueryResult {

    // 是否请求成功
    private Boolean isSuccess;

    // 是否存在
    private Boolean isExist;

    // 车牌号
    private String vehicleNumber;

    // 牌照类型
    private String licensePlateTypeCode;

    // 所有人
    private String owner;

    // 车辆类型
    private String vehicleClassification;

    // 车辆长度
    private String vehicleLength;

    // 车辆宽度
    private String vehicleWidth;

    // 车辆高度
    private String vehicleHeight;

    // 业务状态
    private String businessState;

    // 业务状态代码
    private String businessStateCode;

    // 响应内容
    private String responseMessage;

    // 错误编码
    private String errorCode;

    // 错误信息
    private String errorMessage;

    public Boolean getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public Boolean getIsExist() {
        return isExist;
    }

    public void setIsExist(Boolean isExist) {
        this.isExist = isExist;
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

    public String getVehicleClassification() {
        return vehicleClassification;
    }

    public void setVehicleClassification(String vehicleClassification) {
        this.vehicleClassification = vehicleClassification;
    }

    public String getVehicleLength() {
        return vehicleLength;
    }

    public void setVehicleLength(String vehicleLength) {
        this.vehicleLength = vehicleLength;
    }

    public String getVehicleWidth() {
        return vehicleWidth;
    }

    public void setVehicleWidth(String vehicleWidth) {
        this.vehicleWidth = vehicleWidth;
    }

    public String getVehicleHeight() {
        return vehicleHeight;
    }

    public void setVehicleHeight(String vehicleHeight) {
        this.vehicleHeight = vehicleHeight;
    }

    public String getBusinessState() {
        return businessState;
    }

    public void setBusinessState(String businessState) {
        this.businessState = businessState;
    }

    public String getBusinessStateCode() {
        return businessStateCode;
    }

    public void setBusinessStateCode(String businessStateCode) {
        this.businessStateCode = businessStateCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
