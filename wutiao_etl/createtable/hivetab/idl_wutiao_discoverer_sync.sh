#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="idl_wutiao_discoverer_sync"

tablecomment="五条发现者中间表"

hive <<EOF
use $database;
create  table if not exists $table (
 uid                   bigint
,last_apply_date       string            COMMENT '申请时间'
,last_quit_date        string            COMMENT '淘汰时间'
,next_apply_date       string            COMMENT '下次申请时间'
,status                int               COMMENT '1 正常 0 淘汰'
,create_dt             string            COMMENT '创建时间'
,update_dt             string            COMMENT '更新时间'
)
comment '$tablecomment'
partitioned by (ds string comment '日期分区字段,yyyy-MM-dd')
row format delimited fields terminated by '\001'
stored as parquet;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
