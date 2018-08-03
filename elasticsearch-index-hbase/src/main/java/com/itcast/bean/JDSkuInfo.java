package com.itcast.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/6/24
 * Time: 21:44
 * Work contact: Astion_Leo@163.com
 */


public class JDSkuInfo {

    private String title;

    private Double op_price;

    private Double p_price;

    private String img_url;

    @JSONField(serialize=false)
    private String coupon;

    @JSONField(serialize=false)
    private String sales;

    @JSONField(serialize=false)
    private String url;

    @JSONField(serialize=false)
    private String skuId;

    @JSONField(serialize=false)
    private String params;

    private Date time;

    public Double getOp_price() {
        return op_price;
    }

    public void setOp_price(Double op_price) {
        this.op_price = op_price;
    }

    public Double getP_price() {
        return p_price;
    }

    public void setP_price(Double p_price) {
        this.p_price = p_price;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public String getSales() {
        return sales;
    }

    public void setSales(String sales) {
        this.sales = sales;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String param) {
        this.params = param;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
