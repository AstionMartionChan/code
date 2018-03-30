package com.rltx.truck.broker.service;

import com.rltx.truck.broker.bo.TruckCarrierReportBo;
import com.rltx.truck.broker.bo.TruckCreditQueryBo;
import com.rltx.truck.broker.vo.CommParamsVo;
import com.rltx.truck.broker.vo.TruckCarrierDocumentVo;
import com.rltx.truck.broker.vo.TruckCreditQueryDocumentVo;
import com.wondersgroup.cuteinfo.client.exchangeserver.exchangetransport.exception.UMessageTransportException;
import freemarker.template.TemplateException;
import org.dom4j.DocumentException;

import java.io.IOException;

/**
 * Created by Leo_Chan on 2017/4/14.
 */
public interface ITruckCarrierReportService {



    /**
     * 无车承运人运单上报
     * @param truckCarrierDocumentVo    运单上报字段vo
     * @param waybillId                 运单id
     * @param commParamsVo              共同字段
     * @return
     * @throws IOException
     * @throws TemplateException
     * @throws UMessageTransportException 运单上报结果bo
     */
    TruckCarrierReportBo truckCarrierReport(TruckCarrierDocumentVo truckCarrierDocumentVo, Long waybillId, CommParamsVo commParamsVo) throws IOException, TemplateException, UMessageTransportException;


    /**
     * 车辆查询
     * @param truckCreditQueryDocumentVo 车辆查询vo
     * @param currentOrgId 当前调用组织id
     * @return
     * @throws IOException
     * @throws TemplateException
     * @throws DocumentException
     */
    TruckCreditQueryBo truckCreditQuery(TruckCreditQueryDocumentVo truckCreditQueryDocumentVo, Long currentOrgId) throws IOException, TemplateException;
}
