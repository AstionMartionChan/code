package com.rltx.remoting.service;

import com.rltx.truck.broker.bo.VehicleCheckBo;
import com.rltx.truck.broker.controller.converter.CapitalFlowDocumentConverter;
import com.rltx.truck.broker.result.BrokerReportCapitalFlowResult;
import com.rltx.truck.broker.service.ILoginkQueryService;
import com.rltx.truck.broker.service.ILoginkReportService;
import com.rltx.truck.broker.service.ITruckCarrierReportService;
import com.rltx.truck.broker.vo.CapitalFlowDocumentVo;
import com.rltx.truck.broker.vo.TruckCreditQueryDocumentVo;
import com.wondersgroup.cuteinfo.client.exchangeserver.exchangetransport.exception.UMessageTransportException;
import freemarker.template.TemplateException;
import javafx.scene.input.DataFormat;
import org.apache.http.client.utils.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
public class LoginkReportServiceTest {

    @Resource(name = "loginkReportService")
    private ILoginkReportService loginkReportService;

    @Test
    public void test() {
        BrokerReportCapitalFlowResult result = new BrokerReportCapitalFlowResult();
        result.setCode("315751272983128068");
        result.setShippingNoteNumber("WB283928938923");
        result.setDescription("备注文字。");
        result.setCarrier("上海卡小车物流有限公司");
        result.setVehicleNumber("沪B28282");
        result.setLicensePlateTypeCode("02");
        result.setPaymentMeansCode1("39");
        result.setBankCode("ICBK");
        result.setSequenceCode1("RE289893232322");
        result.setMonetaryAmount1(232323.000);
        result.setDateTime1(DateUtils.parseDate("2018-03-02 00:00:00", new String[]{"yyyy-MM-dd HH:mm:ss"}));
        result.setPaymentMeansCode2("42");
        result.setSequenceCode2("RE289893232323");
        result.setMonetaryAmount2(43435.000);
        result.setDateTime2(DateUtils.parseDate("2018-03-02 00:00:00", new String[]{"yyyy-MM-dd HH:mm:ss"}));
        result.setPaymentMeansCode3("91");
        result.setSequenceCode3("RE289893232324");
        result.setMonetaryAmount3(3434.000);
        result.setDateTime3(DateUtils.parseDate("2018-03-02 00:00:00", new String[]{"yyyy-MM-dd HH:mm:ss"}));
        result.setPaymentMeansCode4("92");
        result.setSequenceCode4("23243.000");
        result.setMonetaryAmount4(3434.000);
        result.setDateTime4(DateUtils.parseDate("2018-03-04 00:00:00", new String[]{"yyyy-MM-dd HH:mm:ss"}));
        result.setSenderCode("13914");

        CapitalFlowDocumentVo capitalFlowDocumentVo = CapitalFlowDocumentConverter.toCapitalFlowDocumentVo(result);

        try {
            loginkReportService.capitalFlowReport(capitalFlowDocumentVo, "13914", "ronglian2017");
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UMessageTransportException e) {
            e.printStackTrace();
        }
    }

}
