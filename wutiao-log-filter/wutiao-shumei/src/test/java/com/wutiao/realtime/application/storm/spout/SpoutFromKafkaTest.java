package com.wutiao.realtime.application.storm.spout;

import com.wutiao.realtime.application.storm.model.UserIdentifier;
import com.wutiao.realtime.application.storm.util.PropertyLoader;
import org.apache.storm.shade.org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhouml on 13/09/2018.
 */
public class SpoutFromKafkaTest {

    private static String ABNORMAL_VALUE = "-1";

    public static String getFromEnum(String value, UserIdentifier identifier) {
        String result = null;

        switch (identifier) {
            case PHONE:
                if (StringUtils.isNotEmpty(value) && !ABNORMAL_VALUE.equals(value)) {
                    result = value;
                }
                break;
            case OUID:
                if (StringUtils.isNotEmpty(value) && !ABNORMAL_VALUE.equals(value)) {
                    result = value;
                }
                break;
            case DID:
                if (StringUtils.isNotEmpty(value) && !ABNORMAL_VALUE.equals(value)) {
                    result = value;
                }
                break;
            default:
                System.out.println("Not valid identifier !!!");
                break;
        }

        return result;
    }

    public static void main(String[] args) throws IOException {
        /*
        String registerLog = "{\"ouid\":10101010101010,\"timestamp\":1434556935000,\"did\":\"BCA00-CCFFF-E000001\",\"event\":\"register\",\"project\":\"qkl\",\"properties\":{\"_ip\":\"180.79.35.65\",\"_appver\":\"2.0.7\",\"_os\":\"ios\",\"_osver\":\"iOS 7.0\",\"_model\":\"iPhone 5\",\"_mfr\":\"apple\",\"_res\":\"1920*1080\",\"_nettype\":\"4g\",\"_carrier\":\"cmcc\",\"_channel\":\"360\",\"terminal\":\"ios\",\"ad\":1,\"gameid\":1001,\"isvisitor\":0,\"step\":0,\"phone\":\"1890000000\",\"inviterid\":\"10101010101011\"}}";

        String shumeiLoginLog = "{\"ouid\":\"13883657215\",\"project\":\"qkl\",\"event\":\"shumei_login\",\"did\":\"7B4525537C0C3CD8D2396055C7DD7C84\",\"properties\":{\"gameid\":\"5\",\"_os\":\"Android\",\"riskLevel\":\"PASS\",\"_ip\":\"14.109.209.13\",\"_gps\":\"106.49231314659119@29.752634167671204\",\"_res\":\"1280*720\",\"deviceId\":\"20180901135625f5a7b800ee2436111ce3aec72d39eac401c340a0f44e41e6\",\"valid\":\"1\",\"_appver\":\"1.1.0\",\"_sdk\":\"php\",\"gps_city\":\"\",\"_systime\":\"1536891802742\",\"idfv\":\"\",\"_channel\":\"3\",\"timestamp\":\"1536891802632\",\"_carrier\":\"中国移动\",\"ad\":\"228\",\"device_id\":\"868496038673969\",\"tokenId\":\"13883657215\",\"idfa\":\"\",\"ip\":\"14.109.209.13\",\"_sdkver\":\"2.0\",\"captchaValid\":\"1\",\"gps_province\":\"\",\"terminal\":\"\",\"_osver\":\"7.0\",\"phone\":\"13883657215\",\"_nettype\":\"WIFI\",\"_sst\":\"1536891803570\",\"_model\":\"TRT-AL00A\",\"gps_area\":\"\",\"userExist\":\"1\",\"did\":\"7B4525537C0C3CD8D2396055C7DD7C84\"},\"timestamp\":1536891802742}";

        String shumeiRegisterLog = "{\"ouid\":\"18581396393\",\"project\":\"qkl\",\"event\":\"shumei_register\",\"did\":\"9BC9A97B-694C-4295-9516-C70944CFA0E7\",\"properties\":{\"gameid\":\"\",\"_os\":\"iOS\",\"riskLevel\":\"REJECT\",\"_ip\":\"183.163.173.91\",\"_gps\":\"\",\"_res\":\"1136*640\",\"deviceId\":\"201809141023038c1730e19cebbcd84761cd81a0fe837501d693a15ae16f7b\",\"_appver\":\"1.1.0\",\"_sdk\":\"php\",\"gps_city\":\"\",\"_systime\":\"1536891799507\",\"idfv\":\"21D1D09C-4EA5-4AE9-A223-18EEE6D1037C\",\"signupPlatform\":\"phone\",\"_channel\":\"app store\",\"timestamp\":\"1536891799398\",\"_carrier\":\"\",\"ad\":\"\",\"device_id\":\"AED99583-01AE-4B55-B100-F245A5E2111D\",\"tokenId\":\"18581396393\",\"nickName\":\"18581396393\",\"idfa\":\"AED99583-01AE-4B55-B100-F245A5E2111D\",\"ip\":\"183.163.173.91\",\"_sdkver\":\"2.0\",\"gps_province\":\"\",\"terminal\":\"\",\"_osver\":\"9.3.5\",\"phone\":\"18581396393\",\"_nettype\":\"wifi\",\"_sst\":\"1536891799872\",\"_model\":\"iPhone5,3\",\"gps_area\":\"\",\"did\":\"9BC9A97B-694C-4295-9516-C70944CFA0E7\"},\"timestamp\":1536891799507}";

        String shumeiActivation = "{\"ouid\":2447393,\"project\":\"qkl\",\"event\":\"shumei_activation\",\"did\":\"674B1621-06EC-4038-859C-75D2B3F9F1EA\",\"properties\":{\"gameid\":\"\",\"_os\":\"iOS\",\"appVersion\":\"1.1.0\",\"clickIp\":\"\",\"_ip\":\"106.47.250.186\",\"_gps\":\"\",\"apputm\":\"DEFAULT\",\"advertisingId\":\"E0F9ADF2-14C0-46B0-AFFA-753E32823832\",\"clickId\":\"\",\"_res\":\"1334*750\",\"deviceId\":\"2018082211022553f4f99550a997abc8cc4f68138c305f014e0a662eb4efb0\",\"_appver\":\"1.1.0\",\"_sdk\":\"php\",\"gps_city\":\"\",\"_systime\":\"1536891798523\",\"idfv\":\"91A429C5-60C3-409E-9853-C21E1C29AB50\",\"_channel\":\"app store\",\"timestamp\":\"1536891798.283491\",\"_carrier\":\"\",\"ad\":\"\",\"device_id\":\"E0F9ADF2-14C0-46B0-AFFA-753E32823832\",\"tokenId\":\"0\",\"installTimestamp\":\"\",\"idfa\":\"E0F9ADF2-14C0-46B0-AFFA-753E32823832\",\"ip\":\"100.126.100.204\",\"_sdkver\":\"2.0\",\"clickTimestamp\":\"\",\"gps_province\":\"\",\"terminal\":\"\",\"_osver\":\"11.4.1\",\"_nettype\":\"4G\",\"campaign\":\"DEFAULT\",\"_sst\":\"1536891798761\",\"_model\":\"iPhone8,1\",\"gps_area\":\"\",\"isRetargeting\":\"0\",\"did\":\"674B1621-06EC-4038-859C-75D2B3F9F1EA\"},\"timestamp\":1536891798523}";

        List<String> whiteListEvents = Arrays.asList("shumei_login", "register", "shumei_register");

        System.out.println(SpoutFromKafka.parse(registerLog, whiteListEvents));

        System.out.println(SpoutFromKafka.parse(shumeiLoginLog, whiteListEvents));

        System.out.println(SpoutFromKafka.parse(shumeiRegisterLog, whiteListEvents));

        System.out.println(SpoutFromKafka.parse(shumeiActivation, whiteListEvents));

        UserIdentifier id = UserIdentifier.DID;

        switch (id) {
            case PHONE:
                System.out.println("I am phone");
                break;
            case OUID:
                System.out.println("I am ouid");
                break;
            case DID:
                System.out.println("I am did");
                break;
            default:
                break;
        }

        System.out.println(PropertyLoader.loadFromFile("/Users/allen/Desktop/shumei/guangzhou/wutiao-shumei-cache.properties"));
        */
        System.out.println(UserIdentifier.DID);
        System.out.println(UserIdentifier.DID.name());
        System.out.println(getFromEnum("HGTD-YUTY", UserIdentifier.DID));

        // System.out.println(StringUtils.isNotEmpty(""));
        // System.out.println(StringUtils.isNotBlank(" "));

        System.out.println(StringUtils.isBlank(" "));
        System.out.println(StringUtils.isBlank(""));
        System.out.println(StringUtils.isBlank("1234567890"));
    }

}
