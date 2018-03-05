
@ApiImplicitParams({
@ApiImplicitParam(name = "id", value = "自增ID", required = true, dataType = "Long", paramType = "form"),
@ApiImplicitParam(name = "type", value = "类型", required = true, dataType = "String", paramType = "form"),
@ApiImplicitParam(name = "orgCode", value = "组织编码", required = true, dataType = "String", paramType = "form"),
@ApiImplicitParam(name = "year", value = "年份", required = true, dataType = "Integer", paramType = "form"),
@ApiImplicitParam(name = "value", value = "序列号", required = true, dataType = "Long", paramType = "form"),
})
