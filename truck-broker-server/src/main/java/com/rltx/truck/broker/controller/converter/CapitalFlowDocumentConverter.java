package com.rltx.truck.broker.controller.converter;

import com.rltx.truck.broker.result.*;
import com.rltx.truck.broker.utils.UploadUtils;
import com.rltx.truck.broker.vo.*;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 资金流水上报单封装类
 */
public class CapitalFlowDocumentConverter {

    // 报文功能代码 新增
    private static final String MESSAGE_FUNCTION_CODE_CREATE = "9";
    // 资金流水单上报 单证名称
    private static final String DOCUMENT_NAME = "资金流水单";
    // 资金流水单上报 报文参考号
    private static final String DOCUMENT_VERSION_NUMBER = "2015WCCYR";

    public static CapitalFlowDocumentVo toCapitalFlowDocumentVo(BrokerReportCapitalFlowResult result) {
        CapitalFlowDocumentVo vo = new CapitalFlowDocumentVo();

        vo.setMessageReferenceNumber(UploadUtils.getUuid());
        vo.setDocumentName(DOCUMENT_NAME);
        vo.setDocumentVersionNumber(DOCUMENT_VERSION_NUMBER);
        vo.setSenderCode(result.getSenderCode());
        vo.setMessageSendingDateTime(UploadUtils.getFormatTime());
        vo.setMessageFunctionCode(MESSAGE_FUNCTION_CODE_CREATE);

        vo.setDocumentNumber(result.getCode());
        vo.setCarrier(result.getCarrier());
        vo.setVehicleNumber(result.getVehicleNumber());
        vo.setLicensePlateTypeCode(result.getLicensePlateTypeCode());

        List<ShippingNoteListVo> shippingNoteListVoList = new ArrayList<>();
        if (!StringUtils.isBlank(result.getShippingNoteNumber())) {
            ShippingNoteListVo shippingNoteListVo = new ShippingNoteListVo();
            shippingNoteListVo.setShippingNoteNumber(result.getShippingNoteNumber());
            shippingNoteListVo.setRemark(result.getDescription());
            shippingNoteListVoList.add(shippingNoteListVo);
        }
        vo.setShippingNoteList(shippingNoteListVoList);

        List<FinancialListVo> financialListVoList = new ArrayList<>();
        if (!StringUtils.isBlank(result.getPaymentMeansCode1())
                && !StringUtils.isBlank(result.getBankCode())
                && !StringUtils.isBlank(result.getSequenceCode1())
                && result.getMonetaryAmount1() != null
                && result.getDateTime1() != null) {
            FinancialListVo financialListVo = new FinancialListVo();
            financialListVo.setPaymentMeansCode(result.getPaymentMeansCode1());
            financialListVo.setBankCode(result.getBankCode());
            financialListVo.setSequenceCode(result.getSequenceCode1());
            financialListVo.setMonetaryAmount(UploadUtils.formatNumber(result.getMonetaryAmount1()));
            financialListVo.setDateTime(UploadUtils.getFormatTime(result.getDateTime1()));
            financialListVoList.add(financialListVo);
        }

        if (!StringUtils.isBlank(result.getPaymentMeansCode2())
                && !StringUtils.isBlank(result.getSequenceCode2())
                && result.getMonetaryAmount2() != null
                && result.getDateTime2() != null) {
            FinancialListVo financialListVo = new FinancialListVo();
            financialListVo.setPaymentMeansCode(result.getPaymentMeansCode2());
            financialListVo.setSequenceCode(result.getSequenceCode2());
            financialListVo.setMonetaryAmount(UploadUtils.formatNumber(result.getMonetaryAmount2()));
            financialListVo.setDateTime(UploadUtils.getFormatTime(result.getDateTime2()));
            financialListVoList.add(financialListVo);
        }

        if (!StringUtils.isBlank(result.getPaymentMeansCode3())
                && !StringUtils.isBlank(result.getSequenceCode3())
                && result.getMonetaryAmount3() != null
                && result.getDateTime3() != null) {
            FinancialListVo financialListVo = new FinancialListVo();
            financialListVo.setPaymentMeansCode(result.getPaymentMeansCode3());
            financialListVo.setSequenceCode(result.getSequenceCode3());
            financialListVo.setMonetaryAmount(UploadUtils.formatNumber(result.getMonetaryAmount3()));
            financialListVo.setDateTime(UploadUtils.getFormatTime(result.getDateTime3()));
            financialListVoList.add(financialListVo);
        }

        if (!StringUtils.isBlank(result.getPaymentMeansCode4())
                && !StringUtils.isBlank(result.getSequenceCode4())
                && result.getMonetaryAmount4() != null
                && result.getDateTime4() != null) {
            FinancialListVo financialListVo = new FinancialListVo();
            financialListVo.setPaymentMeansCode(result.getPaymentMeansCode4());
            financialListVo.setSequenceCode(result.getSequenceCode4());
            financialListVo.setMonetaryAmount(UploadUtils.formatNumber(result.getMonetaryAmount4()));
            financialListVo.setDateTime(UploadUtils.getFormatTime(result.getDateTime4()));
            financialListVoList.add(financialListVo);
        }

        vo.setFinancialList(financialListVoList);

        return vo;
    }

}
