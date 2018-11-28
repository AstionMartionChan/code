#!/bin/bash 

cd `dirname $0`

source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

rid=$1
month=`date +%Y_%m`
database="wutiao"
tablename="$database.report_wutiao_content_funnel"
echo "set names 'utf8';
create table $tablename ( \
fds varchar(128) COMMENT '日期'
,os varchar(128) COMMENT '终端'
,channel varchar(128) COMMENT '渠道'
,itemtype varchar(128) COMMENT '类别（视频/文章）'
,content_pv int COMMENT '详情页访问pv'
,content_uv int COMMENT '详情页访问uv'
,content_second_pv int COMMENT '详情页点击pv'
,content_second_uv int COMMENT '详情页点击uv'
,content_readlist_pv int COMMENT '详情页列表页pv'
,content_readlist_uv int COMMENT '详情页列表页uv'
,content_exit_pv int COMMENT '详情页退出pv'
,content_exit_uv int COMMENT '详情页退出uv'
,UNIQUE KEY (os,channel,itemtype,fds)
) \
comment '详情页转换漏斗' \
partition by range columns (fds) (\
partition p$nowMonth values less than ( '$nextMonth-01' ), \
partition p_other values less than maxvalue\
);" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port

sh add_partition.sh $tablename $nextMonth