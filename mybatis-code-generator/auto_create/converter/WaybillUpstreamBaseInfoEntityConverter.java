package com.rltx.waybill.service.converter;

import com.rltx.waybill.po.WaybillUpstreamBaseInfoEntity;

/**
 * waybill_upstream_base_info 转换类
 */
public class WaybillUpstreamBaseInfoEntityConverter {

    /**
     * 转换Entity
     * @param vo 基本信息
     * @param commParamsVo 公共参数
     * @return WaybillUpstreamBaseInfoEntity
     */
    public static WaybillUpstreamBaseInfoEntity toWaybillUpstreamBaseInfoEntity(WaybillUpstreamBaseInfoEntityVo vo, CommParamsVo commParamsVo) {
        WaybillUpstreamBaseInfoEntity entity = new WaybillUpstreamBaseInfoEntity();
        // 自增ID
        entity.setId(vo.getId());
        // 运单编码
        entity.setWaybillCode(vo.getWaybillCode());
        // 原系统编号
        entity.setOriSystemNo(vo.getOriSystemNo());
        // 运单号
        entity.setWaybillNo(vo.getWaybillNo());
        // 原始物流订单编码
        entity.setRootLogisticsCode(vo.getRootLogisticsCode());
        // 物流订单编码
        entity.setLogisticsCode(vo.getLogisticsCode());
        // 运输路线编码
        entity.setLteRouteCode(vo.getLteRouteCode());
        // 运输线路名称
        entity.setLteRouteName(vo.getLteRouteName());
        // 标准运距(单位公里)
        entity.setStandardDistance(vo.getStandardDistance());
        // 标准时长
        entity.setStandardTime(vo.getStandardTime());
        // 标准时长单位编码
        entity.setStandardTimeUnitCode(vo.getStandardTimeUnitCode());
        // 司机是否确认
        entity.setDriverConfirmed(vo.getDriverConfirmed());
        // 无车承运人：业务类型
        entity.setBusinessTypeCode(vo.getBusinessTypeCode());
        // 无车承运人：货物类型
        entity.setCargoTypeClassificationCode(vo.getCargoTypeClassificationCode());
        // 业务跟单员编码
        entity.setBizOwnerUserCode(vo.getBizOwnerUserCode());
        // 发货单位组织编码
        entity.setShipperOrgCode(vo.getShipperOrgCode());
        // 发货单位名称
        entity.setShipperOrgName(vo.getShipperOrgName());
        // 发货联系人编码
        entity.setShipperUserCode(vo.getShipperUserCode());
        // 发货联系人姓名
        entity.setShipperUserFullName(vo.getShipperUserFullName());
        // 发货联系人电话
        entity.setShipperUserPhone(vo.getShipperUserPhone());
        // 发货地址省份编码
        entity.setShipperAddrProvinceCode(vo.getShipperAddrProvinceCode());
        // 发货地址城市编码
        entity.setShipperAddrCityCode(vo.getShipperAddrCityCode());
        // 发货地址区县编码
        entity.setShipperAddrCountyCode(vo.getShipperAddrCountyCode());
        // 发货地址（具体地址）
        entity.setShipperAddr(vo.getShipperAddr());
        // 实际发货人姓名
        entity.setShipperActualUserFullName(vo.getShipperActualUserFullName());
        // 无车承运人：发货人个人证件号
        entity.setShipperActualUserCertifyNo(vo.getShipperActualUserCertifyNo());
        // 托运时间
        entity.setConsignmentTime(vo.getConsignmentTime());
        // 预计装货日期
        entity.setLoadingDate(vo.getLoadingDate());
        // 预计卸货时间
        entity.setUnloadingDate(vo.getUnloadingDate());
        // 收货单位组织编码
        entity.setConsigneeOrgCode(vo.getConsigneeOrgCode());
        // 收货单位名称
        entity.setConsigneeOrgName(vo.getConsigneeOrgName());
        // 收货联系人编码
        entity.setConsigneeUserCode(vo.getConsigneeUserCode());
        // 收货联系人姓名
        entity.setConsigneeUserFullName(vo.getConsigneeUserFullName());
        // 收货联系人电话
        entity.setConsigneeUserPhone(vo.getConsigneeUserPhone());
        // 收货地址省份编码
        entity.setConsigneeAddrProvinceCode(vo.getConsigneeAddrProvinceCode());
        // 收货地址城市编码
        entity.setConsigneeAddrCityCode(vo.getConsigneeAddrCityCode());
        // 收货地址区县编码
        entity.setConsigneeAddrCountyCode(vo.getConsigneeAddrCountyCode());
        // 收货地址（具体地址）
        entity.setConsigneeAddr(vo.getConsigneeAddr());
        // 实际收货人姓名
        entity.setConsigneeActualUserFullName(vo.getConsigneeActualUserFullName());
        // 收款类型（组织收款/个人收款）（编码类型：receipts_type）
        entity.setReceiptsType(vo.getReceiptsType());
        // 收款类型名称（组织收款/个人收款）
        entity.setReceiptsTypeName(vo.getReceiptsTypeName());
        // 收款组织/人
        entity.setReceiptsTarget(vo.getReceiptsTarget());
        // 收款组织/人银行账户名
        entity.setReceiptsTargetBankAccountName(vo.getReceiptsTargetBankAccountName());
        // 收款组织/人银行账号
        entity.setReceiptsTargetBankAccountNo(vo.getReceiptsTargetBankAccountNo());
        // 收款组织/人开户银行
        entity.setReceiptsTargetBankName(vo.getReceiptsTargetBankName());
        // 收款组织/人开户支行
        entity.setReceiptsTargetBankBranch(vo.getReceiptsTargetBankBranch());
        // 无车承运人：信用代码
        entity.setUnifiedSocialCreditIdentifier(vo.getUnifiedSocialCreditIdentifier());
        // 无车承运人：无车承运人的许可证
        entity.setTruckBrokerLicense(vo.getTruckBrokerLicense());
        // 无车承运人：车辆所属业主的许可证
        entity.setTruckOwnerLicense(vo.getTruckOwnerLicense());
        // 车辆许可证号
        entity.setTruckLicense(vo.getTruckLicense());
        // 车型编码
        entity.setTruckModelCode(vo.getTruckModelCode());
        // 车型简称
        entity.setTruckModelName(vo.getTruckModelName());
        // 无车承运人：车型分类代码
        entity.setVehicleClassificationCode(vo.getVehicleClassificationCode());
        // 无车承运人：车辆所有人
        entity.setTruckOwner(vo.getTruckOwner());
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
        // 动力类型
        entity.setTruckPowerType(vo.getTruckPowerType());
        // 核载重量（From车辆表）
        entity.setRegTonnage(vo.getRegTonnage());
        // 核载重量单位编码
        entity.setRegTonnageUnitCode(vo.getRegTonnageUnitCode());
        // 核载重量单位名称
        entity.setRegTonnageUnitName(vo.getRegTonnageUnitName());
        // 车辆组织名称
        entity.setTruckOrgName(vo.getTruckOrgName());
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
        entity.setViceDriverName(vo.getViceDriverName());
        // 副驾驶司机电话
        entity.setViceDriverPhone(vo.getViceDriverPhone());
        // 卖方客户组织编码
        entity.setSellerOrgCode(vo.getSellerOrgCode());
        // 卖方客户组织名称
        entity.setSellerOrgName(vo.getSellerOrgName());
        // 买方客户组织编码
        entity.setCustomerOrgCode(vo.getCustomerOrgCode());
        // 买方客户组织名称
        entity.setCustomerOrgName(vo.getCustomerOrgName());
        // 客户运价编码
        entity.setLteRatesCode(vo.getLteRatesCode());
        // 货物计量标准（1、吨，2、方，3、件）
        entity.setMeterageType(vo.getMeterageType());
        // 单位运价
        entity.setUnitPrice(vo.getUnitPrice());
        // 参考运费（运费理论值）
        entity.setCalculatedFee(vo.getCalculatedFee());
        // 协商运费货币单位编码
        entity.setCurrencyUnitCode(vo.getCurrencyUnitCode());
        // 协商运费货币单位名称
        entity.setCurrencyUnitName(vo.getCurrencyUnitName());
        // 双方协商后的最终运费（等于结算单中的最终运费）
        entity.setConfirmedFee(vo.getConfirmedFee());
        // 货币总金额
        entity.setTotalMonetaryAmount(vo.getTotalMonetaryAmount());
        // 预估结算货量
        entity.setExpectSettleVolume(vo.getExpectSettleVolume());
        // 货物毛重(千克）
        entity.setGoodsItemGrossWeight(vo.getGoodsItemGrossWeight());
        // 货物体积
        entity.setGoodsVolume(vo.getGoodsVolume());
        // 总件数
        entity.setGoodsAmount(vo.getGoodsAmount());
        // 成交模式(1 货主人工确认成交/2 系统自动确认成交)（编码类型：trade_method）
        entity.setTradeMethod(vo.getTradeMethod());
        // 承运方组织编码
        entity.setCarrierOrgCode(vo.getCarrierOrgCode());
        // 承运方组织名称
        entity.setCarrierOrgName(vo.getCarrierOrgName());
        // 承运方用户编码（接盘人/接单人）
        entity.setCarrierUserCode(vo.getCarrierUserCode());
        // 承运方用户姓名（接盘人/接单人）
        entity.setCarrierUserFullName(vo.getCarrierUserFullName());
        // 支付方式(1 现金/2 在线银行卡支付/3 物流币支付)（编码类型：pay_method）
        entity.setPayMethod(vo.getPayMethod());
        // 支付方式名称
        entity.setPayMethodName(vo.getPayMethodName());
        // 货损标准
        entity.setStandardGoodsLoss(vo.getStandardGoodsLoss());
        // 货差计算方式 1.按量 2.按系数
        entity.setFreightLossMethod(vo.getFreightLossMethod());
        // 货损标准单位编码
        entity.setGoodsLossUnitCode(vo.getGoodsLossUnitCode());
        // 货损标准单位名称
        entity.setGoodsLossUnitName(vo.getGoodsLossUnitName());
        // 货损备注
        entity.setGoodsLossRemark(vo.getGoodsLossRemark());
        // 单位运价计量单位编码
        entity.setMeasureUnitCode(vo.getMeasureUnitCode());
        // 单位运价计量单位名称
        entity.setMeasureUnitName(vo.getMeasureUnitName());
        // 参考运费货币单位编码
        entity.setCalculatedFeeUnitCode(vo.getCalculatedFeeUnitCode());
        // 参考运费货币单位名称
        entity.setCalculatedFeeUnitName(vo.getCalculatedFeeUnitName());
        // 运单状态(1、待装货，2、运输中，3、待结算，4、待支付，5、运单完成，6、货主取消，7、车主取消)（编码类型：order_status）
        entity.setWaybillStatus(vo.getWaybillStatus());
        // 实际发货/装货时间
        entity.setActualLoadingTime(vo.getActualLoadingTime());
        // 实际收货/卸货时间
        entity.setActualUnloadingTime(vo.getActualUnloadingTime());
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
        // 结算完成时间
        entity.setBillingFinishTime(vo.getBillingFinishTime());
        // 归账日期
        entity.setToAccountDate(vo.getToAccountDate());
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
