package com.rltx.truck.broker.controller;

import com.rltx.truck.broker.bo.EnterpriseQueryBo;
import com.rltx.truck.broker.bo.PersonQueryBo;
import com.rltx.truck.broker.bo.VehicleCheckBo;
import com.rltx.truck.broker.bo.VehicleQueryBo;
import com.rltx.truck.broker.bo.VehicleReportBo;
import com.rltx.truck.broker.bo.WaybillReportBo;
import com.rltx.truck.broker.controller.converter.CapitalFlowDocumentConverter;
import com.rltx.truck.broker.controller.converter.CreditCheckConverter;
import com.rltx.truck.broker.controller.converter.ReportConverter;
import com.rltx.truck.broker.result.*;
import com.rltx.truck.broker.service.ILoginkQueryService;
import com.rltx.truck.broker.service.ILoginkReportService;
import com.rltx.truck.broker.vo.*;
import com.wondersgroup.cuteinfo.client.exchangeserver.exchangetransport.exception.UMessageTransportException;
import freemarker.template.TemplateException;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Leo_Chan on 2017/9/4.
 */
@Controller
public class LoginkController {


    @Resource(name = "loginkReportService")
    private ILoginkReportService loginkReportService;

    @Resource(name = "loginkQueryService")
    private ILoginkQueryService loginkQueryService;


    /**
     * 运单上报
     * @param waybillReportVo       运单上报vo
     * @return
     * @throws TemplateException
     * @throws IOException
     * @throws UMessageTransportException
     */
    @RequestMapping(value = "/external/waybill/report", method = RequestMethod.POST)
    @ResponseBody
    public WaybillReportResult waybillReport(@RequestBody WaybillReportVo waybillReportVo) throws TemplateException, IOException, UMessageTransportException {

        // 转换为上报字段vo
        WaybillReportDocumentVo waybillReportDocumentVo = ReportConverter.toWaybillReportDocumentVo(waybillReportVo);

        // 调用运单上报服务 上报运单
        WaybillReportBo waybillReportBo = loginkReportService.waybillReport(waybillReportDocumentVo,
                waybillReportVo.getSenderCode(),
                waybillReportVo.getSenderPassword());

        WaybillReportResult result = ReportConverter.toWaybillReportResult(waybillReportBo);

        return result;
    }


    /**
     * 检查车辆是否存在国家平台
     * @param vehicleNumber     车牌号
     * @param senderCode        物流交换代码
     * @param senderPassword    物流交换密码
     * @return
     */
    @RequestMapping(value = "/external/vehicle/check", method = RequestMethod.POST)
    @ResponseBody
    public VehicleCheckResult checkVehicle(@RequestParam("vehicleNumber") String vehicleNumber,
                                           @RequestParam("senderCode") String senderCode,
                                           @RequestParam("senderPassword") String senderPassword) throws UnsupportedEncodingException {
        VehicleCheckBo vehicleCheckBo = loginkQueryService.vehicleCheck(vehicleNumber, senderCode, senderPassword);

        VehicleCheckResult result = CreditCheckConverter.toVehicleCheckResult(vehicleCheckBo);

        return result;
    }


    /**
     * 查询人员诚信信息（国家平台）
     * @param personName                     姓名
     * @param identityDocumentNumber         身份证号码
     * @param qualificationCertificateNumber 从业资格证号码
     * @param countrySubdivisionCode         省份代码
     * @param senderCode                     物流交换代码
     * @param senderPassword                 物流交换密码
     * @return
     */
    @RequestMapping(value = "/external/person/query", method = RequestMethod.POST)
    @ResponseBody
    public PersonQueryResult queryPerson(@RequestParam("personName") String personName,
                                         @RequestParam("identityDocumentNumber") String identityDocumentNumber,
                                         @RequestParam("qualificationCertificateNumber") String qualificationCertificateNumber,
                                         @RequestParam("countrySubdivisionCode") String countrySubdivisionCode,
                                         @RequestParam("senderCode") String senderCode,
                                         @RequestParam("senderPassword") String senderPassword) throws UnsupportedEncodingException {
        PersonQueryBo personQueryBo = loginkQueryService.personQuery(personName, identityDocumentNumber, qualificationCertificateNumber, countrySubdivisionCode, senderCode, senderPassword);

        PersonQueryResult result = ReportConverter.toPersonQueryResult(personQueryBo);

        return result;
    }



    /**
     * 查询车辆诚信信息（国家平台）
     * @param vehicleNumber     车牌号
     * @param senderCode        物流交换代码
     * @param senderPassword    物流交换密码
     * @return
     */
    @RequestMapping(value = "/external/vehicle/query", method = RequestMethod.POST)
    @ResponseBody
    public VehicleQueryResult queryVehicle(@RequestParam("vehicleNumber") String vehicleNumber,
                                           @RequestParam("licensePlateTypeCode") String licensePlateTypeCode,
                                           @RequestParam("senderCode") String senderCode,
                                           @RequestParam("senderPassword") String senderPassword) throws UnsupportedEncodingException {
        VehicleQueryBo vehicleQueryBo = loginkQueryService.vehicleQuery(vehicleNumber, licensePlateTypeCode, senderCode, senderPassword);

        VehicleQueryResult result = ReportConverter.toVehicleQueryResult(vehicleQueryBo);

        return result;
    }



    /**
     * 查询企业诚信信息（国家平台）
     * @param enterpriseName         车牌号
     * @param countrySubdivisionCode 省份
     * @param senderCode             物流交换代码
     * @param senderPassword         物流交换密码
     * @return
     */
    @RequestMapping(value = "/external/enterprise/query", method = RequestMethod.POST)
    @ResponseBody
    public EnterpriseQueryResult queryEnterprise(@RequestParam("enterpriseName") String enterpriseName,
                                           @RequestParam("countrySubdivisionCode") String countrySubdivisionCode,
                                           @RequestParam("senderCode") String senderCode,
                                           @RequestParam("senderPassword") String senderPassword) throws UnsupportedEncodingException {
        EnterpriseQueryBo enterpriseQueryBo = loginkQueryService.enterpriseQuery(enterpriseName, countrySubdivisionCode, senderCode, senderPassword);

        EnterpriseQueryResult result = ReportConverter.toEnterpriseQueryResult(enterpriseQueryBo);

        return result;
    }


    /**
     * 车辆诚信信息上报
     * @param vehicleReportVo       车辆上报信息vo
     * @return                      车辆上报结果bo
     * @throws TemplateException
     * @throws IOException
     * @throws UMessageTransportException
     */
    @RequestMapping(value = "/external/vehicle/report", method = RequestMethod.POST)
    @ResponseBody
    public VehicleReportResult vehicleReport(@RequestBody VehicleReportVo vehicleReportVo) throws TemplateException, IOException, UMessageTransportException {
        VehicleReportDocumentVo vehicleReportDocumentVo = ReportConverter.toVehicleReportDocumentVo(vehicleReportVo);

        VehicleReportBo vehicleReportBo = loginkReportService.vehicleReport(vehicleReportDocumentVo,
                vehicleReportVo.getSenderCode(),
                vehicleReportVo.getSenderPassword());

        VehicleReportResult result = ReportConverter.toVehicleReportResult(vehicleReportBo);

        return result;
    }


    /**
     * 资金流水单上报
     * @param brokerReportCapitalFlowResult 资金流水单信息
     * @throws TemplateException
     * @throws IOException
     * @throws UMessageTransportException
     * @return  资金流水单上报结果
     */
    @RequestMapping(value = "/external/capital_flow/report", method = RequestMethod.POST)
    @ResponseBody
    public BrokerReportCapitalFlowResult capitalFlowReport(@RequestBody BrokerReportCapitalFlowResult brokerReportCapitalFlowResult) throws TemplateException, IOException, UMessageTransportException {

        CapitalFlowDocumentVo vo = CapitalFlowDocumentConverter.toCapitalFlowDocumentVo(brokerReportCapitalFlowResult);

        if (CollectionUtils.isEmpty(vo.getShippingNoteList())
                || CollectionUtils.isEmpty(vo.getFinancialList())) {
            brokerReportCapitalFlowResult.setSuccess(false);
            brokerReportCapitalFlowResult.setErrorMessage("必填项未填");
            return brokerReportCapitalFlowResult;
        }

        Boolean b = loginkReportService.capitalFlowReport(vo,
                brokerReportCapitalFlowResult.getSenderCode(),
                brokerReportCapitalFlowResult.getSenderPassword());

        brokerReportCapitalFlowResult.setSuccess(b);

        return brokerReportCapitalFlowResult;
    }

}
