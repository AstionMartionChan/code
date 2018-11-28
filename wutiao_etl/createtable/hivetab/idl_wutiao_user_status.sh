#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="idl_wutiao_user_status"

hive <<EOF
use $database;
create table if not exists $table (
ouid                string     comment    '用户id',
first_time          bigint     comment    '首次成为该用户的时间',
status              string     comment    '是否ut类型，1是0不是',
type                string     comment    'ut具体类型，如自媒体类型'
)comment '区块链用户分类中间表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd',ut string comment '用户类型分区')
row format delimited fields terminated by '\001'
stored as parquet;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF