package com.rltx.truck.broker.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rltx.truck.broker.bo.TokenBo;
import com.rltx.truck.broker.bo.VehicleReportBo;
import com.rltx.truck.broker.bo.WaybillReportBo;
import com.rltx.truck.broker.constant.Constants;
import com.rltx.truck.broker.service.ILoginkReportService;
import com.rltx.truck.broker.utils.TemplateUtils;
import com.rltx.truck.broker.vo.CapitalFlowDocumentVo;
import com.rltx.truck.broker.vo.VehicleReportDocumentVo;
import com.rltx.truck.broker.vo.WaybillReportDocumentVo;
import com.wondersgroup.cuteinfo.client.exchangeserver.exchangetransport.dao.IMessageTransporterDAO;
import com.wondersgroup.cuteinfo.client.exchangeserver.exchangetransport.exception.UMessageTransportException;
import com.wondersgroup.cuteinfo.client.exchangeserver.exchangetransport.factory.ITransportClientFactory;
import com.wondersgroup.cuteinfo.client.exchangeserver.exchangetransport.impl.USendRequset;
import com.wondersgroup.cuteinfo.client.exchangeserver.exchangetransport.impl.USendResponse;
import com.wondersgroup.cuteinfo.client.exchangeserver.usersecurty.UserToken;
import com.wondersgroup.cuteinfo.client.util.UserTokenUtils;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Leo_Chan on 2017/9/4.
 */
@Service("loginkReportService")
public class LoginkReportServiceImpl implements ILoginkReportService {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(LoginkReportServiceImpl.class);


    // 运单上报服务器地址
    @Value("#{sysConfig['logink.report.url']}")
    private String reportUrl;

    // https 证书
    @Value("#{sysConfig['https.security']}")
    private String httpsSecurity;

    // 统一认证服务器地址
    @Value("#{sysConfig['logink.auth.url']}")
    private String authUrl;

    // 运单上报服务器资源ID
    @Value("#{sysConfig['logink.report.resource.id']}")
    private String reportResourceId;

    // 运单上报 接收方物流交换代码
    @Value("#{sysConfig['logink.toaddress']}")
    private String toaddress;

    // 人车户诚信上报 接收方物流交换代码
    @Value("#{sysConfig['credit.toaddress']}")
    private String creditToaddress;

    // token失效时长
    @Value("#{sysConfig['logink.token.invalid.time']}")
    private Long tokenInvalidTime;

    // 上报token缓存变量
    private volatile Map<String, TokenBo> reportTokenBoMap = new HashMap<>();

    // 上报token锁
    private Object reportTokenLock = new Object();

    /**
     * 无车承运人运单上报
     * @param waybillReportDocumentVo   无车承运人运单上报字段元素vo
     * @param senderCode                物流交换代码
     * @param senderPassword            物流交换密码
     * @return                          无车承运人运单上报结果bo
     */
    @Override
    public WaybillReportBo waybillReport(WaybillReportDocumentVo waybillReportDocumentVo,
                                         String senderCode,
                                         String senderPassword) throws IOException, TemplateException, UMessageTransportException {
        WaybillReportBo waybillReportBo = new WaybillReportBo();
        String xml;

        // 设置HTTPS 证书文件
        System.setProperty("javax.net.ssl.keyStorePassword", "changeit");
        System.setProperty("javax.net.ssl.trustStore", httpsSecurity);

        // 调用统一认证的令牌认证服务 获取token id
        UserToken token = getReportToken(senderCode, senderPassword);

        //设置目标地址，发送报文的内容，包括报文类型Actiontype和具体报文xml
        USendRequset sendReq= new USendRequset();
        sendReq.setToaddress(toaddress.split(","));
        waybillReportDocumentVo.setRecipientCode(toaddress);

        //设置待发送的业务报文
        xml = getXml(waybillReportDocumentVo, Constants.FreeMarkerTemplateName.REPORT_WAYBILL_TEMPLATE);
        sendReq.setSendRequestTypeXML(Constants.ActionType.REPORT_WAYBILL_TYPE, xml);

        //调用平台提供的发送服务发送报文
        IMessageTransporterDAO transporter= ITransportClientFactory.createMessageTransporter(token, reportUrl);

        USendResponse response= transporter.send(sendReq);

        waybillReportBo.setReportMessage(xml);
        if (response.isSendResult()) {
            waybillReportBo.setIsSuccess(Boolean.TRUE);
        } else {
            //错误的情况下，会返回异常代码以及异常信息。异常代码请参照《3.2 共建指导性文件：交换接入》中的异常代码信息
            waybillReportBo.setIsSuccess(Boolean.FALSE);
        }
        waybillReportBo.setResponseMessage(JSONObject.toJSONString(response));

        return waybillReportBo;
    }

    @Override
    public Boolean capitalFlowReport(CapitalFlowDocumentVo capitalFlowDocumentVo, String senderCode, String senderPassword) throws TemplateException, IOException, UMessageTransportException {

        String xml;

        // 设置HTTPS 证书文件
        System.setProperty("javax.net.ssl.keyStorePassword", "changeit");
        System.setProperty("javax.net.ssl.trustStore", httpsSecurity);

        // 调用统一认证的令牌认证服务 获取token id
        UserToken token = getReportToken(senderCode, senderPassword);

        //设置目标地址，发送报文的内容，包括报文类型Actiontype和具体报文xml
        USendRequset sendReq= new USendRequset();
        sendReq.setToaddress(toaddress.split(","));
        capitalFlowDocumentVo.setRecipientCode(toaddress);

        //设置待发送的业务报文
        xml = getXml(capitalFlowDocumentVo, Constants.FreeMarkerTemplateName.REPORT_CAPITAL_FLOW_TEMPLATE);
        logger.info("send xml:" + xml);

        sendReq.setSendRequestTypeXML(Constants.ActionType.REPORT_CAPITAL_FLOW_TYPE, xml);

        //调用平台提供的发送服务发送报文
        IMessageTransporterDAO transporter = ITransportClientFactory.createMessageTransporter(token, reportUrl);

        USendResponse response = transporter.send(sendReq);
        logger.info("response:" + JSON.toJSONString(response));

        if (response.isSendResult()) {
            return true;
        } else {
            logger.error(response.getGenericFault().getMessage());
            return false;
        }

    }

    /**
     * 车辆诚信信息上报
     * @param vehicleReportDocumentVo   车辆上报字段元素vo
     * @param senderCode                物流交换代码
     * @param senderPassword            物流交换密码
     * @return                          车辆上报结果bo
     * @throws IOException
     * @throws TemplateException
     * @throws UMessageTransportException
     */
    public VehicleReportBo vehicleReport(VehicleReportDocumentVo vehicleReportDocumentVo,
                              String senderCode,
                              String senderPassword) throws TemplateException, IOException, UMessageTransportException {
        VehicleReportBo vehicleReportBo = new VehicleReportBo();
        vehicleReportDocumentVo.setRecipientCode(creditToaddress);

        USendResponse response = report(vehicleReportDocumentVo, senderCode, senderPassword, Constants.ActionType.REPORT_CREDIT_VEHICLE_TYPE);

        if (response.isSendResult()) {
            vehicleReportBo.setIsSuccess(Boolean.TRUE);
        } else {
            //错误的情况下，会返回异常代码以及异常信息。异常代码请参照《3.2 共建指导性文件：交换接入》中的异常代码信息
            vehicleReportBo.setIsSuccess(Boolean.FALSE);
        }
        vehicleReportBo.setResponseMessage(JSONObject.toJSONString(response));

        return vehicleReportBo;
    }


    /**
     * 报文上报
     * @param objDocumentVo     报文元素vo
     * @param senderCode        物流交换代码
     * @param senderPassword    物流交换密码
     * @param actionType        业务类型
     * @return                  发送响应对象
     * @throws IOException
     * @throws TemplateException
     * @throws UMessageTransportException
     */
    private USendResponse report(Object objDocumentVo,
                                 String senderCode,
                                 String senderPassword,
                                 String actionType) throws IOException, TemplateException, UMessageTransportException {
        String xml;

        // 调用统一认证的令牌认证服务 获取token id
        UserToken token = getReportToken(senderCode, senderPassword);

        //设置目标地址，发送报文的内容，包括报文类型Actiontype和具体报文xml
        USendRequset sendReq= new USendRequset();

        //设置待发送的业务报文
        xml = getXml(objDocumentVo, getTemplateName(actionType));
        sendReq.setSendRequestTypeXML(actionType, xml);

        //调用平台提供的发送服务发送报文
        IMessageTransporterDAO transporter= ITransportClientFactory.createMessageTransporter(token, reportUrl);

        USendResponse response= transporter.send(sendReq);

        return response;
    }


    /**
     * 根据业务类型 获取 对应的模板文件
     * @param actionType    业务类型
     * @return              模板文件名称
     */
    private String getTemplateName(String actionType) {
        if (actionType.equals(Constants.ActionType.REPORT_CREDIT_ENTERPRISE_TYPE)){
            return Constants.FreeMarkerTemplateName.REPORT_ENTERPRISE_TEMPLATE;
        }
        if (actionType.equals(Constants.ActionType.REPORT_CREDIT_PERSON_TYPE)){
            return Constants.FreeMarkerTemplateName.REPORT_PERSON_TEMPLATE;
        }
        if (actionType.equals(Constants.ActionType.REPORT_CREDIT_VEHICLE_TYPE)){
            return Constants.FreeMarkerTemplateName.REPORT_VEHICLE_TEMPLATE;
        }
        if (actionType.equals(Constants.ActionType.REPORT_WAYBILL_TYPE)){
            return Constants.FreeMarkerTemplateName.REPORT_WAYBILL_TEMPLATE;
        }
        return null;
    }




    /**
     * 获取无车承运运单xml
     * @param obj                   数据实体
     * @param ftlName               模板名称
     * @return                      xml报文
     * @throws java.io.IOException
     * @throws freemarker.template.TemplateException
     */
    private static String getXml(Object obj, String ftlName) throws IOException, TemplateException {

        // 调用写入freemarker模板，生成xml内容
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("entity", obj);
        String xml = TemplateUtils.toString(map, ftlName);

        return xml;
    }



    /**
     * 获取上报token方法，当缓存中存在token，并且在两小时以内，返回缓存的token，否则重新获取
     * @param username 物流交换代码
     * @param password 物流交换密码
     * @return UserToken logink的token
     */
    private UserToken getReportToken(String username, String password) {
        // 获取缓存token
        TokenBo tokenBo = reportTokenBoMap.get(username);
        Date operateTime = new Date();
        if (tokenBo == null || (tokenBo.getTokenTime().getTime() + tokenInvalidTime <= operateTime.getTime())) {
            synchronized (reportTokenLock) {
                if (tokenBo == null || (tokenBo.getTokenTime().getTime() + tokenInvalidTime <= operateTime.getTime())) {
                    UserToken token = UserTokenUtils.getTicket(username, password, reportResourceId, authUrl);
                    TokenBo bo = new TokenBo();
                    bo.setToken(token);
                    bo.setTokenTime(operateTime);
                    reportTokenBoMap.put(username, bo);
                }
            }
        }
        return reportTokenBoMap.get(username).getToken();
    }
}
