package com.rltx.lbs.service.converter;

import com.rltx.lbs.po.LbsDeviceTruckRelationEntity;

/**
 * lbs_device_truck_relation 转换类
 */
public class LbsDeviceTruckRelationEntityConverter {

    /**
     * 转换Entity
     * @param vo 基本信息
     * @param commParamsVo 公共参数
     * @return LbsDeviceTruckRelationEntity
     */
    public static LbsDeviceTruckRelationEntity toLbsDeviceTruckRelationEntity(LbsDeviceTruckRelationEntityVo vo, CommParamsVo commParamsVo) {
        LbsDeviceTruckRelationEntity entity = new LbsDeviceTruckRelationEntity();
        // 自增Id
        entity.setId(vo.getId());
        // 编码
        entity.setCode(vo.getCode());
        // 设备编码
        entity.setDeviceCode(vo.getDeviceCode());
        // 设备名称
        entity.setDeviceName(vo.getDeviceName());
        // 设备号
        entity.setDeviceNo(vo.getDeviceNo());
        // 流量卡号
        entity.setSimCardNo(vo.getSimCardNo());
        // 开通时间
        entity.setOpeningTime(vo.getOpeningTime());
        // 到期时间
        entity.setExpiryDate(vo.getExpiryDate());
        // 车辆编码
        entity.setTruckCode(vo.getTruckCode());
        // 车牌号
        entity.setTruckLicenseNo(vo.getTruckLicenseNo());
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