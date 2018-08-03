package com.cfy.po;

import java.util.Date;

/**
 * area_top3_product 表 Entity
 */
public class AreaTop3ProductEntity {

    // 
    private Long id;

    // 
    private Long taskId;

    // 
    private String area;

    // 
    private String areaLevel;

    // 
    private Long productId;

    // 
    private String cityNames;

    // 
    private Integer clickCount;

    // 
    private String productName;

    // 
    private String productStatus;



    /**
     * set 
     * @param id 
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * get 
     * @return Long 
     */
    public Long getId() {
        return id;
    }


    /**
     * set 
     * @param taskId 
     */
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    /**
     * get 
     * @return Long 
     */
    public Long getTaskId() {
        return taskId;
    }


    /**
     * set 
     * @param area 
     */
    public void setArea(String area) {
        this.area = area;
    }

    /**
     * get 
     * @return String 
     */
    public String getArea() {
        return area;
    }


    /**
     * set 
     * @param areaLevel 
     */
    public void setAreaLevel(String areaLevel) {
        this.areaLevel = areaLevel;
    }

    /**
     * get 
     * @return String 
     */
    public String getAreaLevel() {
        return areaLevel;
    }


    /**
     * set 
     * @param productId 
     */
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    /**
     * get 
     * @return Long 
     */
    public Long getProductId() {
        return productId;
    }


    /**
     * set 
     * @param cityNames 
     */
    public void setCityNames(String cityNames) {
        this.cityNames = cityNames;
    }

    /**
     * get 
     * @return String 
     */
    public String getCityNames() {
        return cityNames;
    }


    /**
     * set 
     * @param clickCount 
     */
    public void setClickCount(Integer clickCount) {
        this.clickCount = clickCount;
    }

    /**
     * get 
     * @return Integer 
     */
    public Integer getClickCount() {
        return clickCount;
    }


    /**
     * set 
     * @param productName 
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * get 
     * @return String 
     */
    public String getProductName() {
        return productName;
    }


    /**
     * set 
     * @param productStatus 
     */
    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }

    /**
     * get 
     * @return String 
     */
    public String getProductStatus() {
        return productStatus;
    }


}
