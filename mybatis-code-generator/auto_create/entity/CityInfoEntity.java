package com.cfy.po;

import java.util.Date;

/**
 * city_info 表 Entity
 */
public class CityInfoEntity {

    // 
    private Integer cityId;

    // 
    private String cityName;

    // 
    private String area;



    /**
     * set 
     * @param cityId 
     */
    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    /**
     * get 
     * @return Integer 
     */
    public Integer getCityId() {
        return cityId;
    }


    /**
     * set 
     * @param cityName 
     */
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    /**
     * get 
     * @return String 
     */
    public String getCityName() {
        return cityName;
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


}
