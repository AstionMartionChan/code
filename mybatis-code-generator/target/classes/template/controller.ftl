
@ApiImplicitParams({
<#list entityList as entity>
@ApiImplicitParam(name = "${entity.fieldName}", value = "${entity.remark}", required = true, dataType = "${entity.fieldType}", paramType = "form"),
</#list>
})
