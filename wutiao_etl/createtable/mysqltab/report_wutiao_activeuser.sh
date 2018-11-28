#!/bin/bash 

cd `dirname $0`
source /etc/profile
source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

month=`date +%Y_%m`
tablename="wutiao.report_wutiao_activeuser"
echo "set names 'utf8';
create table $tablename ( \
 fds                    date                comment     '日期分区字段,yyyy-MM-dd'
,appver                 varchar(100)        comment     '版本'
,channel                varchar(100)        comment     '渠道'
,os                     varchar(100)        comment     '终端'
,total_newuser_cnt      bigint(20)         comment     '累计新增账号数'
,once_user_cnt          bigint(20)         comment     '一次性用户数'
,callback_user_cnt      bigint(20)         comment     '召回用户'
,newuser_cnt            bigint(20)         comment     '新增帐号数'
,activeuser_cnt         bigint(20)         comment     '活跃帐号数'
,olduser_cnt            bigint(20)         comment     '去新活跃帐号数'
,day3_activeuser_cnt    bigint(20)          comment     '3日活跃帐号数'
,day7_activeuser_cnt    bigint(20)          comment     '7日活跃帐号数'
,day30_activeuser_cnt   bigint(20)          comment     '月活跃帐号数'
,lost_user_cnt          bigint(20)          comment     '流失帐号数'
,silent_user_cnt        bigint(20)          comment     '沉默帐号数'
,register_user_cnt      bigint(20)         comment     '注册用户数'
,UNIQUE KEY (fds,appver,channel,os)
) \
comment '活跃用户表' \
partition by range columns (fds) (\
partition p$nowMonth values less than ( '$nextMonth-01' ), \
partition p_other values less than maxvalue\
);" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port

sh add_partition.sh $tablename $nextMonth