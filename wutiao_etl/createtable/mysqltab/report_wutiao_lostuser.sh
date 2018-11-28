#!/bin/bash 

cd `dirname $0`
source /etc/profile
source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

month=`date +%Y_%m`
tablename="wutiao.report_wutiao_lostuser"
echo "set names 'utf8';
create table $tablename ( \
 fds                    date              comment    '统计日期'
,appver                 varchar(100)      comment    '客户端版本'
,channel                varchar(100)      comment    '渠道'
,os                     varchar(100)      comment    '终端'
,lost_activeuser_cnt    varchar(64)       comment    '流失帐号数'
,silent_user_cnt        varchar(64)       comment    '沉默帐号数'
,day1_lost_user_cnt     varchar(64)       comment    '次日流失数'
,day3_lost_user_cnt     bigint(20)        comment    '3日流失数'
,day7_lost_user_cnt     bigint(20)        comment    '7日流失数'
,day30_lost_user_cnt    bigint(20)        comment    '月流失数'
,lost_newuser_cnt       bigint(20)        comment    '新用户流失数'
,lost_olduser_cnt       bigint(20)        comment    '老用户流失数'
,UNIQUE KEY (os,fds,appver,channel)
) \
comment '流失用户' \
partition by range columns (fds) (\
partition p$nowMonth values less than ( '$nextMonth-01' ), \
partition p_other values less than maxvalue\
);" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port

sh add_partition.sh $tablename $nextMonth