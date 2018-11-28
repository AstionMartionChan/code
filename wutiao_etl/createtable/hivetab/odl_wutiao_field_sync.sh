#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="odl_wutiao_field_sync"

tablecomment="五条垂直类目表"

hive <<EOF
use $database;
create  table if not exists $table (
 field_id              bigint
,field_name            varchar(50)      COMMENT '领域名称'
,parent_id             int              COMMENT '父类id'
,level                 int
,create_dt             string
,update_dt             string
)
comment '$tablecomment'
row format delimited fields terminated by '\t'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
