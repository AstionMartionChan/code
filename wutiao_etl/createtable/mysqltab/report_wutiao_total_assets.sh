#!/bin/bash 

cd `dirname $0`
source /etc/profile
source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

month=`date +%Y_%m`
tablename="wutiao.report_wutiao_total_assets"
echo "set names 'utf8';
create table $tablename ( \
 fds                                varchar(20)     comment    '统计日期'
,user_flag                          varchar(10)     comment    '新老用户标识'
,total_withdraw_success_rmb         double     comment    '历史总体现rmb'
,total_withdraw_success_usercnt     bigint     comment    '历史总体现用户数'
,total_withdraw_success_wb          double     comment    '历史总提现wb'
,day_withdraw_success_rmb           double     comment    '当日提现rmb'
,day_withdraw_success_wb            double     comment    '当日提现wb'
,day_withdraw_success_usercnt       bigint     comment    '当日提现成功用户数'
,total_getwb_wb                     double     comment    '总领币'
,os                                 varchar(30)     comment    '终端'
,appver                             varchar(30)     comment    '版本'
,channel                            varchar(30)     comment    '渠道'
,moneytype                          varchar(10)     comment    '提现类型'
,active_usercnt                     bigint          comment    '活跃帐号'
,apply_usercnt                      bigint          comment    '申请提现帐号'
,apply_coin                         double          comment    '申请提现币'
,apply_rmb                          double          comment    '申请提现金额'
,actual_rate                        double          comment    '汇率'
) \
comment '总资产分析' \
partition by range columns (fds) (\
partition p$nowMonth values less than ( '$nextMonth-01' ), \
partition p_other values less than maxvalue\
);" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port

sh add_partition.sh $tablename $nextMonth