package com.rltx.org.config.result;

import java.util.Date;

/**
 * org_config_action_field_define
 */
@ApiModel("org_config_action_field_define")
public class OrgConfigActionFieldDefineResult {

    // 自增ID
    @ApiModelProperty("自增ID")
    private Long id;

    // 业务动作编码
    @ApiModelProperty("业务动作编码")
    private String actionCode;

    // 要操作的对象
    @ApiModelProperty("要操作的对象")
    private String targetObject;

    // 对象名称
    @ApiModelProperty("对象名称")
    private String targetObjectName;

    // 要操作的字段
    @ApiModelProperty("要操作的字段")
    private String targetField;

    // 要操作的字段名称
    @ApiModelProperty("要操作的字段名称")
    private String targetFieldName;

    // 数据源
    @ApiModelProperty("数据源")
    private String dataSource;

    // 可见性配置：0用户不可见，1用户可见
    @ApiModelProperty("可见性配置：0用户不可见，1用户可见")
    private Integer visibleConfig;

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
     * set 业务动作编码
     * @param actionCode 业务动作编码
     */
    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    /**
     * get 业务动作编码
     * @return String 业务动作编码
     */
    public String getActionCode() {
        return actionCode;
    }


    /**
     * set 要操作的对象
     * @param targetObject 要操作的对象
     */
    public void setTargetObject(String targetObject) {
        this.targetObject = targetObject;
    }

    /**
     * get 要操作的对象
     * @return String 要操作的对象
     */
    public String getTargetObject() {
        return targetObject;
    }


    /**
     * set 对象名称
     * @param targetObjectName 对象名称
     */
    public void setTargetObjectName(String targetObjectName) {
        this.targetObjectName = targetObjectName;
    }

    /**
     * get 对象名称
     * @return String 对象名称
     */
    public String getTargetObjectName() {
        return targetObjectName;
    }


    /**
     * set 要操作的字段
     * @param targetField 要操作的字段
     */
    public void setTargetField(String targetField) {
        this.targetField = targetField;
    }

    /**
     * get 要操作的字段
     * @return String 要操作的字段
     */
    public String getTargetField() {
        return targetField;
    }


    /**
     * set 要操作的字段名称
     * @param targetFieldName 要操作的字段名称
     */
    public void setTargetFieldName(String targetFieldName) {
        this.targetFieldName = targetFieldName;
    }

    /**
     * get 要操作的字段名称
     * @return String 要操作的字段名称
     */
    public String getTargetFieldName() {
        return targetFieldName;
    }


    /**
     * set 数据源
     * @param dataSource 数据源
     */
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * get 数据源
     * @return String 数据源
     */
    public String getDataSource() {
        return dataSource;
    }


    /**
     * set 可见性配置：0用户不可见，1用户可见
     * @param visibleConfig 可见性配置：0用户不可见，1用户可见
     */
    public void setVisibleConfig(Integer visibleConfig) {
        this.visibleConfig = visibleConfig;
    }

    /**
     * get 可见性配置：0用户不可见，1用户可见
     * @return Integer 可见性配置：0用户不可见，1用户可见
     */
    public Integer getVisibleConfig() {
        return visibleConfig;
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
