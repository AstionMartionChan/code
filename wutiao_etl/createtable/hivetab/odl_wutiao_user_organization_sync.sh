#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="odl_wutiao_user_organization_sync"

tablecomment="五条用户组织表"

hive <<EOF
use $database;
create  table if not exists $table (
 uid                  bigint
,organization_name    varchar(255)      COMMENT '//机构名称'
,organization_code    varchar(255)      COMMENT '//机构代码'
,organization_prove   varchar(255)      COMMENT '//机构代码证'
,organization_level   int               COMMENT '0无等级1国家级2省部级3厅级4县处级5县处级以下'
,location             varchar(255)      COMMENT '位置'
,create_dt            string            COMMENT '创建时间'
,update_dt            string            COMMENT '更新时间'
,\`_slot\`            int               COMMENT 'do NOT modify'
)
comment '$tablecomment'
partitioned by (ds string comment '日期分区字段,yyyy-MM-dd')
row format delimited fields terminated by '\t'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
