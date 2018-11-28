#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="odl_wutiao_sdk_channel_media_type_sync"

tablecomment="五条市场媒体表"

hive <<EOF
use $database;
create  table if not exists $table (
 id                       int
,name                     varchar(200)
,leader                   int
,callback_type            int
,ext1                     varchar(200)        COMMENT'废弃字段'
,add_time                 int
,edit_time                int
,channel_id               int                 COMMENT'渠道类型ID'
)
comment '$tablecomment'
row format delimited fields terminated by '\t'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
