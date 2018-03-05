package com.rltx.waybill.service.converter;

import com.rltx.waybill.po.WaybillPaybillEntity;

/**
 * waybill_paybill 转换类
 */
public class WaybillPaybillEntityConverter {

    /**
     * 转换Entity
     * @param vo 基本信息
     * @param commParamsVo 公共参数
     * @return WaybillPaybillEntity
     */
    public static WaybillPaybillEntity toWaybillPaybillEntity(WaybillPaybillEntityVo vo, CommParamsVo commParamsVo) {
        WaybillPaybillEntity entity = new WaybillPaybillEntity();
        // 付款单ID
        entity.setId(vo.getId());
        // 编码
        entity.setCode(vo.getCode());
        // 付款单号
        entity.setPaymentNo(vo.getPaymentNo());
        // 运单编码
        entity.setWaybillCode(vo.getWaybillCode());
        // 运单号
        entity.setWaybillNo(vo.getWaybillNo());
        // 结算类型（1、外协结算，2、内部结算）
        entity.setBillingClass(vo.getBillingClass());
        // 最近一次打印编号
        entity.setLastPrintNo(vo.getLastPrintNo());
        // 应付运费
        entity.setConfirmedTranFee(vo.getConfirmedTranFee());
        // 应付运费货币单位编码
        entity.setTranFeeUnitCode(vo.getTranFeeUnitCode());
        // 应付行车费
        entity.setConfirmedDrivingFee(vo.getConfirmedDrivingFee());
        // 应付行车费计量单位编码
        entity.setConfirmedDrivingFeeUnitCode(vo.getConfirmedDrivingFeeUnitCode());
        // 应付总费用(=实际结算总费用=实际结算运费+实际结算行车费)
        entity.setConfirmedTotalFee(vo.getConfirmedTotalFee());
        // 应付总费用货币单位编码
        entity.setTotalFeeUnitCode(vo.getTotalFeeUnitCode());
        // 应付总费用货币单位名称
        entity.setTotalFeeUnitName(vo.getTotalFeeUnitName());
        // 实付总费用
        entity.setActualFee(vo.getActualFee());
        // 实付总费用货币单位编码
        entity.setActualFeeUnitCode(vo.getActualFeeUnitCode());
        // 实付总费用货币单位名称
        entity.setActualFeeUnitName(vo.getActualFeeUnitName());
        // 付款方式（1.现金 2.转账 3.平台在线支付）
        entity.setPayMethod(vo.getPayMethod());
        // 付款状态：1.付款中/2.付款完成/3.付款已作废/4.付款异常
        entity.setPayStatus(vo.getPayStatus());
        // 请款组织编码
        entity.setPayRequestOrgCode(vo.getPayRequestOrgCode());
        // 请款组织名称
        entity.setPayRequestOrgName(vo.getPayRequestOrgName());
        // 请款人编码（当前操作人）
        entity.setPayRequestUserCode(vo.getPayRequestUserCode());
        // 请款人姓名
        entity.setPayRequestUserFullName(vo.getPayRequestUserFullName());
        // 请款时间
        entity.setPayRequestTime(vo.getPayRequestTime());
        // 付款组织编码
        entity.setPayerOrgCode(vo.getPayerOrgCode());
        // 付款组织名称
        entity.setPayerOrgName(vo.getPayerOrgName());
        // 付款操作人编码
        entity.setPayerOpCode(vo.getPayerOpCode());
        // 付款操作人姓名
        entity.setPayerOpFullName(vo.getPayerOpFullName());
        // 付款操作时间
        entity.setPayerOpTime(vo.getPayerOpTime());
        // 收款人编码
        entity.setPayeeUserCode(vo.getPayeeUserCode());
        // 收款人姓名
        entity.setPayeeUserFullName(vo.getPayeeUserFullName());
        // 收款人开户行名称
        entity.setPayeeBankName(vo.getPayeeBankName());
        // 收款人开户支行名称
        entity.setPayeeBankBranchName(vo.getPayeeBankBranchName());
        // 收款人银行开户人姓名
        entity.setPayeeBankAccountFullName(vo.getPayeeBankAccountFullName());
        // 收款人银行账号
        entity.setPayeeBankAccountNo(vo.getPayeeBankAccountNo());
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
