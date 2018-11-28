#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="odl_wutiao_audit_status_sync"

tablecomment="五条审计状态表"

hive <<EOF
use $database;
create  table if not exists $table (
 id                        varchar(255)   comment  '自增id'
,status_id                 varchar(255)   comment  '审核状态id'
,status_name               varchar(255)   comment  '审核状态名称'
)
comment '$tablecomment'
row format delimited fields terminated by '\t'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
