#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="idl_wutiao_filter"

hive <<EOF
use $database;
create table if not exists $table (
ouid                        string      comment     '帐号id'
,did                        string      comment     '设备id'
,phone                      string      comment     '手机号'
)comment '五条脏数据过滤中间表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd',ut string comment 'uid:帐号,did:设备')
row format delimited fields terminated by '\001'
stored as parquet;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF