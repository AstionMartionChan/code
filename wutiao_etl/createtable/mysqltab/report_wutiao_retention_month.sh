#!/bin/bash 

cd `dirname $0`

source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

rid=$1
month=`date +%Y_%m`
database="wutiao"
tablename="$database.report_wutiao_retention_month"
echo "set names 'utf8';
create table $tablename ( \
 fds                       varchar(20)     COMMENT '日期'
,os                        varchar(50)     COMMENT '终端'
,appver                    varchar(50)     COMMENT '版本'
,channel                   varchar(50)     COMMENT '渠道'
,month_active_did_cnt      int             COMMENT '月活跃设备'
,month_active_ouid_cnt     int             COMMENT '月活跃用户'
,month_new_did_cnt         int             COMMENT '月新增设备'
,month_new_ouid_cnt        int             COMMENT '月新增注册'
,month_withdraw_ouid_cnt   int             COMMENT '月提现人数'
,month_withdraw_rmb        double          COMMENT '月提现金额'
,month1_retention          int             COMMENT '1月留存'
,month2_retention          int             COMMENT '2月留存'
,month3_retention          int             COMMENT '3月留存'
,month4_retention          int             COMMENT '4月留存'
,month5_retention          int             COMMENT '5月留存'
,month6_retention          int             COMMENT '6月留存'
,UNIQUE KEY (os,appver,channel,fds)
) \
comment '留存月数据' \
partition by range columns (fds) (\
partition p$nowMonth values less than ( '$nextMonth-01' ), \
partition p_other values less than maxvalue\
);" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port

sh add_partition.sh $tablename $nextMonth