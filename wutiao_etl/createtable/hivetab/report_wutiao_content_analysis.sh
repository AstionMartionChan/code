#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="report_wutiao_content_analysis"

tablecomment="内容分析"
hive <<EOF
use $database;
create  table if not exists $table (
fds varchar(128) COMMENT '日期'
,os varchar(128) COMMENT '终端'
,channel varchar(128) COMMENT '渠道'
,sortid varchar(128) COMMENT '垂直类目'
,usertype varchar(128) COMMENT '用户类别'
,itemtype varchar(128) COMMENT '类别（视频/文章）'
,his_play_cnt int COMMENT '累计播放量'
,play_cnt int COMMENT '每日日播放量'
,valid_play_cnt int COMMENT '每日有效播放量'
,finish_play_cnt int COMMENT '播完量'
,avg_playtime int COMMENT '平均播放时长'
,recommend_play_cnt int COMMENT '相关推荐播放次数'
,pagenum int COMMENT '使用页面数量'
,his_read_cnt int COMMENT '累计阅读量'
,read_cnt int COMMENT '每日阅读量'
,valid_read_cnt int COMMENT '每日有效阅读量'
,finish_read_cnt int COMMENT '阅读完成量'
,jumpout_cnt int COMMENT '跳出次数'
,recommend_read_cnt int COMMENT '相关推荐阅读率'
,comment_cnt int COMMENT '评论量'
,reforward_cnt int COMMENT '转发量'
,reward_cnt int COMMENT '打赏量'
,attention_cnt int COMMENT '关注量'
)
comment '$tablecomment'
partitioned by (ds string comment '日期分区字段,yyyy-MM-dd')
row format delimited fields terminated by '|'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE; 

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
