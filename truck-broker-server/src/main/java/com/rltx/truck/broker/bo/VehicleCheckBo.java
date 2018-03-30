package com.rltx.truck.broker.bo;

/**
 * Created by Leo_Chan on 2017/9/12.
 */
public class VehicleCheckBo {

    // 是否请求成功
    private Boolean isSuccess;

    // 车辆是否存在
    private Boolean isExist;

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
