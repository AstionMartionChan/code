package com.rltx.truck.broker.service.converter;


import com.rltx.truck.broker.po.TruckBrokerReportLogEntity;
import com.rltx.truck.broker.vo.CommParamsVo;

/**
 * 无车承运人上报记录转换类
 */
public class TruckBrokerReportLogConverter {

    /**
     * 转换无车承运人上报记录po
     * @param waybillId 运单id
     * @param reportMessage 上报报文
     * @param responseMessage 响应报文
     * @param commParamsVo 共通字段
     * @return
     */
    public static TruckBrokerReportLogEntity toEntity(Long waybillId, String reportMessage, String responseMessage, CommParamsVo commParamsVo){
        TruckBrokerReportLogEntity entity = new TruckBrokerReportLogEntity();
        entity.setWaybillId(waybillId);
        entity.setReportMessage(reportMessage);
        entity.setResponseMessage(responseMessage);
        entity.setDisabled(false);
        entity.setDeleted(false);
        entity.setAppCode(commParamsVo.getAppCode());
        entity.setCreator(commParamsVo.getCreator());
        entity.setCreatorUsername(commParamsVo.getCreatorUsername());
        entity.setCreateTime(commParamsVo.getCreateTime());
        entity.setUpdateUser(commParamsVo.getUpdateUser());
        entity.setUpdateUsername(commParamsVo.getUpdateUsername());
        entity.setUpdateTime(commParamsVo.getUpdateTime());
        entity.setIp(commParamsVo.getIp());
        entity.setOwnerUserId(commParamsVo.getOwnerUserId());
        entity.setOwnerOrgId(commParamsVo.getOwnerOrgId());
        entity.setOwnerOrgName(commParamsVo.getOwnerOrgName());
        entity.setSynchronousId(commParamsVo.getSynchronousId());
        return entity;
    }
}
