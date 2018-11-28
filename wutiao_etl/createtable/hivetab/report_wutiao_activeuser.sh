#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="report_wutiao_activeuser"

hive <<EOF
use $database;
create table if not exists $table (
 fds                     string     comment    '统计日期'
,appver                  string     comment    '客户端版本'
,channel                 string     comment    '渠道'
,os                      string     comment    '终端'
,total_newuser_cnt       bigint     comment    '累计新增账号数'
,once_user_cnt           bigint     comment    '一次性用户数'
,callback_user_cnt       bigint     comment    '召回用户'
,newuser_cnt             bigint     comment    '新增帐号数'
,activeuser_cnt          bigint     comment    '活跃帐号数'
,olduser_cnt             bigint     comment    '去新活跃帐号数'
,day3_activeuser_cnt     bigint     comment    '3日活跃帐号数'
,day7_activeuser_cnt     bigint     comment    '7日活跃帐号数'
,day30_activeuser_cnt    bigint     comment    '月活跃帐号数'
,lost_user_cnt           bigint     comment    '流失帐号数'
,silent_user_cnt         bigint     comment    '沉默帐号数'
,register_user_cnt       bigint     comment    '注册用户数'
,visitor_user_cnt        bigint     comment    '游客数'
,visitor_new_user_cnt    bigint     comment    '新游客'
,visitor_old_user_cnt    bigint     comment    '去新游客'
)comment '区块链活跃用户统计表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '|'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF