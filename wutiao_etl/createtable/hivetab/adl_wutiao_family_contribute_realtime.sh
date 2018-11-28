#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="adl_wutiao_family_contribute_realtime"

hive <<EOF
use $database;
create table if not exists $table (
familyid              string     comment    '家庭id'
,ouid                  string     comment    '用户id'
,contributionpoint        double     comment    '贡献力'
)comment '区块链家庭贡献力表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd',hour string comment '时间分区，yyyyMMddHH代表每小时，29day代表过去29天的固定值')
row format delimited fields terminated by '\001'
stored as parquet;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF