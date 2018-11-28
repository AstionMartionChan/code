#!/bin/bash 

cd `dirname $0`

source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

rid=$1
month=`date +%Y_%m`
database="wutiao"
tablename="$database.report_wutiao_search_analysis"
echo "set names 'utf8';
create table $tablename ( \
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
,UNIQUE KEY (os,appver,channel,fds)
) \
comment '用户搜索分析' \
partition by range columns (fds) (\
partition p$nowMonth values less than ( '$nextMonth-01' ), \
partition p_other values less than maxvalue\
);" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port

sh add_partition.sh $tablename $nextMonth