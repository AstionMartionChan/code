package com.rltx.truck.broker.controller.impl;

import com.rltx.truck.broker.base.BaseContext;
import com.rltx.truck.broker.bo.TruckCarrierReportBo;
import com.rltx.truck.broker.bo.TruckCreditQueryBo;
import com.rltx.truck.broker.controller.converter.CapitalFlowDocumentConverter;
import com.rltx.truck.broker.controller.converter.ReportConverter;
import com.rltx.truck.broker.exception.ValidateException;
import com.rltx.truck.broker.result.BrokerReportCapitalFlowResult;
import com.rltx.truck.broker.result.TruckCarrierReportResult;
import com.rltx.truck.broker.result.TruckCreditQueryResult;
import com.rltx.truck.broker.service.ILoginkReportService;
import com.rltx.truck.broker.service.IReportService;
import com.rltx.truck.broker.service.ITruckCarrierReportService;
import com.rltx.truck.broker.vo.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;


@Controller("reportController")
public class ReportController extends BaseContext implements IReportService {

    @Resource(name = "truckCarrierReportService")
    private ITruckCarrierReportService truckCarrierReportService;

    @Resource(name = "loginkReportService")
    private ILoginkReportService loginkReportService;

    /**
     * 无车承运人运单上报 接口
     * @param truckCarrierReportVo  上报vo
     * @param commParamsVo          通用参数vo
     * @return
     * @throws Exception
     */
    @Override
    public TruckCarrierReportResult truckCarrierReport(TruckCarrierReportVo truckCarrierReportVo, CommParamsVo commParamsVo) throws Exception {
        TruckCarrierReportResult result = null;

        // 校验上传报文vo是否有缺失的必填字段
        validateTruckCarrierReportVo(truckCarrierReportVo);

        // 转换为service vo
        TruckCarrierDocumentVo truckCarrierDocumentVo = ReportConverter.toTruckCarrierDcumentVo(truckCarrierReportVo);

        // 调用service 上传报文并返回bo
        TruckCarrierReportBo truckCarrierReportBo =
                truckCarrierReportService.truckCarrierReport(truckCarrierDocumentVo, truckCarrierReportVo.getWaybillId(), commParamsVo);

        // converter 转换为 result
        result = ReportConverter.toTruckCarrierReportResult(truckCarrierReportBo);

        return result;
    }



    /**
     * 车辆诚信查询 接口
     * @param truckCreditQueryVo    车辆查询vo
     * @param commParamsVo          通用参数vo
     * @return
     * @throws Exception
     */
    @Override
    public TruckCreditQueryResult truckQueryResult(TruckCreditQueryVo truckCreditQueryVo, CommParamsVo commParamsVo) throws Exception {
        TruckCreditQueryResult truckCreditQueryResult = null;

        // 校验查询vo是否有缺失的必填字段
        validateTruckCreditQueryVo(truckCreditQueryVo);

        // 转换为service vo
        TruckCreditQueryDocumentVo truckCreditQueryDocumentVo = ReportConverter.toTruckCreditQueryDocumentVo(truckCreditQueryVo);

        // 调用service 查询返回bo
        TruckCreditQueryBo truckCreditQueryBo = truckCarrierReportService.truckCreditQuery(truckCreditQueryDocumentVo, commParamsVo.getOwnerOrgId());

        truckCreditQueryResult = ReportConverter.toTruckCreditQueryResult(truckCreditQueryBo);


        return truckCreditQueryResult;
    }

    @Override
    public BrokerReportCapitalFlowResult capitalFlowReport(BrokerReportCapitalFlowResult brokerReportCapitalFlowResult) throws Exception {

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

    private void validateTruckCreditQueryVo(TruckCreditQueryVo vo) {

        if (vo.getVehicleNumber() == null
                || vo.getVehicleNumber().equals("")){
            throw new ValidateException("车牌号必填");
        }

        if (vo.getLicensePlateTypeCode() == null
                || vo.getLicensePlateTypeCode().equals("")){
            throw new ValidateException("牌照类型代码必填");
        }

    }



    /**
     * 校验cuteInfoEntity 必填参数
     * @param truckCarrierReportVo
     */
    private void validateTruckCarrierReportVo(TruckCarrierReportVo truckCarrierReportVo) {
        if (truckCarrierReportVo.getSenderCode() == null
                || truckCarrierReportVo.getSenderCode().equals("")) {
            throw new ValidateException("发送方代码必填");
        }
        if (truckCarrierReportVo.getOriginalDocumentNumber() == null
                || truckCarrierReportVo.getOriginalDocumentNumber().equals("")) {
            throw new ValidateException("原始单号必填");
        }
        if (truckCarrierReportVo.getShippingNoteNumber() == null
                || truckCarrierReportVo.getShippingNoteNumber().equals("")) {
            throw new ValidateException("托运单号必填");
        }
        if (truckCarrierReportVo.getCarrier() == null
                || truckCarrierReportVo.getCarrier().equals("")) {
            throw new ValidateException("承运人必填");
        }
        if (truckCarrierReportVo.getBusinessTypeCode() == null
                || truckCarrierReportVo.getBusinessTypeCode().equals("")) {
            throw new ValidateException("业务类型代码必填");
        }
        if (truckCarrierReportVo.getDespatchActualDateTime() == null
                || truckCarrierReportVo.getDespatchActualDateTime().equals("")) {
            throw new ValidateException("发运实际日期时间必填");
        }
        if (truckCarrierReportVo.getGoodsReceiptDateTime() == null
                || truckCarrierReportVo.getGoodsReceiptDateTime().equals("")) {
            throw new ValidateException("收货日期时间必填");
        }
        if (truckCarrierReportVo.getConsignorCountrySubdivisionCode() == null
                || truckCarrierReportVo.getConsignorCountrySubdivisionCode().equals("")) {
            throw new ValidateException("发货方国家行政区划代码必填");
        }
        if (truckCarrierReportVo.getConsigneeCountrySubdivisionCode() == null
                || truckCarrierReportVo.getConsigneeCountrySubdivisionCode().equals("")) {
            throw new ValidateException("收货方国家行政区划代码必填");
        }
        if (truckCarrierReportVo.getTotalMonetaryAmount() == null) {
            throw new ValidateException("货币总金额必填");
        }
        if (truckCarrierReportVo.getLicensePlateTypeCode() == null
                || truckCarrierReportVo.getLicensePlateTypeCode().equals("")) {
            throw new ValidateException("牌照类型代码必填");
        }
        if (truckCarrierReportVo.getVehicleNumber() == null
                || truckCarrierReportVo.getVehicleNumber().equals("")) {
            throw new ValidateException("车辆牌照号必填");
        }
        if (truckCarrierReportVo.getVehicleClassificationCode() == null
                || truckCarrierReportVo.getVehicleClassificationCode().equals("")) {
            throw new ValidateException("车辆分类代码必填");
        }
        if (truckCarrierReportVo.getVehicleTonnage() == null) {
            throw new ValidateException("车辆载质量必填");
        }
        if (truckCarrierReportVo.getRoadTransportCertificateNumber() == null
                || truckCarrierReportVo.getRoadTransportCertificateNumber().equals("")) {
            throw new ValidateException("道路运输证号必填");
        }
        if (truckCarrierReportVo.getGoodsReceiptDateTime() == null
                || truckCarrierReportVo.getGoodsReceiptDateTime().equals("")) {
            throw new ValidateException("收货日期时间必填");
        }
//        if (truckCarrierReportVo.getDriver() != null && truckCarrierReportVo.getDriver().size() > 0) {
//            for (DriverDocumentVo driverDocumentVo : truckCarrierReportVo.getDriver()) {
//                if (driverDocumentVo.getNameOfPerson() == null
//                        || driverDocumentVo.getNameOfPerson().equals("")) {
//                    throw new ValidateException("驾驶员姓名必填");
//                }
//            }
//        }
//        if (truckCarrierReportVo.getGoodsInfo() == null || truckCarrierReportVo.getGoodsInfo().size() == 0) {
//            throw new ValidateException("货物信息必填");
//        } else {
//            for (GoodsInfoDocumentVo goodsInfoDocumentVo : truckCarrierReportVo.getGoodsInfo()) {
//                if (goodsInfoDocumentVo.getDescriptionOfGoods() == null
//                        || goodsInfoDocumentVo.getDescriptionOfGoods().equals("")) {
//                    throw new ValidateException("货物名称必填");
//                }
//                if (goodsInfoDocumentVo.getCargoTypeClassificationCode() == null
//                        || goodsInfoDocumentVo.getCargoTypeClassificationCode().equals("")) {
//                    throw new ValidateException("货物类型分类代码必填");
//                }
//                if (goodsInfoDocumentVo.getGoodsItemGrossWeight() == null) {
//                    throw new ValidateException("货物项毛重必填");
//                }
//            }
//        }
    }


    /**
     * 封装无车承运运单 数据
     * @return
     */
//    private static TruckCarrierReportVo createTruckCarrierReportVo() {
//        String uuid = UploadUtils.getUuid();
//        System.out.println("----------------------uuid:   " + uuid);
//        TruckCarrierReportVo truckCarrierReportVo = new TruckCarrierReportVo();
//        truckCarrierReportVo.setMessageReferenceNumber(uuid);
//        truckCarrierReportVo.setDocumentName("无车承运人电子路单");
//        truckCarrierReportVo.setDocumentVersionNumber("2015WCCYR");
//        truckCarrierReportVo.setSenderCode("13850");
//        truckCarrierReportVo.setRecipientCode("wcjc0001");
//        truckCarrierReportVo.setMessageSendingDateTime(UploadUtils.getFormatTime());
//        truckCarrierReportVo.setMessageFunctionCode("9");
//        truckCarrierReportVo.setOriginalDocumentNumber("L2017022114367145");
//        truckCarrierReportVo.setShippingNoteNumber("Y2017022114567489");
//        truckCarrierReportVo.setCarrier("安徽迅捷物流有限公司");
//        truckCarrierReportVo.setConsignmentDateTime(UploadUtils.getFormatTime());
//        truckCarrierReportVo.setBusinessTypeCode("1002996");
//        truckCarrierReportVo.setDespatchActualDateTime(UploadUtils.getFormatTime());
//        truckCarrierReportVo.setGoodsReceiptDateTime(UploadUtils.getFormatTime());
//        truckCarrierReportVo.setConsignorCountrySubdivisionCode("310115");
//        truckCarrierReportVo.setConsigneeCountrySubdivisionCode("310115");
//        truckCarrierReportVo.setTotalMonetaryAmount(123.00);
//        truckCarrierReportVo.setLicensePlateTypeCode("01");
//        truckCarrierReportVo.setVehicleNumber("沪A88888");
//        truckCarrierReportVo.setVehicleClassificationCode("H01");
//        truckCarrierReportVo.setVehicleTonnage(2.0);
//        truckCarrierReportVo.setRoadTransportCertificateNumber("000000000001");
//        ArrayList<GoodsInfoDocumentVo> list = new ArrayList<GoodsInfoDocumentVo>();
//        GoodsInfoDocumentVo goodsInfoDocumentEntity = new GoodsInfoDocumentVo();
//        goodsInfoDocumentEntity.setDescriptionOfGoods("石油");
//        goodsInfoDocumentEntity.setCargoTypeClassificationCode("90");
//        goodsInfoDocumentEntity.setGoodsItemGrossWeight(3.0);
//        list.add(goodsInfoDocumentEntity);
//        goodsInfoDocumentEntity = new GoodsInfoDocumentVo();
//        goodsInfoDocumentEntity.setDescriptionOfGoods("煤炭");
//        goodsInfoDocumentEntity.setCargoTypeClassificationCode("90");
//        goodsInfoDocumentEntity.setGoodsItemGrossWeight(4.0);
//        list.add(goodsInfoDocumentEntity);
//        truckCarrierReportVo.setGoodsInfo(list);
//        List<DriverDocumentVo> driverList = new ArrayList<>();
//        DriverDocumentVo driverDocumentVo = new DriverDocumentVo();
//        driverDocumentVo.setNameOfPerson("Leo");
//        driverDocumentVo.setTelephoneNumber("13918702109");
//        driverList.add(driverDocumentVo);
//        truckCarrierReportVo.setDriver(driverList);
//
//        return truckCarrierReportVo;
//    }
}
