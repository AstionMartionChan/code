#!/bin/bash 

cd `dirname $0`

source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

rid=$1
month=`date +%Y_%m`
database="wutiao"
tablename="$database.report_wutiao_read_path"
echo "set names 'utf8';
create table $tablename ( \
fds varchar(128) COMMENT '日期'
,os varchar(128) COMMENT '终端'
,channel varchar(128) COMMENT '渠道'
,itemtype varchar(128) COMMENT '类别（视频/文章）'
,homepage_pv int COMMENT '页面浏览pv'
,homepage_uv int COMMENT '页面浏览uv'
,second_pv int COMMENT '二跳浏览pv'
,second_uv int COMMENT '二跳浏览uv'
,content_pv int COMMENT '详情播放页pv'
,content_uv int COMMENT '详情播放页uv'
,readlist_pv int COMMENT '列表页pv'
,readlist_uv int COMMENT '列表页uv'
,exit_pv int COMMENT '退出数pv'
,exit_uv int COMMENT '退出数uv'
,UNIQUE KEY (os,channel,itemtype,fds)
) \
comment '访问路径分析' \
partition by range columns (fds) (\
partition p$nowMonth values less than ( '$nextMonth-01' ), \
partition p_other values less than maxvalue\
);" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port

sh add_partition.sh $tablename $nextMonth