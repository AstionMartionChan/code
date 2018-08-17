package com.itcast.exception;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/6/26
 * Time: 13:56
 * Work contact: Astion_Leo@163.com
 */


public class PriceGetFailException extends RuntimeException {

    public PriceGetFailException(String msg) {
        super("商品价格获取失败: " + msg);
    }
}
