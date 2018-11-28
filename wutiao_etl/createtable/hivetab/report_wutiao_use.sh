#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="report_wutiao_use"

tablecomment="用户使用分析"

hive <<EOF
use $database;
create  table if not exists $table (
 fds                    varchar(20)       COMMENT '日期'
,os                     varchar(50)       COMMENT '终端'
,appver                 varchar(50)       COMMENT '版本'
,channel                varchar(50)       COMMENT '渠道'
,didflag                varchar(50)       COMMENT '设备类型'
,appUV                  int               comment 'appUV'
,appPV                  int               comment 'appPV'
,usetime                bigint            comment '使用时长'
,enterbackground_cnt    int               comment '进入后台次数'
,day_read_cnt           int               comment '阅读次数'
,day_play_cnt           int               comment '播放次数'
,day_like_cnt           int               comment '点赞次数'
,day_comment_cnt        int               comment '评论回复次数'
,day_share_cnt          int               comment '分享次数'
,day_favor_cnt          int               comment '收藏次数'
)
comment '$tablecomment'
partitioned by (ds string comment '日期分区字段,yyyy-MM-dd')
row format delimited fields terminated by '|'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE; 

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
