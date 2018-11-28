#! /bin/sh
 
cd `dirname $0`
source /etc/profile
 
database="wutiao"
table="odl_wutiao_wb"
 
hive <<EOF
use $database;
create table if not exists $table (
uid              string           comment '用户编号'
,itemId          bigint           comment '文章编号'
,itemType        bigint           comment '文章类型'
,messageId       string           comment '消息ID'
,sourceId        bigint           comment '用户来源编号'
,sourceType      bigint           comment '用户来源币的类型 10发表内容/11阅读/12投票/13分享/14评论/15发现'
,\`time\`        bigint           comment '分币的时间(unix time)'
,actionType      int              comment '操作类型 1注册/2邀请/3被邀请/4好友/5举报/9其他/10发表内容/11阅读/12投票/13分享/14评论/15发现/16邀请额外收益'
,value           double           comment '获取多少币'
,kafkauniqueid        string       comment       'kafka标识'
)comment '区块链分币源表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd', hour string comment '小时分区字段, yyyyMMddHH')
row format delimited fields terminated by '\001'
stored as parquet;
 
alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');
 
EOF