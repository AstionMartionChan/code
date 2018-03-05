package com.rltx.waybill.result;

import java.util.Date;

/**
 * waybill_upstream_goods_packing_list
 */
@ApiModel("waybill_upstream_goods_packing_list")
public class WaybillUpstreamGoodsPackingListResult {

    // 自增ID
    @ApiModelProperty("自增ID")
    private Long id;

    // 货品清单编码
    @ApiModelProperty("货品清单编码")
    private String waybillGoodsPackingListCode;

    // 运单编码
    @ApiModelProperty("运单编码")
    private String waybillCode;

    // 货品名称
    @ApiModelProperty("货品名称")
    private String goodsName;

    // 货物单价
    @ApiModelProperty("货物单价")
    private Double unitPrice;

    // 货物单价单位编码
    @ApiModelProperty("货物单价单位编码")
    private String unitPriceUnitCode;

    // 数量
    @ApiModelProperty("数量")
    private Double numberQty;

    // 数量单位编码
    @ApiModelProperty("数量单位编码")
    private String numberQtyUnitCode;

    // 总重量
    @ApiModelProperty("总重量")
    private Double ttlWeight;

    // 总重量单位编码
    @ApiModelProperty("总重量单位编码")
    private String ttlWeightUnitCode;

    // 标准货量
    @ApiModelProperty("标准货量")
    private Double totalWeight;

    // 标准货量单位编码
    @ApiModelProperty("标准货量单位编码")
    private String totalWeightUnitCode;

    // 总体积
    @ApiModelProperty("总体积")
    private Double totalVolume;

    // 总体积单位编码
    @ApiModelProperty("总体积单位编码")
    private String totalVolumeUnitCode;

    // 描述
    @ApiModelProperty("描述")
    private String description;

    // 备注
    @ApiModelProperty("备注")
    private String remark;

    // 是否禁用
    @ApiModelProperty("是否禁用")
    private Boolean disabled;

    // 是否删除
    @ApiModelProperty("是否删除")
    private Boolean deleted;

    // 操作模块编码
    @ApiModelProperty("操作模块编码")
    private String moduleCode;

    // 创建人编码
    @ApiModelProperty("创建人编码")
    private String creatorUserCode;

    // 创建人用户名
    @ApiModelProperty("创建人用户名")
    private String creatorUsername;

    // 创建时间
    @ApiModelProperty("创建时间")
    private Date createTime;

    // 更新人编码
    @ApiModelProperty("更新人编码")
    private String updateUserCode;

    // 更新人用户名
    @ApiModelProperty("更新人用户名")
    private String updateUsername;

    // 更新时间
    @ApiModelProperty("更新时间")
    private Date updateTime;

    // 操作者IP
    @ApiModelProperty("操作者IP")
    private String ip;

    // 操作者所处经度
    @ApiModelProperty("操作者所处经度")
    private Double operatorLongitude;

    // 操作者所处纬度
    @ApiModelProperty("操作者所处纬度")
    private Double operatorLatitude;

    // 所属用户编码
    @ApiModelProperty("所属用户编码")
    private String ownerUserCode;

    // 所属公司编码
    @ApiModelProperty("所属公司编码")
    private String ownerOrgCode;

    // 所属公司名字
    @ApiModelProperty("所属公司名字")
    private String ownerOrgName;

    // 同步串号
    @ApiModelProperty("同步串号")
    private String synchronousId;



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
     * set 货品清单编码
     * @param waybillGoodsPackingListCode 货品清单编码
     */
    public void setWaybillGoodsPackingListCode(String waybillGoodsPackingListCode) {
        this.waybillGoodsPackingListCode = waybillGoodsPackingListCode;
    }

    /**
     * get 货品清单编码
     * @return String 货品清单编码
     */
    public String getWaybillGoodsPackingListCode() {
        return waybillGoodsPackingListCode;
    }


    /**
     * set 运单编码
     * @param waybillCode 运单编码
     */
    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    /**
     * get 运单编码
     * @return String 运单编码
     */
    public String getWaybillCode() {
        return waybillCode;
    }


    /**
     * set 货品名称
     * @param goodsName 货品名称
     */
    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    /**
     * get 货品名称
     * @return String 货品名称
     */
    public String getGoodsName() {
        return goodsName;
    }


    /**
     * set 货物单价
     * @param unitPrice 货物单价
     */
    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    /**
     * get 货物单价
     * @return Double 货物单价
     */
    public Double getUnitPrice() {
        return unitPrice;
    }


    /**
     * set 货物单价单位编码
     * @param unitPriceUnitCode 货物单价单位编码
     */
    public void setUnitPriceUnitCode(String unitPriceUnitCode) {
        this.unitPriceUnitCode = unitPriceUnitCode;
    }

    /**
     * get 货物单价单位编码
     * @return String 货物单价单位编码
     */
    public String getUnitPriceUnitCode() {
        return unitPriceUnitCode;
    }


    /**
     * set 数量
     * @param numberQty 数量
     */
    public void setNumberQty(Double numberQty) {
        this.numberQty = numberQty;
    }

    /**
     * get 数量
     * @return Double 数量
     */
    public Double getNumberQty() {
        return numberQty;
    }


    /**
     * set 数量单位编码
     * @param numberQtyUnitCode 数量单位编码
     */
    public void setNumberQtyUnitCode(String numberQtyUnitCode) {
        this.numberQtyUnitCode = numberQtyUnitCode;
    }

    /**
     * get 数量单位编码
     * @return String 数量单位编码
     */
    public String getNumberQtyUnitCode() {
        return numberQtyUnitCode;
    }


    /**
     * set 总重量
     * @param ttlWeight 总重量
     */
    public void setTtlWeight(Double ttlWeight) {
        this.ttlWeight = ttlWeight;
    }

    /**
     * get 总重量
     * @return Double 总重量
     */
    public Double getTtlWeight() {
        return ttlWeight;
    }


    /**
     * set 总重量单位编码
     * @param ttlWeightUnitCode 总重量单位编码
     */
    public void setTtlWeightUnitCode(String ttlWeightUnitCode) {
        this.ttlWeightUnitCode = ttlWeightUnitCode;
    }

    /**
     * get 总重量单位编码
     * @return String 总重量单位编码
     */
    public String getTtlWeightUnitCode() {
        return ttlWeightUnitCode;
    }


    /**
     * set 标准货量
     * @param totalWeight 标准货量
     */
    public void setTotalWeight(Double totalWeight) {
        this.totalWeight = totalWeight;
    }

    /**
     * get 标准货量
     * @return Double 标准货量
     */
    public Double getTotalWeight() {
        return totalWeight;
    }


    /**
     * set 标准货量单位编码
     * @param totalWeightUnitCode 标准货量单位编码
     */
    public void setTotalWeightUnitCode(String totalWeightUnitCode) {
        this.totalWeightUnitCode = totalWeightUnitCode;
    }

    /**
     * get 标准货量单位编码
     * @return String 标准货量单位编码
     */
    public String getTotalWeightUnitCode() {
        return totalWeightUnitCode;
    }


    /**
     * set 总体积
     * @param totalVolume 总体积
     */
    public void setTotalVolume(Double totalVolume) {
        this.totalVolume = totalVolume;
    }

    /**
     * get 总体积
     * @return Double 总体积
     */
    public Double getTotalVolume() {
        return totalVolume;
    }


    /**
     * set 总体积单位编码
     * @param totalVolumeUnitCode 总体积单位编码
     */
    public void setTotalVolumeUnitCode(String totalVolumeUnitCode) {
        this.totalVolumeUnitCode = totalVolumeUnitCode;
    }

    /**
     * get 总体积单位编码
     * @return String 总体积单位编码
     */
    public String getTotalVolumeUnitCode() {
        return totalVolumeUnitCode;
    }


    /**
     * set 描述
     * @param description 描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * get 描述
     * @return String 描述
     */
    public String getDescription() {
        return description;
    }


    /**
     * set 备注
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * get 备注
     * @return String 备注
     */
    public String getRemark() {
        return remark;
    }


    /**
     * set 是否禁用
     * @param disabled 是否禁用
     */
    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * get 是否禁用
     * @return Boolean 是否禁用
     */
    public Boolean getDisabled() {
        return disabled;
    }


    /**
     * set 是否删除
     * @param deleted 是否删除
     */
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * get 是否删除
     * @return Boolean 是否删除
     */
    public Boolean getDeleted() {
        return deleted;
    }


    /**
     * set 操作模块编码
     * @param moduleCode 操作模块编码
     */
    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    /**
     * get 操作模块编码
     * @return String 操作模块编码
     */
    public String getModuleCode() {
        return moduleCode;
    }


    /**
     * set 创建人编码
     * @param creatorUserCode 创建人编码
     */
    public void setCreatorUserCode(String creatorUserCode) {
        this.creatorUserCode = creatorUserCode;
    }

    /**
     * get 创建人编码
     * @return String 创建人编码
     */
    public String getCreatorUserCode() {
        return creatorUserCode;
    }


    /**
     * set 创建人用户名
     * @param creatorUsername 创建人用户名
     */
    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }

    /**
     * get 创建人用户名
     * @return String 创建人用户名
     */
    public String getCreatorUsername() {
        return creatorUsername;
    }


    /**
     * set 创建时间
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * get 创建时间
     * @return Date 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }


    /**
     * set 更新人编码
     * @param updateUserCode 更新人编码
     */
    public void setUpdateUserCode(String updateUserCode) {
        this.updateUserCode = updateUserCode;
    }

    /**
     * get 更新人编码
     * @return String 更新人编码
     */
    public String getUpdateUserCode() {
        return updateUserCode;
    }


    /**
     * set 更新人用户名
     * @param updateUsername 更新人用户名
     */
    public void setUpdateUsername(String updateUsername) {
        this.updateUsername = updateUsername;
    }

    /**
     * get 更新人用户名
     * @return String 更新人用户名
     */
    public String getUpdateUsername() {
        return updateUsername;
    }


    /**
     * set 更新时间
     * @param updateTime 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * get 更新时间
     * @return Date 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }


    /**
     * set 操作者IP
     * @param ip 操作者IP
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * get 操作者IP
     * @return String 操作者IP
     */
    public String getIp() {
        return ip;
    }


    /**
     * set 操作者所处经度
     * @param operatorLongitude 操作者所处经度
     */
    public void setOperatorLongitude(Double operatorLongitude) {
        this.operatorLongitude = operatorLongitude;
    }

    /**
     * get 操作者所处经度
     * @return Double 操作者所处经度
     */
    public Double getOperatorLongitude() {
        return operatorLongitude;
    }


    /**
     * set 操作者所处纬度
     * @param operatorLatitude 操作者所处纬度
     */
    public void setOperatorLatitude(Double operatorLatitude) {
        this.operatorLatitude = operatorLatitude;
    }

    /**
     * get 操作者所处纬度
     * @return Double 操作者所处纬度
     */
    public Double getOperatorLatitude() {
        return operatorLatitude;
    }


    /**
     * set 所属用户编码
     * @param ownerUserCode 所属用户编码
     */
    public void setOwnerUserCode(String ownerUserCode) {
        this.ownerUserCode = ownerUserCode;
    }

    /**
     * get 所属用户编码
     * @return String 所属用户编码
     */
    public String getOwnerUserCode() {
        return ownerUserCode;
    }


    /**
     * set 所属公司编码
     * @param ownerOrgCode 所属公司编码
     */
    public void setOwnerOrgCode(String ownerOrgCode) {
        this.ownerOrgCode = ownerOrgCode;
    }

    /**
     * get 所属公司编码
     * @return String 所属公司编码
     */
    public String getOwnerOrgCode() {
        return ownerOrgCode;
    }


    /**
     * set 所属公司名字
     * @param ownerOrgName 所属公司名字
     */
    public void setOwnerOrgName(String ownerOrgName) {
        this.ownerOrgName = ownerOrgName;
    }

    /**
     * get 所属公司名字
     * @return String 所属公司名字
     */
    public String getOwnerOrgName() {
        return ownerOrgName;
    }


    /**
     * set 同步串号
     * @param synchronousId 同步串号
     */
    public void setSynchronousId(String synchronousId) {
        this.synchronousId = synchronousId;
    }

    /**
     * get 同步串号
     * @return String 同步串号
     */
    public String getSynchronousId() {
        return synchronousId;
    }


}
