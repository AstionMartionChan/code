package com.rltx.waybill.service.converter;

import com.rltx.waybill.po.WaybillTicketDetailEntity;

/**
 * waybill_ticket_detail 转换类
 */
public class WaybillTicketDetailEntityConverter {

    /**
     * 转换Entity
     * @param vo 基本信息
     * @param commParamsVo 公共参数
     * @return WaybillTicketDetailEntity
     */
    public static WaybillTicketDetailEntity toWaybillTicketDetailEntity(WaybillTicketDetailEntityVo vo, CommParamsVo commParamsVo) {
        WaybillTicketDetailEntity entity = new WaybillTicketDetailEntity();
        // id
        entity.setId(vo.getId());
        // 货单编码
        entity.setCode(vo.getCode());
        // 运单编码
        entity.setWaybillCode(vo.getWaybillCode());
        // 配货单号
        entity.setDistributionNo(vo.getDistributionNo());
        // 单号
        entity.setTicketNo(vo.getTicketNo());
        // 货物名称
        entity.setGoodsName(vo.getGoodsName());
        // 重量
        entity.setTtlWeight(vo.getTtlWeight());
        // 重量单位
        entity.setTtlWeightUnit(vo.getTtlWeightUnit());
        // 体积
        entity.setTtlVolume(vo.getTtlVolume());
        // 体积单位
        entity.setTtlVolumeUnit(vo.getTtlVolumeUnit());
        // 数量
        entity.setTtlNumber(vo.getTtlNumber());
        // 数量单位
        entity.setTtlNumberUnit(vo.getTtlNumberUnit());
        // 晚到车索赔
        entity.setWdcClaim(vo.getWdcClaim());
        // 晚到货索赔
        entity.setWdhClaim(vo.getWdhClaim());
        // 晚回单索赔
        entity.setWhdClaim(vo.getWhdClaim());
        // 箱破损索赔
        entity.setXpsClaim(vo.getXpsClaim());
        // 其他索赔
        entity.setOtherClaim(vo.getOtherClaim());
        // 索赔备注
        entity.setClaimDescription(vo.getClaimDescription());
        // 状态（1、正常，2、异常冻结，3、异常释放）
        entity.setStatus(vo.getStatus());
        // 自定义属性
        entity.setCustomData(vo.getCustomData());
        // 标签
        entity.setTicketTag(vo.getTicketTag());
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
