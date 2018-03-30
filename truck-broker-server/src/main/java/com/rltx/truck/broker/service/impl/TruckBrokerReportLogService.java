package com.rltx.truck.broker.service.impl;

import com.rltx.truck.broker.dao.ITruckBrokerReportLogDao;
import com.rltx.truck.broker.po.TruckBrokerReportLogEntity;
import com.rltx.truck.broker.service.ITruckBrokerReportLogService;
import com.rltx.truck.broker.service.converter.TruckBrokerReportLogConverter;
import com.rltx.truck.broker.vo.CommParamsVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 无车承运人上报日志service实现类
 */
@Service("truckBrokerReportLogService")
@Transactional(readOnly = true)
public class TruckBrokerReportLogService implements ITruckBrokerReportLogService {

    @Resource(name = "truckBrokerReportLogDao")
    private ITruckBrokerReportLogDao truckBrokerReportLogDao;

    @Override
    @Transactional(readOnly = false)
    public void saveLog(Long waybillId, String reportMessage, String responseMessage, CommParamsVo commParamsVo) {

        TruckBrokerReportLogEntity entity = TruckBrokerReportLogConverter.toEntity(waybillId, reportMessage, responseMessage, commParamsVo);
        truckBrokerReportLogDao.save(entity);
    }
}
