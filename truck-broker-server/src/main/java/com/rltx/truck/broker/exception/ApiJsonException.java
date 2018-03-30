package com.rltx.truck.broker.exception;

import com.wl.framework.log.support.BusinessException;

/**
 * json异常类
 */
public class ApiJsonException extends BusinessException {

    // code
    private Integer code;

    //错误信息
    private String message;

    // 异常码
    private Integer errorNo;

    //错误信息打印
    //private Throwable cause;

    public ApiJsonException(Integer code, Integer errorNo, String message, Throwable cause) {
        super();
        this.code = code;
        this.errorNo = errorNo;
        this.message = message;
        //this.cause = cause;
    }

    public ApiJsonException(Integer code, Integer errorNo, String message) {
        super();
        this.code = code;
        this.errorNo = errorNo;
        this.message = message;
        //this.cause = cause;
    }

    public Integer getCode() {
        return code;
    }

    public Integer getErrorNo() {
        return errorNo;
    }

    @Override
    public String getMessage() {
        return message;
    }

  /*  @Override
    public Throwable getCause() {
        return cause;
    }*/
}
