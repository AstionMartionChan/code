package com.rltx.waybill.service.converter;

import com.rltx.waybill.po.WaybillComsumerEventEntity;

/**
 * waybill_comsumer_event 转换类
 */
public class WaybillComsumerEventEntityConverter {

    /**
     * 转换Entity
     * @param vo 基本信息
     * @param commParamsVo 公共参数
     * @return WaybillComsumerEventEntity
     */
    public static WaybillComsumerEventEntity toWaybillComsumerEventEntity(WaybillComsumerEventEntityVo vo, CommParamsVo commParamsVo) {
        WaybillComsumerEventEntity entity = new WaybillComsumerEventEntity();
        // 自增id
        entity.setId(vo.getId());
        // 事件code
        entity.setEventCode(vo.getEventCode());
        // 消费者名称
        entity.setConsumerName(vo.getConsumerName());
        // 主题
        entity.setTopic(vo.getTopic());
        // 消息体
        entity.setMessage(vo.getMessage());
        // 状态
        entity.setStatus(vo.getStatus());
        // 失败原因
        entity.setFailedReason(vo.getFailedReason());
        // 创建时间
        entity.setCreateTime(vo.getCreateTime());
        // 更新时间
        entity.setUpdateTime(vo.getUpdateTime());
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
