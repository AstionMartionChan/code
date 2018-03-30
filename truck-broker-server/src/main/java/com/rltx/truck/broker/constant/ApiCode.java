package com.rltx.truck.broker.constant;

/**
 * 通讯响应码
 */
public interface ApiCode {

    /**
     * 通信响应码-成功
     */
    Integer CODE_SUCCESS = 200;

    /**
     * 通信响应码-请求参数错误
     */
    Integer CODE_BAD_REQUEST = 400;

    /**
     * 通信响应码-无权限
     */
    Integer CODE_UNAUTHORIZED = 401;

    /**
     * 通信响应码-禁止访问
     */
    Integer CODE_FORBIDDEN = 403;

    /**
     * 通信响应码-服务器异常
     */
    Integer CODE_INTERNAL_SERVER_ERROR = 500;

}
