package com.rltx.truck.broker.constant;

/**
 * Created by Leo_Chan on 2017/4/17.
 */
public interface Constants {


    /**
     * actionType  活动类型
     *             企业查询：QueryEnterpriseCredit
     *             车辆查询：QueryVehicleCredit
     *             人员查询：QueryPersonCredit
     */
    interface CreditQueryActionType {
        String QUERY_ENTERPRISE_CREDIT = "QueryEnterpriseCredit";
        String QUERY_VEHICLE_CREDIT = "QueryVehicleCredit";
        String QUERY_PERSON_CREDIT = "QueryPersonCredit";
    }

    public interface BrokerError {

        /**
         * RLTX-Broker-Error 001 ： 物流交换代码错误
         */
        String SENDER_CODE_ERROR = "RBE001";

        /**
         * RLTX-Broker-Error 002 ： 无道路运输证
         */
        String TRUCK_LICENCE_NOT_FOUND = "RBE002";

        /**
         * RLTX-Broker-Error 003 ： 道路运输证过期
         */
        String TRUCK_LICENCE_EXPIRED = "RBE003";

        /**
         * RLTX-Broker-Error 003 ： 道路运输证过期
         */
        String TRUCK_LICENCE_EXPIRED_MESSAGE = "道路运输证过期";
    }

    /**
     * 业务动作类型
     */
    interface ActionType {

        // 人员诚信查询 业务类型
        String CREDIT_PERSON_QUERY_TYPE = "QueryPersonCredit";

        // 企业诚信查询 业务类型
        String CREDIT_ENTERPRISE_QUERY_TYPE = "QueryEnterpriseCredit";

        // 车辆诚信查询 业务类型
        String CREDIT_VEHICLE_QUERY_TYPE = "QueryVehicleCredit";

        // 车辆校验 业务类型
        String CREDIT_VEHICLE_CHECK_TYPE = "CheckPersonVehicleEnterpriseInformation";

        // 车辆信用上报 业务类型
        String REPORT_CREDIT_VEHICLE_TYPE = "LOGINK_CN_CREDIT_VEHICLE";

        // 人员信用上报 业务类型
        String REPORT_CREDIT_PERSON_TYPE = "LOGINK_CN_CREDIT_PERSON";

        // 企业信用上报 业务类型
        String REPORT_CREDIT_ENTERPRISE_TYPE = "LOGINK_CN_CREDIT_ENTERPRISE";

        // 无车承运人运单上报 业务类型
        String REPORT_WAYBILL_TYPE = "LOGINK_CN_FREIGHTBROKER_WAYBILL";

        // 无车承运人资金流水上报 业务类型
        String REPORT_CAPITAL_FLOW_TYPE = "LOGINK_CN_FREIGHTCHARGES";
    }


    interface LicensePlateTypeCode {
        // 黄牌
        String YELLOW_COLOR_TYPE = "01";
        // 蓝牌
        String BLUE_COLOR_TYPE = "02";
        // 其他牌
        String OTHER_TYPE = "99";
    }


    /**
     * FreeMarker模板名称
     */
    interface FreeMarkerTemplateName {

        // 无车承运人上报模板
        String REPORT_WAYBILL_TEMPLATE = "cuteinfo.ftl";

        // 车辆上报模板
        String REPORT_VEHICLE_TEMPLATE = "vehicle_report.ftl";

        // 人员上报模板
        String REPORT_PERSON_TEMPLATE = "person_report.ftl";

        // 车辆上报模板
        String REPORT_ENTERPRISE_TEMPLATE = "enterprise_report.ftl";

        // 资金流水上报模板
        String REPORT_CAPITAL_FLOW_TEMPLATE = "capital_flow.ftl";
    }
}
