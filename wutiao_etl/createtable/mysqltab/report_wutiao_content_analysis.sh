#!/bin/bash 

cd `dirname $0`

source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

rid=$1
month=`date +%Y_%m`
database="wutiao"
tablename="$database.report_wutiao_content_analysis"
echo "set names 'utf8';
create table $tablename ( \
fds varchar(20) COMMENT '日期'
,os varchar(50) COMMENT '终端'
,channel varchar(50) COMMENT '渠道'
,sortid varchar(50) COMMENT '垂直类目'
,usertype varchar(20) COMMENT '用户类别'
,itemtype varchar(20) COMMENT '类别(视频/文章)'
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
,UNIQUE KEY (os,channel,sortid,usertype,itemtype,fds)
) \
comment '内容分析' \
partition by range columns (fds) (\
partition p$nowMonth values less than ( '$nextMonth-01' ), \
partition p_other values less than maxvalue\
);" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port

sh add_partition.sh $tablename $nextMonth