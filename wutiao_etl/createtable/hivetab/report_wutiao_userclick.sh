#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="report_wutiao_userclick"

tablecomment="用户点击分析"

hive <<EOF
use $database;
create  table if not exists $table (
fds varchar(128) COMMENT '日期'
,os varchar(128) COMMENT '终端'
,appver varchar(128) COMMENT '版本'
,channel varchar(128) COMMENT '渠道'
,homepage_pv int COMMENT '首页点击pv'
,homepage_attention_pv int COMMENT '首页关注点击pv'
,homepage_latest_pv int COMMENT '首页最新点击pv'
,homepage_income_pv int COMMENT '首页收益点击pv'
,search_pv int COMMENT '搜索点击pv'
,video_pv int COMMENT '视频点击pv'
,find_pv int COMMENT '发现点击pv'
,wallet_pv int COMMENT '钱包点击pv'
,personal_pv int COMMENT '我的点击pv'
,banner_pv int COMMENT '热点精选pv'
,recommend_pv int COMMENT '推荐pv'
,search_no_result_pv int COMMENT '搜索无结果pv'
,homepage_uv int COMMENT '首页点击uv'
,homepage_attention_uv int COMMENT '首页关注点击uv'
,homepage_latest_uv int COMMENT '首页最新点击uv'
,homepage_income_uv int COMMENT '首页收益点击uv'
,search_uv int COMMENT '搜索点击uv'
,video_uv int COMMENT '视频点击uv'
,find_uv int COMMENT '发现点击uv'
,wallet_uv int COMMENT '钱包点击uv'
,personal_uv int COMMENT '我的点击uv'
,banner_uv int COMMENT '热点精选uv'
,recommend_uv int COMMENT '推荐uv'
,search_no_result_uv int COMMENT '搜索无结果uv'
)
comment '$tablecomment'
partitioned by (ds string comment '日期分区字段,yyyy-MM-dd')
row format delimited fields terminated by '|'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE; 

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
