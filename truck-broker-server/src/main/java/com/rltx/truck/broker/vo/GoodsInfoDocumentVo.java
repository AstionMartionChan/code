package com.rltx.truck.broker.vo;

/**
 * Created by Leo_Chan on 2017/3/20.
 */
public class GoodsInfoDocumentVo {
    /*
    name: 货物名称
    desc: 必填。
     */
    private String descriptionOfGoods;

     /*
    name: 货物类型分类代码
    desc: 必填。详见代码集4.2.5
     */
    private String cargoTypeClassificationCode;

     /*
    name: 货物项毛重
    desc: 必填。重量单位以KGM千克填写数值，保留3位小数，如整数的话，以.000填充。小数点不计入总长。
    如是轻泡货等货物，请估算重量。如是一笔业务分几辆车运，需报送每辆车实际运输的货物重量。
     */
    private Double goodsItemGrossWeight;

     /*
    name: 体积
    desc: 选填。体积单位以DMQ立方米填写数值，保留4位小数，如整数的话，以.0000填充。小数点不计入总长。
     */
    private Double cube;

     /*
    name: 总件数
    desc: 选填。
     */
    private Integer totalNumberOfPackages;


    public String getDescriptionOfGoods() {
        return descriptionOfGoods;
    }

    public void setDescriptionOfGoods(String descriptionOfGoods) {
        this.descriptionOfGoods = descriptionOfGoods;
    }

    public String getCargoTypeClassificationCode() {
        return cargoTypeClassificationCode;
    }

    public void setCargoTypeClassificationCode(String cargoTypeClassificationCode) {
        this.cargoTypeClassificationCode = cargoTypeClassificationCode;
    }

    public Double getGoodsItemGrossWeight() {
        return goodsItemGrossWeight;
    }

    public void setGoodsItemGrossWeight(Double goodsItemGrossWeight) {
        this.goodsItemGrossWeight = goodsItemGrossWeight;
    }

    public Double getCube() {
        return cube;
    }

    public void setCube(Double cube) {
        this.cube = cube;
    }

    public Integer getTotalNumberOfPackages() {
        return totalNumberOfPackages;
    }

    public void setTotalNumberOfPackages(Integer totalNumberOfPackages) {
        this.totalNumberOfPackages = totalNumberOfPackages;
    }
}
