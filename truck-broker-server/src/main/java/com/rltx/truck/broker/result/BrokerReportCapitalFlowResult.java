package com.rltx.truck.broker.result;

import java.util.Date;

/**
 * 上报资金流水表
 */
public class BrokerReportCapitalFlowResult {

    // 单证号
    private String code;
    // 托运单号（对应电子路单中12项）
    private String shippingNoteNumber;
    // 承运人（实际承运人，实际收取运输费的人员，如车队、司机等）
    private String carrier;
    // 车辆牌照号（例：浙A32153）
    private String vehicleNumber;
    // 牌照类型代码（01：大型汽车号牌，02：小型汽车号牌，99：其他号牌）（例：01）
    private String licensePlateTypeCode;
    // 付款方式代码1（33：银行汇票，39：银行转账，42：现金支付，7：第三方平台支付，71：支付宝支付，72：微信支付，9：其他方式支付，91：油卡支付，92：道路桥闸通行费支付）
    private String paymentMeansCode1;
    // 银行代码（如18行填银行转账，此字段必填，如中国银行代码为BKCH。详见代码集）
    private String bankCode;
    // 流水号/序列号1(银行或第三方支付平台的资金流水单号，现金等其他方式可填财务记账号)
    private String sequenceCode1;
    // 货币金额1（资金流水金额默认人民币）
    private Double monetaryAmount1;
    // 付款时间1
    private Date dateTime1;
    // 付款方式代码2（33：银行汇票，39：银行转账，42：现金支付，7：第三方平台支付，71：支付宝支付，72：微信支付，9：其他方式支付，91：油卡支付，92：道路桥闸通行费支付）
    private String paymentMeansCode2;
    // 货币金额1（资金流水金额默认人民币）
    private Double monetaryAmount2;
    // 流水号/序列号1(银行或第三方支付平台的资金流水单号，现金等其他方式可填财务记账号)
    private String sequenceCode2;
    // 付款时间1
    private Date dateTime2;
    // 付款方式代码3（33：银行汇票，39：银行转账，42：现金支付，7：第三方平台支付，71：支付宝支付，72：微信支付，9：其他方式支付，91：油卡支付，92：道路桥闸通行费支付）
    private String paymentMeansCode3;
    // 流水号/序列号1(银行或第三方支付平台的资金流水单号，现金等其他方式可填财务记账号)
    private String sequenceCode3;
    // 货币金额1（资金流水金额默认人民币）
    private Double monetaryAmount3;
    // 付款时间1
    private Date dateTime3;
    // 付款方式代码4（33：银行汇票，39：银行转账，42：现金支付，7：第三方平台支付，71：支付宝支付，72：微信支付，9：其他方式支付，91：油卡支付，92：道路桥闸通行费支付）
    private String paymentMeansCode4;
    // 流水号/序列号1(银行或第三方支付平台的资金流水单号，现金等其他方式可填财务记账号)
    private String sequenceCode4;
    // 货币金额1（资金流水金额默认人民币）
    private Double monetaryAmount4;
    // 付款时间1
    private Date dateTime4;
    // 描述
    private String description;


    // 无车承运人物流交换代码
    private String senderCode;
    // 物流交换密码
    private String senderPassword;
    // 执行结果
    private Boolean success;
    // 错误编号
    private String errorCode;
    // 错误信息
    private String errorMessage;


    /**
    * set 单证号
    * @param code 单证号
    */
    public void setCode(String code) {
    this.code = code;
    }

    /**
    * get 单证号
    * @return String 单证号
    */
    public String getCode() {
    return code;
    }


    /**
    * set 托运单号（对应电子路单中12项）
    * @param shippingNoteNumber 托运单号（对应电子路单中12项）
    */
    public void setShippingNoteNumber(String shippingNoteNumber) {
    this.shippingNoteNumber = shippingNoteNumber;
    }

    /**
    * get 托运单号（对应电子路单中12项）
    * @return String 托运单号（对应电子路单中12项）
    */
    public String getShippingNoteNumber() {
    return shippingNoteNumber;
    }


    /**
    * set 承运人（实际承运人，实际收取运输费的人员，如车队、司机等）
    * @param carrier 承运人（实际承运人，实际收取运输费的人员，如车队、司机等）
    */
    public void setCarrier(String carrier) {
    this.carrier = carrier;
    }

    /**
    * get 承运人（实际承运人，实际收取运输费的人员，如车队、司机等）
    * @return String 承运人（实际承运人，实际收取运输费的人员，如车队、司机等）
    */
    public String getCarrier() {
    return carrier;
    }


    /**
    * set 车辆牌照号（例：浙A32153）
    * @param vehicleNumber 车辆牌照号（例：浙A32153）
    */
    public void setVehicleNumber(String vehicleNumber) {
    this.vehicleNumber = vehicleNumber;
    }

    /**
    * get 车辆牌照号（例：浙A32153）
    * @return String 车辆牌照号（例：浙A32153）
    */
    public String getVehicleNumber() {
    return vehicleNumber;
    }


    /**
    * set 牌照类型代码（01：大型汽车号牌，02：小型汽车号牌，99：其他号牌）（例：01）
    * @param licensePlateTypeCode 牌照类型代码（01：大型汽车号牌，02：小型汽车号牌，99：其他号牌）（例：01）
    */
    public void setLicensePlateTypeCode(String licensePlateTypeCode) {
    this.licensePlateTypeCode = licensePlateTypeCode;
    }

    /**
    * get 牌照类型代码（01：大型汽车号牌，02：小型汽车号牌，99：其他号牌）（例：01）
    * @return String 牌照类型代码（01：大型汽车号牌，02：小型汽车号牌，99：其他号牌）（例：01）
    */
    public String getLicensePlateTypeCode() {
    return licensePlateTypeCode;
    }


    /**
    * set 付款方式代码1（33：银行汇票，39：银行转账，42：现金支付，7：第三方平台支付，71：支付宝支付，72：微信支付，9：其他方式支付，91：油卡支付，92：道路桥闸通行费支付）
    * @param paymentMeansCode1 付款方式代码1（33：银行汇票，39：银行转账，42：现金支付，7：第三方平台支付，71：支付宝支付，72：微信支付，9：其他方式支付，91：油卡支付，92：道路桥闸通行费支付）
    */
    public void setPaymentMeansCode1(String paymentMeansCode1) {
    this.paymentMeansCode1 = paymentMeansCode1;
    }

    /**
    * get 付款方式代码1（33：银行汇票，39：银行转账，42：现金支付，7：第三方平台支付，71：支付宝支付，72：微信支付，9：其他方式支付，91：油卡支付，92：道路桥闸通行费支付）
    * @return String 付款方式代码1（33：银行汇票，39：银行转账，42：现金支付，7：第三方平台支付，71：支付宝支付，72：微信支付，9：其他方式支付，91：油卡支付，92：道路桥闸通行费支付）
    */
    public String getPaymentMeansCode1() {
    return paymentMeansCode1;
    }


    /**
    * set 银行代码（如18行填银行转账，此字段必填，如中国银行代码为BKCH。详见代码集）
    * @param bankCode 银行代码（如18行填银行转账，此字段必填，如中国银行代码为BKCH。详见代码集）
    */
    public void setBankCode(String bankCode) {
    this.bankCode = bankCode;
    }

    /**
    * get 银行代码（如18行填银行转账，此字段必填，如中国银行代码为BKCH。详见代码集）
    * @return String 银行代码（如18行填银行转账，此字段必填，如中国银行代码为BKCH。详见代码集）
    */
    public String getBankCode() {
    return bankCode;
    }


    /**
    * set 流水号/序列号1(银行或第三方支付平台的资金流水单号，现金等其他方式可填财务记账号)
    * @param sequenceCode1 流水号/序列号1(银行或第三方支付平台的资金流水单号，现金等其他方式可填财务记账号)
    */
    public void setSequenceCode1(String sequenceCode1) {
    this.sequenceCode1 = sequenceCode1;
    }

    /**
    * get 流水号/序列号1(银行或第三方支付平台的资金流水单号，现金等其他方式可填财务记账号)
    * @return String 流水号/序列号1(银行或第三方支付平台的资金流水单号，现金等其他方式可填财务记账号)
    */
    public String getSequenceCode1() {
    return sequenceCode1;
    }


    /**
    * set 货币金额1（资金流水金额默认人民币）
    * @param monetaryAmount1 货币金额1（资金流水金额默认人民币）
    */
    public void setMonetaryAmount1(Double monetaryAmount1) {
    this.monetaryAmount1 = monetaryAmount1;
    }

    /**
    * get 货币金额1（资金流水金额默认人民币）
    * @return Double 货币金额1（资金流水金额默认人民币）
    */
    public Double getMonetaryAmount1() {
    return monetaryAmount1;
    }


    /**
    * set 付款时间1
    * @param dateTime1 付款时间1
    */
    public void setDateTime1(Date dateTime1) {
    this.dateTime1 = dateTime1;
    }

    /**
    * get 付款时间1
    * @return Date 付款时间1
    */
    public Date getDateTime1() {
    return dateTime1;
    }


    /**
    * set 付款方式代码2（33：银行汇票，39：银行转账，42：现金支付，7：第三方平台支付，71：支付宝支付，72：微信支付，9：其他方式支付，91：油卡支付，92：道路桥闸通行费支付）
    * @param paymentMeansCode2 付款方式代码2（33：银行汇票，39：银行转账，42：现金支付，7：第三方平台支付，71：支付宝支付，72：微信支付，9：其他方式支付，91：油卡支付，92：道路桥闸通行费支付）
    */
    public void setPaymentMeansCode2(String paymentMeansCode2) {
    this.paymentMeansCode2 = paymentMeansCode2;
    }

    /**
    * get 付款方式代码2（33：银行汇票，39：银行转账，42：现金支付，7：第三方平台支付，71：支付宝支付，72：微信支付，9：其他方式支付，91：油卡支付，92：道路桥闸通行费支付）
    * @return String 付款方式代码2（33：银行汇票，39：银行转账，42：现金支付，7：第三方平台支付，71：支付宝支付，72：微信支付，9：其他方式支付，91：油卡支付，92：道路桥闸通行费支付）
    */
    public String getPaymentMeansCode2() {
    return paymentMeansCode2;
    }


    /**
    * set 货币金额1（资金流水金额默认人民币）
    * @param monetaryAmount2 货币金额1（资金流水金额默认人民币）
    */
    public void setMonetaryAmount2(Double monetaryAmount2) {
    this.monetaryAmount2 = monetaryAmount2;
    }

    /**
    * get 货币金额1（资金流水金额默认人民币）
    * @return Double 货币金额1（资金流水金额默认人民币）
    */
    public Double getMonetaryAmount2() {
    return monetaryAmount2;
    }


    /**
    * set 流水号/序列号1(银行或第三方支付平台的资金流水单号，现金等其他方式可填财务记账号)
    * @param sequenceCode2 流水号/序列号1(银行或第三方支付平台的资金流水单号，现金等其他方式可填财务记账号)
    */
    public void setSequenceCode2(String sequenceCode2) {
    this.sequenceCode2 = sequenceCode2;
    }

    /**
    * get 流水号/序列号1(银行或第三方支付平台的资金流水单号，现金等其他方式可填财务记账号)
    * @return String 流水号/序列号1(银行或第三方支付平台的资金流水单号，现金等其他方式可填财务记账号)
    */
    public String getSequenceCode2() {
    return sequenceCode2;
    }


    /**
    * set 付款时间1
    * @param dateTime2 付款时间1
    */
    public void setDateTime2(Date dateTime2) {
    this.dateTime2 = dateTime2;
    }

    /**
    * get 付款时间1
    * @return Date 付款时间1
    */
    public Date getDateTime2() {
    return dateTime2;
    }


    /**
    * set 付款方式代码3（33：银行汇票，39：银行转账，42：现金支付，7：第三方平台支付，71：支付宝支付，72：微信支付，9：其他方式支付，91：油卡支付，92：道路桥闸通行费支付）
    * @param paymentMeansCode3 付款方式代码3（33：银行汇票，39：银行转账，42：现金支付，7：第三方平台支付，71：支付宝支付，72：微信支付，9：其他方式支付，91：油卡支付，92：道路桥闸通行费支付）
    */
    public void setPaymentMeansCode3(String paymentMeansCode3) {
    this.paymentMeansCode3 = paymentMeansCode3;
    }

    /**
    * get 付款方式代码3（33：银行汇票，39：银行转账，42：现金支付，7：第三方平台支付，71：支付宝支付，72：微信支付，9：其他方式支付，91：油卡支付，92：道路桥闸通行费支付）
    * @return String 付款方式代码3（33：银行汇票，39：银行转账，42：现金支付，7：第三方平台支付，71：支付宝支付，72：微信支付，9：其他方式支付，91：油卡支付，92：道路桥闸通行费支付）
    */
    public String getPaymentMeansCode3() {
    return paymentMeansCode3;
    }


    /**
    * set 流水号/序列号1(银行或第三方支付平台的资金流水单号，现金等其他方式可填财务记账号)
    * @param sequenceCode3 流水号/序列号1(银行或第三方支付平台的资金流水单号，现金等其他方式可填财务记账号)
    */
    public void setSequenceCode3(String sequenceCode3) {
    this.sequenceCode3 = sequenceCode3;
    }

    /**
    * get 流水号/序列号1(银行或第三方支付平台的资金流水单号，现金等其他方式可填财务记账号)
    * @return String 流水号/序列号1(银行或第三方支付平台的资金流水单号，现金等其他方式可填财务记账号)
    */
    public String getSequenceCode3() {
    return sequenceCode3;
    }


    /**
    * set 货币金额1（资金流水金额默认人民币）
    * @param monetaryAmount3 货币金额1（资金流水金额默认人民币）
    */
    public void setMonetaryAmount3(Double monetaryAmount3) {
    this.monetaryAmount3 = monetaryAmount3;
    }

    /**
    * get 货币金额1（资金流水金额默认人民币）
    * @return Double 货币金额1（资金流水金额默认人民币）
    */
    public Double getMonetaryAmount3() {
    return monetaryAmount3;
    }


    /**
    * set 付款时间1
    * @param dateTime3 付款时间1
    */
    public void setDateTime3(Date dateTime3) {
    this.dateTime3 = dateTime3;
    }

    /**
    * get 付款时间1
    * @return Date 付款时间1
    */
    public Date getDateTime3() {
    return dateTime3;
    }


    /**
    * set 付款方式代码4（33：银行汇票，39：银行转账，42：现金支付，7：第三方平台支付，71：支付宝支付，72：微信支付，9：其他方式支付，91：油卡支付，92：道路桥闸通行费支付）
    * @param paymentMeansCode4 付款方式代码4（33：银行汇票，39：银行转账，42：现金支付，7：第三方平台支付，71：支付宝支付，72：微信支付，9：其他方式支付，91：油卡支付，92：道路桥闸通行费支付）
    */
    public void setPaymentMeansCode4(String paymentMeansCode4) {
    this.paymentMeansCode4 = paymentMeansCode4;
    }

    /**
    * get 付款方式代码4（33：银行汇票，39：银行转账，42：现金支付，7：第三方平台支付，71：支付宝支付，72：微信支付，9：其他方式支付，91：油卡支付，92：道路桥闸通行费支付）
    * @return String 付款方式代码4（33：银行汇票，39：银行转账，42：现金支付，7：第三方平台支付，71：支付宝支付，72：微信支付，9：其他方式支付，91：油卡支付，92：道路桥闸通行费支付）
    */
    public String getPaymentMeansCode4() {
    return paymentMeansCode4;
    }


    /**
    * set 流水号/序列号1(银行或第三方支付平台的资金流水单号，现金等其他方式可填财务记账号)
    * @param sequenceCode4 流水号/序列号1(银行或第三方支付平台的资金流水单号，现金等其他方式可填财务记账号)
    */
    public void setSequenceCode4(String sequenceCode4) {
    this.sequenceCode4 = sequenceCode4;
    }

    /**
    * get 流水号/序列号1(银行或第三方支付平台的资金流水单号，现金等其他方式可填财务记账号)
    * @return String 流水号/序列号1(银行或第三方支付平台的资金流水单号，现金等其他方式可填财务记账号)
    */
    public String getSequenceCode4() {
    return sequenceCode4;
    }


    /**
    * set 货币金额1（资金流水金额默认人民币）
    * @param monetaryAmount4 货币金额1（资金流水金额默认人民币）
    */
    public void setMonetaryAmount4(Double monetaryAmount4) {
    this.monetaryAmount4 = monetaryAmount4;
    }

    /**
    * get 货币金额1（资金流水金额默认人民币）
    * @return Double 货币金额1（资金流水金额默认人民币）
    */
    public Double getMonetaryAmount4() {
    return monetaryAmount4;
    }


    /**
    * set 付款时间1
    * @param dateTime4 付款时间1
    */
    public void setDateTime4(Date dateTime4) {
    this.dateTime4 = dateTime4;
    }

    /**
    * get 付款时间1
    * @return Date 付款时间1
    */
    public Date getDateTime4() {
    return dateTime4;
    }


    /**
    * set 描述
    * @param description 描述
    */
    public void setDescription(String description) {
    this.description = description;
    }

    /**
    * get 描述
    * @return String 描述
    */
    public String getDescription() {
    return description;
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

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
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
