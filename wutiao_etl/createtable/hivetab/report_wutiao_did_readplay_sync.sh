#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="report_wutiao_did_readplay_sync"

tablecomment="设备阅读播放"

hive <<EOF
use $database;
create  table if not exists $table (
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
)
comment '$tablecomment'
partitioned by (ds string comment '日期分区字段,yyyy-MM-dd')
row format delimited fields terminated by '|'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE; 

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
