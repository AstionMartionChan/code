#!/bin/bash 

cd `dirname $0`

source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

rid=$1
month=`date +%Y_%m`
database="wutiao"
tablename="$database.report_wutiao_use"
echo "set names 'utf8';
create table $tablename ( \
 fds                    varchar(20)        COMMENT '日期'
,os                     varchar(50)        COMMENT '终端'
,appver                 varchar(50)        COMMENT '版本'
,channel                varchar(50)        COMMENT '渠道'
,didflag                varchar(50)        COMMENT '设备类型'
,appUV                  int                comment 'appUV'
,appPV                  int                comment 'appPV'
,usetime                bigint             comment '使用时长'
,enterbackground_cnt    int                comment '进入后台次数'
,day_read_cnt           int                comment '阅读次数'
,day_play_cnt           int                comment '播放次数'
,day_like_cnt           int                comment '点赞次数'
,day_comment_cnt        int                comment '评论回复次数'
,day_share_cnt          int                comment '分享次数'
,day_favor_cnt          int                comment '收藏次数'
,UNIQUE KEY (os,appver,channel,didflag,fds)
) \
comment '用户使用分析' \
partition by range columns (fds) (\
partition p$nowMonth values less than ( '$nextMonth-01' ), \
partition p_other values less than maxvalue\
);" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port

sh add_partition.sh $tablename $nextMonth