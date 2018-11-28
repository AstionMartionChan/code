#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="odl_wutiao_wutiao_rate_sync"

tablecomment="五条汇率表"

hive <<EOF
use $database;
create  table if not exists $table (
 id                    bigint
,dt                    string           COMMENT '日期'
,now_rate              string           COMMENT '当前汇率'
,actual_rate           string           COMMENT '实际汇率'
,status                int              COMMENT '状态0删除1已确认2未确认'
,create_dt             string           COMMENT '创建时间'
,update_dt             string           COMMENT '修改时间'
)
comment '$tablecomment'
partitioned by (ds string comment '日期分区字段,yyyy-MM-dd')
row format delimited fields terminated by '\t'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
