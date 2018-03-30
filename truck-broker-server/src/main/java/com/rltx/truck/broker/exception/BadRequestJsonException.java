package com.rltx.truck.broker.exception;


import com.rltx.truck.broker.constant.ApiCode;

/**
 * 请求参数错误异常类
 */
public class BadRequestJsonException extends ApiJsonException {

    public BadRequestJsonException(Integer errorNo, String message) {
        super(ApiCode.CODE_BAD_REQUEST, errorNo, message);
    }

    public BadRequestJsonException(Integer errorNo, String message, Throwable cause) {
        super(ApiCode.CODE_BAD_REQUEST, errorNo, message, cause);
    }
}
