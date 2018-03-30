package com.rltx.truck.broker.service;

import com.rltx.truck.broker.bo.VehicleReportBo;
import com.rltx.truck.broker.bo.WaybillReportBo;
import com.rltx.truck.broker.vo.CapitalFlowDocumentVo;
import com.rltx.truck.broker.vo.VehicleReportDocumentVo;
import com.rltx.truck.broker.vo.WaybillReportDocumentVo;
import com.wondersgroup.cuteinfo.client.exchangeserver.exchangetransport.exception.UMessageTransportException;
import freemarker.template.TemplateException;

import java.io.IOException;

/**
 * Created by Leo_Chan on 2017/9/4.
 */
public interface ILoginkReportService {


    /**
     * 无车承运人运单上报
     * @param waybillReportDocumentVo   无车承运人运单上报字段元素vo
     * @param senderCode                物流交换代码
     * @param senderPassword            物流交换密码
     * @return                          无车承运人运单上报结果bo
     */
    WaybillReportBo waybillReport(WaybillReportDocumentVo waybillReportDocumentVo, String senderCode, String senderPassword) throws IOException, TemplateException, UMessageTransportException;


    /**
     * 车辆诚信信息上报
     * @param vehicleReportDocumentVo   车辆上报字段元素vo
     * @param senderCode                物流交换代码
     * @param senderPassword            物流交换密码
     * @return                          车辆上报结果bo
     * @throws TemplateException
     * @throws IOException
     * @throws UMessageTransportException
     */
    VehicleReportBo vehicleReport(VehicleReportDocumentVo vehicleReportDocumentVo, String senderCode, String senderPassword) throws TemplateException, IOException, UMessageTransportException;


    /**
     * 资金流水上报
     * @param capitalFlowDocumentVo   资金流水上报字段元素vo
     * @param senderCode                物流交换代码
     * @param senderPassword            物流交换密码
     * @return                          车辆上报结果bo
     * @throws TemplateException
     * @throws IOException
     * @throws UMessageTransportException
     */
    Boolean capitalFlowReport(CapitalFlowDocumentVo capitalFlowDocumentVo, String senderCode, String senderPassword) throws TemplateException, IOException, UMessageTransportException;
}
