package com.rltx.truck.broker.vo;

import java.util.List;

/**
 * Created by Leo_Chan on 2017/8/22.
 */
public class WaybillReportDocumentVo {


    /*
    name: 报文参考号
    desc: 报文的唯一标识符，由系统按GUID规则自动产生
    */
    private String messageReferenceNumber;

    /*
    name: 单证名称
    desc: 无车承运人电子路单
     */
    private String documentName;

    /*
    name: 报文版本号
    desc: 2015WCCYR
    */
    private String documentVersionNumber;

    /*
    name: 发送方代码
    desc: 无车承运人物流交换代码
     */
    private String senderCode;

    /*
    name: 接收方代码
    desc: 监测平台代码
     */
    private String recipientCode;

    /*
    name: 发送日期时间
    desc: 格式YYYYMMDDhhmmss
     */
    private String messageSendingDateTime;

    /*
    name: 报文功能代码
    desc: 9（新增） 5（更改） 1（删除）  详见代码集4.2.1
     */
    private String messageFunctionCode;

    /*
    name: 原始单号
    desc: 必填。上游企业委托运输单号
     */
    private String originalDocumentNumber;

    /*
    name: 托运单号
    desc: 必填。本电子路单号
     */
    private String shippingNoteNumber;

    /*
    name: 承运人
    desc: 必填。无车承运试点企业名称
     */
    private String carrier;

    /*
    name: 统一社会信用代码
    desc: 选填，无车承运人的统一社会信用代码
     */
    private String unifiedSocialCreditIdentifier;

    /*
    name: 许可证编号
    desc: 选填，无车承运人的道路运输经营许可证（无车承运）编号
     */
    private String permitNumber;

    /*
    name: 托运日期时间
    desc: 必填。无车承运人系统正式生成运单的日期时间，YYYYMMDDhhmmss
     */
    private String consignmentDateTime;

    /*
    name: 业务类型代码
    desc: 必填。详见代码集4.2.2
     */
    private String businessTypeCode;

    /*
    name: 发运实际日期时间
    desc: 必填。货物装车后的发车时间YYYYMMDDhhmmss
     */
    private String despatchActualDateTime;

    /*
    name: 收货日期时间
    desc: 必填。货物运到后的签收时间。如签收时间无法确定，填货物运到收货地的时间YYYYMMDDhhmmss
     */
    private String goodsReceiptDateTime;

    /*
    name: 发货人
    desc: 选填。单位或个人，如集装箱运输业务无法明确实际发货人可填货代信息
     */
    private String consignor;

    /*
    name: 个人证件号
    desc: 选填。
     */
    private String personalIdentityDocument;

    /*
    name: 装货地点
    desc: 选填。实际装货的地点
     */
    private String placeOfLoading;

    /*
    name: 国家行政区划代码
    desc: 必填。装货地点的国家行政区划代码，参照GB/T 2260《中华人民共和国行政区划代码》的代码，精确到区县。
     */
    private String consignorCountrySubdivisionCode;

    /*
    name: 收货人
    desc: 选填。单位或个人
     */
    private String consignee;

    /*
    name: 收货地点
    desc: 选填。具体的收货地址
     */
    private String goodsReceiptPlace;

    /*
    name: 国家行政区划代码
    desc: 必填。收货地点的国家行政区划代码，参照GB/T 2260《中华人民共和国行政区划代码》的代码，精确到区县。
     */
    private String consigneeCountrySubdivisionCode;

    /*
    name: 货币总金额
    desc: 必填。托运人付给无车承运人企业的运输费用，货币单位为人民币，保留3位小数，如整数的话，
    以.000填充。如是一笔业务分几辆车运，需将托运人针对这笔业务付给无车承运人企业的运输费用分摊到每辆车上。
     */
    private Double totalMonetaryAmount;

    /*
    name: 备注
    desc: 选填。
     */
    private String remark;

    /*
    name: 牌照类型代码
    desc: 必填。详见代码集4.2.3
     */
    private String licensePlateTypeCode;

    /*
    name: 车辆牌照号
    desc: 必填。
     */
    private String vehicleNumber;

    /*
    name: 车辆分类代码
    desc: 必填。详见代码集4.2.4
     */
    private String vehicleClassificationCode;

    /*
    name: 车辆载质量
    desc: 必填。默认单位：吨，保留两位小数，如整数的话，以.00填充。小数点不计入总长。
     */
    private Double vehicleTonnage;

    /*
    name: 道路运输证号
    desc: 必填。车辆的道路运输证号，填道路运输证证件编号
     */
    private String roadTransportCertificateNumber;

    /*
    name: 挂车牌照号
    desc: 选填。
     */
    private String trailerVehiclePlateNumber;

    /*
    name: 所有人
    desc: 选填。车辆所有人（或企业）的名称或姓名
     */
    private String owner;

    /*
    name: 许可证编号
    desc: 选填。车辆所属业户的道路运输经营许可证编号
     */
    private String vehiclePermitNumber;

    /*
    name: 司机信息
    desc: 选填。如运输过程中有多个驾驶员，可循环
     */
    private List<DriverDocumentVo> driver;

    /*
    name: 货物信息
    desc: 必填。如一车货有不同货物，则可循环
     */
    private List<GoodsInfoDocumentVo> goodsInfo;

    /*
    name: 自由文本
    desc: 选填。
     */
    private String freeText;


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

    public String getOriginalDocumentNumber() {
        return originalDocumentNumber;
    }

    public void setOriginalDocumentNumber(String originalDocumentNumber) {
        this.originalDocumentNumber = originalDocumentNumber;
    }

    public String getShippingNoteNumber() {
        return shippingNoteNumber;
    }

    public void setShippingNoteNumber(String shippingNoteNumber) {
        this.shippingNoteNumber = shippingNoteNumber;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getUnifiedSocialCreditIdentifier() {
        return unifiedSocialCreditIdentifier;
    }

    public void setUnifiedSocialCreditIdentifier(String unifiedSocialCreditIdentifier) {
        this.unifiedSocialCreditIdentifier = unifiedSocialCreditIdentifier;
    }

    public String getPermitNumber() {
        return permitNumber;
    }

    public void setPermitNumber(String permitNumber) {
        this.permitNumber = permitNumber;
    }

    public String getConsignmentDateTime() {
        return consignmentDateTime;
    }

    public void setConsignmentDateTime(String consignmentDateTime) {
        this.consignmentDateTime = consignmentDateTime;
    }

    public String getBusinessTypeCode() {
        return businessTypeCode;
    }

    public void setBusinessTypeCode(String businessTypeCode) {
        this.businessTypeCode = businessTypeCode;
    }

    public String getDespatchActualDateTime() {
        return despatchActualDateTime;
    }

    public void setDespatchActualDateTime(String despatchActualDateTime) {
        this.despatchActualDateTime = despatchActualDateTime;
    }

    public String getGoodsReceiptDateTime() {
        return goodsReceiptDateTime;
    }

    public void setGoodsReceiptDateTime(String goodsReceiptDateTime) {
        this.goodsReceiptDateTime = goodsReceiptDateTime;
    }

    public String getConsignor() {
        return consignor;
    }

    public void setConsignor(String consignor) {
        this.consignor = consignor;
    }

    public String getPersonalIdentityDocument() {
        return personalIdentityDocument;
    }

    public void setPersonalIdentityDocument(String personalIdentityDocument) {
        this.personalIdentityDocument = personalIdentityDocument;
    }

    public String getPlaceOfLoading() {
        return placeOfLoading;
    }

    public void setPlaceOfLoading(String placeOfLoading) {
        this.placeOfLoading = placeOfLoading;
    }

    public String getConsignorCountrySubdivisionCode() {
        return consignorCountrySubdivisionCode;
    }

    public void setConsignorCountrySubdivisionCode(String consignorCountrySubdivisionCode) {
        this.consignorCountrySubdivisionCode = consignorCountrySubdivisionCode;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getGoodsReceiptPlace() {
        return goodsReceiptPlace;
    }

    public void setGoodsReceiptPlace(String goodsReceiptPlace) {
        this.goodsReceiptPlace = goodsReceiptPlace;
    }

    public String getConsigneeCountrySubdivisionCode() {
        return consigneeCountrySubdivisionCode;
    }

    public void setConsigneeCountrySubdivisionCode(String consigneeCountrySubdivisionCode) {
        this.consigneeCountrySubdivisionCode = consigneeCountrySubdivisionCode;
    }

    public Double getTotalMonetaryAmount() {
        return totalMonetaryAmount;
    }

    public void setTotalMonetaryAmount(Double totalMonetaryAmount) {
        this.totalMonetaryAmount = totalMonetaryAmount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getLicensePlateTypeCode() {
        return licensePlateTypeCode;
    }

    public void setLicensePlateTypeCode(String licensePlateTypeCode) {
        this.licensePlateTypeCode = licensePlateTypeCode;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleClassificationCode() {
        return vehicleClassificationCode;
    }

    public void setVehicleClassificationCode(String vehicleClassificationCode) {
        this.vehicleClassificationCode = vehicleClassificationCode;
    }

    public Double getVehicleTonnage() {
        return vehicleTonnage;
    }

    public void setVehicleTonnage(Double vehicleTonnage) {
        this.vehicleTonnage = vehicleTonnage;
    }

    public String getRoadTransportCertificateNumber() {
        return roadTransportCertificateNumber;
    }

    public void setRoadTransportCertificateNumber(String roadTransportCertificateNumber) {
        this.roadTransportCertificateNumber = roadTransportCertificateNumber;
    }

    public String getTrailerVehiclePlateNumber() {
        return trailerVehiclePlateNumber;
    }

    public void setTrailerVehiclePlateNumber(String trailerVehiclePlateNumber) {
        this.trailerVehiclePlateNumber = trailerVehiclePlateNumber;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getVehiclePermitNumber() {
        return vehiclePermitNumber;
    }

    public void setVehiclePermitNumber(String vehiclePermitNumber) {
        this.vehiclePermitNumber = vehiclePermitNumber;
    }

    public List<DriverDocumentVo> getDriver() {
        return driver;
    }

    public void setDriver(List<DriverDocumentVo> driver) {
        this.driver = driver;
    }

    public List<GoodsInfoDocumentVo> getGoodsInfo() {
        return goodsInfo;
    }

    public void setGoodsInfo(List<GoodsInfoDocumentVo> goodsInfo) {
        this.goodsInfo = goodsInfo;
    }

    public String getFreeText() {
        return freeText;
    }

    public void setFreeText(String freeText) {
        this.freeText = freeText;
    }
}
