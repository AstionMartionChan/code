#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="odl_wutiao_user_authentication_sync"

tablecomment="五条用户认证表"

hive <<EOF
use $database;
create  table if not exists $table (
 uid                   bigint            COMMENT 'uid'
,type                  int               COMMENT '类型0无类型1个人21媒体-群媒体22媒体新闻机构3国家机构4企业5其他组织'
,status                int               COMMENT '0无状态1审核中2审核通过3审核未通过'
,create_dt             string            COMMENT '创建时间'
,update_dt             string
,public_welfare        int
)
comment '$tablecomment'
partitioned by (ds string comment '日期分区字段,yyyy-MM-dd')
row format delimited fields terminated by '\t'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
