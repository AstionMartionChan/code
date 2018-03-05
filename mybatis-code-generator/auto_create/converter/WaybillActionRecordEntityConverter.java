package com.rltx.waybill.service.converter;

import com.rltx.waybill.po.WaybillActionRecordEntity;

/**
 * waybill_action_record 转换类
 */
public class WaybillActionRecordEntityConverter {

    /**
     * 转换Entity
     * @param vo 基本信息
     * @param commParamsVo 公共参数
     * @return WaybillActionRecordEntity
     */
    public static WaybillActionRecordEntity toWaybillActionRecordEntity(WaybillActionRecordEntityVo vo, CommParamsVo commParamsVo) {
        WaybillActionRecordEntity entity = new WaybillActionRecordEntity();
        // 自增ID
        entity.setId(vo.getId());
        // 编码
        entity.setCode(vo.getCode());
        // 运单编码
        entity.setWaybillCode(vo.getWaybillCode());
        // 业务动作分组，如：行车记录、异常等
        entity.setActionGroup(vo.getActionGroup());
        // 业务动作编码
        entity.setActionCode(vo.getActionCode());
        // 业务动作记录编码（可重复），进行一个业务动作操作所产生的记录编码一致
        entity.setActionRecordCode(vo.getActionRecordCode());
        // 要操作的对象
        entity.setTargetObject(vo.getTargetObject());
        // 要操作的字段
        entity.setTargetField(vo.getTargetField());
        // 操作的字段值
        entity.setTargetFieldValue(vo.getTargetFieldValue());
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