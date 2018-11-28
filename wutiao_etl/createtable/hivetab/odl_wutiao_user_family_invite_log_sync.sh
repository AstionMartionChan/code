#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="odl_wutiao_user_family_invite_log_sync"

tablecomment="五条用户邀请表"

hive <<EOF
use $database;
create  table if not exists $table (
 id                    bigint
,phone                 varchar(50)      COMMENT '被邀请者的手机号'
,invite_id             bigint           COMMENT '邀请者的UID'
,status                int
,create_dt             string           COMMENT '添加时间'
,update_dt             string           COMMENT '更新时间'
,\`_slot\`             int              COMMENT 'do NOT modify'
,source                int              COMMENT '1:家庭邀请，2:好友邀请'
)
comment '$tablecomment'
partitioned by (ds string comment '日期分区字段,yyyy-MM-dd')
row format delimited fields terminated by '\t'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
