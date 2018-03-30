package com.rltx.truck.broker.result;

/**
 * 封装了ajax的返回属性
 *
 * Created by zhuyi on 2015/5/18.
 */
public class AjaxResult<T> {

    // code定义
    // 原:<pre>
    //      100 => 业务成功
    //      101 => 业务失败
    //      500 => 程序出错
    // </pre>
    // 新:<pre>
    //      200 => 业务成功
    //      500 => 业务失败
    // </pre>
    private Integer code;

    // ajax内容，根据code不同，content不同
    // <pre>
    //      100 => data : {}
    //      101 => message : {}
    //      500 => error : {}
    //  </pre>
    @Deprecated
    private T content;

    // ajax内容，根据code不同，data不同
    // <pre>
    //      200 => data : {}
    //      500 => data : {
    //                  errNo: => ajax错误code
    //                  msg:   => 错误信息
    //             }
    //  </pre>
    private T data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
