#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="adl_wutiao_contribute_verify_realtime"

hive <<EOF
use $database;
create table if not exists $table (
uid                      string     comment    '统计日期'
,contributionpoin        double     comment    '贡献力'
,verifypoin              double     comment    '审核力'
,registe_time            bigint     comment    '注册时间'
,inviter                 string     comment    '邀请者'
,last_active_time        bigint     comment    '最后活跃时间'
,verify_flag             string     comment    '是否审核者标识'
,inviter_time            bigint     comment    '邀请时间'
,day_contributionpoint   double     comment    '当日贡献力'
)comment '五条实时贡献力审核力统计表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '\001'
stored as parquet;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF