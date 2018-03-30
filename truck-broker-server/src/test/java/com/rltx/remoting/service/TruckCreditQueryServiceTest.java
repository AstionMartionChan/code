package com.rltx.remoting.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rltx.truck.broker.bo.TruckCreditQueryBo;
import com.rltx.truck.broker.bo.VehicleCheckBo;
import com.rltx.truck.broker.port.credit.*;
import com.rltx.truck.broker.result.DummyResult;
import com.rltx.truck.broker.service.ILoginkQueryService;
import com.rltx.truck.broker.service.ITruckCarrierReportService;
import com.rltx.truck.broker.service.impl.LoginkQueryServiceImpl;
import com.rltx.truck.broker.vo.TruckCreditQueryDocumentVo;
import com.wondersgroup.cuteinfo.client.auth.IAuthenServiceServiceStub;
import com.wondersgroup.cuteinfo.client.exchangeserver.usersecurty.UserToken;
import com.wondersgroup.cuteinfo.client.util.UserTokenUtils;
import freemarker.template.TemplateException;
import it.sauronsoftware.base64.Base64;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sun.misc.BASE64Decoder;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhuyi on 17/4/17.
 */
@ContextConfiguration(locations = {
        "classpath*:/applicationContext.xml",
        "classpath*:/applicationContext-datasource.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(value = "localhost")
public class TruckCreditQueryServiceTest {

    @Resource(name = "loginkQueryService")
    private ILoginkQueryService loginkQueryService;

    @Test
    public void test() {
        try {
            System.out.println(JSON.toJSONString(loginkQueryService.vehicleQuery("陕KD3006","01", "13914", "ronglian2017")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test2() {
        try {
            System.out.println(JSON.toJSONString(loginkQueryService.vehicleCheck("陕KD3006", "13914", "ronglian2017")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return;
    }

}
