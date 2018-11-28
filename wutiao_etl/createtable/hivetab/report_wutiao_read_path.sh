#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="report_wutiao_read_path"

tablecomment="访问路径分析"

hive <<EOF
use $database;
create  table if not exists $table (
fds varchar(128) COMMENT '日期'
,os varchar(128) COMMENT '终端'
,channel varchar(128) COMMENT '渠道'
,itemtype varchar(128) COMMENT '类别（视频/文章）'
,homepage_pv int COMMENT '页面浏览pv'
,homepage_uv int COMMENT '页面浏览uv'
,second_pv int COMMENT '二跳浏览pv'
,second_uv int COMMENT '二跳浏览uv'
,content_pv int COMMENT '详情播放页pv'
,content_uv int COMMENT '详情播放页uv'
,readlist_pv int COMMENT '列表页pv'
,readlist_uv int COMMENT '列表页uv'
,exit_pv int COMMENT '退出数pv'
,exit_uv int COMMENT '退出数uv'
)
comment '$tablecomment'
partitioned by (ds string comment '日期分区字段,yyyy-MM-dd')
row format delimited fields terminated by '|'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE; 

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
