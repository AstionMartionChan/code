#!/bin/bash 

cd `dirname $0`

source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

rid=$1
month=`date +%Y_%m`
database="wutiao"
tablename="$database.report_wutiao_content_interact_sync"
echo "set names 'utf8';
create table $tablename ( \
 fds                        varchar(20)       COMMENT '日期'
,itemtype                   varchar(50)       COMMENT '内容类型'
,sortid                     varchar(50)       COMMENT '垂直类目'
,total_itemid_sum           int               COMMENT '总内容数'
,day_readplay_content_sum   int               COMMENT '阅读播放内容数'
,day_share_content_sum      int               COMMENT '被分享的内容总数'
,day_share_cnt              int               COMMENT '被分享的内容数'
,day_favour_content_sum     int               COMMENT '被收藏的内容总数'
,day_favour_cnt             int               COMMENT '被收藏的总数'
,day_comment_content_sum    int               COMMENT '被评论回复的内容总数'
,day_comment_cnt            int               COMMENT '被评论回复的内容数'
,day_like_content_sum       int               COMMENT '被点赞的内容总数'
,day_like_cnt               int               COMMENT '被点赞的内容数'
,day_attention_cnt          int               COMMENT '关注次数'
,day_nointerest_cnt         int               COMMENT '不感兴次数'
,day_report_cnt             int               COMMENT '举报次数'
,day_active_did_cnt         int               COMMENT '活跃设备数'
,day_readplay_did_cnt       int               COMMENT '阅读播放设备数'
,day_share_did_cnt          int               COMMENT '分享设备数'
,day_favor_did_cnt          int               COMMENT '收藏设备数'
,day_comment_did_cnt        int               COMMENT '评论设备数'
,day_like_did_cnt           int               COMMENT '点赞设备数'
,day_attention_did_cnt      int               COMMENT '关注设备数'
,UNIQUE KEY (itemtype,sortid,fds)
) \
comment '内容互动分析' \
partition by range columns (fds) (\
partition p$nowMonth values less than ( '$nextMonth-01' ), \
partition p_other values less than maxvalue\
);" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port

sh add_partition.sh $tablename $nextMonth