package com.rltx.waybill.po;

import java.util.Date;

/**
 * waybill_settle_field_config 表 Entity
 */
public class WaybillSettleFieldConfigEntity {

    // 自增ID
    private Long id;

    // 字段配置code唯一性
    private String fieldConfigCode;

    // 字段定义名称
    private String fieldDefineCode;

    // 表示该属性是否从平台拷贝过来 1.平台 2. 企业自定义
    private Integer type;

    // 显示名字
    private String showName;

    // Format
    private String format;

    // 元素类型
    private String elementCode;

    // 额外属性，json格式存储
    private String extraParams;

    // 检索元素类型
    private String searchElementCode;

    // 检索额外属性，json格式存储
    private String searchExtraParams;

    // 描述
    private String description;

    // 备注
    private String remark;

    // 是否禁用
    private Boolean disabled;

    // 是否删除
    private Boolean deleted;

    // 操作模块编码
    private String moduleCode;

    // 创建人编码
    private String creatorUserCode;

    // 创建人用户名
    private String creatorUsername;

    // 创建时间
    private Date createTime;

    // 更新人编码
    private String updateUserCode;

    // 更新人用户名
    private String updateUsername;

    // 更新时间
    private Date updateTime;

    // 操作者IP
    private String ip;

    // 操作者所处经度
    private Double operatorLongitude;

    // 操作者所处纬度
    private Double operatorLatitude;

    // 所属用户编码
    private String ownerUserCode;

    // 所属公司编码
    private String ownerOrgCode;

    // 所属公司名字
    private String ownerOrgName;

    // 同步串号
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
     * set 字段配置code唯一性
     * @param fieldConfigCode 字段配置code唯一性
     */
    public void setFieldConfigCode(String fieldConfigCode) {
        this.fieldConfigCode = fieldConfigCode;
    }

    /**
     * get 字段配置code唯一性
     * @return String 字段配置code唯一性
     */
    public String getFieldConfigCode() {
        return fieldConfigCode;
    }


    /**
     * set 字段定义名称
     * @param fieldDefineCode 字段定义名称
     */
    public void setFieldDefineCode(String fieldDefineCode) {
        this.fieldDefineCode = fieldDefineCode;
    }

    /**
     * get 字段定义名称
     * @return String 字段定义名称
     */
    public String getFieldDefineCode() {
        return fieldDefineCode;
    }


    /**
     * set 表示该属性是否从平台拷贝过来 1.平台 2. 企业自定义
     * @param type 表示该属性是否从平台拷贝过来 1.平台 2. 企业自定义
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * get 表示该属性是否从平台拷贝过来 1.平台 2. 企业自定义
     * @return Integer 表示该属性是否从平台拷贝过来 1.平台 2. 企业自定义
     */
    public Integer getType() {
        return type;
    }


    /**
     * set 显示名字
     * @param showName 显示名字
     */
    public void setShowName(String showName) {
        this.showName = showName;
    }

    /**
     * get 显示名字
     * @return String 显示名字
     */
    public String getShowName() {
        return showName;
    }


    /**
     * set Format
     * @param format Format
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * get Format
     * @return String Format
     */
    public String getFormat() {
        return format;
    }


    /**
     * set 元素类型
     * @param elementCode 元素类型
     */
    public void setElementCode(String elementCode) {
        this.elementCode = elementCode;
    }

    /**
     * get 元素类型
     * @return String 元素类型
     */
    public String getElementCode() {
        return elementCode;
    }


    /**
     * set 额外属性，json格式存储
     * @param extraParams 额外属性，json格式存储
     */
    public void setExtraParams(String extraParams) {
        this.extraParams = extraParams;
    }

    /**
     * get 额外属性，json格式存储
     * @return String 额外属性，json格式存储
     */
    public String getExtraParams() {
        return extraParams;
    }


    /**
     * set 检索元素类型
     * @param searchElementCode 检索元素类型
     */
    public void setSearchElementCode(String searchElementCode) {
        this.searchElementCode = searchElementCode;
    }

    /**
     * get 检索元素类型
     * @return String 检索元素类型
     */
    public String getSearchElementCode() {
        return searchElementCode;
    }


    /**
     * set 检索额外属性，json格式存储
     * @param searchExtraParams 检索额外属性，json格式存储
     */
    public void setSearchExtraParams(String searchExtraParams) {
        this.searchExtraParams = searchExtraParams;
    }

    /**
     * get 检索额外属性，json格式存储
     * @return String 检索额外属性，json格式存储
     */
    public String getSearchExtraParams() {
        return searchExtraParams;
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
