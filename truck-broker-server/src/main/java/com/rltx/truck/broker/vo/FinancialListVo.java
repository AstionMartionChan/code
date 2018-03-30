package com.rltx.truck.broker.vo;

public class FinancialListVo {

    /*
    name: 付款方式代码
     */
    private String paymentMeansCode;

    /*
    name: 银行代码
     */
    private String bankCode;

    /*
    name: 流水号/序列号
     */
    private String sequenceCode;

    /*
    name: 货币金额
     */
    private String monetaryAmount;

    /*
    name: 日期时间
     */
    private String DateTime;

    public String getPaymentMeansCode() {
        return paymentMeansCode;
    }

    public void setPaymentMeansCode(String paymentMeansCode) {
        this.paymentMeansCode = paymentMeansCode;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getSequenceCode() {
        return sequenceCode;
    }

    public void setSequenceCode(String sequenceCode) {
        this.sequenceCode = sequenceCode;
    }

    public String getMonetaryAmount() {
        return monetaryAmount;
    }

    public void setMonetaryAmount(String monetaryAmount) {
        this.monetaryAmount = monetaryAmount;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }
}
