package com.rltx.truck.broker.controller.converter;

import com.rltx.truck.broker.bo.VehicleCheckBo;
import com.rltx.truck.broker.result.VehicleCheckResult;

/**
 * Created by Leo_Chan on 2017/9/12.
 */
public class CreditCheckConverter {

    public static VehicleCheckResult toVehicleCheckResult(VehicleCheckBo vehicleCheckBo) {
        VehicleCheckResult result = new VehicleCheckResult();
        result.setIsSuccess(vehicleCheckBo.getIsSuccess());
        result.setIsExist(vehicleCheckBo.getIsExist());
        result.setResponseMessage(vehicleCheckBo.getResponseMessage());
        result.setErrorCode(vehicleCheckBo.getErrorCode());
        result.setErrorMessage(vehicleCheckBo.getErrorMessage());

        return result;
    }
}
