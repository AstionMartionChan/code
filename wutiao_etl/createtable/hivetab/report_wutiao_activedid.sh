#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="report_wutiao_activedid"

hive <<EOF
use $database;
create table if not exists $table (
 fds                    string     comment    '统计日期'
,appver                 string     comment    '客户端版本'
,channel                string     comment    '渠道'
,os                     string     comment    '终端'
,newdid_cnt             bigint     comment    '新增设备数'
,total_newdid_cnt       bigint     comment    '累计新增设备数'
,activedid_cnt          bigint     comment    '活跃设备数'
,olddid_cnt             bigint     comment    '去新活跃设备数'
,day3_activedid_cnt     bigint     comment    '3日活跃设备数'
,day7_activedid_cnt     bigint     comment    '7日活跃设备数'
,day30_activedid_cnt    bigint     comment    '月活跃设备数'
,crash_did_cnt          bigint     comment    '崩溃设备数'
,crash_cnt              bigint     comment    '崩溃次数'
,openclient_cnt         bigint     comment    '启动次数'
,newuser_cnt            bigint     comment    '新增帐号数'
)comment '区块链活跃设备统计表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '|'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF