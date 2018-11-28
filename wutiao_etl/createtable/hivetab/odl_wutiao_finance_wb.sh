#! /bin/sh
 
cd `dirname $0`
source /etc/profile
 
database="wutiao"
table="odl_wutiao_finance_wb"
 
hive <<EOF
use $database;
create table if not exists $table (
     uid          string   comment '用户id'
    ,sourceid     bigint   comment '用户来源编号'
    ,sourcetype   bigint   comment '用户来源币的类型 10发表内容/11阅读/12投票/13分享/14评论/15发现'
    ,time         int      comment '分币的时间'
    ,actiontype   int      comment '操作类型 1注册/2邀请/3被邀请/4好友/5举报/9其他/10发表内容/11阅读/12投票/13分享/14评论/15发现/16邀请额外收益'
    ,coin_value   double   comment '获取多少币'
)comment '财务-分币记录快照'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '|'
stored as TEXTFILE;
 
alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');
 
EOF