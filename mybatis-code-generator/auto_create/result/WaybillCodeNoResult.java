package com.rltx.waybill.result;

import java.util.Date;

/**
 * waybill_code_no
 */
@ApiModel("waybill_code_no")
public class WaybillCodeNoResult {

    // 自增ID
    @ApiModelProperty("自增ID")
    private Long id;

    // 类型
    @ApiModelProperty("类型")
    private String type;

    // 组织编码
    @ApiModelProperty("组织编码")
    private String orgCode;

    // 年份
    @ApiModelProperty("年份")
    private Integer year;

    // 序列号
    @ApiModelProperty("序列号")
    private Long value;



    /**
     * set 自增ID
     * @param id 自增ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * get 自增ID
     * @return Long 自增ID
     */
    public Long getId() {
        return id;
    }


    /**
     * set 类型
     * @param type 类型
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * get 类型
     * @return String 类型
     */
    public String getType() {
        return type;
    }


    /**
     * set 组织编码
     * @param orgCode 组织编码
     */
    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    /**
     * get 组织编码
     * @return String 组织编码
     */
    public String getOrgCode() {
        return orgCode;
    }


    /**
     * set 年份
     * @param year 年份
     */
    public void setYear(Integer year) {
        this.year = year;
    }

    /**
     * get 年份
     * @return Integer 年份
     */
    public Integer getYear() {
        return year;
    }


    /**
     * set 序列号
     * @param value 序列号
     */
    public void setValue(Long value) {
        this.value = value;
    }

    /**
     * get 序列号
     * @return Long 序列号
     */
    public Long getValue() {
        return value;
    }


}
