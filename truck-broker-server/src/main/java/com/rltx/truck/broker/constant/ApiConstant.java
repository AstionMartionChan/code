package com.rltx.truck.broker.constant;

/**
 * Api常量定义
 * <p/>
 */
public interface ApiConstant {

    /**
     * 通用模块
     */
    public interface Common {

        /**
         * 通用-未知错误
         */
        Integer CODE_UNKNOWN_ERROR = 9900;
    }


    /**
     * 上报模块
     */
    public interface Report {

        /**
         * vo 参数验证错误
         */
        Integer CODE_VO_VALIDATE_ERROR = 10000;


        /**
         * 运单上报错误
         */
        Integer CODE_REPORT_ERROR = 10001;
    }




}
