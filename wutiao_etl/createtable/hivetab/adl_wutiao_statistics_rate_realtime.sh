#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="adl_wutiao_statistics_rate_realtime"

hive <<EOF
use $database;
create table if not exists $table (
total_palyread_usercnt     bigint    comment   '总浏览',
validread_usercnt          bigint    comment   '有效阅读人数',
like_usercnt               bigint    comment   '点赞人数',
comment_usercnt            bigint    comment   '评论人数',
share_usercnt              bigint    comment   '分享人数',
validread_rate             double    comment   '有效浏览率',
like_rate                  double    comment   '点赞率',
comment_rate               double    comment   '评论率',
share_rate                 double    comment   '分享率'
)comment '区块链平台最近30天统计表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '|'
stored as textfile;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF