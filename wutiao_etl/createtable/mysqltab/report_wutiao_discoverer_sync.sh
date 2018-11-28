#!/bin/bash 

cd `dirname $0`

source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

rid=$1
month=`date +%Y_%m`
database="wutiao"
tablename="$database.report_wutiao_discoverer_sync"
echo "set names 'utf8';
create table $tablename ( \
 fds                        varchar(20)       COMMENT '日期'
,itemtype                   varchar(50)       COMMENT '内容类型'
,sortid                     varchar(50)       COMMENT '垂直类目'
,day_new_discoverer_cnt     int               COMMENT '当日新增发现者数'
,day_active_discoverer_cnt  int               COMMENT '当日在职活跃发现者数'
,day_all_discoverer_cnt     int               COMMENT '当日在职发现者数'
,day_voted_discoverer_cnt   int               COMMENT '当日投票发现者数'
,day_voted_content_sum      int               COMMENT '当日被投票内容数'
,day_voted_content_cnt      int               COMMENT '当日投票总次数'
,day_content_sum            int               COMMENT '当天的发现者库内容数'
,UNIQUE KEY (itemtype,sortid,fds)
) \
comment '发现者统计分析' \
partition by range columns (fds) (\
partition p$nowMonth values less than ( '$nextMonth-01' ), \
partition p_other values less than maxvalue\
);" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port

sh add_partition.sh $tablename $nextMonth