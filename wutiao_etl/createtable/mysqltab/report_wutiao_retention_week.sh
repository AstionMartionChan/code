#!/bin/bash 

cd `dirname $0`

source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

rid=$1
month=`date +%Y_%m`
database="wutiao"
tablename="$database.report_wutiao_retention_week"
echo "set names 'utf8';
create table $tablename ( \
 fds                      varchar(20)     COMMENT '日期'
,yeardate                 varchar(20)     COMMENT '年份'
,weeknum                  varchar(20)     COMMENT '周数'
,os                       varchar(50)     COMMENT '终端'
,appver                   varchar(50)     COMMENT '版本'
,channel                  varchar(50)     COMMENT '渠道'
,week_active_did_cnt      int             COMMENT '周活跃设备'
,week_active_ouid_cnt     int             COMMENT '周活跃用户'
,week_new_did_cnt         int             COMMENT '周新增设备'
,week_new_ouid_cnt        int             COMMENT '周新增注册'
,week_withdraw_ouid_cnt   int             COMMENT '周提现人数'
,week_withdraw_rmb        double          COMMENT '周提现金额'
,week1_retention          int             COMMENT '1周留存'
,week2_retention          int             COMMENT '2周留存'
,week3_retention          int             COMMENT '3周留存'
,week4_retention          int             COMMENT '4周留存'
,week5_retention          int             COMMENT '5周留存'
,week6_retention          int             COMMENT '6周留存'
,week7_retention          int             COMMENT '7周留存'
,week8_retention          int             COMMENT '8周留存'
,UNIQUE KEY (os,appver,channel,fds)
) \
comment '留存周数据' \
partition by range columns (fds) (\
partition p$nowMonth values less than ( '$nextMonth-01' ), \
partition p_other values less than maxvalue\
);" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port

sh add_partition.sh $tablename $nextMonth