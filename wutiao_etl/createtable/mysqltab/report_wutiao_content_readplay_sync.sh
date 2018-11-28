#!/bin/bash 

cd `dirname $0`

source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

rid=$1
month=`date +%Y_%m`
database="wutiao"
tablename="$database.report_wutiao_content_readplay_sync"
echo "set names 'utf8';
create table $tablename ( \
 fds                                varchar(128)      COMMENT '日期'
,sortid                             varchar(128)      COMMENT '垂直类目'
,total_article_sum                  int               COMMENT '文章数'
,total_video_sum                    int               COMMENT '视频数'
,day_read_content_sum               int               COMMENT '被阅读的内容数'
,day_read_cnt                       int               COMMENT '阅读次数'
,day_play_content_sum               int               COMMENT '被播放的内容数'
,day_play_cnt                       int               COMMENT '播放次数'
,day_valid_read_content_sum         int               COMMENT '被有效阅读内容数'
,day_valid_read_cnt                 int               COMMENT '有效阅读次数'
,day_valid_play_content_sum         int               COMMENT '被有效播放内容数'
,day_valid_play_cnt                 int               COMMENT '有效播放次数'
,day_active_did_cnt                 int               COMMENT '当日设备数'
,day_read_did_cnt                   int               COMMENT '阅读设备数'
,day_play_did_cnt                   int               COMMENT '播放设备数'
,day_validread_did_cnt              int               COMMENT '有效阅读设备数'
,day_validplay_did_cnt              int               COMMENT '有效播放设备数'
,day_showinpage1_content_sum        int               COMMENT '曝光文章数'
,day_showinpage1_cnt                int               COMMENT '曝光文章次数'
,day_showinpage2_content_sum        int               COMMENT '曝光视频数'
,day_showinpage2_cnt                int               COMMENT '曝光视频次数'
,UNIQUE KEY (sortid,fds)
) \
comment '内容阅读播放' \
partition by range columns (fds) (\
partition p$nowMonth values less than ( '$nextMonth-01' ), \
partition p_other values less than maxvalue\
);" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port

sh add_partition.sh $tablename $nextMonth