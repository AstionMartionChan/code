package com.itcast.bean;


import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/6/24
 * Time: 21:44
 * Work contact: Astion_Leo@163.com
 */


public class JDSkuSearchInfo {

    private String id;

    private String title;

    private Double op_price;

    private Double p_price;

    private String img_url;

    private Date time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
