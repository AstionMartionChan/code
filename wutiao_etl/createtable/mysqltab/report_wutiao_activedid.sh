#!/bin/bash 

cd `dirname $0`
source /etc/profile
source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

month=`date +%Y_%m`
tablename="wutiao.report_wutiao_activedid"
echo "set names 'utf8';
create table $tablename ( \
 fds                    date                comment     '日期分区字段,yyyy-MM-dd'
,appver                 varchar(100)        comment     '版本'
,channel                varchar(100)        comment     '渠道'
,os                     varchar(100)        comment     '终端'
,newdid_cnt             varchar(64)         comment     '新增设备数'
,total_newdid_cnt       bigint(20)          comment     '新增设备数'
,activedid_cnt          bigint(20)          comment     '活跃设备数'
,olddid_cnt             bigint(20)          comment     '去新活跃设备数'
,day3_activedid_cnt     bigint(20)          comment     '3日活跃设备数'
,day7_activedid_cnt     bigint(20)          comment     '7日活跃设备数'
,day30_activedid_cnt    bigint(20)          comment     '月活跃设备数'
,crash_did_cnt          bigint(20)          comment     '崩溃设备数'
,crash_cnt              bigint(20)          comment     '崩溃次数'
,avg_openclient_cnt     decimal(10,2)       comment     '人均启动次数'
,newuser_cnt            bigint(20)          comment     '新增帐号数'
,UNIQUE KEY (appver,channel,os,fds)
) \
comment '活跃设备表' \
partition by range columns (fds) (\
partition p$nowMonth values less than ( '$nextMonth-01' ), \
partition p_other values less than maxvalue\
);" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port

sh add_partition.sh $tablename $nextMonth