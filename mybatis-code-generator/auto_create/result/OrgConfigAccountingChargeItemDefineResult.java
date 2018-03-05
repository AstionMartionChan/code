package com.rltx.org.config.result;

import java.util.Date;

/**
 * org_config_accounting_charge_item_define
 */
@ApiModel("org_config_accounting_charge_item_define")
public class OrgConfigAccountingChargeItemDefineResult {

    // 自增ID
    @ApiModelProperty("自增ID")
    private Long id;

    // 费用科目代码
    @ApiModelProperty("费用科目代码")
    private String code;

    // 费用科目名称
    @ApiModelProperty("费用科目名称")
    private String chargeItemName;

    // 记账数量单位编码
    @ApiModelProperty("记账数量单位编码")
    private String chargeItemNumberUnitCode;

    // 记账数量单位名称
    @ApiModelProperty("记账数量单位名称")
    private String chargeItemNumberUnitName;

    // 记账单价单位编码
    @ApiModelProperty("记账单价单位编码")
    private String chargeItemPriceUnitCode;

    // 记账单价单位名称
    @ApiModelProperty("记账单价单位名称")
    private String chargeItemPriceUnitName;

    // 记账金额计量单位编码
    @ApiModelProperty("记账金额计量单位编码")
    private String chargeItemAmountsUnitCode;

    // 记账金额计量单位名称
    @ApiModelProperty("记账金额计量单位名称")
    private String chargeItemAmountsUnitName;

    // 往来户说明（例如：中石油、交警、路霸、高管处….文字描述，可做自动完成辅助输入）
    @ApiModelProperty("往来户说明（例如：中石油、交警、路霸、高管处….文字描述，可做自动完成辅助输入）")
    private String usageDesc;

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
     * set 费用科目代码
     * @param code 费用科目代码
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * get 费用科目代码
     * @return String 费用科目代码
     */
    public String getCode() {
        return code;
    }


    /**
     * set 费用科目名称
     * @param chargeItemName 费用科目名称
     */
    public void setChargeItemName(String chargeItemName) {
        this.chargeItemName = chargeItemName;
    }

    /**
     * get 费用科目名称
     * @return String 费用科目名称
     */
    public String getChargeItemName() {
        return chargeItemName;
    }


    /**
     * set 记账数量单位编码
     * @param chargeItemNumberUnitCode 记账数量单位编码
     */
    public void setChargeItemNumberUnitCode(String chargeItemNumberUnitCode) {
        this.chargeItemNumberUnitCode = chargeItemNumberUnitCode;
    }

    /**
     * get 记账数量单位编码
     * @return String 记账数量单位编码
     */
    public String getChargeItemNumberUnitCode() {
        return chargeItemNumberUnitCode;
    }


    /**
     * set 记账数量单位名称
     * @param chargeItemNumberUnitName 记账数量单位名称
     */
    public void setChargeItemNumberUnitName(String chargeItemNumberUnitName) {
        this.chargeItemNumberUnitName = chargeItemNumberUnitName;
    }

    /**
     * get 记账数量单位名称
     * @return String 记账数量单位名称
     */
    public String getChargeItemNumberUnitName() {
        return chargeItemNumberUnitName;
    }


    /**
     * set 记账单价单位编码
     * @param chargeItemPriceUnitCode 记账单价单位编码
     */
    public void setChargeItemPriceUnitCode(String chargeItemPriceUnitCode) {
        this.chargeItemPriceUnitCode = chargeItemPriceUnitCode;
    }

    /**
     * get 记账单价单位编码
     * @return String 记账单价单位编码
     */
    public String getChargeItemPriceUnitCode() {
        return chargeItemPriceUnitCode;
    }


    /**
     * set 记账单价单位名称
     * @param chargeItemPriceUnitName 记账单价单位名称
     */
    public void setChargeItemPriceUnitName(String chargeItemPriceUnitName) {
        this.chargeItemPriceUnitName = chargeItemPriceUnitName;
    }

    /**
     * get 记账单价单位名称
     * @return String 记账单价单位名称
     */
    public String getChargeItemPriceUnitName() {
        return chargeItemPriceUnitName;
    }


    /**
     * set 记账金额计量单位编码
     * @param chargeItemAmountsUnitCode 记账金额计量单位编码
     */
    public void setChargeItemAmountsUnitCode(String chargeItemAmountsUnitCode) {
        this.chargeItemAmountsUnitCode = chargeItemAmountsUnitCode;
    }

    /**
     * get 记账金额计量单位编码
     * @return String 记账金额计量单位编码
     */
    public String getChargeItemAmountsUnitCode() {
        return chargeItemAmountsUnitCode;
    }


    /**
     * set 记账金额计量单位名称
     * @param chargeItemAmountsUnitName 记账金额计量单位名称
     */
    public void setChargeItemAmountsUnitName(String chargeItemAmountsUnitName) {
        this.chargeItemAmountsUnitName = chargeItemAmountsUnitName;
    }

    /**
     * get 记账金额计量单位名称
     * @return String 记账金额计量单位名称
     */
    public String getChargeItemAmountsUnitName() {
        return chargeItemAmountsUnitName;
    }


    /**
     * set 往来户说明（例如：中石油、交警、路霸、高管处….文字描述，可做自动完成辅助输入）
     * @param usageDesc 往来户说明（例如：中石油、交警、路霸、高管处….文字描述，可做自动完成辅助输入）
     */
    public void setUsageDesc(String usageDesc) {
        this.usageDesc = usageDesc;
    }

    /**
     * get 往来户说明（例如：中石油、交警、路霸、高管处….文字描述，可做自动完成辅助输入）
     * @return String 往来户说明（例如：中石油、交警、路霸、高管处….文字描述，可做自动完成辅助输入）
     */
    public String getUsageDesc() {
        return usageDesc;
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
