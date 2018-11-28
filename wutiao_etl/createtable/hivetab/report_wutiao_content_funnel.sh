#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="report_wutiao_content_funnel"

tablecomment="访问路径分析"

hive <<EOF
use $database;
create  table if not exists $table (
fds varchar(128) COMMENT '日期'
,os varchar(128) COMMENT '终端'
,channel varchar(128) COMMENT '渠道'
,itemtype varchar(128) COMMENT '类别（视频/文章）'
,content_pv int COMMENT '详情页访问pv'
,content_uv int COMMENT '详情页访问uv'
,content_second_pv int COMMENT '详情页点击pv'
,content_second_uv int COMMENT '详情页点击uv'
,content_readlist_pv int COMMENT '详情页列表页pv'
,content_readlist_uv int COMMENT '详情页列表页uv'
,content_exit_pv int COMMENT '详情页退出pv'
,content_exit_uv int COMMENT '详情页退出uv'
)
comment '$tablecomment'
partitioned by (ds string comment '日期分区字段,yyyy-MM-dd')
row format delimited fields terminated by '|'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE; 

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
