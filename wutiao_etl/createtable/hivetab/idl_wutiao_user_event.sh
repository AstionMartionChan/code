#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="idl_wutiao_user_event"

hive <<EOF
use $database;
create table if not exists $table (
 os                      string     comment    '终端'
,appver                  string     comment    '客户端版本'
,channel                 string     comment    '渠道'
,ouid                    string     comment    '用户id'
,curr_event              string     comment    '当前事件'
,curr_time               bigint     comment    '当前时间'
,next_event              string     comment    '后一次事件'
,next_time               bigint     comment    '后一次时间'
,pre_event               string     comment    '前一次事件'
,pre_time                bigint     comment    '前一次时间'
,pos                     string     comment    '当前页码'
,target                  string     comment    '目标页码'
,itemtype                bigint     comment    '文章类型'
,sortid                  string     comment    '垂直类目'
)comment '区块链用户事件中间表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '\001'
stored as parquet;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF