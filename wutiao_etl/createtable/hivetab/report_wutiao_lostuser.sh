#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="report_wutiao_lostuser"

hive <<EOF
use $database;
create table if not exists $table (
 fds                    string     comment    '统计日期'
,appver                 string     comment    '客户端版本'
,channel                string     comment    '渠道'
,os                     string     comment    '终端'
,lost_activeuser_cnt    bigint     comment    '流失帐号数'
,silent_user_cnt        bigint     comment    '沉默帐号数'
,day1_lost_user_cnt     bigint     comment    '次日流失数'
,day3_lost_user_cnt     bigint     comment    '3日流失数'
,day7_lost_user_cnt     bigint     comment    '7日流失数'
,day30_lost_user_cnt    bigint     comment    '月流失数'
,lost_newuser_cnt       bigint     comment    '新用户流失数'
,lost_olduser_cnt       bigint     comment    '老用户流失数'
)comment '区块链流失用户统计表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '|'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF