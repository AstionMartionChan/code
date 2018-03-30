package com.rltx.truck.broker.utils;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.dom4j.Element;
import java.io.*;
import java.util.List;


/**
 * SAX解析工具
 */
public class XmlUtils{

    public static String getElementValue(String xml, String el) {
        if (xml == null) {
            return null;
        }

        String value = null;
        try {
            SAXReader saxReader = new SAXReader();
            InputStream inputStream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            Document document = saxReader.read(inputStream);
            Element element = document.getRootElement();
            value = getElementValue(element, el);
        } catch (Exception e){
        }

        return value;
    }

    private static String getElementValue(Element element, String el) {

        if (element == null || el == null) {
            return null;
        }

        if (el.equals(element.getName())) {
            return element.getStringValue();
        }

        List<Element> list = element.elements();
        String value = null;
        for (Element e : list) {
            value = getElementValue(e, el);
            if (value != null) {
                return value;
            } else {
                continue;
            }
        }

        return value;
    }

    public static void main(String[] args) {
        String xml = "<Root>\n" +
                "  <Header>\n" +
                "    <MessageReferenceNumber></MessageReferenceNumber>\n" +
                "    <DocumentName></DocumentName>\n" +
                "    <DocumentVersionNumber></DocumentVersionNumber>\n" +
                "    <SenderCode></SenderCode>\n" +
                "    <MessageSendingDateTime></MessageSendingDateTime>\n" +
                "  </Header>\n" +
                "  <Body>\n" +
                "    <VehicleNumber>豫PJ6468</VehicleNumber>\n" +
                "    <LicensePlateTypeCode>黄色</LicensePlateTypeCode>\n" +
                "    <Owner></Owner>\n" +
                "    <DrivingLicenceInformation>\n" +
                "      <VehicleNumber>豫PJ6468</VehicleNumber>\n" +
                "      <VehicleEngineNumber>LVBS6PEB4AH011518</VehicleEngineNumber>\n" +
                "      <Photo></Photo>\n" +
                "    </DrivingLicenceInformation>\n" +
                "    <RoadTransportCertificateInformation>\n" +
                "      <RoadTransportCertificateNumber>411620024690</RoadTransportCertificateNumber>\n" +
                "      <VehicleNumber>豫PJ6468</VehicleNumber>\n" +
                "      <VehicleClassification>重型全挂牵引车</VehicleClassification>\n" +
                "      <VehicleLength>7030 mm</VehicleLength>\n" +
                "      <VehicleWidth>2495 mm</VehicleWidth>\n" +
                "      <VehicleHeight>3460 mm</VehicleHeight>\n" +
                "      <BusinessScope>道路普通货物运输</BusinessScope>\n" +
                "      <LicenseInitialCollectionDate>2011-04-28</LicenseInitialCollectionDate>\n" +
                "      <PeriodStartDate>2013-11-22</PeriodStartDate>\n" +
                "      <PeriodEndDate>2011-10-31</PeriodEndDate>\n" +
                "      <CertificationUnit>川汇区道路运输管理所</CertificationUnit>\n" +
                "      <Photo></Photo>\n" +
                "    </RoadTransportCertificateInformation>\n" +
                "  </Body>\n" +
                "</Root>";
        System.out.println(XmlUtils.getElementValue(xml, "PeriodEndDate"));
    }
}
