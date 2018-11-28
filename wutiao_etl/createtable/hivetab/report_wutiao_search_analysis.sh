#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="report_wutiao_search_analysis"

tablecomment="用户搜索分析"

hive <<EOF
use $database;
create  table if not exists $table (
fds varchar(128) COMMENT '日期'
,os varchar(128) COMMENT '终端'
,appver varchar(128) COMMENT '版本'
,channel varchar(128) COMMENT '渠道'
,search_click_pv int COMMENT '搜索点击PV'
,search_click_uv int COMMENT '搜索点击UV'
,search_no_result_pv int COMMENT '搜索无结果PV'
,search_no_result_uv int COMMENT '搜索无结果UV'
,search_result_click_pv int COMMENT '搜索结果页点击PV'
,search_result_click_uv int COMMENT '搜索结果页点击UV'
)
comment '$tablecomment'
partitioned by (ds string comment '日期分区字段,yyyy-MM-dd')
row format delimited fields terminated by '|'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE; 

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
