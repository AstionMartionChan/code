#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="adl_wutiao_money_realtime"

hive <<EOF
use $database;
create table if not exists $table (
uid                      string     comment    '用户id'
,money_gain              double     comment    '领币数'
,money_distribute        double     comment    '分币数'
,discover_money_gain     double     comment    '发现者领币数'
,media_status            string     comment    '是否自媒体'
,creative_money_gain     double     comment    '创作者领币数'
)comment '区块链活跃用户统计表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd',hour string comment '时间分区，99代表当日，999代表历史')
row format delimited fields terminated by '\001'
stored as parquet;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF