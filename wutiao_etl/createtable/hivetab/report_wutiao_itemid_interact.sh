#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="report_wutiao_itemid_interact"

hive <<EOF
use $database;
create table if not exists $table (
 fds                    varchar(128)     comment    '日期'
,sortid                 varchar(128)     comment    '垂直类目'
,itemtype               varchar(128)     comment    '文章类型'
,comment_cnt            int              comment    '评论'
,vote_cnt               int              comment    '投票'
,like_cnt               int              comment    '点赞'
,report_cnt             int              comment    '举报'
,nointerest_cnt         int              comment    '不感兴趣'
)comment '内容互动分析'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '|'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF