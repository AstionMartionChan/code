#!/bin/bash 

cd `dirname $0`
source /etc/profile
source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

month=`date +%Y_%m`
tablename="wutiao.report_wutiao_colligate_app"
echo "set names 'utf8';
create table $tablename ( \
 fds                         date                comment     '日期分区字段,yyyy-MM-dd'
,os                          varchar(100)        comment     '终端'
,appver                      varchar(100)        comment     '版本'
,channel                     varchar(100)        comment     '渠道'
,total_newdid_cnt            bigint              comment     '累计激活数'
,activedid_cnt               bigint              comment     '活跃设备数'
,newdid_cnt                  bigint              comment     '新增激活数'
,total_newuser_cnt           bigint              comment     '累计新增账号数'
,activeuser_cnt              bigint              comment     '活跃帐号数'
,newuser_cnt                 bigint              comment     '新增帐号数'
,withdraw_success_usercnt    bigint              comment     '提现成功用户数'
,withdraw_success_rmb        double              comment     '提现成功金额'
,day1_retention              bigint              COMMENT     '1日留存'
,day3_retention              bigint              COMMENT     '3日留存'
,day7_retention              bigint              COMMENT     '7日留存'
,UNIQUE KEY (os,appver,channel,fds)
) \
comment '综合数据(app)' \
partition by range columns (fds) (\
partition p$nowMonth values less than ( '$nextMonth-01' ), \
partition p_other values less than maxvalue\
);" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port

sh add_partition.sh $tablename $nextMonth