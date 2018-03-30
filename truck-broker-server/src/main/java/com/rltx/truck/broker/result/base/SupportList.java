package com.rltx.truck.broker.result.base;

import com.alibaba.fastjson.annotation.JSONType;

import java.util.ArrayList;
import java.util.List;

/**
 * 辅助list类
 *
 * Created by zhuyi on 16/12/9.
 */
@JSONType(alphabetic = false)
public class SupportList<E> extends ArrayList<E> {

    // 标识符
    private Long mark;

    // 是否有更多 
    private Boolean hasMore;

    // data list
    private List<E> dataList;

    public SupportList(List<E> list) {
        super(list);
        this.dataList = list;
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

    public List<E> getDataList() {
        return dataList;
    }

    public void setDataList(List<E> dataList) {
        this.dataList = dataList;
    }
}
