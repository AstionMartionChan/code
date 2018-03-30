package com.rltx.truck.broker.result.base;

import com.alibaba.fastjson.annotation.JSONType;

/**
 * 基础返回类
 * <p/>
*/
@JSONType(alphabetic = false, orders = {"code", "data", "mark", "hasMore"})
public class BaseResult<T> {

    // 返回码
    private Integer code;

    // 内容值
    private T data;

    // 标识符
    private Long mark;

    // 是否有更多 
    private Boolean hasMore;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Long getMark() {
        return mark;
    }

    public void setMark(Long mark) {
        this.mark = mark;
    }

    public Boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }
}
