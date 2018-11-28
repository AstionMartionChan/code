#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="report_wutiao_interact"

tablecomment="用户互动分析"

hive <<EOF
use $database;
create  table if not exists $table (
fds varchar(128) COMMENT '日期'
,os varchar(128) COMMENT '终端'
,appver varchar(128) COMMENT '版本'
,channel varchar(128) COMMENT '渠道'
,checktype varchar(128) COMMENT '次数/用户数'
,comment_cnt int COMMENT '评论数量'
,vote_cnt int COMMENT '投票数量'
,search_cnt int COMMENT '搜索数量'
,favour_cnt int COMMENT '收藏数量'
,like_cnt int COMMENT '点赞数量'
,share_cnt int COMMENT '分享数量'
,attention_cnt int COMMENT '关注数量'
,reply_cnt int COMMENT '回复数量'
,report_cnt int COMMENT '举报量'
,nointerest_cnt int COMMENT '不感兴趣量'
)
comment '$tablecomment'
partitioned by (ds string comment '日期分区字段,yyyy-MM-dd')
row format delimited fields terminated by '|'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE; 

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
