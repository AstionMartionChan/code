#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="report_wutiao_total_assets"

hive <<EOF
use $database;
create table if not exists $table (
 fds                                string     comment    '统计日期'
,user_flag                          string     comment    '新老用户标识'
,total_withdraw_success_rmb         double     comment    '历史总体现rmb'
,total_withdraw_success_usercnt     bigint     comment    '历史总体现用户数'
,total_withdraw_success_wb          double     comment    '历史总提现wb'
,day_withdraw_success_rmb           double     comment    '当日提现rmb'
,day_withdraw_success_wb            double     comment    '当日提现wb'
,day_withdraw_success_usercnt       bigint     comment    '当日提现成功用户数'
,total_getwb_wb                     double     comment    '总领币'
,os                                 string     comment    '终端'
,appver                             string     comment    '版本'
,channel                            string     comment    '渠道'
,moneytype                          string     comment    '提现类型'
,active_usercnt                     bigint          comment    '活跃帐号'
,apply_usercnt                      bigint          comment    '申请提现帐号'
,apply_coin                         double          comment    '申请提现币'
,apply_rmb                          double          comment    '申请提现金额'
,actual_rate                        double          comment    '汇率'
)comment '区块链总资产分析表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '|'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF