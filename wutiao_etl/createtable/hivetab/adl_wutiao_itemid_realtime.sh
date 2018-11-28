#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="adl_wutiao_itemid_realtime"

hive <<EOF
use $database;
create table if not exists $table (
itemid                    string     comment    '资讯id'
,comment_cnt               bigint     comment    '新用户标识'
,like_cnt                  bigint     comment    '喜欢数'
,read_cnt                  bigint     comment    '阅读数'
,itemtype                 string     comment    '资讯类型'
)comment '区块链活跃用户统计表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd',hour string comment '时间分区，99代表当日')
row format delimited fields terminated by '\001'
stored as parquet;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF