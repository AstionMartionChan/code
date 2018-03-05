package com.rltx.waybill.service.converter;

import com.rltx.waybill.po.WaybillCodeNoEntity;

/**
 * waybill_code_no 转换类
 */
public class WaybillCodeNoEntityConverter {

    /**
     * 转换Entity
     * @param vo 基本信息
     * @param commParamsVo 公共参数
     * @return WaybillCodeNoEntity
     */
    public static WaybillCodeNoEntity toWaybillCodeNoEntity(WaybillCodeNoEntityVo vo, CommParamsVo commParamsVo) {
        WaybillCodeNoEntity entity = new WaybillCodeNoEntity();
        // 自增ID
        entity.setId(vo.getId());
        // 类型
        entity.setType(vo.getType());
        // 组织编码
        entity.setOrgCode(vo.getOrgCode());
        // 年份
        entity.setYear(vo.getYear());
        // 序列号
        entity.setValue(vo.getValue());
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
