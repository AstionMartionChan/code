#! /bin/sh
 
cd `dirname $0`
source /etc/profile
 
database="wutiao"
table="idl_wutiao_finance_uid_coin"
 
hive <<EOF
use $database;
create table if not exists $table (
     uid          string   comment '用户id'
    ,tp           string   comment '货币来源'
    ,coin         double   comment '币数'
)comment '财务-用户币数种类分布记录快照'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '|'
stored as TEXTFILE;
 
alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');
 
EOF