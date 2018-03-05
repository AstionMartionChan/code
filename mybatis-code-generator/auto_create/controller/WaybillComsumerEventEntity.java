
@ApiImplicitParams({
@ApiImplicitParam(name = "id", value = "自增id", required = true, dataType = "Long", paramType = "form"),
@ApiImplicitParam(name = "eventCode", value = "事件code", required = true, dataType = "String", paramType = "form"),
@ApiImplicitParam(name = "consumerName", value = "消费者名称", required = true, dataType = "String", paramType = "form"),
@ApiImplicitParam(name = "topic", value = "主题", required = true, dataType = "String", paramType = "form"),
@ApiImplicitParam(name = "message", value = "消息体", required = true, dataType = "String", paramType = "form"),
@ApiImplicitParam(name = "status", value = "状态", required = true, dataType = "String", paramType = "form"),
@ApiImplicitParam(name = "failedReason", value = "失败原因", required = true, dataType = "String", paramType = "form"),
@ApiImplicitParam(name = "createTime", value = "创建时间", required = true, dataType = "Date", paramType = "form"),
@ApiImplicitParam(name = "updateTime", value = "更新时间", required = true, dataType = "Date", paramType = "form"),
})
