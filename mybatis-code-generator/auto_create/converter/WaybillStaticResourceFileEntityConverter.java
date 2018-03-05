package com.rltx.waybill.service.converter;

import com.rltx.waybill.po.WaybillStaticResourceFileEntity;

/**
 * waybill_static_resource_file 转换类
 */
public class WaybillStaticResourceFileEntityConverter {

    /**
     * 转换Entity
     * @param vo 基本信息
     * @param commParamsVo 公共参数
     * @return WaybillStaticResourceFileEntity
     */
    public static WaybillStaticResourceFileEntity toWaybillStaticResourceFileEntity(WaybillStaticResourceFileEntityVo vo, CommParamsVo commParamsVo) {
        WaybillStaticResourceFileEntity entity = new WaybillStaticResourceFileEntity();
        // 自增ID
        entity.setId(vo.getId());
        // 资源唯一编码（使用UUID）
        entity.setResourceCode(vo.getResourceCode());
        // 文件存储路径
        entity.setStorePath(vo.getStorePath());
        // 
        entity.setFileName(vo.getFileName());
        // 
        entity.setOriginalName(vo.getOriginalName());
        // jpg/pdf/excel/ppt/...
        entity.setFileType(vo.getFileType());
        // 文件大小(kb)
        entity.setFileSize(vo.getFileSize());
        // 高度
        entity.setHeight(vo.getHeight());
        // 宽度
        entity.setWidth(vo.getWidth());
        // 文件最后修改日期
        entity.setLastModifyDate(vo.getLastModifyDate());
        // 0 不需要权限 1 需要权限
        entity.setIsPermission(vo.getIsPermission());
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
