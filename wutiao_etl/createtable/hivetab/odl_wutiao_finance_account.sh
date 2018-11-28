#! /bin/sh
 
cd `dirname $0`
source /etc/profile
 
database="wutiao"
table="odl_wutiao_finance_account"
 
hive <<EOF
use $database;
create table if not exists $table (
     id           string   comment '账户id'
    ,address      string   comment '地址'
    ,create_time  string   comment '地址创建时间'
    ,uid          string   comment '统一用户id'
    ,ts           bigint   comment '时间戳'
)comment '财务-用户标号记录快照'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '\t'
stored as TEXTFILE;
 
alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');
 
EOF