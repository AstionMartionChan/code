package com.rltx.waybill.service.converter;

import com.rltx.waybill.po.WaybillThumbnailResourceFileEntity;

/**
 * waybill_thumbnail_resource_file 转换类
 */
public class WaybillThumbnailResourceFileEntityConverter {

    /**
     * 转换Entity
     * @param vo 基本信息
     * @param commParamsVo 公共参数
     * @return WaybillThumbnailResourceFileEntity
     */
    public static WaybillThumbnailResourceFileEntity toWaybillThumbnailResourceFileEntity(WaybillThumbnailResourceFileEntityVo vo, CommParamsVo commParamsVo) {
        WaybillThumbnailResourceFileEntity entity = new WaybillThumbnailResourceFileEntity();
        // 自增ID
        entity.setId(vo.getId());
        // 静态资源唯一编码
        entity.setResourceCode(vo.getResourceCode());
        // 图片访问绝对路径
        entity.setStorePath(vo.getStorePath());
        // 图片高度
        entity.setHeight(vo.getHeight());
        // 图片宽度
        entity.setWidth(vo.getWidth());
        // 是否原始尺寸
        entity.setIsOriginalSize(vo.getIsOriginalSize());
        // 描述
        entity.setDescription(vo.getDescription());
        // 备注（平台运营人员备注）
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
