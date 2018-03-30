package com.rltx.truck.broker.vo;

/**
 * Created by Leo_Chan on 2017/4/17.
 */
public class TruckCreditQueryDocumentVo {

    // 牌照号
    private String vehicleNumber;

    // 牌照类型代码
    private String licensePlateTypeCode;

    // 道路许可证
    private String roadTransportCertificateNumber;

    // 查询类型代码
    private String searchTypeCode;

    // 开始时间
    private String startTime;

    // 结束时间
    private String endTime;

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

    public String getRoadTransportCertificateNumber() {
        return roadTransportCertificateNumber;
    }

    public void setRoadTransportCertificateNumber(String roadTransportCertificateNumber) {
        this.roadTransportCertificateNumber = roadTransportCertificateNumber;
    }

    public String getSearchTypeCode() {
        return searchTypeCode;
    }

    public void setSearchTypeCode(String searchTypeCode) {
        this.searchTypeCode = searchTypeCode;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
