package com.itcast.po;

import java.util.Map;

/**
 * Created by Leo_Chan on 2018/1/25.
 */
public class Page {

    private String context;


    private Map<String, Object> params;

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
}
