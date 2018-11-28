#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="odl_wutiao_users_sync"

tablecomment="五条用户信息表"

hive <<EOF
use $database;
create  table if not exists $table (
 uid                   bigint            COMMENT '用户id'
,realname              varchar(20)       COMMENT '真实姓名'
,family_id             bigint            COMMENT '家庭ID'
,sex                   int               COMMENT '性别1男2女'
,phone                 varchar(20)       COMMENT '手机号'
,province              varchar(20)       COMMENT '省'
,city                  varchar(20)       COMMENT '市'
,district              varchar(20)       COMMENT '区'
,field                 varchar(20)       COMMENT '领域'
,id_card_num           varchar(20)       COMMENT '身份证号'
,alipay_account        varchar(100)      COMMENT '支付宝账户'
,wallet_address        varchar(50)       COMMENT '区块链钱包地址'
,accsesstoken          varchar(50)       COMMENT 'Token'
,source                int               COMMENT '1自然注册2家庭邀请3营运后台注册4三筒数据导入'
,status                int               COMMENT '状态0正常   1被禁'
,role                  int               COMMENT '角色:0普通用户1自媒体2发现者3发现者+自媒体'
,create_dt             string            COMMENT '创建时间'
,update_dt             string            COMMENT '更新时间'
,face_auth_dt          string            COMMENT '最后一次实人认证的时间'
)
comment '$tablecomment'
partitioned by (ds string comment '日期分区字段,yyyy-MM-dd')
row format delimited fields terminated by '\t'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
