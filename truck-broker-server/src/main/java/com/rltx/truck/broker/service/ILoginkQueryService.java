package com.rltx.truck.broker.service;

import com.rltx.truck.broker.bo.EnterpriseQueryBo;
import com.rltx.truck.broker.bo.PersonQueryBo;
import com.rltx.truck.broker.bo.VehicleCheckBo;
import com.rltx.truck.broker.bo.VehicleQueryBo;

import java.io.UnsupportedEncodingException;

/**
 * Created by Leo_Chan on 2017/9/12.
 */
public interface ILoginkQueryService {

    /**
     * 校验车辆是否在国家平台存在
     * @param vehicleNumber         车牌号
     * @param senderCode            物流交换代码
     * @param senderPassword        物流交换密码
     * @return
     */
    VehicleCheckBo vehicleCheck(String vehicleNumber, String senderCode, String senderPassword) throws UnsupportedEncodingException;


    /**
     * 查询国家平台上的车辆运政信息
     * @param vehicleNumber         车牌号
     * @param licensePlateTypeCode  牌照类型
     * @param senderCode            物流交换代码
     * @param senderPassword        物流交换密码
     * @return
     */
    VehicleQueryBo vehicleQuery(String vehicleNumber, String licensePlateTypeCode, String senderCode, String senderPassword) throws UnsupportedEncodingException;


    /**
     * 查询国家平台上的企业运政信息
     * @param enterpriseName         企业名称
     * @param countrySubdivisionCode 省份代码
     * @param senderCode             物流交换代码
     * @param senderPassword         物流交换密码
     * @return
     */
    EnterpriseQueryBo enterpriseQuery(String enterpriseName, String countrySubdivisionCode, String senderCode, String senderPassword) throws UnsupportedEncodingException;


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
    PersonQueryBo personQuery(String personName, String identityDocumentNumber,
                              String qualificationCertificateNumber, String countrySubdivisionCode,
                              String senderCode, String senderPassword) throws UnsupportedEncodingException;
}
