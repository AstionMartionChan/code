package com.rltx.truck.broker.bo;

/**
 * Created by Leo_Chan on 2017/4/14.
 */
public class TruckCarrierReportBo {

    // 是否上报成功
    private Boolean isSuccess;

    // 报错信息
    private String errorMsg;

    // 上报报文
    private String reportMessage;

    // 响应报文
    private String responseMessage;

    public Boolean getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getReportMessage() {
        return reportMessage;
    }

    public void setReportMessage(String reportMessage) {
        this.reportMessage = reportMessage;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }


}
