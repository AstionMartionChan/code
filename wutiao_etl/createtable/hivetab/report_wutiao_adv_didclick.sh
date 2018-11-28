#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="report_wutiao_adv_didclick"

tablecomment="用户广告位转化"

hive <<EOF
use $database;
create  table if not exists $table (
 fds                    varchar(20)       COMMENT '日期'
,os                     varchar(50)       COMMENT '终端'
,appver                 varchar(50)       COMMENT '版本'
,channel                varchar(50)       COMMENT '渠道'
,didflag                varchar(50)       COMMENT '设备类型'
,kp_display_uv          int               comment '开屏曝光'
,kp_click_uv            int               comment '开屏点击'
,info_flow_display_uv   int               comment '信息流曝光'
,info_flow_click_uv     int               comment '信息流点击'
,detail_display_uv      int               comment '详情曝光'
,detail_click_uv        int               comment '详情点击'
,hot_spot_display_uv    int               comment '热点曝光'
,hot_spot_click_uv      int               comment '热点点击'
,mine_display_uv        int               comment '我的曝光'
,mine_click_uv          int               comment '我的点击'
,withdraw_display_uv    int               comment '提现曝光'
,withdraw_click_uv      int               comment '提现点击'
)
comment '$tablecomment'
partitioned by (ds string comment '日期分区字段,yyyy-MM-dd')
row format delimited fields terminated by '|'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE; 

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
