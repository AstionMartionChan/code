#!/bin/bash 

cd `dirname $0`

source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

rid=$1
month=`date +%Y_%m`
database="wutiao"
tablename="$database.report_wutiao_userclick"
echo "set names 'utf8';
create table $tablename ( \
fds varchar(20) COMMENT '日期'
,os varchar(50) COMMENT '终端'
,appver varchar(50) COMMENT '版本'
,channel varchar(50) COMMENT '渠道'
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
,UNIQUE KEY (os,appver,channel,fds)
) \
comment '用户点击分析' \
partition by range columns (fds) (\
partition p$nowMonth values less than ( '$nextMonth-01' ), \
partition p_other values less than maxvalue\
);" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port

sh add_partition.sh $tablename $nextMonth