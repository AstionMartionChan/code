package com.rltx.truck.broker.bo;

/**
 * Created by Leo_Chan on 2017/10/16.
 */
public class PersonQueryBo {

    // 是否请求成功
    private Boolean isSuccess;

    // 是否存在
    private Boolean isExist;

    // 响应内容
    private String responseMessage;

    // 姓名
    private String nameOfPerson;

    // 性别
    private String gender;

    // 身份证号码
    private String identityDocumentNumber;

    // 手机号
    private String mobileTelephoneNumber;

    // 从业资格证号
    private String qualificationCertificateNumber;

    // 许可证初始日期
    private String licenseInitialCollectionDate;

    // 有效日期 起始
    private String periodStartDate;

    // 有效日期 截止
    private String periodEndDate;

    // 联系地址
    private String communicationNumber;

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

    public String getNameOfPerson() {
        return nameOfPerson;
    }

    public void setNameOfPerson(String nameOfPerson) {
        this.nameOfPerson = nameOfPerson;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIdentityDocumentNumber() {
        return identityDocumentNumber;
    }

    public void setIdentityDocumentNumber(String identityDocumentNumber) {
        this.identityDocumentNumber = identityDocumentNumber;
    }

    public String getMobileTelephoneNumber() {
        return mobileTelephoneNumber;
    }

    public void setMobileTelephoneNumber(String mobileTelephoneNumber) {
        this.mobileTelephoneNumber = mobileTelephoneNumber;
    }

    public String getQualificationCertificateNumber() {
        return qualificationCertificateNumber;
    }

    public void setQualificationCertificateNumber(String qualificationCertificateNumber) {
        this.qualificationCertificateNumber = qualificationCertificateNumber;
    }

    public String getLicenseInitialCollectionDate() {
        return licenseInitialCollectionDate;
    }

    public void setLicenseInitialCollectionDate(String licenseInitialCollectionDate) {
        this.licenseInitialCollectionDate = licenseInitialCollectionDate;
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

    public String getCommunicationNumber() {
        return communicationNumber;
    }

    public void setCommunicationNumber(String communicationNumber) {
        this.communicationNumber = communicationNumber;
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
