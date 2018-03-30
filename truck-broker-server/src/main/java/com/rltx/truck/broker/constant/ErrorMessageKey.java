package com.rltx.truck.broker.constant;

/**
 * 错误消息Key
 */
public interface ErrorMessageKey {

    /**
     * 通用模块
     */
    public interface Common {

        /**
         * 未知错误
         */
        String COMMON_UNKNOWN_ERROR = "common.unknown.error";

    }


    /**
     * 上报模块
     */
    public interface Report {

        /**
         * vo 参数验证错误
         */
        String REPORT_VO_VALIDATE_ERROR = "report.vo.validate.error";

        /**
         * 运单上报错误
         */
        String REPORT_REPORT_ERROR = "report.report.error";
    }
}
