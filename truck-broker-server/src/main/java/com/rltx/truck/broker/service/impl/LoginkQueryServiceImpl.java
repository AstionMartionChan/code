package com.rltx.truck.broker.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rltx.truck.broker.bo.EnterpriseQueryBo;
import com.rltx.truck.broker.bo.PersonQueryBo;
import com.rltx.truck.broker.bo.TokenBo;
import com.rltx.truck.broker.bo.VehicleCheckBo;
import com.rltx.truck.broker.bo.VehicleQueryBo;
import com.rltx.truck.broker.constant.Constants;
import com.rltx.truck.broker.service.ILoginkQueryService;
import com.rltx.truck.broker.utils.HttpClientUtils;
import com.wondersgroup.cuteinfo.client.exchangeserver.usersecurty.UserToken;
import com.wondersgroup.cuteinfo.client.util.UserTokenUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.DateUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Leo_Chan on 2017/9/12.
 */
@Service("loginkQueryService")
public class LoginkQueryServiceImpl implements ILoginkQueryService {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(LoginkQueryServiceImpl.class);

    // https 证书
    @Value("#{sysConfig['https.security']}")
    private String httpsSecurity;

    // 认证token缓存变量
    private volatile Map<String, TokenBo> authTokenBoMap = new HashMap<>();

    // 认证token锁
    private Object authTokenLock = new Object();

    // 统一认证服务器地址
    @Value("#{sysConfig['logink.auth.url']}")
    private String authUrl;

    // 统一认证服务器资源ID
    @Value("#{sysConfig['logink.auth.resource.id']}")
    private String authResourceId;

    // 车辆信用查询服务url
    @Value("#{sysConfig['credit.query.url']}")
    private String queryUrl;

    // token失效时长
    @Value("#{sysConfig['logink.token.invalid.time']}")
    private Long tokenInvalidTime;


    /**
     * 校验车辆是否在国家平台存在
     * @param vehicleNumber         车牌号
     * @param senderCode            物流交换代码
     * @param senderPassword        物流交换密码
     * @return
     */
    @Override
    public VehicleCheckBo vehicleCheck(String vehicleNumber, String senderCode, String senderPassword) throws UnsupportedEncodingException {

        LOGGER.info("-----------------------------------进入校验车辆method");

        VehicleCheckBo vehicleCheckBo = new VehicleCheckBo();

        // 调用统一认证的令牌认证服务 获取token id
        UserToken token = getCreditQueryToken(senderCode, senderPassword);

        Map<String, Object> userInfo = new LinkedHashMap<>();
        userInfo.put("userid", token.getUsername());
        userInfo.put("token", token.getTokenID().replace("ticket:", ""));

        Map<String, Object> paramContent = new LinkedHashMap<>();
        paramContent.put("VehicleNumber", vehicleNumber);
        paramContent.put("SearchTypeCode", "21");

        LOGGER.info("********************" + JSONObject.toJSONString(userInfo));
        LOGGER.info("********************" + JSONObject.toJSONString(paramContent));

        String url = queryUrl.replace("{actionType}", Constants.ActionType.CREDIT_VEHICLE_CHECK_TYPE)
                .replace("{userInfo}", URLEncoder.encode(JSONObject.toJSONString(userInfo), "utf-8"))
                .replace("{paramContent}", URLEncoder.encode(JSONObject.toJSONString(paramContent), "utf-8"));
        LOGGER.info("-------------------------------" + url);

        String result = HttpClientUtils.sendHttpGetRequest(url);
        vehicleCheckBo.setResponseMessage(result);

        JSONObject jsonResult = JSON.parseObject(result);
        LOGGER.info("---------------------------------" + jsonResult);

        if (Boolean.valueOf(jsonResult.get("status").toString())){
            vehicleCheckBo.setIsSuccess(Boolean.TRUE);

            String content = jsonResult.get("biz_result").toString();
            String resultCode = JSON.parseObject(JSON.parseObject(content).get("vehicleInformationResult").toString()).get("resultCode").toString();
            vehicleCheckBo.setIsExist(resultCode.equals("1") ? Boolean.TRUE : Boolean.FALSE);
        } else {
            vehicleCheckBo.setIsSuccess(Boolean.FALSE);
            vehicleCheckBo.setErrorCode(jsonResult.get("code").toString());
            vehicleCheckBo.setErrorMessage(jsonResult.get("message").toString());
        }

        return vehicleCheckBo;
    }

    /**
     * 查询国家平台上的车辆
     * @param vehicleNumber         车牌号
     * @param licensePlateTypeCode  牌照类型
     * @param senderCode            物流交换代码
     * @param senderPassword        物流交换密码
     * @return
     */
    @Override
    public VehicleQueryBo vehicleQuery(String vehicleNumber, String licensePlateTypeCode, String senderCode, String senderPassword) throws UnsupportedEncodingException {
        LOGGER.info("-----------------------------------进入查询车辆method");

        VehicleQueryBo vehicleQueryBo = new VehicleQueryBo();

        // 调用统一认证的令牌认证服务 获取token id
        UserToken token = getCreditQueryToken(senderCode, senderPassword);

        Map<String, Object> userInfo = new LinkedHashMap<>();
        userInfo.put("userid", token.getUsername());
        userInfo.put("token", token.getTokenID().replace("ticket:", ""));

        Map<String, Object> paramContent = new LinkedHashMap<>();
        paramContent.put("VehicleNumber", vehicleNumber);
        paramContent.put("RoadTransportCertificateNumber", "");
        paramContent.put("LicensePlateTypeCode", toLicensePlateTypeString(licensePlateTypeCode));
        paramContent.put("SearchTypeCode", "02");

        String url = queryUrl.replace("{actionType}", Constants.ActionType.CREDIT_VEHICLE_QUERY_TYPE)
                .replace("{userInfo}", URLEncoder.encode(JSONObject.toJSONString(userInfo), "utf-8"))
                .replace("{paramContent}", URLEncoder.encode(JSONObject.toJSONString(paramContent), "utf-8"));
        LOGGER.info("-------------------------------" + url);

        String result = HttpClientUtils.sendHttpGetRequest(url);
        vehicleQueryBo.setResponseMessage(result);

        JSONObject jsonResult = JSON.parseObject(result);

        if (Boolean.valueOf(jsonResult.get("status").toString())){
            vehicleQueryBo.setIsSuccess(Boolean.TRUE);
            String bizResult = jsonResult.get("biz_result").toString();

            if (StringUtils.isNotEmpty(bizResult)){
                vehicleQueryBo.setIsExist(Boolean.TRUE);

                JSONObject body = JSON.parseObject(JSON.parseObject(bizResult).get("Body").toString());
                vehicleQueryBo.setVehicleNumber(body.get("VehicleNumber").toString());
                vehicleQueryBo.setLicensePlateTypeCode(body.get("LicensePlateTypeCode").toString());
                vehicleQueryBo.setOwner(body.get("Owner").toString());

                JSONObject roadTransportCertificateInformation = JSON.parseObject(body.get("RoadTransportCertificateInformation").toString());
                vehicleQueryBo.setVehicleClassification(roadTransportCertificateInformation.get("VehicleClassification").toString());
                vehicleQueryBo.setVehicleLength(roadTransportCertificateInformation.get("VehicleLength").toString());
                vehicleQueryBo.setVehicleWidth(roadTransportCertificateInformation.get("VehicleWidth").toString());
                vehicleQueryBo.setVehicleHeight(roadTransportCertificateInformation.get("VehicleHeight").toString());
                vehicleQueryBo.setBusinessState(roadTransportCertificateInformation.get("BusinessState").toString());
                vehicleQueryBo.setBusinessStateCode(roadTransportCertificateInformation.get("BusinessStateCode").toString());

                // 校验车辆证件是否过期
                String periodEndDate = roadTransportCertificateInformation.get("PeriodEndDate").toString();
                if (!StringUtils.isBlank(periodEndDate)) {
                    Date date = DateUtils.parseDate(periodEndDate, new String[]{"yyyy-MM-dd"});
                    if (date.getTime() < new Date().getTime()) {
                        vehicleQueryBo.setIsSuccess(Boolean.FALSE);
                        vehicleQueryBo.setErrorCode(Constants.BrokerError.TRUCK_LICENCE_EXPIRED);
                        vehicleQueryBo.setErrorMessage(Constants.BrokerError.TRUCK_LICENCE_EXPIRED_MESSAGE);
                    }
                }
            } else {
                vehicleQueryBo.setIsExist(Boolean.FALSE);
            }

        } else {
            vehicleQueryBo.setIsSuccess(Boolean.FALSE);
            vehicleQueryBo.setErrorCode(jsonResult.get("code").toString());
            vehicleQueryBo.setErrorMessage(jsonResult.get("message").toString());
        }
        return vehicleQueryBo;
    }



    /**
     * 查询国家平台上的企业
     * @param enterpriseName         企业名称
     * @param countrySubdivisionCode 省份
     * @param senderCode             物流交换代码
     * @param senderPassword         物流交换密码
     * @return
     */
    @Override
    public EnterpriseQueryBo enterpriseQuery(String enterpriseName, String countrySubdivisionCode, String senderCode, String senderPassword) throws UnsupportedEncodingException {
        LOGGER.info("-----------------------------------进入查询企业method");
        EnterpriseQueryBo enterpriseQueryBo = new EnterpriseQueryBo();

        // 调用统一认证的令牌认证服务 获取token id
        UserToken token = getCreditQueryToken(senderCode, senderPassword);

        Map<String, Object> userInfo = new LinkedHashMap<>();
        userInfo.put("userid", token.getUsername());
        userInfo.put("token", token.getTokenID().replace("ticket:", ""));

        Map<String, Object> paramContent = new LinkedHashMap<>();
        paramContent.put("EnterpriseName", enterpriseName);
        paramContent.put("PermitNumber", "");
        paramContent.put("CountrySubdivisionCode", countrySubdivisionCode);
        paramContent.put("SearchTypeCode", "02");

        String url = queryUrl.replace("{actionType}", Constants.ActionType.CREDIT_ENTERPRISE_QUERY_TYPE)
                .replace("{userInfo}", URLEncoder.encode(JSONObject.toJSONString(userInfo), "utf-8"))
                .replace("{paramContent}", URLEncoder.encode(JSONObject.toJSONString(paramContent), "utf-8"));
        LOGGER.info("-------------------------------" + url);

        String result = HttpClientUtils.sendHttpGetRequest(url);
        enterpriseQueryBo.setResponseMessage(result);

        JSONObject jsonResult = JSON.parseObject(result);

        if (Boolean.valueOf(jsonResult.get("status").toString())){
            enterpriseQueryBo.setIsSuccess(Boolean.TRUE);
            String bizResult = jsonResult.get("biz_result").toString();

            if (StringUtils.isNotEmpty(bizResult)){
                enterpriseQueryBo.setIsExist(Boolean.TRUE);

                JSONObject body = JSON.parseObject(JSON.parseObject(bizResult).get("Body").toString());
                enterpriseQueryBo.setEnterpriseName(body.get("EnterpriseName").toString());
                enterpriseQueryBo.setCommunicationAddress(body.get("CommunicationNumber").toString());

                JSONObject roadTransportBusinessOperatingPermitInformation = JSON.parseObject(body.get("RoadTransportBusinessOperatingPermitInformation").toString());
                enterpriseQueryBo.setBusinessScope(roadTransportBusinessOperatingPermitInformation.get("BusinessScope").toString());
                enterpriseQueryBo.setPermitGrantDate(roadTransportBusinessOperatingPermitInformation.get("PermitGrantDate").toString());
                enterpriseQueryBo.setPeriodStartDate(roadTransportBusinessOperatingPermitInformation.get("PeriodStartDate").toString());
                enterpriseQueryBo.setPeriodEndDate(roadTransportBusinessOperatingPermitInformation.get("PeriodEndDate").toString());
                enterpriseQueryBo.setCertificationUnit(roadTransportBusinessOperatingPermitInformation.get("CertificationUnit").toString());

            } else {
                enterpriseQueryBo.setIsExist(Boolean.FALSE);
            }

        } else {
            enterpriseQueryBo.setIsSuccess(Boolean.FALSE);
            enterpriseQueryBo.setErrorCode(jsonResult.get("code").toString());
            enterpriseQueryBo.setErrorMessage(jsonResult.get("message").toString());
        }


        return enterpriseQueryBo;
    }



    /**
     * 查询国家平台上的人员运政信息
     * @param personName                     姓名
     * @param identityDocumentNumber         身份证号码
     * @param qualificationCertificateNumber 从业资格证号码
     * @param countrySubdivisionCode         省份代码
     * @param senderCode                     物流交换代码
     * @param senderPassword                 物流交换密码
     * @return
     */
    @Override
    public PersonQueryBo personQuery(String personName, String identityDocumentNumber, String qualificationCertificateNumber, String countrySubdivisionCode, String senderCode, String senderPassword) throws UnsupportedEncodingException {
        LOGGER.info("-----------------------------------进入查询企业method");
        PersonQueryBo personQueryBo = new PersonQueryBo();

        // 调用统一认证的令牌认证服务 获取token id
        UserToken token = getCreditQueryToken(senderCode, senderPassword);

        Map<String, Object> userInfo = new LinkedHashMap<>();
        userInfo.put("userid", token.getUsername());
        userInfo.put("token", token.getTokenID().replace("ticket:", ""));

        Map<String, Object> paramContent = new LinkedHashMap<>();
        paramContent.put("NameOfPerson", personName);
        paramContent.put("IdentityDocumentNumber", identityDocumentNumber);
        paramContent.put("QualificationCertificateNumber", qualificationCertificateNumber);
        paramContent.put("CountrySubdivisionCode", countrySubdivisionCode);
        paramContent.put("SearchTypeCode", "02");

        String url = queryUrl.replace("{actionType}", Constants.ActionType.CREDIT_PERSON_QUERY_TYPE)
                .replace("{userInfo}", URLEncoder.encode(JSONObject.toJSONString(userInfo), "utf-8"))
                .replace("{paramContent}", URLEncoder.encode(JSONObject.toJSONString(paramContent), "utf-8"));
        LOGGER.info("-------------------------------" + url);

        String result = HttpClientUtils.sendHttpGetRequest(url);
        personQueryBo.setResponseMessage(result);

        JSONObject jsonResult = JSON.parseObject(result);

        if (Boolean.valueOf(jsonResult.get("status").toString())){
            personQueryBo.setIsSuccess(Boolean.TRUE);
            String bizResult = jsonResult.get("biz_result").toString();

            if (StringUtils.isNotEmpty(bizResult)){
                personQueryBo.setIsExist(Boolean.TRUE);

                JSONObject body = JSON.parseObject(JSON.parseObject(bizResult).get("Body").toString());
                personQueryBo.setNameOfPerson(body.get("NameOfPerson").toString());
                personQueryBo.setGender(body.get("Gender").toString());
                personQueryBo.setIdentityDocumentNumber(body.get("IdentityDocumentNumber").toString());
                personQueryBo.setMobileTelephoneNumber(body.get("MobileTelephoneNumber").toString());


                String qualificationCertificateInformation = JSON.parseArray(body.get("QualificationCertificateInformation").toString()).get(0).toString();
                personQueryBo.setQualificationCertificateNumber(JSONObject.parseObject(qualificationCertificateInformation).get("QualificationCertificateNumber").toString());
                personQueryBo.setLicenseInitialCollectionDate(JSONObject.parseObject(qualificationCertificateInformation).get("LicenseInitialCollectionDate").toString());
                personQueryBo.setPeriodStartDate(JSONObject.parseObject(qualificationCertificateInformation).get("PeriodStartDate").toString());
                personQueryBo.setPeriodEndDate(JSONObject.parseObject(qualificationCertificateInformation).getString("PeriodEndDate"));
                personQueryBo.setCommunicationNumber(JSONObject.parseObject(qualificationCertificateInformation).getString("CommunicationNumber"));

            } else {
                personQueryBo.setIsExist(Boolean.FALSE);
            }

        } else {
            personQueryBo.setIsSuccess(Boolean.FALSE);
            personQueryBo.setErrorCode(jsonResult.get("code").toString());
            personQueryBo.setErrorMessage(jsonResult.get("message").toString());
        }

        return personQueryBo;
    }


    /**
     * 获取权限token方法，当缓存中存在token，并且在两小时以内，返回缓存的token，否则重新获取
     * @param username 物流交换代码
     * @param password 物流交换密码
     * @return UserToken logink的token
     */
    private UserToken getCreditQueryToken(String username, String password) {
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
     * Base64解密
     * @param content
     * @return
     */
    private String getFromBase64(String content) {
        byte[] b = null;
        String result = null;
        if (content != null) {
            BASE64Decoder decoder = new BASE64Decoder();
            try {
                b = decoder.decodeBuffer(content);
                result = new String(b, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    private String toLicensePlateTypeString(String licensePlateTypeCode) {

        if (Constants.LicensePlateTypeCode.YELLOW_COLOR_TYPE.equals(licensePlateTypeCode)){
            return "黄色";
        }
        if (Constants.LicensePlateTypeCode.BLUE_COLOR_TYPE.equals(licensePlateTypeCode)){
            return "蓝色";
        }
        if (Constants.LicensePlateTypeCode.OTHER_TYPE.equals(licensePlateTypeCode)){
            return "其他";
        }

        return null;
    }


}
