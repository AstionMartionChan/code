#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="report_wutiao_did_interact_sync"

tablecomment="设备互动分析"

hive <<EOF
use $database;
create  table if not exists $table (
 fds                                varchar(20)       COMMENT '日期'
,os                                 varchar(50)       COMMENT '终端'
,appver                             varchar(50)       COMMENT '版本'
,channel                            varchar(50)       COMMENT '渠道'
,didflag                            varchar(50)       COMMENT '新老设备'
,itemtype                           varchar(50)       COMMENT '内容类型'
,sortid                             varchar(50)       COMMENT '垂直类目'
,day_share_cnt                      int               COMMENT '被分享的次数'
,day_favour_cnt                     int               COMMENT '被收藏的次数'
,day_comment_cnt                    int               COMMENT '被评论回复的次数'
,day_like_cnt                       int               COMMENT '被点赞的次数'
,day_attention_cnt                  int               COMMENT '关注次数'
,day_nointerest_cnt                 int               COMMENT '不感兴次数'
,day_report_cnt                     int               COMMENT '举报次数'
,day_active_did_cnt                 int               COMMENT '活跃设备数'
,day_share_did_cnt                  int               COMMENT '分享设备数'
,day_favor_did_cnt                  int               COMMENT '收藏设备数'
,day_comment_did_cnt                int               COMMENT '评论设备数'
,day_like_did_cnt                   int               COMMENT '点赞设备数'
,day_attention_did_cnt              int               COMMENT '关注设备数'
)
comment '$tablecomment'
partitioned by (ds string comment '日期分区字段,yyyy-MM-dd')
row format delimited fields terminated by '|'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE; 

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
