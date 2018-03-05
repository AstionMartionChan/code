package com.rltx.waybill.result;

import java.util.Date;

/**
 * waybill_paybill
 */
@ApiModel("waybill_paybill")
public class WaybillPaybillResult {

    // 付款单ID
    @ApiModelProperty("付款单ID")
    private Long id;

    // 编码
    @ApiModelProperty("编码")
    private String code;

    // 付款单号
    @ApiModelProperty("付款单号")
    private String paymentNo;

    // 运单编码
    @ApiModelProperty("运单编码")
    private String waybillCode;

    // 运单号
    @ApiModelProperty("运单号")
    private String waybillNo;

    // 结算类型（1、外协结算，2、内部结算）
    @ApiModelProperty("结算类型（1、外协结算，2、内部结算）")
    private Integer billingClass;

    // 最近一次打印编号
    @ApiModelProperty("最近一次打印编号")
    private String lastPrintNo;

    // 应付运费
    @ApiModelProperty("应付运费")
    private Double confirmedTranFee;

    // 应付运费货币单位编码
    @ApiModelProperty("应付运费货币单位编码")
    private String tranFeeUnitCode;

    // 应付行车费
    @ApiModelProperty("应付行车费")
    private Double confirmedDrivingFee;

    // 应付行车费计量单位编码
    @ApiModelProperty("应付行车费计量单位编码")
    private String confirmedDrivingFeeUnitCode;

    // 应付总费用(=实际结算总费用=实际结算运费+实际结算行车费)
    @ApiModelProperty("应付总费用(=实际结算总费用=实际结算运费+实际结算行车费)")
    private Double confirmedTotalFee;

    // 应付总费用货币单位编码
    @ApiModelProperty("应付总费用货币单位编码")
    private String totalFeeUnitCode;

    // 应付总费用货币单位名称
    @ApiModelProperty("应付总费用货币单位名称")
    private String totalFeeUnitName;

    // 实付总费用
    @ApiModelProperty("实付总费用")
    private Double actualFee;

    // 实付总费用货币单位编码
    @ApiModelProperty("实付总费用货币单位编码")
    private String actualFeeUnitCode;

    // 实付总费用货币单位名称
    @ApiModelProperty("实付总费用货币单位名称")
    private String actualFeeUnitName;

    // 付款方式（1.现金 2.转账 3.平台在线支付）
    @ApiModelProperty("付款方式（1.现金 2.转账 3.平台在线支付）")
    private Integer payMethod;

    // 付款状态：1.付款中/2.付款完成/3.付款已作废/4.付款异常
    @ApiModelProperty("付款状态：1.付款中/2.付款完成/3.付款已作废/4.付款异常")
    private Integer payStatus;

    // 请款组织编码
    @ApiModelProperty("请款组织编码")
    private String payRequestOrgCode;

    // 请款组织名称
    @ApiModelProperty("请款组织名称")
    private String payRequestOrgName;

    // 请款人编码（当前操作人）
    @ApiModelProperty("请款人编码（当前操作人）")
    private String payRequestUserCode;

    // 请款人姓名
    @ApiModelProperty("请款人姓名")
    private String payRequestUserFullName;

    // 请款时间
    @ApiModelProperty("请款时间")
    private Date payRequestTime;

    // 付款组织编码
    @ApiModelProperty("付款组织编码")
    private String payerOrgCode;

    // 付款组织名称
    @ApiModelProperty("付款组织名称")
    private String payerOrgName;

    // 付款操作人编码
    @ApiModelProperty("付款操作人编码")
    private String payerOpCode;

    // 付款操作人姓名
    @ApiModelProperty("付款操作人姓名")
    private String payerOpFullName;

    // 付款操作时间
    @ApiModelProperty("付款操作时间")
    private Date payerOpTime;

    // 收款人编码
    @ApiModelProperty("收款人编码")
    private String payeeUserCode;

    // 收款人姓名
    @ApiModelProperty("收款人姓名")
    private String payeeUserFullName;

    // 收款人开户行名称
    @ApiModelProperty("收款人开户行名称")
    private String payeeBankName;

    // 收款人开户支行名称
    @ApiModelProperty("收款人开户支行名称")
    private String payeeBankBranchName;

    // 收款人银行开户人姓名
    @ApiModelProperty("收款人银行开户人姓名")
    private String payeeBankAccountFullName;

    // 收款人银行账号
    @ApiModelProperty("收款人银行账号")
    private String payeeBankAccountNo;

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
     * set 付款单ID
     * @param id 付款单ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * get 付款单ID
     * @return Long 付款单ID
     */
    public Long getId() {
        return id;
    }


    /**
     * set 编码
     * @param code 编码
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * get 编码
     * @return String 编码
     */
    public String getCode() {
        return code;
    }


    /**
     * set 付款单号
     * @param paymentNo 付款单号
     */
    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
    }

    /**
     * get 付款单号
     * @return String 付款单号
     */
    public String getPaymentNo() {
        return paymentNo;
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
     * set 运单号
     * @param waybillNo 运单号
     */
    public void setWaybillNo(String waybillNo) {
        this.waybillNo = waybillNo;
    }

    /**
     * get 运单号
     * @return String 运单号
     */
    public String getWaybillNo() {
        return waybillNo;
    }


    /**
     * set 结算类型（1、外协结算，2、内部结算）
     * @param billingClass 结算类型（1、外协结算，2、内部结算）
     */
    public void setBillingClass(Integer billingClass) {
        this.billingClass = billingClass;
    }

    /**
     * get 结算类型（1、外协结算，2、内部结算）
     * @return Integer 结算类型（1、外协结算，2、内部结算）
     */
    public Integer getBillingClass() {
        return billingClass;
    }


    /**
     * set 最近一次打印编号
     * @param lastPrintNo 最近一次打印编号
     */
    public void setLastPrintNo(String lastPrintNo) {
        this.lastPrintNo = lastPrintNo;
    }

    /**
     * get 最近一次打印编号
     * @return String 最近一次打印编号
     */
    public String getLastPrintNo() {
        return lastPrintNo;
    }


    /**
     * set 应付运费
     * @param confirmedTranFee 应付运费
     */
    public void setConfirmedTranFee(Double confirmedTranFee) {
        this.confirmedTranFee = confirmedTranFee;
    }

    /**
     * get 应付运费
     * @return Double 应付运费
     */
    public Double getConfirmedTranFee() {
        return confirmedTranFee;
    }


    /**
     * set 应付运费货币单位编码
     * @param tranFeeUnitCode 应付运费货币单位编码
     */
    public void setTranFeeUnitCode(String tranFeeUnitCode) {
        this.tranFeeUnitCode = tranFeeUnitCode;
    }

    /**
     * get 应付运费货币单位编码
     * @return String 应付运费货币单位编码
     */
    public String getTranFeeUnitCode() {
        return tranFeeUnitCode;
    }


    /**
     * set 应付行车费
     * @param confirmedDrivingFee 应付行车费
     */
    public void setConfirmedDrivingFee(Double confirmedDrivingFee) {
        this.confirmedDrivingFee = confirmedDrivingFee;
    }

    /**
     * get 应付行车费
     * @return Double 应付行车费
     */
    public Double getConfirmedDrivingFee() {
        return confirmedDrivingFee;
    }


    /**
     * set 应付行车费计量单位编码
     * @param confirmedDrivingFeeUnitCode 应付行车费计量单位编码
     */
    public void setConfirmedDrivingFeeUnitCode(String confirmedDrivingFeeUnitCode) {
        this.confirmedDrivingFeeUnitCode = confirmedDrivingFeeUnitCode;
    }

    /**
     * get 应付行车费计量单位编码
     * @return String 应付行车费计量单位编码
     */
    public String getConfirmedDrivingFeeUnitCode() {
        return confirmedDrivingFeeUnitCode;
    }


    /**
     * set 应付总费用(=实际结算总费用=实际结算运费+实际结算行车费)
     * @param confirmedTotalFee 应付总费用(=实际结算总费用=实际结算运费+实际结算行车费)
     */
    public void setConfirmedTotalFee(Double confirmedTotalFee) {
        this.confirmedTotalFee = confirmedTotalFee;
    }

    /**
     * get 应付总费用(=实际结算总费用=实际结算运费+实际结算行车费)
     * @return Double 应付总费用(=实际结算总费用=实际结算运费+实际结算行车费)
     */
    public Double getConfirmedTotalFee() {
        return confirmedTotalFee;
    }


    /**
     * set 应付总费用货币单位编码
     * @param totalFeeUnitCode 应付总费用货币单位编码
     */
    public void setTotalFeeUnitCode(String totalFeeUnitCode) {
        this.totalFeeUnitCode = totalFeeUnitCode;
    }

    /**
     * get 应付总费用货币单位编码
     * @return String 应付总费用货币单位编码
     */
    public String getTotalFeeUnitCode() {
        return totalFeeUnitCode;
    }


    /**
     * set 应付总费用货币单位名称
     * @param totalFeeUnitName 应付总费用货币单位名称
     */
    public void setTotalFeeUnitName(String totalFeeUnitName) {
        this.totalFeeUnitName = totalFeeUnitName;
    }

    /**
     * get 应付总费用货币单位名称
     * @return String 应付总费用货币单位名称
     */
    public String getTotalFeeUnitName() {
        return totalFeeUnitName;
    }


    /**
     * set 实付总费用
     * @param actualFee 实付总费用
     */
    public void setActualFee(Double actualFee) {
        this.actualFee = actualFee;
    }

    /**
     * get 实付总费用
     * @return Double 实付总费用
     */
    public Double getActualFee() {
        return actualFee;
    }


    /**
     * set 实付总费用货币单位编码
     * @param actualFeeUnitCode 实付总费用货币单位编码
     */
    public void setActualFeeUnitCode(String actualFeeUnitCode) {
        this.actualFeeUnitCode = actualFeeUnitCode;
    }

    /**
     * get 实付总费用货币单位编码
     * @return String 实付总费用货币单位编码
     */
    public String getActualFeeUnitCode() {
        return actualFeeUnitCode;
    }


    /**
     * set 实付总费用货币单位名称
     * @param actualFeeUnitName 实付总费用货币单位名称
     */
    public void setActualFeeUnitName(String actualFeeUnitName) {
        this.actualFeeUnitName = actualFeeUnitName;
    }

    /**
     * get 实付总费用货币单位名称
     * @return String 实付总费用货币单位名称
     */
    public String getActualFeeUnitName() {
        return actualFeeUnitName;
    }


    /**
     * set 付款方式（1.现金 2.转账 3.平台在线支付）
     * @param payMethod 付款方式（1.现金 2.转账 3.平台在线支付）
     */
    public void setPayMethod(Integer payMethod) {
        this.payMethod = payMethod;
    }

    /**
     * get 付款方式（1.现金 2.转账 3.平台在线支付）
     * @return Integer 付款方式（1.现金 2.转账 3.平台在线支付）
     */
    public Integer getPayMethod() {
        return payMethod;
    }


    /**
     * set 付款状态：1.付款中/2.付款完成/3.付款已作废/4.付款异常
     * @param payStatus 付款状态：1.付款中/2.付款完成/3.付款已作废/4.付款异常
     */
    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    /**
     * get 付款状态：1.付款中/2.付款完成/3.付款已作废/4.付款异常
     * @return Integer 付款状态：1.付款中/2.付款完成/3.付款已作废/4.付款异常
     */
    public Integer getPayStatus() {
        return payStatus;
    }


    /**
     * set 请款组织编码
     * @param payRequestOrgCode 请款组织编码
     */
    public void setPayRequestOrgCode(String payRequestOrgCode) {
        this.payRequestOrgCode = payRequestOrgCode;
    }

    /**
     * get 请款组织编码
     * @return String 请款组织编码
     */
    public String getPayRequestOrgCode() {
        return payRequestOrgCode;
    }


    /**
     * set 请款组织名称
     * @param payRequestOrgName 请款组织名称
     */
    public void setPayRequestOrgName(String payRequestOrgName) {
        this.payRequestOrgName = payRequestOrgName;
    }

    /**
     * get 请款组织名称
     * @return String 请款组织名称
     */
    public String getPayRequestOrgName() {
        return payRequestOrgName;
    }


    /**
     * set 请款人编码（当前操作人）
     * @param payRequestUserCode 请款人编码（当前操作人）
     */
    public void setPayRequestUserCode(String payRequestUserCode) {
        this.payRequestUserCode = payRequestUserCode;
    }

    /**
     * get 请款人编码（当前操作人）
     * @return String 请款人编码（当前操作人）
     */
    public String getPayRequestUserCode() {
        return payRequestUserCode;
    }


    /**
     * set 请款人姓名
     * @param payRequestUserFullName 请款人姓名
     */
    public void setPayRequestUserFullName(String payRequestUserFullName) {
        this.payRequestUserFullName = payRequestUserFullName;
    }

    /**
     * get 请款人姓名
     * @return String 请款人姓名
     */
    public String getPayRequestUserFullName() {
        return payRequestUserFullName;
    }


    /**
     * set 请款时间
     * @param payRequestTime 请款时间
     */
    public void setPayRequestTime(Date payRequestTime) {
        this.payRequestTime = payRequestTime;
    }

    /**
     * get 请款时间
     * @return Date 请款时间
     */
    public Date getPayRequestTime() {
        return payRequestTime;
    }


    /**
     * set 付款组织编码
     * @param payerOrgCode 付款组织编码
     */
    public void setPayerOrgCode(String payerOrgCode) {
        this.payerOrgCode = payerOrgCode;
    }

    /**
     * get 付款组织编码
     * @return String 付款组织编码
     */
    public String getPayerOrgCode() {
        return payerOrgCode;
    }


    /**
     * set 付款组织名称
     * @param payerOrgName 付款组织名称
     */
    public void setPayerOrgName(String payerOrgName) {
        this.payerOrgName = payerOrgName;
    }

    /**
     * get 付款组织名称
     * @return String 付款组织名称
     */
    public String getPayerOrgName() {
        return payerOrgName;
    }


    /**
     * set 付款操作人编码
     * @param payerOpCode 付款操作人编码
     */
    public void setPayerOpCode(String payerOpCode) {
        this.payerOpCode = payerOpCode;
    }

    /**
     * get 付款操作人编码
     * @return String 付款操作人编码
     */
    public String getPayerOpCode() {
        return payerOpCode;
    }


    /**
     * set 付款操作人姓名
     * @param payerOpFullName 付款操作人姓名
     */
    public void setPayerOpFullName(String payerOpFullName) {
        this.payerOpFullName = payerOpFullName;
    }

    /**
     * get 付款操作人姓名
     * @return String 付款操作人姓名
     */
    public String getPayerOpFullName() {
        return payerOpFullName;
    }


    /**
     * set 付款操作时间
     * @param payerOpTime 付款操作时间
     */
    public void setPayerOpTime(Date payerOpTime) {
        this.payerOpTime = payerOpTime;
    }

    /**
     * get 付款操作时间
     * @return Date 付款操作时间
     */
    public Date getPayerOpTime() {
        return payerOpTime;
    }


    /**
     * set 收款人编码
     * @param payeeUserCode 收款人编码
     */
    public void setPayeeUserCode(String payeeUserCode) {
        this.payeeUserCode = payeeUserCode;
    }

    /**
     * get 收款人编码
     * @return String 收款人编码
     */
    public String getPayeeUserCode() {
        return payeeUserCode;
    }


    /**
     * set 收款人姓名
     * @param payeeUserFullName 收款人姓名
     */
    public void setPayeeUserFullName(String payeeUserFullName) {
        this.payeeUserFullName = payeeUserFullName;
    }

    /**
     * get 收款人姓名
     * @return String 收款人姓名
     */
    public String getPayeeUserFullName() {
        return payeeUserFullName;
    }


    /**
     * set 收款人开户行名称
     * @param payeeBankName 收款人开户行名称
     */
    public void setPayeeBankName(String payeeBankName) {
        this.payeeBankName = payeeBankName;
    }

    /**
     * get 收款人开户行名称
     * @return String 收款人开户行名称
     */
    public String getPayeeBankName() {
        return payeeBankName;
    }


    /**
     * set 收款人开户支行名称
     * @param payeeBankBranchName 收款人开户支行名称
     */
    public void setPayeeBankBranchName(String payeeBankBranchName) {
        this.payeeBankBranchName = payeeBankBranchName;
    }

    /**
     * get 收款人开户支行名称
     * @return String 收款人开户支行名称
     */
    public String getPayeeBankBranchName() {
        return payeeBankBranchName;
    }


    /**
     * set 收款人银行开户人姓名
     * @param payeeBankAccountFullName 收款人银行开户人姓名
     */
    public void setPayeeBankAccountFullName(String payeeBankAccountFullName) {
        this.payeeBankAccountFullName = payeeBankAccountFullName;
    }

    /**
     * get 收款人银行开户人姓名
     * @return String 收款人银行开户人姓名
     */
    public String getPayeeBankAccountFullName() {
        return payeeBankAccountFullName;
    }


    /**
     * set 收款人银行账号
     * @param payeeBankAccountNo 收款人银行账号
     */
    public void setPayeeBankAccountNo(String payeeBankAccountNo) {
        this.payeeBankAccountNo = payeeBankAccountNo;
    }

    /**
     * get 收款人银行账号
     * @return String 收款人银行账号
     */
    public String getPayeeBankAccountNo() {
        return payeeBankAccountNo;
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
