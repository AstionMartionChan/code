#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="idl_wutiao_newuser_daily_realtime"

hive <<EOF
use $database;
create table if not exists $table (
ouid                    string     comment    '用户id'
,first_time               bigint     comment    '首次登录时间'
)comment '区块链活跃用户统计表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd',ut string comment 'uid/did')
row format delimited fields terminated by '\001'
stored as textfile;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF