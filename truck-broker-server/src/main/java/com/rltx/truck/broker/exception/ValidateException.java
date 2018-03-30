package com.rltx.truck.broker.exception;


/**
 * Created by Leo_Chan on 2017/4/14.
 */
public class ValidateException extends RuntimeException {

    // 错误消息
    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ValidateException(String msg) {
        super();
        this.msg = msg;
    }
}
