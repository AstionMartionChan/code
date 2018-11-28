#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="odl_wutiao_city_sync"

tablecomment="五条城市表"

hive <<EOF
use $database;
create  table if not exists $table (
 id                    int
,city_id               varchar(50)      COMMENT '城市id'
,city_name             varchar(50)      COMMENT '城市名'
,parent_id             varchar(50)      COMMENT '上一级id'
,level                 int              COMMENT '级别0省1市2区/县'
,create_dt             string           COMMENT '创建时间'
,update_dt             string           COMMENT '更新时间'
)
comment '$tablecomment'
row format delimited fields terminated by '\t'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
