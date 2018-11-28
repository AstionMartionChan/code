#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="idl_wutiao_user_family"

hive <<EOF
use $database;
create table if not exists $table (
 ouid                    string     comment    '用户id'
,familyid                string     comment    '家庭id'
,createid                string     comment    '创建者id'
,createdate              bigint     comment    '创建日期'
,inviterid               string     comment    '邀请者id'
,invitedate              bigint     comment    '邀请日期'
,first_os                string     comment    '首次终端'
,last_os                 string     comment    '末次终端'
,first_appver            string     comment    '首次版本'
,last_appver             string     comment    '末次版本'
,first_channel           string     comment    '首次渠道'
,last_channel            string     comment    '末次渠道'
)comment '区块链用户家庭中间表'
partitioned by (ut string comment '用户分区',ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '\001'
stored as parquet;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF