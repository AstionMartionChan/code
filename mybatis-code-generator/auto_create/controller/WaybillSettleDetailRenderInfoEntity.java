
@ApiImplicitParams({
@ApiImplicitParam(name = "id", value = "自增ID", required = true, dataType = "Long", paramType = "form"),
@ApiImplicitParam(name = "fieldConfigCode", value = "字段配置code唯一性", required = true, dataType = "String", paramType = "form"),
@ApiImplicitParam(name = "sequence", value = "顺序号", required = true, dataType = "Integer", paramType = "form"),
@ApiImplicitParam(name = "description", value = "描述", required = true, dataType = "String", paramType = "form"),
@ApiImplicitParam(name = "remark", value = "备注", required = true, dataType = "String", paramType = "form"),
@ApiImplicitParam(name = "disabled", value = "是否禁用", required = true, dataType = "Boolean", paramType = "form"),
@ApiImplicitParam(name = "deleted", value = "是否删除", required = true, dataType = "Boolean", paramType = "form"),
@ApiImplicitParam(name = "moduleCode", value = "操作模块编码", required = true, dataType = "String", paramType = "form"),
@ApiImplicitParam(name = "creatorUserCode", value = "创建人编码", required = true, dataType = "String", paramType = "form"),
@ApiImplicitParam(name = "creatorUsername", value = "创建人用户名", required = true, dataType = "String", paramType = "form"),
@ApiImplicitParam(name = "createTime", value = "创建时间", required = true, dataType = "Date", paramType = "form"),
@ApiImplicitParam(name = "updateUserCode", value = "更新人编码", required = true, dataType = "String", paramType = "form"),
@ApiImplicitParam(name = "updateUsername", value = "更新人用户名", required = true, dataType = "String", paramType = "form"),
@ApiImplicitParam(name = "updateTime", value = "更新时间", required = true, dataType = "Date", paramType = "form"),
@ApiImplicitParam(name = "ip", value = "操作者IP", required = true, dataType = "String", paramType = "form"),
@ApiImplicitParam(name = "operatorLongitude", value = "操作者所处经度", required = true, dataType = "Double", paramType = "form"),
@ApiImplicitParam(name = "operatorLatitude", value = "操作者所处纬度", required = true, dataType = "Double", paramType = "form"),
@ApiImplicitParam(name = "ownerUserCode", value = "所属用户编码", required = true, dataType = "String", paramType = "form"),
@ApiImplicitParam(name = "ownerOrgCode", value = "所属公司编码", required = true, dataType = "String", paramType = "form"),
@ApiImplicitParam(name = "ownerOrgName", value = "所属公司名字", required = true, dataType = "String", paramType = "form"),
@ApiImplicitParam(name = "synchronousId", value = "同步串号", required = true, dataType = "String", paramType = "form"),
})
