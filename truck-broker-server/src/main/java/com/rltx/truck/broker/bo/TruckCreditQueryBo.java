package com.rltx.truck.broker.bo;

/**
 * Created by Leo_Chan on 2017/4/17.
 */
public class TruckCreditQueryBo {

    // 是否成功
    private Boolean isSuccess;

    // 错误编号
    private String errorCode;

    // 错误信息
    private String errorMsg;

    // 返回报文
    private String resultXml;


    public Boolean getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getResultXml() {
        return resultXml;
    }

    public void setResultXml(String resultXml) {
        this.resultXml = resultXml;
    }
}
