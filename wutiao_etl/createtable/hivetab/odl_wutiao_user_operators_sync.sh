#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="odl_wutiao_user_operators_sync"

tablecomment="五条用户操作表"

hive <<EOF
use $database;
create  table if not exists $table (
 uid                      bigint         COMMENT 'uid'
,operators_name           varchar(50)    COMMENT '运营者姓名'
,operators_identity_type  int            COMMENT '证件类型0身份证'
,operators_identity_card  varchar(50)    COMMENT '证件号码'
,operators_images         varchar(255)   COMMENT '证件照片'
,operators_phone          varchar(50)    COMMENT '电话'
,operators_email          varchar(50)    COMMENT '邮箱'
,operators_accredit       varchar(255)   COMMENT '授权证件照'
,auxiliary_information    varchar(255)   COMMENT '辅助信息'
,other_contact            varchar(255)   COMMENT '其他联系方式'
,professional             varchar(255)   COMMENT '专业证书'
,create_dt                string         COMMENT '创建时间'
,update_dt                string         COMMENT '更新时间'
,\`_slot\`                int            COMMENT 'do NOT modify'
)
comment '$tablecomment'
partitioned by (ds string comment '日期分区字段,yyyy-MM-dd')
row format delimited fields terminated by '\t'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
