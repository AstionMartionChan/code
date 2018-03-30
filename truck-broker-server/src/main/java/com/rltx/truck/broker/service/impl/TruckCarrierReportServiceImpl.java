package com.rltx.truck.broker.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.rltx.truck.broker.bo.TokenBo;
import com.rltx.truck.broker.bo.TruckCarrierReportBo;
import com.rltx.truck.broker.bo.TruckCreditQueryBo;
import com.rltx.truck.broker.constant.Constants;
import com.rltx.truck.broker.dao.IOrgInfDao;
import com.rltx.truck.broker.po.OrgInfEntity;
import com.rltx.truck.broker.port.credit.*;
import com.rltx.truck.broker.service.ITruckBrokerReportLogService;
import com.rltx.truck.broker.service.ITruckCarrierReportService;
import com.rltx.truck.broker.utils.TemplateUtils;
import com.rltx.truck.broker.utils.UploadUtils;
import com.rltx.truck.broker.utils.XmlUtils;
import com.rltx.truck.broker.vo.CommParamsVo;
import com.rltx.truck.broker.vo.TruckCarrierDocumentVo;
import com.rltx.truck.broker.vo.TruckCreditQueryDocumentVo;
import com.wondersgroup.cuteinfo.client.exchangeserver.exchangetransport.dao.IMessageTransporterDAO;
import com.wondersgroup.cuteinfo.client.exchangeserver.exchangetransport.exception.UMessageTransportException;
import com.wondersgroup.cuteinfo.client.exchangeserver.exchangetransport.factory.ITransportClientFactory;
import com.wondersgroup.cuteinfo.client.exchangeserver.exchangetransport.impl.USendRequset;
import com.wondersgroup.cuteinfo.client.exchangeserver.exchangetransport.impl.USendResponse;
import com.wondersgroup.cuteinfo.client.exchangeserver.usersecurty.UserToken;
import com.wondersgroup.cuteinfo.client.util.UserTokenUtils;
import freemarker.template.TemplateException;
import it.sauronsoftware.base64.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Leo_Chan on 2017/4/14.
 */
@Service("truckCarrierReportService")
public class TruckCarrierReportServiceImpl implements ITruckCarrierReportService {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(TruckCarrierReportServiceImpl.class);

    // 运单上报服务器地址
    @Value("#{sysConfig['logink.report.url']}")
    private String reportUrl;

    // 运单上报服务器资源ID
    @Value("#{sysConfig['logink.report.resource.id']}")
    private String reportResourceId;

    // 统一认证服务器资源ID
    @Value("#{sysConfig['logink.auth.resource.id']}")
    private String authResourceId;

    // 统一认证服务器地址
    @Value("#{sysConfig['logink.auth.url']}")
    private String authUrl;

    // 接收方物流交换代码
    @Value("#{sysConfig['logink.toaddress']}")
    private String toaddress;

    // https 证书
    @Value("#{sysConfig['https.security']}")
    private String httpsSecurity;

    // token失效时长
    @Value("#{sysConfig['logink.token.invalid.time']}")
    private Long tokenInvalidTime;

    @Resource(name = "truckBrokerReportLogService")
    private ITruckBrokerReportLogService truckBrokerReportLogService;

    @Resource(name = "orgInfDao")
    private IOrgInfDao orgInfDao;

    // 认证token缓存变量
    private volatile Map<String, TokenBo> authTokenBoMap = new HashMap<>();

    // 认证token锁
    private Object authTokenLock = new Object();

    // 上报token缓存变量
    private volatile Map<String, TokenBo> reportTokenBoMap = new HashMap<>();

    // 上报token锁
    private Object reportTokenLock = new Object();

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
    @Override
    public TruckCarrierReportBo truckCarrierReport(TruckCarrierDocumentVo truckCarrierDocumentVo, Long waybillId, CommParamsVo commParamsVo) throws IOException, TemplateException, UMessageTransportException {
        LOGGER.info("---------进入上报method");

        // 查询组织信息和物流交换信息（物流交换代码，物流交换密码）
        OrgInfEntity orgInfEntity = orgInfDao.getOrgInf(commParamsVo.getOwnerOrgId());

        TruckCarrierReportBo truckCarrierReportBo = new TruckCarrierReportBo();

        // 验证组织信息是否正确
        if (orgInfEntity != null && orgInfEntity.getSenderCode() != null & orgInfEntity.getSenderPassword() != null) {

            String username = orgInfEntity.getSenderCode();
            String password = orgInfEntity.getSenderPassword();

            // 设置发送方物流交换代码
            truckCarrierDocumentVo.setSenderCode(username);
            // 设置接收方物流交换代码
            truckCarrierDocumentVo.setRecipientCode(toaddress);

            String xml = null;

            // 设置HTTPS 证书文件
//            System.setProperty("javax.net.ssl.keyStorePassword", "changeit");
//            String truststoreFile = System.getenv("JAVA_HOME") + "/jre/lib/security/" + "cuteinfo_client.trustStore";
//            System.setProperty("javax.net.ssl.trustStore", httpsSecurity);

            IMessageTransporterDAO transporter = null;

            // 调用统一认证的令牌认证服务 获取token id
            long start = System.currentTimeMillis();
            UserToken token = getReportToken(username, password);
            LOGGER.info("---------------------------username:  " + username);
            LOGGER.info("---------------------------password:  " + password);
            LOGGER.info("---------------------------token:  " + token.getTokenID());
            long end = System.currentTimeMillis();
            LOGGER.info("get token:  "+ (end - start) + "second");

            //设置目标地址，发送报文的内容，包括报文类型Actiontype和具体报文xml
            USendRequset sendReq= new USendRequset();
            sendReq.setToaddress(toaddress.split(","));

            //设置待发送的业务报文
            xml = getXml(truckCarrierDocumentVo, "cuteinfo.ftl");
            sendReq.setSendRequestTypeXML("LOGINK_CN_FREIGHTBROKER_WAYBILL", xml);

            //调用平台提供的发送服务发送报文
            transporter= ITransportClientFactory.createMessageTransporter(token, reportUrl);

            start = System.currentTimeMillis();
            USendResponse response= transporter.send(sendReq);
            end = System.currentTimeMillis();
            LOGGER.info("send:  "+ (end - start) + " second");


            if (response.isSendResult()) {
                truckCarrierReportBo.setIsSuccess(true);
            } else {
                //错误的情况下，会返回异常代码以及异常信息。异常代码请参照《3.2 共建指导性文件：交换接入》中的异常代码信息
                truckCarrierReportBo.setIsSuccess(false);
            }

            // 更新上报日志表
            truckBrokerReportLogService.saveLog(waybillId, xml, JSONObject.toJSONString(response), commParamsVo);

        } else {
            truckCarrierReportBo.setIsSuccess(false);
            truckCarrierReportBo.setErrorMsg("物流交换代码错误");
        }

        LOGGER.info("---------结束上报method");
        return truckCarrierReportBo;
    }


    /**
     * 车辆查询
     * @param truckCreditQueryDocumentVo 车辆查询vo
     * @return
     * @throws IOException
     * @throws TemplateException
     */
    @Override
    public TruckCreditQueryBo truckCreditQuery(TruckCreditQueryDocumentVo truckCreditQueryDocumentVo, Long currentOrgId) throws IOException, TemplateException {
        LOGGER.info("---------进入查询method");

        OrgInfEntity orgInfEntity = orgInfDao.getOrgInf(currentOrgId);

        TruckCreditQueryBo truckCreditQueryBo = new TruckCreditQueryBo();

        // 验证组织信息是否正确
        if (orgInfEntity != null && orgInfEntity.getSenderCode() != null & orgInfEntity.getSenderPassword() != null) {

            String username = orgInfEntity.getSenderCode();
            String password = orgInfEntity.getSenderPassword();
            // 创建信用接入客户端
            long start = System.currentTimeMillis();
            LoginkServiceService service = new LoginkServiceService();
            LoginkServiceImp loginkServicePort = service.getLoginkServicePort();
            long end = System.currentTimeMillis();
            LOGGER.info("create LoginkServiceService:  "+ (end - start) + "second");

            // 调用统一认证的令牌认证服务 获取token id
            start = System.currentTimeMillis();
            UserToken token = getAuthToken(username, password);
            end = System.currentTimeMillis();
            LOGGER.info("get  token:  "+ (end - start) + "second");

            /**
             * 安全码
             * logisticsExchangeCode 物流交换代码
             * userTokenID           令牌token
             */
            Security security = new Security();
            security.setLogisticsExchangeCode(token.getUsername());
            String tokenStr = token.getTokenID().split(":")[1];
            security.setUserTokenID(tokenStr);

            /**
             * 认证码
             * userName     物流交换代码
             * userPassword 密码
             * userId       用户id（其实也是物流交换代码）
             * serviceId    服务id
             *
             *              预发布环境：08427C9AAC4CC627E0530B1EA8C0C437
             *              正式环境：  120380A218FC003EE053C0A87F0C003E
             */
            Authentication authentication = new Authentication();
            authentication.setUserName(username);
            authentication.setUserId(username);
            authentication.setUserPassword(password);
            authentication.setServiceId(authResourceId);


            /**
             * 公共信息
             * serviceType 服务类型 默认为3
             * actionType  活动类型
             *             企业查询：QueryEnterpriseCredit
             *             车辆查询：QueryVehicleCredit
             *             人员查询：QueryPersonCredit
             */
            PublicInformation publicInformation = new PublicInformation();
            publicInformation.setServiceType("3");
            publicInformation.setActionType(Constants.CreditQueryActionType.QUERY_VEHICLE_CREDIT);

            String xml = getXml(truckCreditQueryDocumentVo, "vehicle.ftl");

            String base64Str = Base64.encode(xml, "UTF-8");
            start = System.currentTimeMillis();
            // 信用查询 并获取返回报文
            GenericResult genericResult =
                    loginkServicePort.interfaceName(security, authentication, publicInformation, base64Str);

            end = System.currentTimeMillis();
            LOGGER.info("query:  "+ (end - start) + "second");

            String resultXml = Base64.decode(genericResult.getBusinessInformation(), "UTF-8");

            truckCreditQueryBo.setResultXml(resultXml);

            // 如果resultCode为true 并且 业务信息不为空,则设置true
            if (genericResult.isResultCode()
                    && StringUtils.isNotEmpty(genericResult.getBusinessInformation())) {

                truckCreditQueryBo.setIsSuccess(true);

                // 校验车辆证件是否过期
                String periodEndDate = XmlUtils.getElementValue(resultXml, "PeriodEndDate");
                if (!StringUtils.isBlank(periodEndDate)) {
                    try {
                        Date date = DateUtils.parseDate(periodEndDate, new String[]{"yyyy-MM-dd"});
                        if (date.getTime() < new Date().getTime()) {
                            truckCreditQueryBo.setIsSuccess(false);
                            truckCreditQueryBo.setErrorCode(Constants.BrokerError.TRUCK_LICENCE_EXPIRED);
                        }
                    } catch (ParseException e) { }
                }
            } else {
                truckCreditQueryBo.setIsSuccess(false);
                truckCreditQueryBo.setErrorCode(Constants.BrokerError.TRUCK_LICENCE_NOT_FOUND);
            }
        } else {
            truckCreditQueryBo.setIsSuccess(false);
            truckCreditQueryBo.setErrorCode(Constants.BrokerError.SENDER_CODE_ERROR);
        }

        LOGGER.info("---------结束查询method");
        return truckCreditQueryBo;
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
     * 获取权限token方法，当缓存中存在token，并且在两小时以内，返回缓存的token，否则重新获取
     * @param username 物流交换代码
     * @param password 物流交换密码
     * @return UserToken logink的token
     */
    private UserToken getAuthToken(String username, String password) {
        // 获取缓存token
        TokenBo tokenBo = authTokenBoMap.get(username);
        Date operateTime = new Date();
        if (tokenBo == null || (tokenBo.getTokenTime().getTime() + tokenInvalidTime <= operateTime.getTime())) {
            synchronized (authTokenLock) {
                if (tokenBo == null || (tokenBo.getTokenTime().getTime() + tokenInvalidTime <= operateTime.getTime())) {
                    UserToken token = UserTokenUtils.getTicket(username, password, authResourceId, authUrl);
                    TokenBo bo = new TokenBo();
                    bo.setToken(token);
                    bo.setTokenTime(operateTime);
                    authTokenBoMap.put(username, bo);
                }
            }
        }
        return authTokenBoMap.get(username).getToken();
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

    /**
     * 获取无车承运运单xml
     * @return
     */
    private static String getCuteinfoXml() {
        String uuid = UploadUtils.getUuid();
        System.out.println(uuid);
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<Root>\n" +
                "\t<Header>\n" +
                "\t\t<MessageReferenceNumber>" + uuid + "</MessageReferenceNumber>\n" +
                "\t\t<DocumentName>无车承运人电子路单</DocumentName>\n" +
                "\t\t<DocumentVersionNumber>2015WCCYR</DocumentVersionNumber>\n" +
                "\t\t<SenderCode>13914</SenderCode>\n" +
                "\t\t<RecipientCode>wcjc0001</RecipientCode>\n" +
                "\t\t<MessageSendingDateTime>" + UploadUtils.getFormatTime() + "</MessageSendingDateTime>\n" +
                "\t\t<MessageFunctionCode>9</MessageFunctionCode>\n" +
                "\t</Header>\n" +
                "\t<Body>\n" +
                "\t\t<OriginalDocumentNumber>TESTORDER000001</OriginalDocumentNumber>\n" +
                "\t\t<ShippingNoteNumber>TESTSHIPPING00001</ShippingNoteNumber>\n" +
                "\t\t<Carrier>Test公司</Carrier>\n" +
                "\t\t<!-- <UnifiedSocialCreditIdentifier></UnifiedSocialCreditIdentifier> -->\n" +
                "\t\t<!-- <PermitNumber></PermitNumber> -->\n" +
                "\t\t<ConsignmentDateTime>20170303095758</ConsignmentDateTime>\n" +
                "\t\t<BusinessTypeCode>1002996</BusinessTypeCode>\n" +
                "\t\t<DespatchActualDateTime>20170303095758</DespatchActualDateTime>\n" +
                "\t\t<GoodsReceiptDateTime>20170303095758</GoodsReceiptDateTime>\n" +
                "\t\t<ConsignorInfo>\n" +
                "\t\t\t<!-- <Consignor></Consignor> -->\n" +
                "\t\t\t<!-- <PersonalIdentityDocument></PersonalIdentityDocument> -->\n" +
                "\t\t\t<!-- <PlaceOfLoading></PlaceOfLoading> -->\n" +
                "\t\t\t<CountrySubdivisionCode>310101</CountrySubdivisionCode>\n" +
                "\t\t</ConsignorInfo>\n" +
                "\t\t<ConsigneeInfo>\n" +
                "\t\t\t<!-- <Consignee></Consignee> -->\n" +
                "\t\t\t<!-- <GoodsReceiptPlace></GoodsReceiptPlace> -->\n" +
                "\t\t\t<CountrySubdivisionCode>310115</CountrySubdivisionCode>\n" +
                "\t\t</ConsigneeInfo>\n" +
                "\t\t<PriceInfo>\n" +
                "\t\t\t<TotalMonetaryAmount>1.000</TotalMonetaryAmount>\n" +
                "\t\t\t<!-- <Remark></Remark> -->\n" +
                "\t\t</PriceInfo>\n" +
                "\t\t<VehicleInfo>\n" +
                "\t\t\t<LicensePlateTypeCode>01</LicensePlateTypeCode>\n" +
                "\t\t\t<VehicleNumber>沪A88888</VehicleNumber>\n" +
                "\t\t\t<VehicleClassificationCode>H01</VehicleClassificationCode>\n" +
                "\t\t\t<VehicleTonnage>1.00</VehicleTonnage>\n" +
                "\t\t\t<RoadTransportCertificateNumber>000000000001</RoadTransportCertificateNumber>\n" +
                "\t\t\t<!-- <TrailerVehiclePlateNumber></TrailerVehiclePlateNumber> -->\n" +
                "\t\t\t<!-- <Owner></Owner> -->\n" +
                "\t\t\t<!-- <PermitNumber></PermitNumber> -->\n" +
                "\t\t\t<Driver>\n" +
                "\t\t\t\t<NameOfPerson>测试驾驶员</NameOfPerson>\n" +
                "\t\t\t\t<!-- <QualificationCertificateNumber></QualificationCertificateNumber> -->\n" +
                "\t\t\t\t<!-- <TelephoneNumber></TelephoneNumber> -->\n" +
                "\t\t\t</Driver>\n" +
                "\t\t\t<GoodsInfo>\n" +
                "\t\t\t\t<DescriptionOfGoods>测试货物</DescriptionOfGoods>\n" +
                "\t\t\t\t<CargoTypeClassificationCode>90</CargoTypeClassificationCode>\n" +
                "\t\t\t\t<GoodsItemGrossWeight>1.000</GoodsItemGrossWeight>\n" +
                "\t\t\t\t<!-- <Cube></Cube> -->\n" +
                "\t\t\t\t<!-- <TotalNumberOfPackages></TotalNumberOfPackages> -->\n" +
                "\t\t\t</GoodsInfo>\n" +
                "\t\t</VehicleInfo>\n" +
                "\t\t<!-- <FreeText></FreeText> -->\n" +
                "\t</Body>\n" +
                "</Root>";

        return xml;
    }
}
