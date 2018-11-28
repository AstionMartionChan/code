#!/bin/bash 

cd `dirname $0`

source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

rid=$1
month=`date +%Y_%m`
database="wutiao"
tablename="$database.report_wutiao_retention"
echo "set names 'utf8';
create table $tablename ( \
fds varchar(128) COMMENT '日期'
,os varchar(128) COMMENT '终端'
,appver varchar(128) COMMENT '版本'
,channel varchar(128) COMMENT '渠道'
,newuser_cnt int COMMENT '新增帐号数'
,day1_retention int COMMENT '1日留存'
,day2_retention int COMMENT '2日留存'
,day3_retention int COMMENT '3日留存'
,day4_retention int COMMENT '4日留存'
,day5_retention int COMMENT '5日留存'
,day6_retention int COMMENT '6日留存'
,day7_retention int COMMENT '7日留存'
,day15_retention int COMMENT '15日留存'
,day30_retention int COMMENT '30日留存'
,UNIQUE KEY (os,appver,channel,fds)
) \
comment '留存用户' \
partition by range columns (fds) (\
partition p$nowMonth values less than ( '$nextMonth-01' ), \
partition p_other values less than maxvalue\
);" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port

sh add_partition.sh $tablename $nextMonth