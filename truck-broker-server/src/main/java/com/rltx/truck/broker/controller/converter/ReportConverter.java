package com.rltx.truck.broker.controller.converter;

import com.rltx.truck.broker.bo.EnterpriseQueryBo;
import com.rltx.truck.broker.bo.PersonQueryBo;
import com.rltx.truck.broker.bo.TruckCarrierReportBo;
import com.rltx.truck.broker.bo.TruckCreditQueryBo;
import com.rltx.truck.broker.bo.VehicleQueryBo;
import com.rltx.truck.broker.bo.VehicleReportBo;
import com.rltx.truck.broker.bo.WaybillReportBo;
import com.rltx.truck.broker.result.EnterpriseQueryResult;
import com.rltx.truck.broker.result.PersonQueryResult;
import com.rltx.truck.broker.result.TruckCarrierReportResult;
import com.rltx.truck.broker.result.TruckCreditQueryResult;
import com.rltx.truck.broker.result.VehicleQueryResult;
import com.rltx.truck.broker.result.VehicleReportResult;
import com.rltx.truck.broker.result.WaybillReportResult;
import com.rltx.truck.broker.utils.PropertiesUtils;
import com.rltx.truck.broker.utils.UploadUtils;
import com.rltx.truck.broker.vo.DriverDocumentVo;
import com.rltx.truck.broker.vo.GoodsInfoDocumentVo;
import com.rltx.truck.broker.vo.TruckCarrierDocumentVo;
import com.rltx.truck.broker.vo.TruckCarrierReportVo;
import com.rltx.truck.broker.vo.TruckCreditQueryDocumentVo;
import com.rltx.truck.broker.vo.TruckCreditQueryVo;
import com.rltx.truck.broker.vo.VehicleReportDocumentVo;
import com.rltx.truck.broker.vo.VehicleReportVo;
import com.rltx.truck.broker.vo.WaybillReportDocumentVo;
import com.rltx.truck.broker.vo.WaybillReportVo;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

/**
 * Created by Leo_Chan on 2017/4/17.
 */
public class ReportConverter {

    // 报文功能代码 新增
    private static final String REPORT_MESSAGE_FUNCTION_CODE_CREATE = "9";
    // 报文功能代码 修改
    private static final String REPORT_MESSAGE_FUNCTION_CODE_UPDATE = "5";
    // 无车承运人运单上报 单证名称
    private static final String WAYBILL_REPORT_DOCUMENT_NAME = "无车承运人电子路单";
    // 无车承运人运单上报 报文参考号
    private static final String WAYBILL_REPORT_DOCUMENT_VERSION_NUMBER = "2015WCCYR";
    // 车辆上报 单证名称
    private static final String VEHICLE_REPORT_DOCUMENT_NAME = "车辆诚信信息上传";
    // 车辆上报 报文参考号
    private static final String VEHICLE_REPORT_DOCUMENT_VERSION_NUMBER = "V2014";


    /**
     * TruckCarrierReportBo 转换为 TruckCarrierReportResult
     * @param truckCarrierReportBo
     * @return
     */
    public static TruckCarrierReportResult toTruckCarrierReportResult(TruckCarrierReportBo truckCarrierReportBo) {
        TruckCarrierReportResult result = new TruckCarrierReportResult();
        result.setIsSuccess(truckCarrierReportBo.getIsSuccess());
        result.setErrorMsg(truckCarrierReportBo.getErrorMsg());
        result.setReportMessage(truckCarrierReportBo.getReportMessage());
        result.setResponseMessage(truckCarrierReportBo.getResponseMessage());

        return result;
    }


    /**
     * TruckCreditQueryBo 转换为 TruckCreditQueryResult
     * @param truckCreditQueryBo
     * @return
     */
    public static TruckCreditQueryResult toTruckCreditQueryResult(TruckCreditQueryBo truckCreditQueryBo) {
        TruckCreditQueryResult result = new TruckCreditQueryResult();
        result.setIsSuccess(truckCreditQueryBo.getIsSuccess());
        result.setErrorCode(truckCreditQueryBo.getErrorCode());
        result.setResponseMessage(truckCreditQueryBo.getResultXml());

        return result;
    }


    /**
     * 创建TruckCarrierDcumentVo并初始化默认值
     * @return
     */
    private static TruckCarrierDocumentVo createTruckCarrierDcumentVo() {
        Properties properties = PropertiesUtils.PROPERTIES.getProperties("cuteinfo.properties");

        TruckCarrierDocumentVo truckCarrierDocumentVo = new TruckCarrierDocumentVo();
        truckCarrierDocumentVo.setMessageReferenceNumber(UploadUtils.getUuid());
        truckCarrierDocumentVo.setDocumentName("无车承运人电子路单");
        truckCarrierDocumentVo.setDocumentVersionNumber("2015WCCYR");
//        truckCarrierDocumentVo.setRecipientCode(toaddress);
        truckCarrierDocumentVo.setMessageSendingDateTime(UploadUtils.getFormatTime());
//        truckCarrierDocumentVo.setConsignmentDateTime(UploadUtils.getFormatTime());

        return truckCarrierDocumentVo;
    }


    /**
     * TruckCarrierReportVo 转换为 TruckCarrierDcumentVo
     * @param vo
     * @return
     */
    public static TruckCarrierDocumentVo toTruckCarrierDcumentVo(TruckCarrierReportVo vo) {
        TruckCarrierDocumentVo truckCarrierDocumentVo = createTruckCarrierDcumentVo();

        // 运单信息报文
        truckCarrierDocumentVo.setMessageFunctionCode(vo.getIsUpdate() ?
                REPORT_MESSAGE_FUNCTION_CODE_UPDATE : REPORT_MESSAGE_FUNCTION_CODE_CREATE);
        truckCarrierDocumentVo.setSenderCode(vo.getSenderCode());
        truckCarrierDocumentVo.setOriginalDocumentNumber(vo.getOriginalDocumentNumber());
        truckCarrierDocumentVo.setShippingNoteNumber(vo.getShippingNoteNumber());
        truckCarrierDocumentVo.setCarrier(vo.getCarrier());
        truckCarrierDocumentVo.setConsignmentDateTime(UploadUtils.getFormatTime(vo.getConsignmentDateTime()));
        truckCarrierDocumentVo.setUnifiedSocialCreditIdentifier(vo.getUnifiedSocialCreditIdentifier());
        truckCarrierDocumentVo.setPermitNumber(vo.getPermitNumber());
        truckCarrierDocumentVo.setBusinessTypeCode(vo.getBusinessTypeCode());
        truckCarrierDocumentVo.setDespatchActualDateTime(UploadUtils.getFormatTime(vo.getDespatchActualDateTime()));
        truckCarrierDocumentVo.setGoodsReceiptDateTime(UploadUtils.getFormatTime(vo.getGoodsReceiptDateTime()));
        truckCarrierDocumentVo.setConsignor(vo.getConsignor());
        truckCarrierDocumentVo.setPersonalIdentityDocument(vo.getPersonalIdentityDocument());
        truckCarrierDocumentVo.setPlaceOfLoading(vo.getPlaceOfLoading());
        truckCarrierDocumentVo.setConsignorCountrySubdivisionCode(vo.getConsignorCountrySubdivisionCode());
        truckCarrierDocumentVo.setConsignee(vo.getConsignee());
        truckCarrierDocumentVo.setGoodsReceiptPlace(vo.getGoodsReceiptPlace());
        truckCarrierDocumentVo.setConsigneeCountrySubdivisionCode(vo.getConsigneeCountrySubdivisionCode());
        truckCarrierDocumentVo.setTotalMonetaryAmount(vo.getTotalMonetaryAmount());
        truckCarrierDocumentVo.setRemark(vo.getRemark());
        truckCarrierDocumentVo.setLicensePlateTypeCode(vo.getLicensePlateTypeCode());
        truckCarrierDocumentVo.setVehicleNumber(vo.getVehicleNumber());
        truckCarrierDocumentVo.setVehicleClassificationCode(vo.getVehicleClassificationCode());
        truckCarrierDocumentVo.setVehicleTonnage(vo.getVehicleTonnage());
        truckCarrierDocumentVo.setRoadTransportCertificateNumber(vo.getRoadTransportCertificateNumber());
        truckCarrierDocumentVo.setTrailerVehiclePlateNumber(vo.getTrailerVehiclePlateNumber());
        truckCarrierDocumentVo.setOwner(vo.getOwner());
        truckCarrierDocumentVo.setVehiclePermitNumber(vo.getVehiclePermitNumber());
        truckCarrierDocumentVo.setFreeText(vo.getFreeText());

        // 司机信息报文
        List<DriverDocumentVo> driverDocumentVoList = new ArrayList<>();
        DriverDocumentVo driverDocumentVo = new DriverDocumentVo();
        driverDocumentVo.setNameOfPerson(vo.getNameOfPerson());
        driverDocumentVo.setQualificationCertificateNumber(vo.getQualificationCertificateNumber());
        driverDocumentVo.setTelephoneNumber(vo.getTelephoneNumber());
        driverDocumentVoList.add(driverDocumentVo);
        truckCarrierDocumentVo.setDriver(driverDocumentVoList);

        // 货物信息报文
        List<GoodsInfoDocumentVo> goodsInfoDocumentVoList = new ArrayList<>();
        GoodsInfoDocumentVo goodsInfoDocumentVo = new GoodsInfoDocumentVo();
        goodsInfoDocumentVo.setDescriptionOfGoods(vo.getDescriptionOfGoods());
        goodsInfoDocumentVo.setCargoTypeClassificationCode(vo.getCargoTypeClassificationCode());
        goodsInfoDocumentVo.setGoodsItemGrossWeight(vo.getGoodsItemGrossWeight());
        goodsInfoDocumentVo.setCube(vo.getCube());
        goodsInfoDocumentVo.setTotalNumberOfPackages(vo.getTotalNumberOfPackages());
        goodsInfoDocumentVoList.add(goodsInfoDocumentVo);
        truckCarrierDocumentVo.setGoodsInfo(goodsInfoDocumentVoList);

        return truckCarrierDocumentVo;
    }


    /**
     * TruckCreditQueryVo 转换为 TruckCreditQueryDocumentVo
     * @param truckCreditQueryVo
     * @return
     */
    public static TruckCreditQueryDocumentVo toTruckCreditQueryDocumentVo(TruckCreditQueryVo truckCreditQueryVo) {
        TruckCreditQueryDocumentVo truckCreditQueryDocumentVo = new TruckCreditQueryDocumentVo();
        truckCreditQueryDocumentVo.setVehicleNumber(truckCreditQueryVo.getVehicleNumber());
        truckCreditQueryDocumentVo.setLicensePlateTypeCode(truckCreditQueryVo.getLicensePlateTypeCode());
        truckCreditQueryDocumentVo.setRoadTransportCertificateNumber(truckCreditQueryVo.getRoadTransportCertificateNumber());
        truckCreditQueryDocumentVo.setSearchTypeCode("02");
        truckCreditQueryDocumentVo.setStartTime(UploadUtils.getFormatTime());

        return truckCreditQueryDocumentVo;
    }


    /**
     * WaybillReportVo 转换为 WaybillReportDocumentVo
     * @param waybillReportVo
     * @return
     */
    public static WaybillReportDocumentVo toWaybillReportDocumentVo(WaybillReportVo waybillReportVo) {
        WaybillReportDocumentVo waybillReportDocumentVo = new WaybillReportDocumentVo();

        waybillReportDocumentVo.setMessageReferenceNumber(generateGUID());
        waybillReportDocumentVo.setDocumentName(WAYBILL_REPORT_DOCUMENT_NAME);
        waybillReportDocumentVo.setDocumentVersionNumber(WAYBILL_REPORT_DOCUMENT_VERSION_NUMBER);
        waybillReportDocumentVo.setSenderCode(waybillReportVo.getSenderCode());
        waybillReportDocumentVo.setRecipientCode(waybillReportVo.getRecipientCode());
        waybillReportDocumentVo.setMessageSendingDateTime(UploadUtils.getFormatTime());
        waybillReportDocumentVo.setMessageFunctionCode(waybillReportVo.getMessageFunctionCode());
        waybillReportDocumentVo.setOriginalDocumentNumber(waybillReportVo.getOriginalDocumentNumber());
        waybillReportDocumentVo.setShippingNoteNumber(waybillReportVo.getShippingNoteNumber());
        waybillReportDocumentVo.setCarrier(waybillReportVo.getCarrier());
        waybillReportDocumentVo.setUnifiedSocialCreditIdentifier(waybillReportVo.getUnifiedSocialCreditIdentifier());
        waybillReportDocumentVo.setPermitNumber(waybillReportVo.getPermitNumber());
        waybillReportDocumentVo.setConsignmentDateTime(waybillReportVo.getConsignmentDateTime());
        waybillReportDocumentVo.setBusinessTypeCode(waybillReportVo.getBusinessTypeCode());
        waybillReportDocumentVo.setDespatchActualDateTime(waybillReportVo.getDespatchActualDateTime());
        waybillReportDocumentVo.setGoodsReceiptDateTime(waybillReportVo.getGoodsReceiptDateTime());
        waybillReportDocumentVo.setConsignor(waybillReportVo.getConsignor());
        waybillReportDocumentVo.setPersonalIdentityDocument(waybillReportVo.getPersonalIdentityDocument());
        waybillReportDocumentVo.setPlaceOfLoading(waybillReportVo.getPlaceOfLoading());
        waybillReportDocumentVo.setConsignorCountrySubdivisionCode(waybillReportVo.getConsignorCountrySubdivisionCode());
        waybillReportDocumentVo.setConsignee(waybillReportVo.getConsignee());
        waybillReportDocumentVo.setGoodsReceiptPlace(waybillReportVo.getGoodsReceiptPlace());
        waybillReportDocumentVo.setConsigneeCountrySubdivisionCode(waybillReportVo.getConsigneeCountrySubdivisionCode());
        waybillReportDocumentVo.setTotalMonetaryAmount(waybillReportVo.getTotalMonetaryAmount());
        waybillReportDocumentVo.setRemark(waybillReportVo.getRemark());
        waybillReportDocumentVo.setLicensePlateTypeCode(waybillReportVo.getLicensePlateTypeCode());
        waybillReportDocumentVo.setVehicleNumber(waybillReportVo.getVehicleNumber());
        waybillReportDocumentVo.setVehicleClassificationCode(waybillReportVo.getVehicleClassificationCode());
        waybillReportDocumentVo.setVehicleTonnage(waybillReportVo.getVehicleTonnage());
        waybillReportDocumentVo.setRoadTransportCertificateNumber(waybillReportVo.getRoadTransportCertificateNumber());
        waybillReportDocumentVo.setTrailerVehiclePlateNumber(waybillReportVo.getTrailerVehiclePlateNumber());
        waybillReportDocumentVo.setOwner(waybillReportVo.getOwner());
        waybillReportDocumentVo.setVehiclePermitNumber(waybillReportVo.getVehiclePermitNumber());
        waybillReportDocumentVo.setFreeText(waybillReportVo.getFreeText());


        // 司机信息封装
        String driverNameOfPersonListString = waybillReportVo.getDriverNameOfPersonListString();
        String driverQualificationCertificateNumberListString = waybillReportVo.getDriverQualificationCertificateNumberListString();
        String driverTelephoneNumberListString = waybillReportVo.getDriverTelephoneNumberListString();

        if (driverNameOfPersonListString != null
                && driverQualificationCertificateNumberListString != null
                && driverTelephoneNumberListString != null){

            String[] driverNameOfPersonArray = driverNameOfPersonListString.split(",");
            String[] driverQualificationCertificateNumberArray = driverQualificationCertificateNumberListString.split(",");
            String[] driverTelephoneNumberArray = driverTelephoneNumberListString.split(",");

            List<DriverDocumentVo> driverDocumentVoList = new ArrayList<>();
            DriverDocumentVo driverDocumentVo = null;
            for (int x = 0; x < driverNameOfPersonArray.length; x++){
                driverDocumentVo = new DriverDocumentVo();
                if (!driverNameOfPersonArray[x].equals("null")){
                    driverDocumentVo.setNameOfPerson(driverNameOfPersonArray[x]);
                }
                if (!driverQualificationCertificateNumberArray[x].equals("null")){
                    driverDocumentVo.setQualificationCertificateNumber(driverQualificationCertificateNumberArray[x]);
                }
                if (!driverTelephoneNumberArray[x].equals("null")){
                    driverDocumentVo.setTelephoneNumber(driverTelephoneNumberArray[x]);
                }

                if (driverDocumentVo.getNameOfPerson() == null
                        && driverDocumentVo.getQualificationCertificateNumber() == null
                        && driverDocumentVo.getTelephoneNumber() == null){
                    continue;
                }
                driverDocumentVoList.add(driverDocumentVo);
            }

            waybillReportDocumentVo.setDriver(driverDocumentVoList);
        }


        // 货物信息封装
        List<GoodsInfoDocumentVo> goodsInfoList = new ArrayList<>();
        GoodsInfoDocumentVo goodsInfoDocumentVo = null;
        String[] descriptionOfGoodsArray = waybillReportVo.getGoodsDescriptionOfGoodsListString().split(",");
        String[] cargoTypeClassificationCodeArray = waybillReportVo.getGoodsCargoTypeClassificationCodeListString().split(",");
        String[] goodsItemGrossWeightArray = waybillReportVo.getGoodsGoodsItemGrossWeightListString().split(",");
        String[] goodsCubeArray = waybillReportVo.getGoodsCubeListString().split(",");
        String[] goodsTotalNumberOfPackagesArray = waybillReportVo.getGoodsTotalNumberOfPackagesListString().split(",");

        for (int x = 0; x < descriptionOfGoodsArray.length; x++){
            goodsInfoDocumentVo = new GoodsInfoDocumentVo();

            goodsInfoDocumentVo.setDescriptionOfGoods(descriptionOfGoodsArray[x]);
            goodsInfoDocumentVo.setCargoTypeClassificationCode(cargoTypeClassificationCodeArray[x]);
            goodsInfoDocumentVo.setGoodsItemGrossWeight(Double.parseDouble(goodsItemGrossWeightArray[x]));
            if (!goodsCubeArray[x].equals("null")){
                goodsInfoDocumentVo.setCube(Double.parseDouble(goodsCubeArray[x]));
            }
            if (!goodsTotalNumberOfPackagesArray[x].equals("null")){
                goodsInfoDocumentVo.setTotalNumberOfPackages(Integer.parseInt(goodsTotalNumberOfPackagesArray[x]));
            }

            goodsInfoList.add(goodsInfoDocumentVo);
        }

        waybillReportDocumentVo.setGoodsInfo(goodsInfoList);


        return waybillReportDocumentVo;
    }



    public static VehicleReportDocumentVo toVehicleReportDocumentVo(VehicleReportVo vehicleReportVo) {
        VehicleReportDocumentVo vehicleReportDocumentVo = new VehicleReportDocumentVo();

        vehicleReportDocumentVo.setMessageReferenceNumber(generateGUID());
        vehicleReportDocumentVo.setDocumentName(VEHICLE_REPORT_DOCUMENT_NAME);
        vehicleReportDocumentVo.setDocumentVersionNumber(VEHICLE_REPORT_DOCUMENT_VERSION_NUMBER);
        vehicleReportDocumentVo.setSenderCode(vehicleReportVo.getSenderCode());
        vehicleReportDocumentVo.setMessageSendingDateTime(UploadUtils.getFormatTime());
        vehicleReportDocumentVo.setVehicleNumber(vehicleReportVo.getVehicleNumber());
        vehicleReportDocumentVo.setLicensePlateTypeCode(vehicleReportVo.getLicensePlateTypeCode());
        vehicleReportDocumentVo.setOwner(vehicleReportVo.getOwner());

        return vehicleReportDocumentVo;
    }



    public static VehicleReportResult toVehicleReportResult(VehicleReportBo vehicleReportBo) {
        VehicleReportResult result = new VehicleReportResult();
        result.setIsSuccess(vehicleReportBo.getIsSuccess());
        result.setResponseMessage(vehicleReportBo.getResponseMessage());
        result.setErrorMsg(vehicleReportBo.getErrorMsg());

        return result;
    }


    /**
     * WaybillReportBo 转换为 WaybillReportResult
     * @param waybillReportBo
     * @return
     */
    public static WaybillReportResult toWaybillReportResult(WaybillReportBo waybillReportBo) {
        WaybillReportResult result = new WaybillReportResult();

        result.setIsSuccess(waybillReportBo.getIsSuccess());
        result.setErrorMsg(waybillReportBo.getErrorMsg());
        result.setReportMessage(waybillReportBo.getReportMessage());
        result.setResponseMessage(waybillReportBo.getResponseMessage());

        return result;
    }


    public static VehicleQueryResult toVehicleQueryResult(VehicleQueryBo vehicleQueryBo) {
        VehicleQueryResult result = new VehicleQueryResult();

        result.setIsSuccess(vehicleQueryBo.getIsSuccess());
        result.setIsExist(vehicleQueryBo.getIsExist());
        result.setVehicleNumber(vehicleQueryBo.getVehicleNumber());
        result.setLicensePlateTypeCode(vehicleQueryBo.getLicensePlateTypeCode());
        result.setOwner(vehicleQueryBo.getOwner());
        result.setVehicleClassification(vehicleQueryBo.getVehicleClassification());
        result.setVehicleLength(vehicleQueryBo.getVehicleLength());
        result.setVehicleWidth(vehicleQueryBo.getVehicleWidth());
        result.setVehicleHeight(vehicleQueryBo.getVehicleHeight());
        result.setBusinessState(vehicleQueryBo.getBusinessState());
        result.setBusinessState(vehicleQueryBo.getBusinessState());
        result.setResponseMessage(vehicleQueryBo.getResponseMessage());
        result.setErrorCode(vehicleQueryBo.getErrorCode());
        result.setErrorMessage(vehicleQueryBo.getErrorMessage());

        return result;
    }



    public static EnterpriseQueryResult toEnterpriseQueryResult(EnterpriseQueryBo enterpriseQueryBo) {
        EnterpriseQueryResult result = new EnterpriseQueryResult();

        result.setIsSuccess(enterpriseQueryBo.getIsSuccess());
        result.setIsExist(enterpriseQueryBo.getIsExist());
        result.setResponseMessage(enterpriseQueryBo.getResponseMessage());
        result.setEnterpriseName(enterpriseQueryBo.getEnterpriseName());
        result.setCommunicationAddress(enterpriseQueryBo.getCommunicationAddress());
        result.setBusinessScope(enterpriseQueryBo.getBusinessScope());
        result.setPermitGrantDate(enterpriseQueryBo.getPermitGrantDate());
        result.setPeriodStartDate(enterpriseQueryBo.getPeriodStartDate());
        result.setPeriodEndDate(enterpriseQueryBo.getPeriodEndDate());
        result.setCertificationUnit(enterpriseQueryBo.getCertificationUnit());
        result.setErrorCode(enterpriseQueryBo.getErrorCode());
        result.setErrorMessage(enterpriseQueryBo.getErrorMessage());

        return result;
    }


    public static PersonQueryResult toPersonQueryResult(PersonQueryBo personQueryBo) {
        PersonQueryResult result = new PersonQueryResult();
        result.setIsSuccess(personQueryBo.getIsSuccess());
        result.setIsExist(personQueryBo.getIsExist());
        result.setResponseMessage(personQueryBo.getResponseMessage());
        result.setErrorCode(personQueryBo.getErrorCode());
        result.setErrorMessage(personQueryBo.getErrorMessage());
        result.setNameOfPerson(personQueryBo.getNameOfPerson());
        result.setGender(personQueryBo.getGender());
        result.setIdentityDocumentNumber(personQueryBo.getIdentityDocumentNumber());
        result.setMobileTelephoneNumber(personQueryBo.getMobileTelephoneNumber());
        result.setQualificationCertificateNumber(personQueryBo.getQualificationCertificateNumber());
        result.setLicenseInitialCollectionDate(personQueryBo.getLicenseInitialCollectionDate());
        result.setPeriodStartDate(personQueryBo.getPeriodStartDate());
        result.setPeriodEndDate(personQueryBo.getPeriodEndDate());
        result.setCommunicationNumber(personQueryBo.getCommunicationNumber());

        return result;
    }


    /**
     * 生成GUID
     * @return  GUID字符串
     */
    private static String generateGUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
