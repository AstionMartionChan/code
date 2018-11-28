#!/bin/bash 

cd `dirname $0`

source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

rid=$1
month=`date +%Y_%m`
database="wutiao"
tablename="$database.report_wutiao_adv_didclick"
echo "set names 'utf8';
create table $tablename ( \
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
,UNIQUE KEY (os,appver,channel,didflag,fds)
) \
comment '广告位转化' \
partition by range columns (fds) (\
partition p$nowMonth values less than ( '$nextMonth-01' ), \
partition p_other values less than maxvalue\
);" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port

sh add_partition.sh $tablename $nextMonth