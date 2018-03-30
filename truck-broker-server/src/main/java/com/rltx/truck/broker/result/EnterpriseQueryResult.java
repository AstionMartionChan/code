package com.rltx.truck.broker.result;

/**
 * Created by Leo_Chan on 2017/10/9.
 */
public class EnterpriseQueryResult {

    // 是否请求成功
    private Boolean isSuccess;

    // 是否存在
    private Boolean isExist;

    // 响应内容
    private String responseMessage;

    // 企业名称
    private String enterpriseName;

    // 通讯地址
    private String communicationAddress;

    // 业务范围
    private String businessScope;

    // 许可证批准日期
    private String permitGrantDate;

    // 起始有效日期
    private String periodStartDate;

    // 截止有效日期
    private String periodEndDate;

    // 认证单位
    private String certificationUnit;

    // 错误编码
    private String errorCode;

    // 错误信息
    private String errorMessage;


    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

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

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getCommunicationAddress() {
        return communicationAddress;
    }

    public void setCommunicationAddress(String communicationAddress) {
        this.communicationAddress = communicationAddress;
    }

    public String getBusinessScope() {
        return businessScope;
    }

    public void setBusinessScope(String businessScope) {
        this.businessScope = businessScope;
    }

    public String getPermitGrantDate() {
        return permitGrantDate;
    }

    public void setPermitGrantDate(String permitGrantDate) {
        this.permitGrantDate = permitGrantDate;
    }

    public String getPeriodStartDate() {
        return periodStartDate;
    }

    public void setPeriodStartDate(String periodStartDate) {
        this.periodStartDate = periodStartDate;
    }

    public String getPeriodEndDate() {
        return periodEndDate;
    }

    public void setPeriodEndDate(String periodEndDate) {
        this.periodEndDate = periodEndDate;
    }

    public String getCertificationUnit() {
        return certificationUnit;
    }

    public void setCertificationUnit(String certificationUnit) {
        this.certificationUnit = certificationUnit;
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
