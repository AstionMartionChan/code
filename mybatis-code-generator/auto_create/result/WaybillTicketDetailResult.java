package com.rltx.waybill.result;

import java.util.Date;

/**
 * waybill_ticket_detail
 */
@ApiModel("waybill_ticket_detail")
public class WaybillTicketDetailResult {

    // id
    @ApiModelProperty("id")
    private Long id;

    // 货单编码
    @ApiModelProperty("货单编码")
    private String code;

    // 运单编码
    @ApiModelProperty("运单编码")
    private String waybillCode;

    // 配货单号
    @ApiModelProperty("配货单号")
    private String distributionNo;

    // 单号
    @ApiModelProperty("单号")
    private String ticketNo;

    // 货物名称
    @ApiModelProperty("货物名称")
    private String goodsName;

    // 重量
    @ApiModelProperty("重量")
    private Double ttlWeight;

    // 重量单位
    @ApiModelProperty("重量单位")
    private Long ttlWeightUnit;

    // 体积
    @ApiModelProperty("体积")
    private Double ttlVolume;

    // 体积单位
    @ApiModelProperty("体积单位")
    private Long ttlVolumeUnit;

    // 数量
    @ApiModelProperty("数量")
    private Double ttlNumber;

    // 数量单位
    @ApiModelProperty("数量单位")
    private Long ttlNumberUnit;

    // 晚到车索赔
    @ApiModelProperty("晚到车索赔")
    private Double wdcClaim;

    // 晚到货索赔
    @ApiModelProperty("晚到货索赔")
    private Double wdhClaim;

    // 晚回单索赔
    @ApiModelProperty("晚回单索赔")
    private Double whdClaim;

    // 箱破损索赔
    @ApiModelProperty("箱破损索赔")
    private Double xpsClaim;

    // 其他索赔
    @ApiModelProperty("其他索赔")
    private Double otherClaim;

    // 索赔备注
    @ApiModelProperty("索赔备注")
    private String claimDescription;

    // 状态（1、正常，2、异常冻结，3、异常释放）
    @ApiModelProperty("状态（1、正常，2、异常冻结，3、异常释放）")
    private Integer status;

    // 自定义属性
    @ApiModelProperty("自定义属性")
    private String customData;

    // 标签
    @ApiModelProperty("标签")
    private String ticketTag;

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
     * set id
     * @param id id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * get id
     * @return Long id
     */
    public Long getId() {
        return id;
    }


    /**
     * set 货单编码
     * @param code 货单编码
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * get 货单编码
     * @return String 货单编码
     */
    public String getCode() {
        return code;
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
     * set 配货单号
     * @param distributionNo 配货单号
     */
    public void setDistributionNo(String distributionNo) {
        this.distributionNo = distributionNo;
    }

    /**
     * get 配货单号
     * @return String 配货单号
     */
    public String getDistributionNo() {
        return distributionNo;
    }


    /**
     * set 单号
     * @param ticketNo 单号
     */
    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    /**
     * get 单号
     * @return String 单号
     */
    public String getTicketNo() {
        return ticketNo;
    }


    /**
     * set 货物名称
     * @param goodsName 货物名称
     */
    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    /**
     * get 货物名称
     * @return String 货物名称
     */
    public String getGoodsName() {
        return goodsName;
    }


    /**
     * set 重量
     * @param ttlWeight 重量
     */
    public void setTtlWeight(Double ttlWeight) {
        this.ttlWeight = ttlWeight;
    }

    /**
     * get 重量
     * @return Double 重量
     */
    public Double getTtlWeight() {
        return ttlWeight;
    }


    /**
     * set 重量单位
     * @param ttlWeightUnit 重量单位
     */
    public void setTtlWeightUnit(Long ttlWeightUnit) {
        this.ttlWeightUnit = ttlWeightUnit;
    }

    /**
     * get 重量单位
     * @return Long 重量单位
     */
    public Long getTtlWeightUnit() {
        return ttlWeightUnit;
    }


    /**
     * set 体积
     * @param ttlVolume 体积
     */
    public void setTtlVolume(Double ttlVolume) {
        this.ttlVolume = ttlVolume;
    }

    /**
     * get 体积
     * @return Double 体积
     */
    public Double getTtlVolume() {
        return ttlVolume;
    }


    /**
     * set 体积单位
     * @param ttlVolumeUnit 体积单位
     */
    public void setTtlVolumeUnit(Long ttlVolumeUnit) {
        this.ttlVolumeUnit = ttlVolumeUnit;
    }

    /**
     * get 体积单位
     * @return Long 体积单位
     */
    public Long getTtlVolumeUnit() {
        return ttlVolumeUnit;
    }


    /**
     * set 数量
     * @param ttlNumber 数量
     */
    public void setTtlNumber(Double ttlNumber) {
        this.ttlNumber = ttlNumber;
    }

    /**
     * get 数量
     * @return Double 数量
     */
    public Double getTtlNumber() {
        return ttlNumber;
    }


    /**
     * set 数量单位
     * @param ttlNumberUnit 数量单位
     */
    public void setTtlNumberUnit(Long ttlNumberUnit) {
        this.ttlNumberUnit = ttlNumberUnit;
    }

    /**
     * get 数量单位
     * @return Long 数量单位
     */
    public Long getTtlNumberUnit() {
        return ttlNumberUnit;
    }


    /**
     * set 晚到车索赔
     * @param wdcClaim 晚到车索赔
     */
    public void setWdcClaim(Double wdcClaim) {
        this.wdcClaim = wdcClaim;
    }

    /**
     * get 晚到车索赔
     * @return Double 晚到车索赔
     */
    public Double getWdcClaim() {
        return wdcClaim;
    }


    /**
     * set 晚到货索赔
     * @param wdhClaim 晚到货索赔
     */
    public void setWdhClaim(Double wdhClaim) {
        this.wdhClaim = wdhClaim;
    }

    /**
     * get 晚到货索赔
     * @return Double 晚到货索赔
     */
    public Double getWdhClaim() {
        return wdhClaim;
    }


    /**
     * set 晚回单索赔
     * @param whdClaim 晚回单索赔
     */
    public void setWhdClaim(Double whdClaim) {
        this.whdClaim = whdClaim;
    }

    /**
     * get 晚回单索赔
     * @return Double 晚回单索赔
     */
    public Double getWhdClaim() {
        return whdClaim;
    }


    /**
     * set 箱破损索赔
     * @param xpsClaim 箱破损索赔
     */
    public void setXpsClaim(Double xpsClaim) {
        this.xpsClaim = xpsClaim;
    }

    /**
     * get 箱破损索赔
     * @return Double 箱破损索赔
     */
    public Double getXpsClaim() {
        return xpsClaim;
    }


    /**
     * set 其他索赔
     * @param otherClaim 其他索赔
     */
    public void setOtherClaim(Double otherClaim) {
        this.otherClaim = otherClaim;
    }

    /**
     * get 其他索赔
     * @return Double 其他索赔
     */
    public Double getOtherClaim() {
        return otherClaim;
    }


    /**
     * set 索赔备注
     * @param claimDescription 索赔备注
     */
    public void setClaimDescription(String claimDescription) {
        this.claimDescription = claimDescription;
    }

    /**
     * get 索赔备注
     * @return String 索赔备注
     */
    public String getClaimDescription() {
        return claimDescription;
    }


    /**
     * set 状态（1、正常，2、异常冻结，3、异常释放）
     * @param status 状态（1、正常，2、异常冻结，3、异常释放）
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * get 状态（1、正常，2、异常冻结，3、异常释放）
     * @return Integer 状态（1、正常，2、异常冻结，3、异常释放）
     */
    public Integer getStatus() {
        return status;
    }


    /**
     * set 自定义属性
     * @param customData 自定义属性
     */
    public void setCustomData(String customData) {
        this.customData = customData;
    }

    /**
     * get 自定义属性
     * @return String 自定义属性
     */
    public String getCustomData() {
        return customData;
    }


    /**
     * set 标签
     * @param ticketTag 标签
     */
    public void setTicketTag(String ticketTag) {
        this.ticketTag = ticketTag;
    }

    /**
     * get 标签
     * @return String 标签
     */
    public String getTicketTag() {
        return ticketTag;
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
