package com.rltx.truck.broker.result;

import com.alibaba.fastjson.annotation.JSONType;

/**
 * Api异常对象
 * <p/>
*/
@JSONType(alphabetic = false, orders = {"errNo", "msg"})
public class ApiError {

    // code
    private transient Integer code;

    // 异常码
    private Integer errNo;

    // 异常信息
    private String msg;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getErrNo() {
        return errNo;
    }

    public void setErrNo(Integer errNo) {
        this.errNo = errNo;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
