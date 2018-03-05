package com.rltx.waybill.service.converter;

import com.rltx.waybill.po.WaybillBaseInfoEntity;

/**
 * waybill_base_info 转换类
 */
public class WaybillBaseInfoEntityConverter {

    /**
     * 转换Entity
     * @param vo 基本信息
     * @param commParamsVo 公共参数
     * @return WaybillBaseInfoEntity
     */
    public static WaybillBaseInfoEntity toWaybillBaseInfoEntity(WaybillBaseInfoEntityVo vo, CommParamsVo commParamsVo) {
        WaybillBaseInfoEntity entity = new WaybillBaseInfoEntity();
        // 自增ID
        entity.setId(vo.getId());
        // 运单编码
        entity.setCode(vo.getCode());
        // 运单号
        entity.setWaybillNo(vo.getWaybillNo());
        // 原始物流订单编码
        entity.setRootLogisticsCode(vo.getRootLogisticsCode());
        // 物流订单编码
        entity.setLogisticsCode(vo.getLogisticsCode());
        // 货物名称
        entity.setGoodsName(vo.getGoodsName());
        // 货物单价
        entity.setGoodsPrice(vo.getGoodsPrice());
        // 货物单价单位编码
        entity.setGoodsPriceUnitCode(vo.getGoodsPriceUnitCode());
        // 货物数量
        entity.setGoodsAmount(vo.getGoodsAmount());
        // 货物数量单位编码
        entity.setGoodsAmountUnitCode(vo.getGoodsAmountUnitCode());
        // 货物重量
        entity.setGoodsWeight(vo.getGoodsWeight());
        // 货物重量单位编码
        entity.setGoodsWeightUnitCode(vo.getGoodsWeightUnitCode());
        // 货物体积
        entity.setGoodsVolume(vo.getGoodsVolume());
        // 货物体积单位编码
        entity.setGoodsVolumeUnitCode(vo.getGoodsVolumeUnitCode());
        // 运输路线编码
        entity.setRouteCode(vo.getRouteCode());
        // 运输线路名称
        entity.setRouteName(vo.getRouteName());
        // 标准运距(单位公里)
        entity.setStandardDistance(vo.getStandardDistance());
        // 标准时长
        entity.setStandardTime(vo.getStandardTime());
        // 标准时长单位编码
        entity.setStandardTimeUnitCode(vo.getStandardTimeUnitCode());
        // 发货单位名称
        entity.setLoadingOrgName(vo.getLoadingOrgName());
        // 发货联系人姓名
        entity.setLoadingUserFullName(vo.getLoadingUserFullName());
        // 发货联系人电话
        entity.setLoadingUserPhone(vo.getLoadingUserPhone());
        // 发货地址省份编码
        entity.setLoadingProvinceCode(vo.getLoadingProvinceCode());
        // 发货地址城市编码
        entity.setLoadingCityCode(vo.getLoadingCityCode());
        // 发货地址区县编码
        entity.setLoadingCountyCode(vo.getLoadingCountyCode());
        // 发货地址（具体地址）
        entity.setLoadingAddr(vo.getLoadingAddr());
        // 收货单位名称
        entity.setUnloadingOrgName(vo.getUnloadingOrgName());
        // 收货联系人姓名
        entity.setUnloadingUserFullName(vo.getUnloadingUserFullName());
        // 收货联系人电话
        entity.setUnloadingUserPhone(vo.getUnloadingUserPhone());
        // 收货地址省份编码
        entity.setUnloadingProvinceCode(vo.getUnloadingProvinceCode());
        // 收货地址城市编码
        entity.setUnloadingCityCode(vo.getUnloadingCityCode());
        // 收货地址区县编码
        entity.setUnloadingCountyCode(vo.getUnloadingCountyCode());
        // 收货地址（具体地址）
        entity.setUnloadingAddr(vo.getUnloadingAddr());
        // 车辆许可证号
        entity.setTruckLicense(vo.getTruckLicense());
        // 车型编码
        entity.setTruckModelCode(vo.getTruckModelCode());
        // 车型简称
        entity.setTruckModelName(vo.getTruckModelName());
        // 车辆编码
        entity.setTruckCode(vo.getTruckCode());
        // 车牌号
        entity.setTruckLicenseNo(vo.getTruckLicenseNo());
        // 牌照类型
        entity.setLicensePlateTypeCode(vo.getLicensePlateTypeCode());
        // 挂车车辆编码
        entity.setTrailerTruckCode(vo.getTrailerTruckCode());
        // 挂车车牌号
        entity.setTrailerTruckLicenseNo(vo.getTrailerTruckLicenseNo());
        // 行驶证号码
        entity.setDrivingLicenseNo(vo.getDrivingLicenseNo());
        // 车长
        entity.setTruckLength(vo.getTruckLength());
        // 车长单位编码
        entity.setTruckLengthUnitCode(vo.getTruckLengthUnitCode());
        // 动力类型
        entity.setTruckPowerType(vo.getTruckPowerType());
        // 核载重量（From车辆表）
        entity.setRegTonnage(vo.getRegTonnage());
        // 核载重量单位编码
        entity.setRegTonnageUnitCode(vo.getRegTonnageUnitCode());
        // 司机编码
        entity.setDriverCode(vo.getDriverCode());
        // 司机姓名
        entity.setDriverFullName(vo.getDriverFullName());
        // 司机电话
        entity.setDriverPhone(vo.getDriverPhone());
        // 驾驶证号
        entity.setDriverLicenseNo(vo.getDriverLicenseNo());
        // 副驾驶司机编码（执行人）
        entity.setViceDriverCode(vo.getViceDriverCode());
        // 副驾驶司机姓名
        entity.setViceDriverFullName(vo.getViceDriverFullName());
        // 副驾驶司机电话
        entity.setViceDriverPhone(vo.getViceDriverPhone());
        // 卖方客户组织名称
        entity.setSellerOrgName(vo.getSellerOrgName());
        // 买方客户组织名称
        entity.setCustomerOrgName(vo.getCustomerOrgName());
        // 运价编码
        entity.setRatesCode(vo.getRatesCode());
        // 货物计量标准（1、吨，2、方，3、件）
        entity.setMeterageType(vo.getMeterageType());
        // 司机运价
        entity.setDriverPrice(vo.getDriverPrice());
        // 司机运价单位编码
        entity.setDriverPriceUnitCode(vo.getDriverPriceUnitCode());
        // 预估结算货量
        entity.setExpectSettleVolume(vo.getExpectSettleVolume());
        // 承运方组织编码
        entity.setCarrierOrgCode(vo.getCarrierOrgCode());
        // 承运方组织名称
        entity.setCarrierOrgName(vo.getCarrierOrgName());
        // 承运方用户编码（接盘人/接单人）
        entity.setCarrierUserCode(vo.getCarrierUserCode());
        // 承运方用户姓名（接盘人/接单人）
        entity.setCarrierUserFullName(vo.getCarrierUserFullName());
        // 合理货差
        entity.setReasonableGoodsLoss(vo.getReasonableGoodsLoss());
        // 货差计算方式 1.按量 2.按系数
        entity.setFreightLossMethod(vo.getFreightLossMethod());
        // 货损标准单位编码
        entity.setGoodsLossUnitCode(vo.getGoodsLossUnitCode());
        // 货损标准单位名称
        entity.setGoodsLossUnitName(vo.getGoodsLossUnitName());
        // 货损备注
        entity.setGoodsLossRemark(vo.getGoodsLossRemark());
        // 运单状态(1、待装货，2、运输中，3、待结算，4、待支付，5、运单完成，6、货主取消，7、车主取消)（编码类型：order_status）
        entity.setWaybillStatus(vo.getWaybillStatus());
        // 装货时间
        entity.setLoadingTime(vo.getLoadingTime());
        // 卸货时间
        entity.setUnloadingTime(vo.getUnloadingTime());
        // 当前业务动作编码
        entity.setCurrentActionCode(vo.getCurrentActionCode());
        // 当前业务动作名称
        entity.setCurrentActionName(vo.getCurrentActionName());
        // 实际结算行车费
        entity.setConfirmedDrivingFee(vo.getConfirmedDrivingFee());
        // 实际结算行车费计量单位编码
        entity.setConfirmedDrivingFeeUnitCode(vo.getConfirmedDrivingFeeUnitCode());
        // 实际结算总费用
        entity.setConfirmedTotalFee(vo.getConfirmedTotalFee());
        // 实际结算总费用计量单位编码
        entity.setConfirmedTotalFeeUnitCode(vo.getConfirmedTotalFeeUnitCode());
        // 结算货量（单位吨）
        entity.setConfirmedSettleVolume(vo.getConfirmedSettleVolume());
        // 实际结算货量单位编码
        entity.setConfirmedSettleVolumeUnitCode(vo.getConfirmedSettleVolumeUnitCode());
        // 实际货差扣款（单位元）
        entity.setConfirmedLossDeduction(vo.getConfirmedLossDeduction());
        // 结算状态1、待结算，2、已结算 ，3、结算已作废 
        entity.setBillStatus(vo.getBillStatus());
        // 结算操作人编码
        entity.setBillUserCode(vo.getBillUserCode());
        // 结算操作人姓名
        entity.setBillUserFullName(vo.getBillUserFullName());
        // 结算组织编码
        entity.setBillOrgCode(vo.getBillOrgCode());
        // 结算组织名称
        entity.setBillOrgName(vo.getBillOrgName());
        // 结算完成时间
        entity.setBillFinishTime(vo.getBillFinishTime());
        // 收款人（车主/司机）编码
        entity.setPayeeUserCode(vo.getPayeeUserCode());
        // 收款人
        entity.setPayeeUserFullName(vo.getPayeeUserFullName());
        // 收款人开户行名称
        entity.setPayeeBankName(vo.getPayeeBankName());
        // 收款人开户支行名称
        entity.setPayeeBankBranchName(vo.getPayeeBankBranchName());
        // 收款人银行开户人名字
        entity.setPayeeBankAccountName(vo.getPayeeBankAccountName());
        // 收款人银行账号
        entity.setPayeeBankAccountNo(vo.getPayeeBankAccountNo());
        // 归账日期
        entity.setToAccountDate(vo.getToAccountDate());
        // 货币总金额（上报运单）
        entity.setReportTotalMonetaryAmount(vo.getReportTotalMonetaryAmount());
        // 实际收货人姓名
        entity.setReportActualUnloadingUserFullName(vo.getReportActualUnloadingUserFullName());
        // 实际发货人姓名
        entity.setReportActualLoadingUserFullName(vo.getReportActualLoadingUserFullName());
        // 托运时间
        entity.setReportConsignmentTime(vo.getReportConsignmentTime());
        // 货物毛重（千克）（上报运单）
        entity.setReportGoodsItemGrossWeight(vo.getReportGoodsItemGrossWeight());
        // 上报备注
        entity.setReportNotes(vo.getReportNotes());
        // 上报状态（1、未上报，2、已上报）
        entity.setReportStatus(vo.getReportStatus());
        // 上报人用户编码
        entity.setReportUserCode(vo.getReportUserCode());
        // 上报人姓名
        entity.setReportUserFullName(vo.getReportUserFullName());
        // 上报时间
        entity.setReportTime(vo.getReportTime());
        // 无车承运人：业务类型
        entity.setReportBusinessTypeCode(vo.getReportBusinessTypeCode());
        // 无车承运人：货物类型
        entity.setReportCargoTypeClassificationCode(vo.getReportCargoTypeClassificationCode());
        // 无车承运人：信用代码
        entity.setReportUnifiedSocialCreditIdentifier(vo.getReportUnifiedSocialCreditIdentifier());
        // 无车承运人：无车承运人的许可证
        entity.setReportTruckBrokerLicense(vo.getReportTruckBrokerLicense());
        // 无车承运人：车辆所属业主的许可证
        entity.setReportTruckOwnerLicense(vo.getReportTruckOwnerLicense());
        // 无车承运人：装货人个人证件号
        entity.setReportActualLoadingUserCertifyNo(vo.getReportActualLoadingUserCertifyNo());
        // 无车承运人：车型分类代码
        entity.setReportVehicleClassificationCode(vo.getReportVehicleClassificationCode());
        // 无车承运人：车辆所有人
        entity.setReportTruckOwner(vo.getReportTruckOwner());
        // 自定义属性
        entity.setCustomData(vo.getCustomData());
        // 标签
        entity.setWaybillTag(vo.getWaybillTag());
        // 描述
        entity.setDescription(vo.getDescription());
        // 备注
        entity.setRemark(vo.getRemark());
        // 是否禁用
        entity.setDisabled(vo.getDisabled());
        // 是否删除
        entity.setDeleted(vo.getDeleted());
        // 操作模块编码
        entity.setModuleCode(vo.getModuleCode());
        // 创建人编码
        entity.setCreatorUserCode(vo.getCreatorUserCode());
        // 创建人用户名
        entity.setCreatorUsername(vo.getCreatorUsername());
        // 创建时间
        entity.setCreateTime(vo.getCreateTime());
        // 更新人编码
        entity.setUpdateUserCode(vo.getUpdateUserCode());
        // 更新人用户名
        entity.setUpdateUsername(vo.getUpdateUsername());
        // 更新时间
        entity.setUpdateTime(vo.getUpdateTime());
        // 操作者IP
        entity.setIp(vo.getIp());
        // 操作者所处经度
        entity.setOperatorLongitude(vo.getOperatorLongitude());
        // 操作者所处纬度
        entity.setOperatorLatitude(vo.getOperatorLatitude());
        // 所属用户编码
        entity.setOwnerUserCode(vo.getOwnerUserCode());
        // 所属公司编码
        entity.setOwnerOrgCode(vo.getOwnerOrgCode());
        // 所属公司名字
        entity.setOwnerOrgName(vo.getOwnerOrgName());
        // 同步串号
        entity.setSynchronousId(vo.getSynchronousId());
        // 操作者IP
        entity.setIp(commParamsVo.getIp());
        // 操作模块编码
        entity.setModuleCode(commParamsVo.getModuleCode());
        // 创建人编码
        entity.setCreatorUserCode(commParamsVo.getCreatorUserCode());
        // 创建人用户名
        entity.setCreatorUsername(commParamsVo.getCreatorUsername());
        // 更新人编码
        entity.setUpdateUserCode(commParamsVo.getUpdateUserCode());
        // 更新人用户名
        entity.setUpdateUsername(commParamsVo.getUpdateUsername());
        // 操作者所处纬度
        entity.setOperatorLatitude(commParamsVo.getOperatorLatitude());
        // 操作者所处经度
        entity.setOperatorLongitude(commParamsVo.getOperatorLongitude());
        // 同步串号
        entity.setSynchronousId(commParamsVo.getSynchronousId());
        // 所属用户编码
        entity.setOwnerUserCode(commParamsVo.getOwnerUserCode());
        // 所属公司名字
        entity.setOwnerOrgName(commParamsVo.getOwnerOrgName());
        // 所属公司编码
        entity.setOwnerOrgCode(commParamsVo.getOwnerOrgCode());
        // 创建时间
        entity.setCreateTime(commParamsVo.getCreateTime());
        // 更新时间
        entity.setUpdateTime(commParamsVo.getUpdateTime());
        return entity;
    }
}
