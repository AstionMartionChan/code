#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="adl_wutiao_discover_moneygain_rank_realtime"

hive <<EOF
use $database;
create table if not exists $table (
uid                        string    comment   '用户uid',
discover_money_gain        double    comment   '发现者领币数',
verifypoint                double    comment   '审核力',
vote_cnt                   bigint    comment   '投票数',
meida_status               string    comment   '自媒体标识'
)comment '发现者领币排行榜'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd',hour string comment '小时,yyyyMMddHH')
row format delimited fields terminated by '|'
stored as textfile;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF