package com.rltx.waybill.service.converter;

import com.rltx.waybill.po.WaybillActionChargeItemRelationEntity;

/**
 * waybill_action_charge_item_relation 转换类
 */
public class WaybillActionChargeItemRelationEntityConverter {

    /**
     * 转换Entity
     * @param vo 基本信息
     * @param commParamsVo 公共参数
     * @return WaybillActionChargeItemRelationEntity
     */
    public static WaybillActionChargeItemRelationEntity toWaybillActionChargeItemRelationEntity(WaybillActionChargeItemRelationEntityVo vo, CommParamsVo commParamsVo) {
        WaybillActionChargeItemRelationEntity entity = new WaybillActionChargeItemRelationEntity();
        // 自增ID
        entity.setId(vo.getId());
        // 运单编码
        entity.setWaybillCode(vo.getWaybillCode());
        // 模板编码
        entity.setTemplateCode(vo.getTemplateCode());
        // 业务动作编码
        entity.setActionCode(vo.getActionCode());
        // 费用科目编码
        entity.setChargeItemCode(vo.getChargeItemCode());
        // 费用科目名称
        entity.setChargeItemName(vo.getChargeItemName());
        // 记账数量单位编码
        entity.setChargeItemNumberUnitCode(vo.getChargeItemNumberUnitCode());
        // 记账数量单位名称
        entity.setChargeItemNumberUnitName(vo.getChargeItemNumberUnitName());
        // 记账单价单位编码
        entity.setChargeItemPriceUnitCode(vo.getChargeItemPriceUnitCode());
        // 记账单价单位名称
        entity.setChargeItemPriceUnitName(vo.getChargeItemPriceUnitName());
        // 记账金额计量单位编码
        entity.setChargeItemAmountsUnitCode(vo.getChargeItemAmountsUnitCode());
        // 记账金额计量单位名称
        entity.setChargeItemAmountsUnitName(vo.getChargeItemAmountsUnitName());
        // 往来户说明（例如：中石油、交警、路霸、高管处….文字描述，可做自动完成辅助输入）
        entity.setUsageDesc(vo.getUsageDesc());
        // 可见性配置：bit0Web端是否可见，bit1司机端是否可见
        entity.setVisibleConfig(vo.getVisibleConfig());
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
