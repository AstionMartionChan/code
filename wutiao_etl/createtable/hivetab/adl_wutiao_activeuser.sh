#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="adl_wutiao_activeuser"

hive <<EOF
use $database;
create table if not exists $table (
fds                      string     comment    '统计日期'
,user_flag               string     comment    '新用户标识'
,appver                  string     comment    '客户端版本'
,channel                 string     comment    '渠道'
,model                   string     comment    '设备型号'
,res                     string     comment    '分辨率'
,os                      string     comment    '操作系统'
,nettype                 string     comment    '网络类型'
,carrier                 string     comment    '运营商'
,comment_user_cnt        bigint     comment    '评论用户数'
,comment_cnt             bigint     comment    '评论量'
,vote_user_cnt           bigint     comment    '投票用户数'
,vote_cnt                bigint     comment    '投票量'
,search_user_cnt         bigint     comment    '搜索用户数'
,search_cnt              bigint     comment    '搜索量'
,favor_user_cnt          bigint     comment    '收藏用户数'
,favor_cnt               bigint     comment    '收藏量'
,like_user_cnt           bigint     comment    '点赞用户数'
,like_cnt                bigint     comment    '点赞量'
,share_user_cnt          bigint     comment    '分享用户数'
,share_cnt               bigint     comment    '分享量'
,attention_user_cnt      bigint     comment    '关注用户数'
,attention_cnt           bigint     comment    '关注量'
,reply_user_cnt          bigint     comment    '回复用户数'
,reply_cnt               bigint     comment    '回复量+'
,enterbackground_cnt     bigint     comment    '进入后台次数'
,use_time                bigint     comment    '使用总时长'
,pagenum                 bigint     comment    '使用页面数'
,crash_cnt               bigint     comment    '崩溃次数'
,crash_user_cnt          bigint     comment    '崩溃用户数'
,openclient_user_cnt     bigint     comment    '启动用户数'
,openclient_cnt          bigint     comment    '启动次数'
,report_user_cnt         bigint     comment    '举报用户数'
,report_cnt              bigint     comment    '举报次数'
,nointerest_user_cnt     bigint     comment    '不感兴趣用户数'
,nointerest_cnt          bigint     comment    '不感兴趣次数'
,appPV                   bigint     comment    'appPV'
,appUV                   bigint     comment    'appUV'
,enterfront_cnt          bigint     comment    '进入前台次数'
,gaptime                 bigint     comment    '从后台切入前台时间间隔'
)comment '区块链活跃用户统计表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd',ut string comment '用户分区')
row format delimited fields terminated by '\001'
stored as parquet;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF