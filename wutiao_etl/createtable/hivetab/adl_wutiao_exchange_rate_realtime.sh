#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="adl_wutiao_exchange_rate_realtime"

hive <<EOF
use $database;
create table if not exists $table (
fds                      string     comment    '用户id'
,total_register          bigint     comment    '累计注册用户数'
,total_withdraw_rmb        double     comment    '累计提现金额'
,total_withdraw_coin     double     comment    '累计提现金币'
,total_money_gain            double     comment    '累计领币'
)comment '区块链汇率统计表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '|'
stored as textfile;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF