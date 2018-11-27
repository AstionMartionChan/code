# 配置文件详解
---
## 结构说明
```bash
[section]
option = value

e.g.
[hive]
command_hive = /usr/bin/hive

...
```  
参数说明  
* section : 章节或主题名
* option : 标签名
* value : 配置的数值
  
---  
## 命名规范  
* 使用小写加下划线进行命名  
* linux命令相关的option请以 command_xxx 来开头  
* 系统设置和环境变量请以 systime_xxx 来开头
* section 的命名请尽量简短  

---   

## 重点说明
* <table><tr><td bgcolor=yellow > <font color=red face="黑体" size=4> git上的 kingnetdc.conf 文件为demo版本, 已经设定了必填的配置项,数值需要根据具体情况进行修改, 不能减少或更改以存在的'section' 和 'option'配置项和名字.  </font> </td></tr></table>  
* <table><tr><td bgcolor=yellow > <font color=red face="黑体" size=4> command_hive = '/usr/bin/hive' 在读取时会转成 "'/usr/bin/hive'" 需要注意.  
</font> </td></tr></table> 

## demo文件内容部分说明  
* wechat  微信发送消息配置,如需更新请联系项目负责人
* phone  电话通知配置,同上




