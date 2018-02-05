package com.itcast.po;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Leo_Chan on 2018/1/25.
 */
public class Page {

    // 网址
    private String url;

    // 页面document元素
    private String context;

    // 解析之后的参数
    private Map<String, Object> params = new HashMap<>();

    public void addParams(String key, Object value) {
        this.params.put(key, value);
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Page{" +
                "params=" + params +
                '}';
    }
}
