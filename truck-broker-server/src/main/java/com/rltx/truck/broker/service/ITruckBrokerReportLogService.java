package com.rltx.truck.broker.service;


import com.rltx.truck.broker.vo.CommParamsVo;

/**
 * 无车承运人上报日志service类
 */
public interface ITruckBrokerReportLogService {

    /**
     * 保存无车承运人上报日志
     * @param waybillId 运单id
     * @param reportMessage 上报报文
     * @param responseMessage 响应报文
     * @param commParamsVo 共通字段
     */
    void saveLog(Long waybillId, String reportMessage, String responseMessage, CommParamsVo commParamsVo);
}
