#!/bin/bash 

cd `dirname $0`

source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

rid=$1
month=`date +%Y_%m`
database="wutiao"
tablename="$database.report_wutiao_did_readplay_sync"
echo "set names 'utf8';
create table $tablename ( \
 fds                                varchar(20)       COMMENT '日期'
,os                                 varchar(50)       COMMENT '终端'
,appver                             varchar(50)       COMMENT '版本'
,channel                            varchar(50)       COMMENT '渠道'
,didflag                            varchar(50)       COMMENT '新老设备'
,sortid                             varchar(50)       COMMENT '垂直类目'
,day_read_cnt                       int               COMMENT '阅读次数'
,day_play_cnt                       int               COMMENT '播放次数'
,day_valid_read_cnt                 int               COMMENT '有效阅读次数'
,day_valid_play_cnt                 int               COMMENT '有效播放次数'
,day_active_did_cnt                 int               COMMENT '当日设备数'
,day_read_did_cnt                   int               COMMENT '阅读设备数'
,day_play_did_cnt                   int               COMMENT '播放设备数'
,day_validread_did_cnt              int               COMMENT '有效阅读设备数'
,day_validplay_did_cnt              int               COMMENT '有效播放设备数'
,day_showinpage1_cnt                int               COMMENT '曝光文章次数'
,day_showinpage2_cnt                int               COMMENT '曝光视频次数'
,day_readplay_did_cnt               int               COMMENT '阅读播放设备数'
,day_valid_readplay_did_cnt         int               COMMENT '有效阅读播放设备数'
,UNIQUE KEY (os,appver,channel,didflag,sortid,fds)
) \
comment '设备阅读播放' \
partition by range columns (fds) (\
partition p$nowMonth values less than ( '$nextMonth-01' ), \
partition p_other values less than maxvalue\
);" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port

sh add_partition.sh $tablename $nextMonth