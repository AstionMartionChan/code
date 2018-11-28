#!/bin/bash 

cd `dirname $0`

source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

rid=$1
month=`date +%Y_%m`
database="wutiao"
tablename="$database.report_wutiao_interact"
echo "set names 'utf8';
create table $tablename ( \
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
,UNIQUE KEY (os,appver,channel,checktype,fds)
) \
comment '用户互动分析' \
partition by range columns (fds) (\
partition p$nowMonth values less than ( '$nextMonth-01' ), \
partition p_other values less than maxvalue\
);" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port

sh add_partition.sh $tablename $nextMonth