#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="adl_wutiao_play_read_statistics"

hive <<EOF
use $database;
create table if not exists $table (
fds                      string     comment    '统计日期'
,os                      string     comment    '终端'
,channel                 string     comment    '渠道'
,sortid                  string     comment    '垂直类目'
,usertype                string     comment    '用户类别'
,itemtype                string     comment    '类别'
,his_play_cnt            bigint     comment    '累计播放量'
,play_cnt                bigint     comment    '每日播放量'
,valid_play_cnt          bigint     comment    '每日有效播放量'
,finish_play_cnt         bigint     comment    '每日播完量'
,playtime                bigint     comment    '每日播放时长'
,recommend_play_cnt      bigint     comment    '相关推荐播放量'
,his_read_cnt            bigint     comment    '累计阅读量'
,read_cnt                bigint     comment    '每日阅读量'
,valid_read_cnt          bigint     comment    '每日有效阅读量'
,finish_read_cnt         bigint     comment    '每日阅读完成量'
,leaveread_cnt           bigint     comment    '每日离开阅读次数'
,readtime                bigint     comment    '每日阅读时间'
,recommend_read_cnt      bigint     comment    '相关推荐阅读量'
,pagenum                 bigint     comment    '使用页面数量'
,comment_cnt             bigint     comment    '评论量'
,reforward_cnt           bigint     comment    '转发量'
,reward_cnt              bigint     comment    '打赏量'
,attention_cnt           bigint     comment    '关注量'
,cancel_attention_cnt    bigint     comment    '取消关注'
,favour_cnt              bigint     comment    '收藏量'
,cancel_favour_cnt       bigint     comment    '取消收藏'
,like_cnt                bigint     comment    '点赞量'
,cancel_like_cnt         bigint     comment    '取消点赞'
,reply_cnt               bigint     comment    '回复量'
)comment '播放阅读事件统计'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '\001'
stored as parquet;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF