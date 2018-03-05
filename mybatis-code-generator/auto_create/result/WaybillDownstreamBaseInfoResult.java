package com.rltx.waybill.result;

import java.util.Date;

/**
 * waybill_downstream_base_info
 */
@ApiModel("waybill_downstream_base_info")
public class WaybillDownstreamBaseInfoResult {

    // 自增ID
    @ApiModelProperty("自增ID")
    private Long id;

    // 运单编码
    @ApiModelProperty("运单编码")
    private String waybillCode;

    // 原系统编号
    @ApiModelProperty("原系统编号")
    private String oriSystemNo;

    // 运单号
    @ApiModelProperty("运单号")
    private String waybillNo;

    // 原始物流订单编码
    @ApiModelProperty("原始物流订单编码")
    private String rootLogisticsCode;

    // 物流订单编码
    @ApiModelProperty("物流订单编码")
    private String logisticsCode;

    // 运输路线编码
    @ApiModelProperty("运输路线编码")
    private String lteRouteCode;

    // 运输线路名称
    @ApiModelProperty("运输线路名称")
    private String lteRouteName;

    // 标准运距(单位公里)
    @ApiModelProperty("标准运距(单位公里)")
    private Double standardDistance;

    // 标准时长
    @ApiModelProperty("标准时长")
    private Double standardTime;

    // 标准时长单位编码
    @ApiModelProperty("标准时长单位编码")
    private String standardTimeUnitCode;

    // 司机是否确认
    @ApiModelProperty("司机是否确认")
    private Boolean driverConfirmed;

    // 无车承运人：业务类型
    @ApiModelProperty("无车承运人：业务类型")
    private String businessTypeCode;

    // 无车承运人：货物类型
    @ApiModelProperty("无车承运人：货物类型")
    private String cargoTypeClassificationCode;

    // 业务跟单员编码
    @ApiModelProperty("业务跟单员编码")
    private String bizOwnerUserCode;

    // 发货单位组织编码
    @ApiModelProperty("发货单位组织编码")
    private String shipperOrgCode;

    // 发货单位名称
    @ApiModelProperty("发货单位名称")
    private String shipperOrgName;

    // 发货联系人编码
    @ApiModelProperty("发货联系人编码")
    private String shipperUserCode;

    // 发货联系人姓名
    @ApiModelProperty("发货联系人姓名")
    private String shipperUserFullName;

    // 发货联系人电话
    @ApiModelProperty("发货联系人电话")
    private String shipperUserPhone;

    // 发货地址省份编码
    @ApiModelProperty("发货地址省份编码")
    private String shipperAddrProvinceCode;

    // 发货地址城市编码
    @ApiModelProperty("发货地址城市编码")
    private String shipperAddrCityCode;

    // 发货地址区县编码
    @ApiModelProperty("发货地址区县编码")
    private String shipperAddrCountyCode;

    // 发货地址（具体地址）
    @ApiModelProperty("发货地址（具体地址）")
    private String shipperAddr;

    // 实际发货人姓名
    @ApiModelProperty("实际发货人姓名")
    private String shipperActualUserFullName;

    // 无车承运人：发货人个人证件号
    @ApiModelProperty("无车承运人：发货人个人证件号")
    private String shipperActualUserCertifyNo;

    // 托运时间
    @ApiModelProperty("托运时间")
    private Date consignmentTime;

    // 预计装货日期
    @ApiModelProperty("预计装货日期")
    private Date loadingDate;

    // 预计卸货时间
    @ApiModelProperty("预计卸货时间")
    private Date unloadingDate;

    // 收货单位组织编码
    @ApiModelProperty("收货单位组织编码")
    private String consigneeOrgCode;

    // 收货单位名称
    @ApiModelProperty("收货单位名称")
    private String consigneeOrgName;

    // 收货联系人编码
    @ApiModelProperty("收货联系人编码")
    private String consigneeUserCode;

    // 收货联系人姓名
    @ApiModelProperty("收货联系人姓名")
    private String consigneeUserFullName;

    // 收货联系人电话
    @ApiModelProperty("收货联系人电话")
    private String consigneeUserPhone;

    // 收货地址省份编码
    @ApiModelProperty("收货地址省份编码")
    private String consigneeAddrProvinceCode;

    // 收货地址城市编码
    @ApiModelProperty("收货地址城市编码")
    private String consigneeAddrCityCode;

    // 收货地址区县编码
    @ApiModelProperty("收货地址区县编码")
    private String consigneeAddrCountyCode;

    // 收货地址（具体地址）
    @ApiModelProperty("收货地址（具体地址）")
    private String consigneeAddr;

    // 实际收货人姓名
    @ApiModelProperty("实际收货人姓名")
    private String consigneeActualUserFullName;

    // 收款类型（组织收款/个人收款）（编码类型：receipts_type）
    @ApiModelProperty("收款类型（组织收款/个人收款）（编码类型：receipts_type）")
    private Boolean receiptsType;

    // 收款类型名称（组织收款/个人收款）
    @ApiModelProperty("收款类型名称（组织收款/个人收款）")
    private String receiptsTypeName;

    // 收款组织/人
    @ApiModelProperty("收款组织/人")
    private String receiptsTarget;

    // 收款组织/人银行账户名
    @ApiModelProperty("收款组织/人银行账户名")
    private String receiptsTargetBankAccountName;

    // 收款组织/人银行账号
    @ApiModelProperty("收款组织/人银行账号")
    private String receiptsTargetBankAccountNo;

    // 收款组织/人开户银行
    @ApiModelProperty("收款组织/人开户银行")
    private String receiptsTargetBankName;

    // 收款组织/人开户支行
    @ApiModelProperty("收款组织/人开户支行")
    private String receiptsTargetBankBranch;

    // 无车承运人：信用代码
    @ApiModelProperty("无车承运人：信用代码")
    private String unifiedSocialCreditIdentifier;

    // 无车承运人：无车承运人的许可证
    @ApiModelProperty("无车承运人：无车承运人的许可证")
    private String truckBrokerLicense;

    // 无车承运人：车辆所属业主的许可证
    @ApiModelProperty("无车承运人：车辆所属业主的许可证")
    private String truckOwnerLicense;

    // 车辆许可证号
    @ApiModelProperty("车辆许可证号")
    private String truckLicense;

    // 车型编码
    @ApiModelProperty("车型编码")
    private String truckModelCode;

    // 车型简称
    @ApiModelProperty("车型简称")
    private String truckModelName;

    // 无车承运人：车型分类代码
    @ApiModelProperty("无车承运人：车型分类代码")
    private String vehicleClassificationCode;

    // 无车承运人：车辆所有人
    @ApiModelProperty("无车承运人：车辆所有人")
    private String truckOwner;

    // 车辆编码
    @ApiModelProperty("车辆编码")
    private String truckCode;

    // 车牌号
    @ApiModelProperty("车牌号")
    private String truckLicenseNo;

    // 牌照类型
    @ApiModelProperty("牌照类型")
    private String licensePlateTypeCode;

    // 挂车车辆编码
    @ApiModelProperty("挂车车辆编码")
    private String trailerTruckCode;

    // 挂车车牌号
    @ApiModelProperty("挂车车牌号")
    private String trailerTruckLicenseNo;

    // 行驶证号码
    @ApiModelProperty("行驶证号码")
    private String drivingLicenseNo;

    // 车长
    @ApiModelProperty("车长")
    private Double truckLength;

    // 动力类型
    @ApiModelProperty("动力类型")
    private Integer truckPowerType;

    // 核载重量（From车辆表）
    @ApiModelProperty("核载重量（From车辆表）")
    private Double regTonnage;

    // 核载重量单位编码
    @ApiModelProperty("核载重量单位编码")
    private String regTonnageUnitCode;

    // 核载重量单位名称
    @ApiModelProperty("核载重量单位名称")
    private String regTonnageUnitName;

    // 车辆组织名称
    @ApiModelProperty("车辆组织名称")
    private String truckOrgName;

    // 司机编码
    @ApiModelProperty("司机编码")
    private String driverCode;

    // 司机姓名
    @ApiModelProperty("司机姓名")
    private String driverFullName;

    // 司机电话
    @ApiModelProperty("司机电话")
    private String driverPhone;

    // 驾驶证号
    @ApiModelProperty("驾驶证号")
    private String driverLicenseNo;

    // 副驾驶司机编码（执行人）
    @ApiModelProperty("副驾驶司机编码（执行人）")
    private String viceDriverCode;

    // 副驾驶司机姓名
    @ApiModelProperty("副驾驶司机姓名")
    private String viceDriverName;

    // 副驾驶司机电话
    @ApiModelProperty("副驾驶司机电话")
    private String viceDriverPhone;

    // 卖方客户组织编码
    @ApiModelProperty("卖方客户组织编码")
    private String sellerOrgCode;

    // 卖方客户组织名称
    @ApiModelProperty("卖方客户组织名称")
    private String sellerOrgName;

    // 买方客户组织编码
    @ApiModelProperty("买方客户组织编码")
    private String customerOrgCode;

    // 买方客户组织名称
    @ApiModelProperty("买方客户组织名称")
    private String customerOrgName;

    // 客户运价编码
    @ApiModelProperty("客户运价编码")
    private String lteRatesCode;

    // 货物计量标准（1、吨，2、方，3、件）
    @ApiModelProperty("货物计量标准（1、吨，2、方，3、件）")
    private Integer meterageType;

    // 单位运价
    @ApiModelProperty("单位运价")
    private Double unitPrice;

    // 参考运费（运费理论值）
    @ApiModelProperty("参考运费（运费理论值）")
    private Double calculatedFee;

    // 协商运费货币单位编码
    @ApiModelProperty("协商运费货币单位编码")
    private String currencyUnitCode;

    // 协商运费货币单位名称
    @ApiModelProperty("协商运费货币单位名称")
    private String currencyUnitName;

    // 双方协商后的最终运费（等于结算单中的最终运费）
    @ApiModelProperty("双方协商后的最终运费（等于结算单中的最终运费）")
    private Double confirmedFee;

    // 货币总金额
    @ApiModelProperty("货币总金额")
    private Double totalMonetaryAmount;

    // 预估结算货量
    @ApiModelProperty("预估结算货量")
    private Double expectSettleVolume;

    // 货物毛重(千克）
    @ApiModelProperty("货物毛重(千克）")
    private Double goodsItemGrossWeight;

    // 货物体积
    @ApiModelProperty("货物体积")
    private Double goodsVolume;

    // 总件数
    @ApiModelProperty("总件数")
    private Integer goodsAmount;

    // 成交模式(1 货主人工确认成交/2 系统自动确认成交)（编码类型：trade_method）
    @ApiModelProperty("成交模式(1 货主人工确认成交/2 系统自动确认成交)（编码类型：trade_method）")
    private Integer tradeMethod;

    // 承运方组织编码
    @ApiModelProperty("承运方组织编码")
    private String carrierOrgCode;

    // 承运方组织名称
    @ApiModelProperty("承运方组织名称")
    private String carrierOrgName;

    // 承运方用户编码（接盘人/接单人）
    @ApiModelProperty("承运方用户编码（接盘人/接单人）")
    private String carrierUserCode;

    // 承运方用户姓名（接盘人/接单人）
    @ApiModelProperty("承运方用户姓名（接盘人/接单人）")
    private String carrierUserFullName;

    // 支付方式(1 现金/2 在线银行卡支付/3 物流币支付)（编码类型：pay_method）
    @ApiModelProperty("支付方式(1 现金/2 在线银行卡支付/3 物流币支付)（编码类型：pay_method）")
    private Integer payMethod;

    // 支付方式名称
    @ApiModelProperty("支付方式名称")
    private String payMethodName;

    // 货损标准
    @ApiModelProperty("货损标准")
    private Double standardGoodsLoss;

    // 货差计算方式 1.按量 2.按系数
    @ApiModelProperty("货差计算方式 1.按量 2.按系数")
    private Integer freightLossMethod;

    // 货损标准单位编码
    @ApiModelProperty("货损标准单位编码")
    private String goodsLossUnitCode;

    // 货损标准单位名称
    @ApiModelProperty("货损标准单位名称")
    private String goodsLossUnitName;

    // 货损备注
    @ApiModelProperty("货损备注")
    private String goodsLossRemark;

    // 单位运价计量单位编码
    @ApiModelProperty("单位运价计量单位编码")
    private String measureUnitCode;

    // 单位运价计量单位名称
    @ApiModelProperty("单位运价计量单位名称")
    private String measureUnitName;

    // 参考运费货币单位编码
    @ApiModelProperty("参考运费货币单位编码")
    private String calculatedFeeUnitCode;

    // 参考运费货币单位名称
    @ApiModelProperty("参考运费货币单位名称")
    private String calculatedFeeUnitName;

    // 运单状态(1、待装货，2、运输中，3、待结算，4、待支付，5、运单完成，6、货主取消，7、车主取消)（编码类型：order_status）
    @ApiModelProperty("运单状态(1、待装货，2、运输中，3、待结算，4、待支付，5、运单完成，6、货主取消，7、车主取消)（编码类型：order_status）")
    private String waybillStatus;

    // 实际发货/装货时间
    @ApiModelProperty("实际发货/装货时间")
    private Date actualLoadingTime;

    // 实际收货/卸货时间
    @ApiModelProperty("实际收货/卸货时间")
    private Date actualUnloadingTime;

    // 上报备注
    @ApiModelProperty("上报备注")
    private String reportNotes;

    // 上报状态（1、未上报，2、已上报）
    @ApiModelProperty("上报状态（1、未上报，2、已上报）")
    private Integer reportStatus;

    // 上报人用户编码
    @ApiModelProperty("上报人用户编码")
    private String reportUserCode;

    // 上报人姓名
    @ApiModelProperty("上报人姓名")
    private String reportUserFullName;

    // 上报时间
    @ApiModelProperty("上报时间")
    private Date reportTime;

    // 当前业务动作编码
    @ApiModelProperty("当前业务动作编码")
    private String currentActionCode;

    // 当前业务动作名称
    @ApiModelProperty("当前业务动作名称")
    private String currentActionName;

    // 实际结算行车费
    @ApiModelProperty("实际结算行车费")
    private Double confirmedDrivingFee;

    // 实际结算行车费计量单位编码
    @ApiModelProperty("实际结算行车费计量单位编码")
    private String confirmedDrivingFeeUnitCode;

    // 实际结算总费用
    @ApiModelProperty("实际结算总费用")
    private Double confirmedTotalFee;

    // 实际结算总费用计量单位编码
    @ApiModelProperty("实际结算总费用计量单位编码")
    private String confirmedTotalFeeUnitCode;

    // 结算货量（单位吨）
    @ApiModelProperty("结算货量（单位吨）")
    private Double confirmedSettleVolume;

    // 实际货差扣款（单位元）
    @ApiModelProperty("实际货差扣款（单位元）")
    private Double confirmedLossDeduction;

    // 结算状态1、待结算，2、已结算 ，3、结算已作废 
    @ApiModelProperty("结算状态1、待结算，2、已结算 ，3、结算已作废 ")
    private Integer billStatus;

    // 结算操作人编码
    @ApiModelProperty("结算操作人编码")
    private String billUserCode;

    // 结算操作人姓名
    @ApiModelProperty("结算操作人姓名")
    private String billUserFullName;

    // 结算组织编码
    @ApiModelProperty("结算组织编码")
    private String billOrgCode;

    // 结算组织名称
    @ApiModelProperty("结算组织名称")
    private String billOrgName;

    // 收款人（车主/司机）编码
    @ApiModelProperty("收款人（车主/司机）编码")
    private String payeeUserCode;

    // 收款人
    @ApiModelProperty("收款人")
    private String payeeUserFullName;

    // 收款人开户行名称
    @ApiModelProperty("收款人开户行名称")
    private String payeeBankName;

    // 收款人开户支行名称
    @ApiModelProperty("收款人开户支行名称")
    private String payeeBankBranchName;

    // 收款人银行开户人名字
    @ApiModelProperty("收款人银行开户人名字")
    private String payeeBankAccountName;

    // 收款人银行账号
    @ApiModelProperty("收款人银行账号")
    private String payeeBankAccountNo;

    // 结算完成时间
    @ApiModelProperty("结算完成时间")
    private Date billingFinishTime;

    // 归账日期
    @ApiModelProperty("归账日期")
    private Date toAccountDate;

    // 自定义属性
    @ApiModelProperty("自定义属性")
    private String customData;

    // 标签
    @ApiModelProperty("标签")
    private String waybillTag;

    // 描述
    @ApiModelProperty("描述")
    private String description;

    // 备注
    @ApiModelProperty("备注")
    private String remark;

    // 是否禁用
    @ApiModelProperty("是否禁用")
    private Boolean disabled;

    // 是否删除
    @ApiModelProperty("是否删除")
    private Boolean deleted;

    // 操作模块编码
    @ApiModelProperty("操作模块编码")
    private String moduleCode;

    // 创建人编码
    @ApiModelProperty("创建人编码")
    private String creatorUserCode;

    // 创建人用户名
    @ApiModelProperty("创建人用户名")
    private String creatorUsername;

    // 创建时间
    @ApiModelProperty("创建时间")
    private Date createTime;

    // 更新人编码
    @ApiModelProperty("更新人编码")
    private String updateUserCode;

    // 更新人用户名
    @ApiModelProperty("更新人用户名")
    private String updateUsername;

    // 更新时间
    @ApiModelProperty("更新时间")
    private Date updateTime;

    // 操作者IP
    @ApiModelProperty("操作者IP")
    private String ip;

    // 操作者所处经度
    @ApiModelProperty("操作者所处经度")
    private Double operatorLongitude;

    // 操作者所处纬度
    @ApiModelProperty("操作者所处纬度")
    private Double operatorLatitude;

    // 所属用户编码
    @ApiModelProperty("所属用户编码")
    private String ownerUserCode;

    // 所属公司编码
    @ApiModelProperty("所属公司编码")
    private String ownerOrgCode;

    // 所属公司名字
    @ApiModelProperty("所属公司名字")
    private String ownerOrgName;

    // 同步串号
    @ApiModelProperty("同步串号")
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
     * @param waybillCode 运单编码
     */
    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    /**
     * get 运单编码
     * @return String 运单编码
     */
    public String getWaybillCode() {
        return waybillCode;
    }


    /**
     * set 原系统编号
     * @param oriSystemNo 原系统编号
     */
    public void setOriSystemNo(String oriSystemNo) {
        this.oriSystemNo = oriSystemNo;
    }

    /**
     * get 原系统编号
     * @return String 原系统编号
     */
    public String getOriSystemNo() {
        return oriSystemNo;
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
     * set 运输路线编码
     * @param lteRouteCode 运输路线编码
     */
    public void setLteRouteCode(String lteRouteCode) {
        this.lteRouteCode = lteRouteCode;
    }

    /**
     * get 运输路线编码
     * @return String 运输路线编码
     */
    public String getLteRouteCode() {
        return lteRouteCode;
    }


    /**
     * set 运输线路名称
     * @param lteRouteName 运输线路名称
     */
    public void setLteRouteName(String lteRouteName) {
        this.lteRouteName = lteRouteName;
    }

    /**
     * get 运输线路名称
     * @return String 运输线路名称
     */
    public String getLteRouteName() {
        return lteRouteName;
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
     * set 司机是否确认
     * @param driverConfirmed 司机是否确认
     */
    public void setDriverConfirmed(Boolean driverConfirmed) {
        this.driverConfirmed = driverConfirmed;
    }

    /**
     * get 司机是否确认
     * @return Boolean 司机是否确认
     */
    public Boolean getDriverConfirmed() {
        return driverConfirmed;
    }


    /**
     * set 无车承运人：业务类型
     * @param businessTypeCode 无车承运人：业务类型
     */
    public void setBusinessTypeCode(String businessTypeCode) {
        this.businessTypeCode = businessTypeCode;
    }

    /**
     * get 无车承运人：业务类型
     * @return String 无车承运人：业务类型
     */
    public String getBusinessTypeCode() {
        return businessTypeCode;
    }


    /**
     * set 无车承运人：货物类型
     * @param cargoTypeClassificationCode 无车承运人：货物类型
     */
    public void setCargoTypeClassificationCode(String cargoTypeClassificationCode) {
        this.cargoTypeClassificationCode = cargoTypeClassificationCode;
    }

    /**
     * get 无车承运人：货物类型
     * @return String 无车承运人：货物类型
     */
    public String getCargoTypeClassificationCode() {
        return cargoTypeClassificationCode;
    }


    /**
     * set 业务跟单员编码
     * @param bizOwnerUserCode 业务跟单员编码
     */
    public void setBizOwnerUserCode(String bizOwnerUserCode) {
        this.bizOwnerUserCode = bizOwnerUserCode;
    }

    /**
     * get 业务跟单员编码
     * @return String 业务跟单员编码
     */
    public String getBizOwnerUserCode() {
        return bizOwnerUserCode;
    }


    /**
     * set 发货单位组织编码
     * @param shipperOrgCode 发货单位组织编码
     */
    public void setShipperOrgCode(String shipperOrgCode) {
        this.shipperOrgCode = shipperOrgCode;
    }

    /**
     * get 发货单位组织编码
     * @return String 发货单位组织编码
     */
    public String getShipperOrgCode() {
        return shipperOrgCode;
    }


    /**
     * set 发货单位名称
     * @param shipperOrgName 发货单位名称
     */
    public void setShipperOrgName(String shipperOrgName) {
        this.shipperOrgName = shipperOrgName;
    }

    /**
     * get 发货单位名称
     * @return String 发货单位名称
     */
    public String getShipperOrgName() {
        return shipperOrgName;
    }


    /**
     * set 发货联系人编码
     * @param shipperUserCode 发货联系人编码
     */
    public void setShipperUserCode(String shipperUserCode) {
        this.shipperUserCode = shipperUserCode;
    }

    /**
     * get 发货联系人编码
     * @return String 发货联系人编码
     */
    public String getShipperUserCode() {
        return shipperUserCode;
    }


    /**
     * set 发货联系人姓名
     * @param shipperUserFullName 发货联系人姓名
     */
    public void setShipperUserFullName(String shipperUserFullName) {
        this.shipperUserFullName = shipperUserFullName;
    }

    /**
     * get 发货联系人姓名
     * @return String 发货联系人姓名
     */
    public String getShipperUserFullName() {
        return shipperUserFullName;
    }


    /**
     * set 发货联系人电话
     * @param shipperUserPhone 发货联系人电话
     */
    public void setShipperUserPhone(String shipperUserPhone) {
        this.shipperUserPhone = shipperUserPhone;
    }

    /**
     * get 发货联系人电话
     * @return String 发货联系人电话
     */
    public String getShipperUserPhone() {
        return shipperUserPhone;
    }


    /**
     * set 发货地址省份编码
     * @param shipperAddrProvinceCode 发货地址省份编码
     */
    public void setShipperAddrProvinceCode(String shipperAddrProvinceCode) {
        this.shipperAddrProvinceCode = shipperAddrProvinceCode;
    }

    /**
     * get 发货地址省份编码
     * @return String 发货地址省份编码
     */
    public String getShipperAddrProvinceCode() {
        return shipperAddrProvinceCode;
    }


    /**
     * set 发货地址城市编码
     * @param shipperAddrCityCode 发货地址城市编码
     */
    public void setShipperAddrCityCode(String shipperAddrCityCode) {
        this.shipperAddrCityCode = shipperAddrCityCode;
    }

    /**
     * get 发货地址城市编码
     * @return String 发货地址城市编码
     */
    public String getShipperAddrCityCode() {
        return shipperAddrCityCode;
    }


    /**
     * set 发货地址区县编码
     * @param shipperAddrCountyCode 发货地址区县编码
     */
    public void setShipperAddrCountyCode(String shipperAddrCountyCode) {
        this.shipperAddrCountyCode = shipperAddrCountyCode;
    }

    /**
     * get 发货地址区县编码
     * @return String 发货地址区县编码
     */
    public String getShipperAddrCountyCode() {
        return shipperAddrCountyCode;
    }


    /**
     * set 发货地址（具体地址）
     * @param shipperAddr 发货地址（具体地址）
     */
    public void setShipperAddr(String shipperAddr) {
        this.shipperAddr = shipperAddr;
    }

    /**
     * get 发货地址（具体地址）
     * @return String 发货地址（具体地址）
     */
    public String getShipperAddr() {
        return shipperAddr;
    }


    /**
     * set 实际发货人姓名
     * @param shipperActualUserFullName 实际发货人姓名
     */
    public void setShipperActualUserFullName(String shipperActualUserFullName) {
        this.shipperActualUserFullName = shipperActualUserFullName;
    }

    /**
     * get 实际发货人姓名
     * @return String 实际发货人姓名
     */
    public String getShipperActualUserFullName() {
        return shipperActualUserFullName;
    }


    /**
     * set 无车承运人：发货人个人证件号
     * @param shipperActualUserCertifyNo 无车承运人：发货人个人证件号
     */
    public void setShipperActualUserCertifyNo(String shipperActualUserCertifyNo) {
        this.shipperActualUserCertifyNo = shipperActualUserCertifyNo;
    }

    /**
     * get 无车承运人：发货人个人证件号
     * @return String 无车承运人：发货人个人证件号
     */
    public String getShipperActualUserCertifyNo() {
        return shipperActualUserCertifyNo;
    }


    /**
     * set 托运时间
     * @param consignmentTime 托运时间
     */
    public void setConsignmentTime(Date consignmentTime) {
        this.consignmentTime = consignmentTime;
    }

    /**
     * get 托运时间
     * @return Date 托运时间
     */
    public Date getConsignmentTime() {
        return consignmentTime;
    }


    /**
     * set 预计装货日期
     * @param loadingDate 预计装货日期
     */
    public void setLoadingDate(Date loadingDate) {
        this.loadingDate = loadingDate;
    }

    /**
     * get 预计装货日期
     * @return Date 预计装货日期
     */
    public Date getLoadingDate() {
        return loadingDate;
    }


    /**
     * set 预计卸货时间
     * @param unloadingDate 预计卸货时间
     */
    public void setUnloadingDate(Date unloadingDate) {
        this.unloadingDate = unloadingDate;
    }

    /**
     * get 预计卸货时间
     * @return Date 预计卸货时间
     */
    public Date getUnloadingDate() {
        return unloadingDate;
    }


    /**
     * set 收货单位组织编码
     * @param consigneeOrgCode 收货单位组织编码
     */
    public void setConsigneeOrgCode(String consigneeOrgCode) {
        this.consigneeOrgCode = consigneeOrgCode;
    }

    /**
     * get 收货单位组织编码
     * @return String 收货单位组织编码
     */
    public String getConsigneeOrgCode() {
        return consigneeOrgCode;
    }


    /**
     * set 收货单位名称
     * @param consigneeOrgName 收货单位名称
     */
    public void setConsigneeOrgName(String consigneeOrgName) {
        this.consigneeOrgName = consigneeOrgName;
    }

    /**
     * get 收货单位名称
     * @return String 收货单位名称
     */
    public String getConsigneeOrgName() {
        return consigneeOrgName;
    }


    /**
     * set 收货联系人编码
     * @param consigneeUserCode 收货联系人编码
     */
    public void setConsigneeUserCode(String consigneeUserCode) {
        this.consigneeUserCode = consigneeUserCode;
    }

    /**
     * get 收货联系人编码
     * @return String 收货联系人编码
     */
    public String getConsigneeUserCode() {
        return consigneeUserCode;
    }


    /**
     * set 收货联系人姓名
     * @param consigneeUserFullName 收货联系人姓名
     */
    public void setConsigneeUserFullName(String consigneeUserFullName) {
        this.consigneeUserFullName = consigneeUserFullName;
    }

    /**
     * get 收货联系人姓名
     * @return String 收货联系人姓名
     */
    public String getConsigneeUserFullName() {
        return consigneeUserFullName;
    }


    /**
     * set 收货联系人电话
     * @param consigneeUserPhone 收货联系人电话
     */
    public void setConsigneeUserPhone(String consigneeUserPhone) {
        this.consigneeUserPhone = consigneeUserPhone;
    }

    /**
     * get 收货联系人电话
     * @return String 收货联系人电话
     */
    public String getConsigneeUserPhone() {
        return consigneeUserPhone;
    }


    /**
     * set 收货地址省份编码
     * @param consigneeAddrProvinceCode 收货地址省份编码
     */
    public void setConsigneeAddrProvinceCode(String consigneeAddrProvinceCode) {
        this.consigneeAddrProvinceCode = consigneeAddrProvinceCode;
    }

    /**
     * get 收货地址省份编码
     * @return String 收货地址省份编码
     */
    public String getConsigneeAddrProvinceCode() {
        return consigneeAddrProvinceCode;
    }


    /**
     * set 收货地址城市编码
     * @param consigneeAddrCityCode 收货地址城市编码
     */
    public void setConsigneeAddrCityCode(String consigneeAddrCityCode) {
        this.consigneeAddrCityCode = consigneeAddrCityCode;
    }

    /**
     * get 收货地址城市编码
     * @return String 收货地址城市编码
     */
    public String getConsigneeAddrCityCode() {
        return consigneeAddrCityCode;
    }


    /**
     * set 收货地址区县编码
     * @param consigneeAddrCountyCode 收货地址区县编码
     */
    public void setConsigneeAddrCountyCode(String consigneeAddrCountyCode) {
        this.consigneeAddrCountyCode = consigneeAddrCountyCode;
    }

    /**
     * get 收货地址区县编码
     * @return String 收货地址区县编码
     */
    public String getConsigneeAddrCountyCode() {
        return consigneeAddrCountyCode;
    }


    /**
     * set 收货地址（具体地址）
     * @param consigneeAddr 收货地址（具体地址）
     */
    public void setConsigneeAddr(String consigneeAddr) {
        this.consigneeAddr = consigneeAddr;
    }

    /**
     * get 收货地址（具体地址）
     * @return String 收货地址（具体地址）
     */
    public String getConsigneeAddr() {
        return consigneeAddr;
    }


    /**
     * set 实际收货人姓名
     * @param consigneeActualUserFullName 实际收货人姓名
     */
    public void setConsigneeActualUserFullName(String consigneeActualUserFullName) {
        this.consigneeActualUserFullName = consigneeActualUserFullName;
    }

    /**
     * get 实际收货人姓名
     * @return String 实际收货人姓名
     */
    public String getConsigneeActualUserFullName() {
        return consigneeActualUserFullName;
    }


    /**
     * set 收款类型（组织收款/个人收款）（编码类型：receipts_type）
     * @param receiptsType 收款类型（组织收款/个人收款）（编码类型：receipts_type）
     */
    public void setReceiptsType(Boolean receiptsType) {
        this.receiptsType = receiptsType;
    }

    /**
     * get 收款类型（组织收款/个人收款）（编码类型：receipts_type）
     * @return Boolean 收款类型（组织收款/个人收款）（编码类型：receipts_type）
     */
    public Boolean getReceiptsType() {
        return receiptsType;
    }


    /**
     * set 收款类型名称（组织收款/个人收款）
     * @param receiptsTypeName 收款类型名称（组织收款/个人收款）
     */
    public void setReceiptsTypeName(String receiptsTypeName) {
        this.receiptsTypeName = receiptsTypeName;
    }

    /**
     * get 收款类型名称（组织收款/个人收款）
     * @return String 收款类型名称（组织收款/个人收款）
     */
    public String getReceiptsTypeName() {
        return receiptsTypeName;
    }


    /**
     * set 收款组织/人
     * @param receiptsTarget 收款组织/人
     */
    public void setReceiptsTarget(String receiptsTarget) {
        this.receiptsTarget = receiptsTarget;
    }

    /**
     * get 收款组织/人
     * @return String 收款组织/人
     */
    public String getReceiptsTarget() {
        return receiptsTarget;
    }


    /**
     * set 收款组织/人银行账户名
     * @param receiptsTargetBankAccountName 收款组织/人银行账户名
     */
    public void setReceiptsTargetBankAccountName(String receiptsTargetBankAccountName) {
        this.receiptsTargetBankAccountName = receiptsTargetBankAccountName;
    }

    /**
     * get 收款组织/人银行账户名
     * @return String 收款组织/人银行账户名
     */
    public String getReceiptsTargetBankAccountName() {
        return receiptsTargetBankAccountName;
    }


    /**
     * set 收款组织/人银行账号
     * @param receiptsTargetBankAccountNo 收款组织/人银行账号
     */
    public void setReceiptsTargetBankAccountNo(String receiptsTargetBankAccountNo) {
        this.receiptsTargetBankAccountNo = receiptsTargetBankAccountNo;
    }

    /**
     * get 收款组织/人银行账号
     * @return String 收款组织/人银行账号
     */
    public String getReceiptsTargetBankAccountNo() {
        return receiptsTargetBankAccountNo;
    }


    /**
     * set 收款组织/人开户银行
     * @param receiptsTargetBankName 收款组织/人开户银行
     */
    public void setReceiptsTargetBankName(String receiptsTargetBankName) {
        this.receiptsTargetBankName = receiptsTargetBankName;
    }

    /**
     * get 收款组织/人开户银行
     * @return String 收款组织/人开户银行
     */
    public String getReceiptsTargetBankName() {
        return receiptsTargetBankName;
    }


    /**
     * set 收款组织/人开户支行
     * @param receiptsTargetBankBranch 收款组织/人开户支行
     */
    public void setReceiptsTargetBankBranch(String receiptsTargetBankBranch) {
        this.receiptsTargetBankBranch = receiptsTargetBankBranch;
    }

    /**
     * get 收款组织/人开户支行
     * @return String 收款组织/人开户支行
     */
    public String getReceiptsTargetBankBranch() {
        return receiptsTargetBankBranch;
    }


    /**
     * set 无车承运人：信用代码
     * @param unifiedSocialCreditIdentifier 无车承运人：信用代码
     */
    public void setUnifiedSocialCreditIdentifier(String unifiedSocialCreditIdentifier) {
        this.unifiedSocialCreditIdentifier = unifiedSocialCreditIdentifier;
    }

    /**
     * get 无车承运人：信用代码
     * @return String 无车承运人：信用代码
     */
    public String getUnifiedSocialCreditIdentifier() {
        return unifiedSocialCreditIdentifier;
    }


    /**
     * set 无车承运人：无车承运人的许可证
     * @param truckBrokerLicense 无车承运人：无车承运人的许可证
     */
    public void setTruckBrokerLicense(String truckBrokerLicense) {
        this.truckBrokerLicense = truckBrokerLicense;
    }

    /**
     * get 无车承运人：无车承运人的许可证
     * @return String 无车承运人：无车承运人的许可证
     */
    public String getTruckBrokerLicense() {
        return truckBrokerLicense;
    }


    /**
     * set 无车承运人：车辆所属业主的许可证
     * @param truckOwnerLicense 无车承运人：车辆所属业主的许可证
     */
    public void setTruckOwnerLicense(String truckOwnerLicense) {
        this.truckOwnerLicense = truckOwnerLicense;
    }

    /**
     * get 无车承运人：车辆所属业主的许可证
     * @return String 无车承运人：车辆所属业主的许可证
     */
    public String getTruckOwnerLicense() {
        return truckOwnerLicense;
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
     * set 无车承运人：车型分类代码
     * @param vehicleClassificationCode 无车承运人：车型分类代码
     */
    public void setVehicleClassificationCode(String vehicleClassificationCode) {
        this.vehicleClassificationCode = vehicleClassificationCode;
    }

    /**
     * get 无车承运人：车型分类代码
     * @return String 无车承运人：车型分类代码
     */
    public String getVehicleClassificationCode() {
        return vehicleClassificationCode;
    }


    /**
     * set 无车承运人：车辆所有人
     * @param truckOwner 无车承运人：车辆所有人
     */
    public void setTruckOwner(String truckOwner) {
        this.truckOwner = truckOwner;
    }

    /**
     * get 无车承运人：车辆所有人
     * @return String 无车承运人：车辆所有人
     */
    public String getTruckOwner() {
        return truckOwner;
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
     * set 核载重量单位名称
     * @param regTonnageUnitName 核载重量单位名称
     */
    public void setRegTonnageUnitName(String regTonnageUnitName) {
        this.regTonnageUnitName = regTonnageUnitName;
    }

    /**
     * get 核载重量单位名称
     * @return String 核载重量单位名称
     */
    public String getRegTonnageUnitName() {
        return regTonnageUnitName;
    }


    /**
     * set 车辆组织名称
     * @param truckOrgName 车辆组织名称
     */
    public void setTruckOrgName(String truckOrgName) {
        this.truckOrgName = truckOrgName;
    }

    /**
     * get 车辆组织名称
     * @return String 车辆组织名称
     */
    public String getTruckOrgName() {
        return truckOrgName;
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
     * @param viceDriverName 副驾驶司机姓名
     */
    public void setViceDriverName(String viceDriverName) {
        this.viceDriverName = viceDriverName;
    }

    /**
     * get 副驾驶司机姓名
     * @return String 副驾驶司机姓名
     */
    public String getViceDriverName() {
        return viceDriverName;
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
     * set 卖方客户组织编码
     * @param sellerOrgCode 卖方客户组织编码
     */
    public void setSellerOrgCode(String sellerOrgCode) {
        this.sellerOrgCode = sellerOrgCode;
    }

    /**
     * get 卖方客户组织编码
     * @return String 卖方客户组织编码
     */
    public String getSellerOrgCode() {
        return sellerOrgCode;
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
     * set 买方客户组织编码
     * @param customerOrgCode 买方客户组织编码
     */
    public void setCustomerOrgCode(String customerOrgCode) {
        this.customerOrgCode = customerOrgCode;
    }

    /**
     * get 买方客户组织编码
     * @return String 买方客户组织编码
     */
    public String getCustomerOrgCode() {
        return customerOrgCode;
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
     * set 客户运价编码
     * @param lteRatesCode 客户运价编码
     */
    public void setLteRatesCode(String lteRatesCode) {
        this.lteRatesCode = lteRatesCode;
    }

    /**
     * get 客户运价编码
     * @return String 客户运价编码
     */
    public String getLteRatesCode() {
        return lteRatesCode;
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
     * set 单位运价
     * @param unitPrice 单位运价
     */
    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    /**
     * get 单位运价
     * @return Double 单位运价
     */
    public Double getUnitPrice() {
        return unitPrice;
    }


    /**
     * set 参考运费（运费理论值）
     * @param calculatedFee 参考运费（运费理论值）
     */
    public void setCalculatedFee(Double calculatedFee) {
        this.calculatedFee = calculatedFee;
    }

    /**
     * get 参考运费（运费理论值）
     * @return Double 参考运费（运费理论值）
     */
    public Double getCalculatedFee() {
        return calculatedFee;
    }


    /**
     * set 协商运费货币单位编码
     * @param currencyUnitCode 协商运费货币单位编码
     */
    public void setCurrencyUnitCode(String currencyUnitCode) {
        this.currencyUnitCode = currencyUnitCode;
    }

    /**
     * get 协商运费货币单位编码
     * @return String 协商运费货币单位编码
     */
    public String getCurrencyUnitCode() {
        return currencyUnitCode;
    }


    /**
     * set 协商运费货币单位名称
     * @param currencyUnitName 协商运费货币单位名称
     */
    public void setCurrencyUnitName(String currencyUnitName) {
        this.currencyUnitName = currencyUnitName;
    }

    /**
     * get 协商运费货币单位名称
     * @return String 协商运费货币单位名称
     */
    public String getCurrencyUnitName() {
        return currencyUnitName;
    }


    /**
     * set 双方协商后的最终运费（等于结算单中的最终运费）
     * @param confirmedFee 双方协商后的最终运费（等于结算单中的最终运费）
     */
    public void setConfirmedFee(Double confirmedFee) {
        this.confirmedFee = confirmedFee;
    }

    /**
     * get 双方协商后的最终运费（等于结算单中的最终运费）
     * @return Double 双方协商后的最终运费（等于结算单中的最终运费）
     */
    public Double getConfirmedFee() {
        return confirmedFee;
    }


    /**
     * set 货币总金额
     * @param totalMonetaryAmount 货币总金额
     */
    public void setTotalMonetaryAmount(Double totalMonetaryAmount) {
        this.totalMonetaryAmount = totalMonetaryAmount;
    }

    /**
     * get 货币总金额
     * @return Double 货币总金额
     */
    public Double getTotalMonetaryAmount() {
        return totalMonetaryAmount;
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
     * set 货物毛重(千克）
     * @param goodsItemGrossWeight 货物毛重(千克）
     */
    public void setGoodsItemGrossWeight(Double goodsItemGrossWeight) {
        this.goodsItemGrossWeight = goodsItemGrossWeight;
    }

    /**
     * get 货物毛重(千克）
     * @return Double 货物毛重(千克）
     */
    public Double getGoodsItemGrossWeight() {
        return goodsItemGrossWeight;
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
     * set 总件数
     * @param goodsAmount 总件数
     */
    public void setGoodsAmount(Integer goodsAmount) {
        this.goodsAmount = goodsAmount;
    }

    /**
     * get 总件数
     * @return Integer 总件数
     */
    public Integer getGoodsAmount() {
        return goodsAmount;
    }


    /**
     * set 成交模式(1 货主人工确认成交/2 系统自动确认成交)（编码类型：trade_method）
     * @param tradeMethod 成交模式(1 货主人工确认成交/2 系统自动确认成交)（编码类型：trade_method）
     */
    public void setTradeMethod(Integer tradeMethod) {
        this.tradeMethod = tradeMethod;
    }

    /**
     * get 成交模式(1 货主人工确认成交/2 系统自动确认成交)（编码类型：trade_method）
     * @return Integer 成交模式(1 货主人工确认成交/2 系统自动确认成交)（编码类型：trade_method）
     */
    public Integer getTradeMethod() {
        return tradeMethod;
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
     * set 支付方式(1 现金/2 在线银行卡支付/3 物流币支付)（编码类型：pay_method）
     * @param payMethod 支付方式(1 现金/2 在线银行卡支付/3 物流币支付)（编码类型：pay_method）
     */
    public void setPayMethod(Integer payMethod) {
        this.payMethod = payMethod;
    }

    /**
     * get 支付方式(1 现金/2 在线银行卡支付/3 物流币支付)（编码类型：pay_method）
     * @return Integer 支付方式(1 现金/2 在线银行卡支付/3 物流币支付)（编码类型：pay_method）
     */
    public Integer getPayMethod() {
        return payMethod;
    }


    /**
     * set 支付方式名称
     * @param payMethodName 支付方式名称
     */
    public void setPayMethodName(String payMethodName) {
        this.payMethodName = payMethodName;
    }

    /**
     * get 支付方式名称
     * @return String 支付方式名称
     */
    public String getPayMethodName() {
        return payMethodName;
    }


    /**
     * set 货损标准
     * @param standardGoodsLoss 货损标准
     */
    public void setStandardGoodsLoss(Double standardGoodsLoss) {
        this.standardGoodsLoss = standardGoodsLoss;
    }

    /**
     * get 货损标准
     * @return Double 货损标准
     */
    public Double getStandardGoodsLoss() {
        return standardGoodsLoss;
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
     * set 单位运价计量单位编码
     * @param measureUnitCode 单位运价计量单位编码
     */
    public void setMeasureUnitCode(String measureUnitCode) {
        this.measureUnitCode = measureUnitCode;
    }

    /**
     * get 单位运价计量单位编码
     * @return String 单位运价计量单位编码
     */
    public String getMeasureUnitCode() {
        return measureUnitCode;
    }


    /**
     * set 单位运价计量单位名称
     * @param measureUnitName 单位运价计量单位名称
     */
    public void setMeasureUnitName(String measureUnitName) {
        this.measureUnitName = measureUnitName;
    }

    /**
     * get 单位运价计量单位名称
     * @return String 单位运价计量单位名称
     */
    public String getMeasureUnitName() {
        return measureUnitName;
    }


    /**
     * set 参考运费货币单位编码
     * @param calculatedFeeUnitCode 参考运费货币单位编码
     */
    public void setCalculatedFeeUnitCode(String calculatedFeeUnitCode) {
        this.calculatedFeeUnitCode = calculatedFeeUnitCode;
    }

    /**
     * get 参考运费货币单位编码
     * @return String 参考运费货币单位编码
     */
    public String getCalculatedFeeUnitCode() {
        return calculatedFeeUnitCode;
    }


    /**
     * set 参考运费货币单位名称
     * @param calculatedFeeUnitName 参考运费货币单位名称
     */
    public void setCalculatedFeeUnitName(String calculatedFeeUnitName) {
        this.calculatedFeeUnitName = calculatedFeeUnitName;
    }

    /**
     * get 参考运费货币单位名称
     * @return String 参考运费货币单位名称
     */
    public String getCalculatedFeeUnitName() {
        return calculatedFeeUnitName;
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
     * set 实际发货/装货时间
     * @param actualLoadingTime 实际发货/装货时间
     */
    public void setActualLoadingTime(Date actualLoadingTime) {
        this.actualLoadingTime = actualLoadingTime;
    }

    /**
     * get 实际发货/装货时间
     * @return Date 实际发货/装货时间
     */
    public Date getActualLoadingTime() {
        return actualLoadingTime;
    }


    /**
     * set 实际收货/卸货时间
     * @param actualUnloadingTime 实际收货/卸货时间
     */
    public void setActualUnloadingTime(Date actualUnloadingTime) {
        this.actualUnloadingTime = actualUnloadingTime;
    }

    /**
     * get 实际收货/卸货时间
     * @return Date 实际收货/卸货时间
     */
    public Date getActualUnloadingTime() {
        return actualUnloadingTime;
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
     * set 结算完成时间
     * @param billingFinishTime 结算完成时间
     */
    public void setBillingFinishTime(Date billingFinishTime) {
        this.billingFinishTime = billingFinishTime;
    }

    /**
     * get 结算完成时间
     * @return Date 结算完成时间
     */
    public Date getBillingFinishTime() {
        return billingFinishTime;
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
