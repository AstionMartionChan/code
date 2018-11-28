#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="report_wutiao_itemid_statistic"

hive <<EOF
use $database;
create table if not exists $table (
 fds                           varchar(128)     comment    '日期'
,sortid                        varchar(128)     comment    '垂直类目'
,itemtype                      varchar(128)     comment    '文章类型'
,itemid_comment_sum            int              comment    '评论'
,itemid_vote_sum               int              comment    '投票'
,itemid_like_sum               int              comment    '点赞'
,itemid_report_sum             int              comment    '举报'
,itemid_nointerest_sum         int              comment    '不感兴趣'
)comment '内容统计析'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '|'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF