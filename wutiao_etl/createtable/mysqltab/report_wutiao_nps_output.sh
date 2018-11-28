#!/bin/bash 

cd `dirname $0`

source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

db_ip="172.17.4.23"
db_user="user_market_center"
db_pwd="8QK6usermarketcenter3@Va9wR6"
db_port="3306"

rid=$1
month=`date +%Y_%m`
database="user_market_center"
tablename="$database.report_wutiao_nps_output"
echo "set names 'utf8';
create table $tablename ( \
 dt                         varchar(20)       COMMENT '周期类型'
,fds                        varchar(20)       COMMENT '日期'
,active_user_cnt            int               COMMENT '活跃用户数'
,create_user_cnt            int               COMMENT '邀请者人数'
,invited_user_cnt           int               COMMENT '被邀请人数'
,recommend_user_cnt         int               COMMENT '推荐者人数'
,unactive_user_cnt          int               COMMENT '被动者人数'
,derogate_user_cnt          int               COMMENT '贬损者人数'
) \
comment 'nps数据' \
;" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port
