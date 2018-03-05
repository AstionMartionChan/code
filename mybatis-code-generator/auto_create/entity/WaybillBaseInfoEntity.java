package com.rltx.waybill.po;

import java.util.Date;

/**
 * waybill_base_info 表 Entity
 */
public class WaybillBaseInfoEntity {

    // 自增ID
    private Long id;

    // 运单编码
    private String code;

    // 运单号
    private String waybillNo;

    // 原始物流订单编码
    private String rootLogisticsCode;

    // 物流订单编码
    private String logisticsCode;

    // 货物名称
    private String goodsName;

    // 货物单价
    private Double goodsPrice;

    // 货物单价单位编码
    private String goodsPriceUnitCode;

    // 货物数量
    private Double goodsAmount;

    // 货物数量单位编码
    private String goodsAmountUnitCode;

    // 货物重量
    private Double goodsWeight;

    // 货物重量单位编码
    private String goodsWeightUnitCode;

    // 货物体积
    private Double goodsVolume;

    // 货物体积单位编码
    private String goodsVolumeUnitCode;

    // 运输路线编码
    private String routeCode;

    // 运输线路名称
    private String routeName;

    // 标准运距(单位公里)
    private Double standardDistance;

    // 标准时长
    private Double standardTime;

    // 标准时长单位编码
    private String standardTimeUnitCode;

    // 发货单位名称
    private String loadingOrgName;

    // 发货联系人姓名
    private String loadingUserFullName;

    // 发货联系人电话
    private String loadingUserPhone;

    // 发货地址省份编码
    private String loadingProvinceCode;

    // 发货地址城市编码
    private String loadingCityCode;

    // 发货地址区县编码
    private String loadingCountyCode;

    // 发货地址（具体地址）
    private String loadingAddr;

    // 收货单位名称
    private String unloadingOrgName;

    // 收货联系人姓名
    private String unloadingUserFullName;

    // 收货联系人电话
    private String unloadingUserPhone;

    // 收货地址省份编码
    private String unloadingProvinceCode;

    // 收货地址城市编码
    private String unloadingCityCode;

    // 收货地址区县编码
    private String unloadingCountyCode;

    // 收货地址（具体地址）
    private String unloadingAddr;

    // 车辆许可证号
    private String truckLicense;

    // 车型编码
    private String truckModelCode;

    // 车型简称
    private String truckModelName;

    // 车辆编码
    private String truckCode;

    // 车牌号
    private String truckLicenseNo;

    // 牌照类型
    private String licensePlateTypeCode;

    // 挂车车辆编码
    private String trailerTruckCode;

    // 挂车车牌号
    private String trailerTruckLicenseNo;

    // 行驶证号码
    private String drivingLicenseNo;

    // 车长
    private Double truckLength;

    // 车长单位编码
    private String truckLengthUnitCode;

    // 动力类型
    private Integer truckPowerType;

    // 核载重量（From车辆表）
    private Double regTonnage;

    // 核载重量单位编码
    private String regTonnageUnitCode;

    // 司机编码
    private String driverCode;

    // 司机姓名
    private String driverFullName;

    // 司机电话
    private String driverPhone;

    // 驾驶证号
    private String driverLicenseNo;

    // 副驾驶司机编码（执行人）
    private String viceDriverCode;

    // 副驾驶司机姓名
    private String viceDriverFullName;

    // 副驾驶司机电话
    private String viceDriverPhone;

    // 卖方客户组织名称
    private String sellerOrgName;

    // 买方客户组织名称
    private String customerOrgName;

    // 运价编码
    private String ratesCode;

    // 货物计量标准（1、吨，2、方，3、件）
    private Integer meterageType;

    // 司机运价
    private Double driverPrice;

    // 司机运价单位编码
    private String driverPriceUnitCode;

    // 预估结算货量
    private Double expectSettleVolume;

    // 承运方组织编码
    private String carrierOrgCode;

    // 承运方组织名称
    private String carrierOrgName;

    // 承运方用户编码（接盘人/接单人）
    private String carrierUserCode;

    // 承运方用户姓名（接盘人/接单人）
    private String carrierUserFullName;

    // 合理货差
    private Double reasonableGoodsLoss;

    // 货差计算方式 1.按量 2.按系数
    private Integer freightLossMethod;

    // 货损标准单位编码
    private String goodsLossUnitCode;

    // 货损标准单位名称
    private String goodsLossUnitName;

    // 货损备注
    private String goodsLossRemark;

    // 运单状态(1、待装货，2、运输中，3、待结算，4、待支付，5、运单完成，6、货主取消，7、车主取消)（编码类型：order_status）
    private String waybillStatus;

    // 装货时间
    private Date loadingTime;

    // 卸货时间
    private Date unloadingTime;

    // 当前业务动作编码
    private String currentActionCode;

    // 当前业务动作名称
    private String currentActionName;

    // 实际结算行车费
    private Double confirmedDrivingFee;

    // 实际结算行车费计量单位编码
    private String confirmedDrivingFeeUnitCode;

    // 实际结算总费用
    private Double confirmedTotalFee;

    // 实际结算总费用计量单位编码
    private String confirmedTotalFeeUnitCode;

    // 结算货量（单位吨）
    private Double confirmedSettleVolume;

    // 实际结算货量单位编码
    private String confirmedSettleVolumeUnitCode;

    // 实际货差扣款（单位元）
    private Double confirmedLossDeduction;

    // 结算状态1、待结算，2、已结算 ，3、结算已作废 
    private Integer billStatus;

    // 结算操作人编码
    private String billUserCode;

    // 结算操作人姓名
    private String billUserFullName;

    // 结算组织编码
    private String billOrgCode;

    // 结算组织名称
    private String billOrgName;

    // 结算完成时间
    private Date billFinishTime;

    // 收款人（车主/司机）编码
    private String payeeUserCode;

    // 收款人
    private String payeeUserFullName;

    // 收款人开户行名称
    private String payeeBankName;

    // 收款人开户支行名称
    private String payeeBankBranchName;

    // 收款人银行开户人名字
    private String payeeBankAccountName;

    // 收款人银行账号
    private String payeeBankAccountNo;

    // 归账日期
    private Date toAccountDate;

    // 货币总金额（上报运单）
    private Double reportTotalMonetaryAmount;

    // 实际收货人姓名
    private String reportActualUnloadingUserFullName;

    // 实际发货人姓名
    private String reportActualLoadingUserFullName;

    // 托运时间
    private Date reportConsignmentTime;

    // 货物毛重（千克）（上报运单）
    private Double reportGoodsItemGrossWeight;

    // 上报备注
    private String reportNotes;

    // 上报状态（1、未上报，2、已上报）
    private Integer reportStatus;

    // 上报人用户编码
    private String reportUserCode;

    // 上报人姓名
    private String reportUserFullName;

    // 上报时间
    private Date reportTime;

    // 无车承运人：业务类型
    private String reportBusinessTypeCode;

    // 无车承运人：货物类型
    private String reportCargoTypeClassificationCode;

    // 无车承运人：信用代码
    private String reportUnifiedSocialCreditIdentifier;

    // 无车承运人：无车承运人的许可证
    private String reportTruckBrokerLicense;

    // 无车承运人：车辆所属业主的许可证
    private String reportTruckOwnerLicense;

    // 无车承运人：装货人个人证件号
    private String reportActualLoadingUserCertifyNo;

    // 无车承运人：车型分类代码
    private String reportVehicleClassificationCode;

    // 无车承运人：车辆所有人
    private String reportTruckOwner;

    // 自定义属性
    private String customData;

    // 标签
    private String waybillTag;

    // 描述
    private String description;

    // 备注
    private String remark;

    // 是否禁用
    private Boolean disabled;

    // 是否删除
    private Boolean deleted;

    // 操作模块编码
    private String moduleCode;

    // 创建人编码
    private String creatorUserCode;

    // 创建人用户名
    private String creatorUsername;

    // 创建时间
    private Date createTime;

    // 更新人编码
    private String updateUserCode;

    // 更新人用户名
    private String updateUsername;

    // 更新时间
    private Date updateTime;

    // 操作者IP
    private String ip;

    // 操作者所处经度
    private Double operatorLongitude;

    // 操作者所处纬度
    private Double operatorLatitude;

    // 所属用户编码
    private String ownerUserCode;

    // 所属公司编码
    private String ownerOrgCode;

    // 所属公司名字
    private String ownerOrgName;

    // 同步串号
    private String synchronousId;



    /**
     * set 自增ID
     * @param id 自增ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * get 自增ID
     * @return Long 自增ID
     */
    public Long getId() {
        return id;
    }


    /**
     * set 运单编码
     * @param code 运单编码
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * get 运单编码
     * @return String 运单编码
     */
    public String getCode() {
        return code;
    }


    /**
     * set 运单号
     * @param waybillNo 运单号
     */
    public void setWaybillNo(String waybillNo) {
        this.waybillNo = waybillNo;
    }

    /**
     * get 运单号
     * @return String 运单号
     */
    public String getWaybillNo() {
        return waybillNo;
    }


    /**
     * set 原始物流订单编码
     * @param rootLogisticsCode 原始物流订单编码
     */
    public void setRootLogisticsCode(String rootLogisticsCode) {
        this.rootLogisticsCode = rootLogisticsCode;
    }

    /**
     * get 原始物流订单编码
     * @return String 原始物流订单编码
     */
    public String getRootLogisticsCode() {
        return rootLogisticsCode;
    }


    /**
     * set 物流订单编码
     * @param logisticsCode 物流订单编码
     */
    public void setLogisticsCode(String logisticsCode) {
        this.logisticsCode = logisticsCode;
    }

    /**
     * get 物流订单编码
     * @return String 物流订单编码
     */
    public String getLogisticsCode() {
        return logisticsCode;
    }


    /**
     * set 货物名称
     * @param goodsName 货物名称
     */
    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    /**
     * get 货物名称
     * @return String 货物名称
     */
    public String getGoodsName() {
        return goodsName;
    }


    /**
     * set 货物单价
     * @param goodsPrice 货物单价
     */
    public void setGoodsPrice(Double goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    /**
     * get 货物单价
     * @return Double 货物单价
     */
    public Double getGoodsPrice() {
        return goodsPrice;
    }


    /**
     * set 货物单价单位编码
     * @param goodsPriceUnitCode 货物单价单位编码
     */
    public void setGoodsPriceUnitCode(String goodsPriceUnitCode) {
        this.goodsPriceUnitCode = goodsPriceUnitCode;
    }

    /**
     * get 货物单价单位编码
     * @return String 货物单价单位编码
     */
    public String getGoodsPriceUnitCode() {
        return goodsPriceUnitCode;
    }


    /**
     * set 货物数量
     * @param goodsAmount 货物数量
     */
    public void setGoodsAmount(Double goodsAmount) {
        this.goodsAmount = goodsAmount;
    }

    /**
     * get 货物数量
     * @return Double 货物数量
     */
    public Double getGoodsAmount() {
        return goodsAmount;
    }


    /**
     * set 货物数量单位编码
     * @param goodsAmountUnitCode 货物数量单位编码
     */
    public void setGoodsAmountUnitCode(String goodsAmountUnitCode) {
        this.goodsAmountUnitCode = goodsAmountUnitCode;
    }

    /**
     * get 货物数量单位编码
     * @return String 货物数量单位编码
     */
    public String getGoodsAmountUnitCode() {
        return goodsAmountUnitCode;
    }


    /**
     * set 货物重量
     * @param goodsWeight 货物重量
     */
    public void setGoodsWeight(Double goodsWeight) {
        this.goodsWeight = goodsWeight;
    }

    /**
     * get 货物重量
     * @return Double 货物重量
     */
    public Double getGoodsWeight() {
        return goodsWeight;
    }


    /**
     * set 货物重量单位编码
     * @param goodsWeightUnitCode 货物重量单位编码
     */
    public void setGoodsWeightUnitCode(String goodsWeightUnitCode) {
        this.goodsWeightUnitCode = goodsWeightUnitCode;
    }

    /**
     * get 货物重量单位编码
     * @return String 货物重量单位编码
     */
    public String getGoodsWeightUnitCode() {
        return goodsWeightUnitCode;
    }


    /**
     * set 货物体积
     * @param goodsVolume 货物体积
     */
    public void setGoodsVolume(Double goodsVolume) {
        this.goodsVolume = goodsVolume;
    }

    /**
     * get 货物体积
     * @return Double 货物体积
     */
    public Double getGoodsVolume() {
        return goodsVolume;
    }


    /**
     * set 货物体积单位编码
     * @param goodsVolumeUnitCode 货物体积单位编码
     */
    public void setGoodsVolumeUnitCode(String goodsVolumeUnitCode) {
        this.goodsVolumeUnitCode = goodsVolumeUnitCode;
    }

    /**
     * get 货物体积单位编码
     * @return String 货物体积单位编码
     */
    public String getGoodsVolumeUnitCode() {
        return goodsVolumeUnitCode;
    }


    /**
     * set 运输路线编码
     * @param routeCode 运输路线编码
     */
    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }

    /**
     * get 运输路线编码
     * @return String 运输路线编码
     */
    public String getRouteCode() {
        return routeCode;
    }


    /**
     * set 运输线路名称
     * @param routeName 运输线路名称
     */
    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    /**
     * get 运输线路名称
     * @return String 运输线路名称
     */
    public String getRouteName() {
        return routeName;
    }


    /**
     * set 标准运距(单位公里)
     * @param standardDistance 标准运距(单位公里)
     */
    public void setStandardDistance(Double standardDistance) {
        this.standardDistance = standardDistance;
    }

    /**
     * get 标准运距(单位公里)
     * @return Double 标准运距(单位公里)
     */
    public Double getStandardDistance() {
        return standardDistance;
    }


    /**
     * set 标准时长
     * @param standardTime 标准时长
     */
    public void setStandardTime(Double standardTime) {
        this.standardTime = standardTime;
    }

    /**
     * get 标准时长
     * @return Double 标准时长
     */
    public Double getStandardTime() {
        return standardTime;
    }


    /**
     * set 标准时长单位编码
     * @param standardTimeUnitCode 标准时长单位编码
     */
    public void setStandardTimeUnitCode(String standardTimeUnitCode) {
        this.standardTimeUnitCode = standardTimeUnitCode;
    }

    /**
     * get 标准时长单位编码
     * @return String 标准时长单位编码
     */
    public String getStandardTimeUnitCode() {
        return standardTimeUnitCode;
    }


    /**
     * set 发货单位名称
     * @param loadingOrgName 发货单位名称
     */
    public void setLoadingOrgName(String loadingOrgName) {
        this.loadingOrgName = loadingOrgName;
    }

    /**
     * get 发货单位名称
     * @return String 发货单位名称
     */
    public String getLoadingOrgName() {
        return loadingOrgName;
    }


    /**
     * set 发货联系人姓名
     * @param loadingUserFullName 发货联系人姓名
     */
    public void setLoadingUserFullName(String loadingUserFullName) {
        this.loadingUserFullName = loadingUserFullName;
    }

    /**
     * get 发货联系人姓名
     * @return String 发货联系人姓名
     */
    public String getLoadingUserFullName() {
        return loadingUserFullName;
    }


    /**
     * set 发货联系人电话
     * @param loadingUserPhone 发货联系人电话
     */
    public void setLoadingUserPhone(String loadingUserPhone) {
        this.loadingUserPhone = loadingUserPhone;
    }

    /**
     * get 发货联系人电话
     * @return String 发货联系人电话
     */
    public String getLoadingUserPhone() {
        return loadingUserPhone;
    }


    /**
     * set 发货地址省份编码
     * @param loadingProvinceCode 发货地址省份编码
     */
    public void setLoadingProvinceCode(String loadingProvinceCode) {
        this.loadingProvinceCode = loadingProvinceCode;
    }

    /**
     * get 发货地址省份编码
     * @return String 发货地址省份编码
     */
    public String getLoadingProvinceCode() {
        return loadingProvinceCode;
    }


    /**
     * set 发货地址城市编码
     * @param loadingCityCode 发货地址城市编码
     */
    public void setLoadingCityCode(String loadingCityCode) {
        this.loadingCityCode = loadingCityCode;
    }

    /**
     * get 发货地址城市编码
     * @return String 发货地址城市编码
     */
    public String getLoadingCityCode() {
        return loadingCityCode;
    }


    /**
     * set 发货地址区县编码
     * @param loadingCountyCode 发货地址区县编码
     */
    public void setLoadingCountyCode(String loadingCountyCode) {
        this.loadingCountyCode = loadingCountyCode;
    }

    /**
     * get 发货地址区县编码
     * @return String 发货地址区县编码
     */
    public String getLoadingCountyCode() {
        return loadingCountyCode;
    }


    /**
     * set 发货地址（具体地址）
     * @param loadingAddr 发货地址（具体地址）
     */
    public void setLoadingAddr(String loadingAddr) {
        this.loadingAddr = loadingAddr;
    }

    /**
     * get 发货地址（具体地址）
     * @return String 发货地址（具体地址）
     */
    public String getLoadingAddr() {
        return loadingAddr;
    }


    /**
     * set 收货单位名称
     * @param unloadingOrgName 收货单位名称
     */
    public void setUnloadingOrgName(String unloadingOrgName) {
        this.unloadingOrgName = unloadingOrgName;
    }

    /**
     * get 收货单位名称
     * @return String 收货单位名称
     */
    public String getUnloadingOrgName() {
        return unloadingOrgName;
    }


    /**
     * set 收货联系人姓名
     * @param unloadingUserFullName 收货联系人姓名
     */
    public void setUnloadingUserFullName(String unloadingUserFullName) {
        this.unloadingUserFullName = unloadingUserFullName;
    }

    /**
     * get 收货联系人姓名
     * @return String 收货联系人姓名
     */
    public String getUnloadingUserFullName() {
        return unloadingUserFullName;
    }


    /**
     * set 收货联系人电话
     * @param unloadingUserPhone 收货联系人电话
     */
    public void setUnloadingUserPhone(String unloadingUserPhone) {
        this.unloadingUserPhone = unloadingUserPhone;
    }

    /**
     * get 收货联系人电话
     * @return String 收货联系人电话
     */
    public String getUnloadingUserPhone() {
        return unloadingUserPhone;
    }


    /**
     * set 收货地址省份编码
     * @param unloadingProvinceCode 收货地址省份编码
     */
    public void setUnloadingProvinceCode(String unloadingProvinceCode) {
        this.unloadingProvinceCode = unloadingProvinceCode;
    }

    /**
     * get 收货地址省份编码
     * @return String 收货地址省份编码
     */
    public String getUnloadingProvinceCode() {
        return unloadingProvinceCode;
    }


    /**
     * set 收货地址城市编码
     * @param unloadingCityCode 收货地址城市编码
     */
    public void setUnloadingCityCode(String unloadingCityCode) {
        this.unloadingCityCode = unloadingCityCode;
    }

    /**
     * get 收货地址城市编码
     * @return String 收货地址城市编码
     */
    public String getUnloadingCityCode() {
        return unloadingCityCode;
    }


    /**
     * set 收货地址区县编码
     * @param unloadingCountyCode 收货地址区县编码
     */
    public void setUnloadingCountyCode(String unloadingCountyCode) {
        this.unloadingCountyCode = unloadingCountyCode;
    }

    /**
     * get 收货地址区县编码
     * @return String 收货地址区县编码
     */
    public String getUnloadingCountyCode() {
        return unloadingCountyCode;
    }


    /**
     * set 收货地址（具体地址）
     * @param unloadingAddr 收货地址（具体地址）
     */
    public void setUnloadingAddr(String unloadingAddr) {
        this.unloadingAddr = unloadingAddr;
    }

    /**
     * get 收货地址（具体地址）
     * @return String 收货地址（具体地址）
     */
    public String getUnloadingAddr() {
        return unloadingAddr;
    }


    /**
     * set 车辆许可证号
     * @param truckLicense 车辆许可证号
     */
    public void setTruckLicense(String truckLicense) {
        this.truckLicense = truckLicense;
    }

    /**
     * get 车辆许可证号
     * @return String 车辆许可证号
     */
    public String getTruckLicense() {
        return truckLicense;
    }


    /**
     * set 车型编码
     * @param truckModelCode 车型编码
     */
    public void setTruckModelCode(String truckModelCode) {
        this.truckModelCode = truckModelCode;
    }

    /**
     * get 车型编码
     * @return String 车型编码
     */
    public String getTruckModelCode() {
        return truckModelCode;
    }


    /**
     * set 车型简称
     * @param truckModelName 车型简称
     */
    public void setTruckModelName(String truckModelName) {
        this.truckModelName = truckModelName;
    }

    /**
     * get 车型简称
     * @return String 车型简称
     */
    public String getTruckModelName() {
        return truckModelName;
    }


    /**
     * set 车辆编码
     * @param truckCode 车辆编码
     */
    public void setTruckCode(String truckCode) {
        this.truckCode = truckCode;
    }

    /**
     * get 车辆编码
     * @return String 车辆编码
     */
    public String getTruckCode() {
        return truckCode;
    }


    /**
     * set 车牌号
     * @param truckLicenseNo 车牌号
     */
    public void setTruckLicenseNo(String truckLicenseNo) {
        this.truckLicenseNo = truckLicenseNo;
    }

    /**
     * get 车牌号
     * @return String 车牌号
     */
    public String getTruckLicenseNo() {
        return truckLicenseNo;
    }


    /**
     * set 牌照类型
     * @param licensePlateTypeCode 牌照类型
     */
    public void setLicensePlateTypeCode(String licensePlateTypeCode) {
        this.licensePlateTypeCode = licensePlateTypeCode;
    }

    /**
     * get 牌照类型
     * @return String 牌照类型
     */
    public String getLicensePlateTypeCode() {
        return licensePlateTypeCode;
    }


    /**
     * set 挂车车辆编码
     * @param trailerTruckCode 挂车车辆编码
     */
    public void setTrailerTruckCode(String trailerTruckCode) {
        this.trailerTruckCode = trailerTruckCode;
    }

    /**
     * get 挂车车辆编码
     * @return String 挂车车辆编码
     */
    public String getTrailerTruckCode() {
        return trailerTruckCode;
    }


    /**
     * set 挂车车牌号
     * @param trailerTruckLicenseNo 挂车车牌号
     */
    public void setTrailerTruckLicenseNo(String trailerTruckLicenseNo) {
        this.trailerTruckLicenseNo = trailerTruckLicenseNo;
    }

    /**
     * get 挂车车牌号
     * @return String 挂车车牌号
     */
    public String getTrailerTruckLicenseNo() {
        return trailerTruckLicenseNo;
    }


    /**
     * set 行驶证号码
     * @param drivingLicenseNo 行驶证号码
     */
    public void setDrivingLicenseNo(String drivingLicenseNo) {
        this.drivingLicenseNo = drivingLicenseNo;
    }

    /**
     * get 行驶证号码
     * @return String 行驶证号码
     */
    public String getDrivingLicenseNo() {
        return drivingLicenseNo;
    }


    /**
     * set 车长
     * @param truckLength 车长
     */
    public void setTruckLength(Double truckLength) {
        this.truckLength = truckLength;
    }

    /**
     * get 车长
     * @return Double 车长
     */
    public Double getTruckLength() {
        return truckLength;
    }


    /**
     * set 车长单位编码
     * @param truckLengthUnitCode 车长单位编码
     */
    public void setTruckLengthUnitCode(String truckLengthUnitCode) {
        this.truckLengthUnitCode = truckLengthUnitCode;
    }

    /**
     * get 车长单位编码
     * @return String 车长单位编码
     */
    public String getTruckLengthUnitCode() {
        return truckLengthUnitCode;
    }


    /**
     * set 动力类型
     * @param truckPowerType 动力类型
     */
    public void setTruckPowerType(Integer truckPowerType) {
        this.truckPowerType = truckPowerType;
    }

    /**
     * get 动力类型
     * @return Integer 动力类型
     */
    public Integer getTruckPowerType() {
        return truckPowerType;
    }


    /**
     * set 核载重量（From车辆表）
     * @param regTonnage 核载重量（From车辆表）
     */
    public void setRegTonnage(Double regTonnage) {
        this.regTonnage = regTonnage;
    }

    /**
     * get 核载重量（From车辆表）
     * @return Double 核载重量（From车辆表）
     */
    public Double getRegTonnage() {
        return regTonnage;
    }


    /**
     * set 核载重量单位编码
     * @param regTonnageUnitCode 核载重量单位编码
     */
    public void setRegTonnageUnitCode(String regTonnageUnitCode) {
        this.regTonnageUnitCode = regTonnageUnitCode;
    }

    /**
     * get 核载重量单位编码
     * @return String 核载重量单位编码
     */
    public String getRegTonnageUnitCode() {
        return regTonnageUnitCode;
    }


    /**
     * set 司机编码
     * @param driverCode 司机编码
     */
    public void setDriverCode(String driverCode) {
        this.driverCode = driverCode;
    }

    /**
     * get 司机编码
     * @return String 司机编码
     */
    public String getDriverCode() {
        return driverCode;
    }


    /**
     * set 司机姓名
     * @param driverFullName 司机姓名
     */
    public void setDriverFullName(String driverFullName) {
        this.driverFullName = driverFullName;
    }

    /**
     * get 司机姓名
     * @return String 司机姓名
     */
    public String getDriverFullName() {
        return driverFullName;
    }


    /**
     * set 司机电话
     * @param driverPhone 司机电话
     */
    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    /**
     * get 司机电话
     * @return String 司机电话
     */
    public String getDriverPhone() {
        return driverPhone;
    }


    /**
     * set 驾驶证号
     * @param driverLicenseNo 驾驶证号
     */
    public void setDriverLicenseNo(String driverLicenseNo) {
        this.driverLicenseNo = driverLicenseNo;
    }

    /**
     * get 驾驶证号
     * @return String 驾驶证号
     */
    public String getDriverLicenseNo() {
        return driverLicenseNo;
    }


    /**
     * set 副驾驶司机编码（执行人）
     * @param viceDriverCode 副驾驶司机编码（执行人）
     */
    public void setViceDriverCode(String viceDriverCode) {
        this.viceDriverCode = viceDriverCode;
    }

    /**
     * get 副驾驶司机编码（执行人）
     * @return String 副驾驶司机编码（执行人）
     */
    public String getViceDriverCode() {
        return viceDriverCode;
    }


    /**
     * set 副驾驶司机姓名
     * @param viceDriverFullName 副驾驶司机姓名
     */
    public void setViceDriverFullName(String viceDriverFullName) {
        this.viceDriverFullName = viceDriverFullName;
    }

    /**
     * get 副驾驶司机姓名
     * @return String 副驾驶司机姓名
     */
    public String getViceDriverFullName() {
        return viceDriverFullName;
    }


    /**
     * set 副驾驶司机电话
     * @param viceDriverPhone 副驾驶司机电话
     */
    public void setViceDriverPhone(String viceDriverPhone) {
        this.viceDriverPhone = viceDriverPhone;
    }

    /**
     * get 副驾驶司机电话
     * @return String 副驾驶司机电话
     */
    public String getViceDriverPhone() {
        return viceDriverPhone;
    }


    /**
     * set 卖方客户组织名称
     * @param sellerOrgName 卖方客户组织名称
     */
    public void setSellerOrgName(String sellerOrgName) {
        this.sellerOrgName = sellerOrgName;
    }

    /**
     * get 卖方客户组织名称
     * @return String 卖方客户组织名称
     */
    public String getSellerOrgName() {
        return sellerOrgName;
    }


    /**
     * set 买方客户组织名称
     * @param customerOrgName 买方客户组织名称
     */
    public void setCustomerOrgName(String customerOrgName) {
        this.customerOrgName = customerOrgName;
    }

    /**
     * get 买方客户组织名称
     * @return String 买方客户组织名称
     */
    public String getCustomerOrgName() {
        return customerOrgName;
    }


    /**
     * set 运价编码
     * @param ratesCode 运价编码
     */
    public void setRatesCode(String ratesCode) {
        this.ratesCode = ratesCode;
    }

    /**
     * get 运价编码
     * @return String 运价编码
     */
    public String getRatesCode() {
        return ratesCode;
    }


    /**
     * set 货物计量标准（1、吨，2、方，3、件）
     * @param meterageType 货物计量标准（1、吨，2、方，3、件）
     */
    public void setMeterageType(Integer meterageType) {
        this.meterageType = meterageType;
    }

    /**
     * get 货物计量标准（1、吨，2、方，3、件）
     * @return Integer 货物计量标准（1、吨，2、方，3、件）
     */
    public Integer getMeterageType() {
        return meterageType;
    }


    /**
     * set 司机运价
     * @param driverPrice 司机运价
     */
    public void setDriverPrice(Double driverPrice) {
        this.driverPrice = driverPrice;
    }

    /**
     * get 司机运价
     * @return Double 司机运价
     */
    public Double getDriverPrice() {
        return driverPrice;
    }


    /**
     * set 司机运价单位编码
     * @param driverPriceUnitCode 司机运价单位编码
     */
    public void setDriverPriceUnitCode(String driverPriceUnitCode) {
        this.driverPriceUnitCode = driverPriceUnitCode;
    }

    /**
     * get 司机运价单位编码
     * @return String 司机运价单位编码
     */
    public String getDriverPriceUnitCode() {
        return driverPriceUnitCode;
    }


    /**
     * set 预估结算货量
     * @param expectSettleVolume 预估结算货量
     */
    public void setExpectSettleVolume(Double expectSettleVolume) {
        this.expectSettleVolume = expectSettleVolume;
    }

    /**
     * get 预估结算货量
     * @return Double 预估结算货量
     */
    public Double getExpectSettleVolume() {
        return expectSettleVolume;
    }


    /**
     * set 承运方组织编码
     * @param carrierOrgCode 承运方组织编码
     */
    public void setCarrierOrgCode(String carrierOrgCode) {
        this.carrierOrgCode = carrierOrgCode;
    }

    /**
     * get 承运方组织编码
     * @return String 承运方组织编码
     */
    public String getCarrierOrgCode() {
        return carrierOrgCode;
    }


    /**
     * set 承运方组织名称
     * @param carrierOrgName 承运方组织名称
     */
    public void setCarrierOrgName(String carrierOrgName) {
        this.carrierOrgName = carrierOrgName;
    }

    /**
     * get 承运方组织名称
     * @return String 承运方组织名称
     */
    public String getCarrierOrgName() {
        return carrierOrgName;
    }


    /**
     * set 承运方用户编码（接盘人/接单人）
     * @param carrierUserCode 承运方用户编码（接盘人/接单人）
     */
    public void setCarrierUserCode(String carrierUserCode) {
        this.carrierUserCode = carrierUserCode;
    }

    /**
     * get 承运方用户编码（接盘人/接单人）
     * @return String 承运方用户编码（接盘人/接单人）
     */
    public String getCarrierUserCode() {
        return carrierUserCode;
    }


    /**
     * set 承运方用户姓名（接盘人/接单人）
     * @param carrierUserFullName 承运方用户姓名（接盘人/接单人）
     */
    public void setCarrierUserFullName(String carrierUserFullName) {
        this.carrierUserFullName = carrierUserFullName;
    }

    /**
     * get 承运方用户姓名（接盘人/接单人）
     * @return String 承运方用户姓名（接盘人/接单人）
     */
    public String getCarrierUserFullName() {
        return carrierUserFullName;
    }


    /**
     * set 合理货差
     * @param reasonableGoodsLoss 合理货差
     */
    public void setReasonableGoodsLoss(Double reasonableGoodsLoss) {
        this.reasonableGoodsLoss = reasonableGoodsLoss;
    }

    /**
     * get 合理货差
     * @return Double 合理货差
     */
    public Double getReasonableGoodsLoss() {
        return reasonableGoodsLoss;
    }


    /**
     * set 货差计算方式 1.按量 2.按系数
     * @param freightLossMethod 货差计算方式 1.按量 2.按系数
     */
    public void setFreightLossMethod(Integer freightLossMethod) {
        this.freightLossMethod = freightLossMethod;
    }

    /**
     * get 货差计算方式 1.按量 2.按系数
     * @return Integer 货差计算方式 1.按量 2.按系数
     */
    public Integer getFreightLossMethod() {
        return freightLossMethod;
    }


    /**
     * set 货损标准单位编码
     * @param goodsLossUnitCode 货损标准单位编码
     */
    public void setGoodsLossUnitCode(String goodsLossUnitCode) {
        this.goodsLossUnitCode = goodsLossUnitCode;
    }

    /**
     * get 货损标准单位编码
     * @return String 货损标准单位编码
     */
    public String getGoodsLossUnitCode() {
        return goodsLossUnitCode;
    }


    /**
     * set 货损标准单位名称
     * @param goodsLossUnitName 货损标准单位名称
     */
    public void setGoodsLossUnitName(String goodsLossUnitName) {
        this.goodsLossUnitName = goodsLossUnitName;
    }

    /**
     * get 货损标准单位名称
     * @return String 货损标准单位名称
     */
    public String getGoodsLossUnitName() {
        return goodsLossUnitName;
    }


    /**
     * set 货损备注
     * @param goodsLossRemark 货损备注
     */
    public void setGoodsLossRemark(String goodsLossRemark) {
        this.goodsLossRemark = goodsLossRemark;
    }

    /**
     * get 货损备注
     * @return String 货损备注
     */
    public String getGoodsLossRemark() {
        return goodsLossRemark;
    }


    /**
     * set 运单状态(1、待装货，2、运输中，3、待结算，4、待支付，5、运单完成，6、货主取消，7、车主取消)（编码类型：order_status）
     * @param waybillStatus 运单状态(1、待装货，2、运输中，3、待结算，4、待支付，5、运单完成，6、货主取消，7、车主取消)（编码类型：order_status）
     */
    public void setWaybillStatus(String waybillStatus) {
        this.waybillStatus = waybillStatus;
    }

    /**
     * get 运单状态(1、待装货，2、运输中，3、待结算，4、待支付，5、运单完成，6、货主取消，7、车主取消)（编码类型：order_status）
     * @return String 运单状态(1、待装货，2、运输中，3、待结算，4、待支付，5、运单完成，6、货主取消，7、车主取消)（编码类型：order_status）
     */
    public String getWaybillStatus() {
        return waybillStatus;
    }


    /**
     * set 装货时间
     * @param loadingTime 装货时间
     */
    public void setLoadingTime(Date loadingTime) {
        this.loadingTime = loadingTime;
    }

    /**
     * get 装货时间
     * @return Date 装货时间
     */
    public Date getLoadingTime() {
        return loadingTime;
    }


    /**
     * set 卸货时间
     * @param unloadingTime 卸货时间
     */
    public void setUnloadingTime(Date unloadingTime) {
        this.unloadingTime = unloadingTime;
    }

    /**
     * get 卸货时间
     * @return Date 卸货时间
     */
    public Date getUnloadingTime() {
        return unloadingTime;
    }


    /**
     * set 当前业务动作编码
     * @param currentActionCode 当前业务动作编码
     */
    public void setCurrentActionCode(String currentActionCode) {
        this.currentActionCode = currentActionCode;
    }

    /**
     * get 当前业务动作编码
     * @return String 当前业务动作编码
     */
    public String getCurrentActionCode() {
        return currentActionCode;
    }


    /**
     * set 当前业务动作名称
     * @param currentActionName 当前业务动作名称
     */
    public void setCurrentActionName(String currentActionName) {
        this.currentActionName = currentActionName;
    }

    /**
     * get 当前业务动作名称
     * @return String 当前业务动作名称
     */
    public String getCurrentActionName() {
        return currentActionName;
    }


    /**
     * set 实际结算行车费
     * @param confirmedDrivingFee 实际结算行车费
     */
    public void setConfirmedDrivingFee(Double confirmedDrivingFee) {
        this.confirmedDrivingFee = confirmedDrivingFee;
    }

    /**
     * get 实际结算行车费
     * @return Double 实际结算行车费
     */
    public Double getConfirmedDrivingFee() {
        return confirmedDrivingFee;
    }


    /**
     * set 实际结算行车费计量单位编码
     * @param confirmedDrivingFeeUnitCode 实际结算行车费计量单位编码
     */
    public void setConfirmedDrivingFeeUnitCode(String confirmedDrivingFeeUnitCode) {
        this.confirmedDrivingFeeUnitCode = confirmedDrivingFeeUnitCode;
    }

    /**
     * get 实际结算行车费计量单位编码
     * @return String 实际结算行车费计量单位编码
     */
    public String getConfirmedDrivingFeeUnitCode() {
        return confirmedDrivingFeeUnitCode;
    }


    /**
     * set 实际结算总费用
     * @param confirmedTotalFee 实际结算总费用
     */
    public void setConfirmedTotalFee(Double confirmedTotalFee) {
        this.confirmedTotalFee = confirmedTotalFee;
    }

    /**
     * get 实际结算总费用
     * @return Double 实际结算总费用
     */
    public Double getConfirmedTotalFee() {
        return confirmedTotalFee;
    }


    /**
     * set 实际结算总费用计量单位编码
     * @param confirmedTotalFeeUnitCode 实际结算总费用计量单位编码
     */
    public void setConfirmedTotalFeeUnitCode(String confirmedTotalFeeUnitCode) {
        this.confirmedTotalFeeUnitCode = confirmedTotalFeeUnitCode;
    }

    /**
     * get 实际结算总费用计量单位编码
     * @return String 实际结算总费用计量单位编码
     */
    public String getConfirmedTotalFeeUnitCode() {
        return confirmedTotalFeeUnitCode;
    }


    /**
     * set 结算货量（单位吨）
     * @param confirmedSettleVolume 结算货量（单位吨）
     */
    public void setConfirmedSettleVolume(Double confirmedSettleVolume) {
        this.confirmedSettleVolume = confirmedSettleVolume;
    }

    /**
     * get 结算货量（单位吨）
     * @return Double 结算货量（单位吨）
     */
    public Double getConfirmedSettleVolume() {
        return confirmedSettleVolume;
    }


    /**
     * set 实际结算货量单位编码
     * @param confirmedSettleVolumeUnitCode 实际结算货量单位编码
     */
    public void setConfirmedSettleVolumeUnitCode(String confirmedSettleVolumeUnitCode) {
        this.confirmedSettleVolumeUnitCode = confirmedSettleVolumeUnitCode;
    }

    /**
     * get 实际结算货量单位编码
     * @return String 实际结算货量单位编码
     */
    public String getConfirmedSettleVolumeUnitCode() {
        return confirmedSettleVolumeUnitCode;
    }


    /**
     * set 实际货差扣款（单位元）
     * @param confirmedLossDeduction 实际货差扣款（单位元）
     */
    public void setConfirmedLossDeduction(Double confirmedLossDeduction) {
        this.confirmedLossDeduction = confirmedLossDeduction;
    }

    /**
     * get 实际货差扣款（单位元）
     * @return Double 实际货差扣款（单位元）
     */
    public Double getConfirmedLossDeduction() {
        return confirmedLossDeduction;
    }


    /**
     * set 结算状态1、待结算，2、已结算 ，3、结算已作废 
     * @param billStatus 结算状态1、待结算，2、已结算 ，3、结算已作废 
     */
    public void setBillStatus(Integer billStatus) {
        this.billStatus = billStatus;
    }

    /**
     * get 结算状态1、待结算，2、已结算 ，3、结算已作废 
     * @return Integer 结算状态1、待结算，2、已结算 ，3、结算已作废 
     */
    public Integer getBillStatus() {
        return billStatus;
    }


    /**
     * set 结算操作人编码
     * @param billUserCode 结算操作人编码
     */
    public void setBillUserCode(String billUserCode) {
        this.billUserCode = billUserCode;
    }

    /**
     * get 结算操作人编码
     * @return String 结算操作人编码
     */
    public String getBillUserCode() {
        return billUserCode;
    }


    /**
     * set 结算操作人姓名
     * @param billUserFullName 结算操作人姓名
     */
    public void setBillUserFullName(String billUserFullName) {
        this.billUserFullName = billUserFullName;
    }

    /**
     * get 结算操作人姓名
     * @return String 结算操作人姓名
     */
    public String getBillUserFullName() {
        return billUserFullName;
    }


    /**
     * set 结算组织编码
     * @param billOrgCode 结算组织编码
     */
    public void setBillOrgCode(String billOrgCode) {
        this.billOrgCode = billOrgCode;
    }

    /**
     * get 结算组织编码
     * @return String 结算组织编码
     */
    public String getBillOrgCode() {
        return billOrgCode;
    }


    /**
     * set 结算组织名称
     * @param billOrgName 结算组织名称
     */
    public void setBillOrgName(String billOrgName) {
        this.billOrgName = billOrgName;
    }

    /**
     * get 结算组织名称
     * @return String 结算组织名称
     */
    public String getBillOrgName() {
        return billOrgName;
    }


    /**
     * set 结算完成时间
     * @param billFinishTime 结算完成时间
     */
    public void setBillFinishTime(Date billFinishTime) {
        this.billFinishTime = billFinishTime;
    }

    /**
     * get 结算完成时间
     * @return Date 结算完成时间
     */
    public Date getBillFinishTime() {
        return billFinishTime;
    }


    /**
     * set 收款人（车主/司机）编码
     * @param payeeUserCode 收款人（车主/司机）编码
     */
    public void setPayeeUserCode(String payeeUserCode) {
        this.payeeUserCode = payeeUserCode;
    }

    /**
     * get 收款人（车主/司机）编码
     * @return String 收款人（车主/司机）编码
     */
    public String getPayeeUserCode() {
        return payeeUserCode;
    }


    /**
     * set 收款人
     * @param payeeUserFullName 收款人
     */
    public void setPayeeUserFullName(String payeeUserFullName) {
        this.payeeUserFullName = payeeUserFullName;
    }

    /**
     * get 收款人
     * @return String 收款人
     */
    public String getPayeeUserFullName() {
        return payeeUserFullName;
    }


    /**
     * set 收款人开户行名称
     * @param payeeBankName 收款人开户行名称
     */
    public void setPayeeBankName(String payeeBankName) {
        this.payeeBankName = payeeBankName;
    }

    /**
     * get 收款人开户行名称
     * @return String 收款人开户行名称
     */
    public String getPayeeBankName() {
        return payeeBankName;
    }


    /**
     * set 收款人开户支行名称
     * @param payeeBankBranchName 收款人开户支行名称
     */
    public void setPayeeBankBranchName(String payeeBankBranchName) {
        this.payeeBankBranchName = payeeBankBranchName;
    }

    /**
     * get 收款人开户支行名称
     * @return String 收款人开户支行名称
     */
    public String getPayeeBankBranchName() {
        return payeeBankBranchName;
    }


    /**
     * set 收款人银行开户人名字
     * @param payeeBankAccountName 收款人银行开户人名字
     */
    public void setPayeeBankAccountName(String payeeBankAccountName) {
        this.payeeBankAccountName = payeeBankAccountName;
    }

    /**
     * get 收款人银行开户人名字
     * @return String 收款人银行开户人名字
     */
    public String getPayeeBankAccountName() {
        return payeeBankAccountName;
    }


    /**
     * set 收款人银行账号
     * @param payeeBankAccountNo 收款人银行账号
     */
    public void setPayeeBankAccountNo(String payeeBankAccountNo) {
        this.payeeBankAccountNo = payeeBankAccountNo;
    }

    /**
     * get 收款人银行账号
     * @return String 收款人银行账号
     */
    public String getPayeeBankAccountNo() {
        return payeeBankAccountNo;
    }


    /**
     * set 归账日期
     * @param toAccountDate 归账日期
     */
    public void setToAccountDate(Date toAccountDate) {
        this.toAccountDate = toAccountDate;
    }

    /**
     * get 归账日期
     * @return Date 归账日期
     */
    public Date getToAccountDate() {
        return toAccountDate;
    }


    /**
     * set 货币总金额（上报运单）
     * @param reportTotalMonetaryAmount 货币总金额（上报运单）
     */
    public void setReportTotalMonetaryAmount(Double reportTotalMonetaryAmount) {
        this.reportTotalMonetaryAmount = reportTotalMonetaryAmount;
    }

    /**
     * get 货币总金额（上报运单）
     * @return Double 货币总金额（上报运单）
     */
    public Double getReportTotalMonetaryAmount() {
        return reportTotalMonetaryAmount;
    }


    /**
     * set 实际收货人姓名
     * @param reportActualUnloadingUserFullName 实际收货人姓名
     */
    public void setReportActualUnloadingUserFullName(String reportActualUnloadingUserFullName) {
        this.reportActualUnloadingUserFullName = reportActualUnloadingUserFullName;
    }

    /**
     * get 实际收货人姓名
     * @return String 实际收货人姓名
     */
    public String getReportActualUnloadingUserFullName() {
        return reportActualUnloadingUserFullName;
    }


    /**
     * set 实际发货人姓名
     * @param reportActualLoadingUserFullName 实际发货人姓名
     */
    public void setReportActualLoadingUserFullName(String reportActualLoadingUserFullName) {
        this.reportActualLoadingUserFullName = reportActualLoadingUserFullName;
    }

    /**
     * get 实际发货人姓名
     * @return String 实际发货人姓名
     */
    public String getReportActualLoadingUserFullName() {
        return reportActualLoadingUserFullName;
    }


    /**
     * set 托运时间
     * @param reportConsignmentTime 托运时间
     */
    public void setReportConsignmentTime(Date reportConsignmentTime) {
        this.reportConsignmentTime = reportConsignmentTime;
    }

    /**
     * get 托运时间
     * @return Date 托运时间
     */
    public Date getReportConsignmentTime() {
        return reportConsignmentTime;
    }


    /**
     * set 货物毛重（千克）（上报运单）
     * @param reportGoodsItemGrossWeight 货物毛重（千克）（上报运单）
     */
    public void setReportGoodsItemGrossWeight(Double reportGoodsItemGrossWeight) {
        this.reportGoodsItemGrossWeight = reportGoodsItemGrossWeight;
    }

    /**
     * get 货物毛重（千克）（上报运单）
     * @return Double 货物毛重（千克）（上报运单）
     */
    public Double getReportGoodsItemGrossWeight() {
        return reportGoodsItemGrossWeight;
    }


    /**
     * set 上报备注
     * @param reportNotes 上报备注
     */
    public void setReportNotes(String reportNotes) {
        this.reportNotes = reportNotes;
    }

    /**
     * get 上报备注
     * @return String 上报备注
     */
    public String getReportNotes() {
        return reportNotes;
    }


    /**
     * set 上报状态（1、未上报，2、已上报）
     * @param reportStatus 上报状态（1、未上报，2、已上报）
     */
    public void setReportStatus(Integer reportStatus) {
        this.reportStatus = reportStatus;
    }

    /**
     * get 上报状态（1、未上报，2、已上报）
     * @return Integer 上报状态（1、未上报，2、已上报）
     */
    public Integer getReportStatus() {
        return reportStatus;
    }


    /**
     * set 上报人用户编码
     * @param reportUserCode 上报人用户编码
     */
    public void setReportUserCode(String reportUserCode) {
        this.reportUserCode = reportUserCode;
    }

    /**
     * get 上报人用户编码
     * @return String 上报人用户编码
     */
    public String getReportUserCode() {
        return reportUserCode;
    }


    /**
     * set 上报人姓名
     * @param reportUserFullName 上报人姓名
     */
    public void setReportUserFullName(String reportUserFullName) {
        this.reportUserFullName = reportUserFullName;
    }

    /**
     * get 上报人姓名
     * @return String 上报人姓名
     */
    public String getReportUserFullName() {
        return reportUserFullName;
    }


    /**
     * set 上报时间
     * @param reportTime 上报时间
     */
    public void setReportTime(Date reportTime) {
        this.reportTime = reportTime;
    }

    /**
     * get 上报时间
     * @return Date 上报时间
     */
    public Date getReportTime() {
        return reportTime;
    }


    /**
     * set 无车承运人：业务类型
     * @param reportBusinessTypeCode 无车承运人：业务类型
     */
    public void setReportBusinessTypeCode(String reportBusinessTypeCode) {
        this.reportBusinessTypeCode = reportBusinessTypeCode;
    }

    /**
     * get 无车承运人：业务类型
     * @return String 无车承运人：业务类型
     */
    public String getReportBusinessTypeCode() {
        return reportBusinessTypeCode;
    }


    /**
     * set 无车承运人：货物类型
     * @param reportCargoTypeClassificationCode 无车承运人：货物类型
     */
    public void setReportCargoTypeClassificationCode(String reportCargoTypeClassificationCode) {
        this.reportCargoTypeClassificationCode = reportCargoTypeClassificationCode;
    }

    /**
     * get 无车承运人：货物类型
     * @return String 无车承运人：货物类型
     */
    public String getReportCargoTypeClassificationCode() {
        return reportCargoTypeClassificationCode;
    }


    /**
     * set 无车承运人：信用代码
     * @param reportUnifiedSocialCreditIdentifier 无车承运人：信用代码
     */
    public void setReportUnifiedSocialCreditIdentifier(String reportUnifiedSocialCreditIdentifier) {
        this.reportUnifiedSocialCreditIdentifier = reportUnifiedSocialCreditIdentifier;
    }

    /**
     * get 无车承运人：信用代码
     * @return String 无车承运人：信用代码
     */
    public String getReportUnifiedSocialCreditIdentifier() {
        return reportUnifiedSocialCreditIdentifier;
    }


    /**
     * set 无车承运人：无车承运人的许可证
     * @param reportTruckBrokerLicense 无车承运人：无车承运人的许可证
     */
    public void setReportTruckBrokerLicense(String reportTruckBrokerLicense) {
        this.reportTruckBrokerLicense = reportTruckBrokerLicense;
    }

    /**
     * get 无车承运人：无车承运人的许可证
     * @return String 无车承运人：无车承运人的许可证
     */
    public String getReportTruckBrokerLicense() {
        return reportTruckBrokerLicense;
    }


    /**
     * set 无车承运人：车辆所属业主的许可证
     * @param reportTruckOwnerLicense 无车承运人：车辆所属业主的许可证
     */
    public void setReportTruckOwnerLicense(String reportTruckOwnerLicense) {
        this.reportTruckOwnerLicense = reportTruckOwnerLicense;
    }

    /**
     * get 无车承运人：车辆所属业主的许可证
     * @return String 无车承运人：车辆所属业主的许可证
     */
    public String getReportTruckOwnerLicense() {
        return reportTruckOwnerLicense;
    }


    /**
     * set 无车承运人：装货人个人证件号
     * @param reportActualLoadingUserCertifyNo 无车承运人：装货人个人证件号
     */
    public void setReportActualLoadingUserCertifyNo(String reportActualLoadingUserCertifyNo) {
        this.reportActualLoadingUserCertifyNo = reportActualLoadingUserCertifyNo;
    }

    /**
     * get 无车承运人：装货人个人证件号
     * @return String 无车承运人：装货人个人证件号
     */
    public String getReportActualLoadingUserCertifyNo() {
        return reportActualLoadingUserCertifyNo;
    }


    /**
     * set 无车承运人：车型分类代码
     * @param reportVehicleClassificationCode 无车承运人：车型分类代码
     */
    public void setReportVehicleClassificationCode(String reportVehicleClassificationCode) {
        this.reportVehicleClassificationCode = reportVehicleClassificationCode;
    }

    /**
     * get 无车承运人：车型分类代码
     * @return String 无车承运人：车型分类代码
     */
    public String getReportVehicleClassificationCode() {
        return reportVehicleClassificationCode;
    }


    /**
     * set 无车承运人：车辆所有人
     * @param reportTruckOwner 无车承运人：车辆所有人
     */
    public void setReportTruckOwner(String reportTruckOwner) {
        this.reportTruckOwner = reportTruckOwner;
    }

    /**
     * get 无车承运人：车辆所有人
     * @return String 无车承运人：车辆所有人
     */
    public String getReportTruckOwner() {
        return reportTruckOwner;
    }


    /**
     * set 自定义属性
     * @param customData 自定义属性
     */
    public void setCustomData(String customData) {
        this.customData = customData;
    }

    /**
     * get 自定义属性
     * @return String 自定义属性
     */
    public String getCustomData() {
        return customData;
    }


    /**
     * set 标签
     * @param waybillTag 标签
     */
    public void setWaybillTag(String waybillTag) {
        this.waybillTag = waybillTag;
    }

    /**
     * get 标签
     * @return String 标签
     */
    public String getWaybillTag() {
        return waybillTag;
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


    /**
     * set 备注
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * get 备注
     * @return String 备注
     */
    public String getRemark() {
        return remark;
    }


    /**
     * set 是否禁用
     * @param disabled 是否禁用
     */
    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * get 是否禁用
     * @return Boolean 是否禁用
     */
    public Boolean getDisabled() {
        return disabled;
    }


    /**
     * set 是否删除
     * @param deleted 是否删除
     */
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * get 是否删除
     * @return Boolean 是否删除
     */
    public Boolean getDeleted() {
        return deleted;
    }


    /**
     * set 操作模块编码
     * @param moduleCode 操作模块编码
     */
    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    /**
     * get 操作模块编码
     * @return String 操作模块编码
     */
    public String getModuleCode() {
        return moduleCode;
    }


    /**
     * set 创建人编码
     * @param creatorUserCode 创建人编码
     */
    public void setCreatorUserCode(String creatorUserCode) {
        this.creatorUserCode = creatorUserCode;
    }

    /**
     * get 创建人编码
     * @return String 创建人编码
     */
    public String getCreatorUserCode() {
        return creatorUserCode;
    }


    /**
     * set 创建人用户名
     * @param creatorUsername 创建人用户名
     */
    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }

    /**
     * get 创建人用户名
     * @return String 创建人用户名
     */
    public String getCreatorUsername() {
        return creatorUsername;
    }


    /**
     * set 创建时间
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * get 创建时间
     * @return Date 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }


    /**
     * set 更新人编码
     * @param updateUserCode 更新人编码
     */
    public void setUpdateUserCode(String updateUserCode) {
        this.updateUserCode = updateUserCode;
    }

    /**
     * get 更新人编码
     * @return String 更新人编码
     */
    public String getUpdateUserCode() {
        return updateUserCode;
    }


    /**
     * set 更新人用户名
     * @param updateUsername 更新人用户名
     */
    public void setUpdateUsername(String updateUsername) {
        this.updateUsername = updateUsername;
    }

    /**
     * get 更新人用户名
     * @return String 更新人用户名
     */
    public String getUpdateUsername() {
        return updateUsername;
    }


    /**
     * set 更新时间
     * @param updateTime 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * get 更新时间
     * @return Date 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }


    /**
     * set 操作者IP
     * @param ip 操作者IP
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * get 操作者IP
     * @return String 操作者IP
     */
    public String getIp() {
        return ip;
    }


    /**
     * set 操作者所处经度
     * @param operatorLongitude 操作者所处经度
     */
    public void setOperatorLongitude(Double operatorLongitude) {
        this.operatorLongitude = operatorLongitude;
    }

    /**
     * get 操作者所处经度
     * @return Double 操作者所处经度
     */
    public Double getOperatorLongitude() {
        return operatorLongitude;
    }


    /**
     * set 操作者所处纬度
     * @param operatorLatitude 操作者所处纬度
     */
    public void setOperatorLatitude(Double operatorLatitude) {
        this.operatorLatitude = operatorLatitude;
    }

    /**
     * get 操作者所处纬度
     * @return Double 操作者所处纬度
     */
    public Double getOperatorLatitude() {
        return operatorLatitude;
    }


    /**
     * set 所属用户编码
     * @param ownerUserCode 所属用户编码
     */
    public void setOwnerUserCode(String ownerUserCode) {
        this.ownerUserCode = ownerUserCode;
    }

    /**
     * get 所属用户编码
     * @return String 所属用户编码
     */
    public String getOwnerUserCode() {
        return ownerUserCode;
    }


    /**
     * set 所属公司编码
     * @param ownerOrgCode 所属公司编码
     */
    public void setOwnerOrgCode(String ownerOrgCode) {
        this.ownerOrgCode = ownerOrgCode;
    }

    /**
     * get 所属公司编码
     * @return String 所属公司编码
     */
    public String getOwnerOrgCode() {
        return ownerOrgCode;
    }


    /**
     * set 所属公司名字
     * @param ownerOrgName 所属公司名字
     */
    public void setOwnerOrgName(String ownerOrgName) {
        this.ownerOrgName = ownerOrgName;
    }

    /**
     * get 所属公司名字
     * @return String 所属公司名字
     */
    public String getOwnerOrgName() {
        return ownerOrgName;
    }


    /**
     * set 同步串号
     * @param synchronousId 同步串号
     */
    public void setSynchronousId(String synchronousId) {
        this.synchronousId = synchronousId;
    }

    /**
     * get 同步串号
     * @return String 同步串号
     */
    public String getSynchronousId() {
        return synchronousId;
    }


}
