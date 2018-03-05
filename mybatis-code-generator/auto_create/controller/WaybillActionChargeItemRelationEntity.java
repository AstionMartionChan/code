
@ApiImplicitParams({
@ApiImplicitParam(name = "id", value = "自增ID", required = true, dataType = "Long", paramType = "form"),
@ApiImplicitParam(name = "waybillCode", value = "运单编码", required = true, dataType = "String", paramType = "form"),
@ApiImplicitParam(name = "templateCode", value = "模板编码", required = true, dataType = "String", paramType = "form"),
@ApiImplicitParam(name = "actionCode", value = "业务动作编码", required = true, dataType = "String", paramType = "form"),
@ApiImplicitParam(name = "chargeItemCode", value = "费用科目编码", required = true, dataType = "String", paramType = "form"),
@ApiImplicitParam(name = "chargeItemName", value = "费用科目名称", required = true, dataType = "String", paramType = "form"),
@ApiImplicitParam(name = "chargeItemNumberUnitCode", value = "记账数量单位编码", required = true, dataType = "String", paramType = "form"),
@ApiImplicitParam(name = "chargeItemNumberUnitName", value = "记账数量单位名称", required = true, dataType = "String", paramType = "form"),
@ApiImplicitParam(name = "chargeItemPriceUnitCode", value = "记账单价单位编码", required = true, dataType = "String", paramType = "form"),
@ApiImplicitParam(name = "chargeItemPriceUnitName", value = "记账单价单位名称", required = true, dataType = "String", paramType = "form"),
@ApiImplicitParam(name = "chargeItemAmountsUnitCode", value = "记账金额计量单位编码", required = true, dataType = "String", paramType = "form"),
@ApiImplicitParam(name = "chargeItemAmountsUnitName", value = "记账金额计量单位名称", required = true, dataType = "String", paramType = "form"),
@ApiImplicitParam(name = "usageDesc", value = "往来户说明（例如：中石油、交警、路霸、高管处….文字描述，可做自动完成辅助输入）", required = true, dataType = "String", paramType = "form"),
@ApiImplicitParam(name = "visibleConfig", value = "可见性配置：bit0Web端是否可见，bit1司机端是否可见", required = true, dataType = "Integer", paramType = "form"),
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