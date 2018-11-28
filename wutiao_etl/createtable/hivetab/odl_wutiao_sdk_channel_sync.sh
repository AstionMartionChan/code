#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="odl_wutiao_sdk_channel_sync"

tablecomment="五条渠道表"

hive <<EOF
use $database;
create  table if not exists $table (
 id                       int
,name                     varchar(100)
,type                     int
,pay_type                 int
,leader                   int                 COMMENT '负责人'
,add_time                 int
,cooperation_type         int
,edit_time                int
,media_id                 int
,store                    int
,operation_classified     int                 COMMENT '运营分类'
,media_type               int
,msg_provide              varchar(100)
,msg_start_time           int
,msg_stop_time            int
,msg_send_hour            varchar(100)
,msg_rate                 int
,legacy                   int
,cpa_start_date           int
,ext1                     varchar(200)
)
comment '$tablecomment'
row format delimited fields terminated by '\t'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
