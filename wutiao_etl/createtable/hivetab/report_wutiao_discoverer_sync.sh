#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="report_wutiao_discoverer_sync"

tablecomment="发现者统计分析"

hive <<EOF
use $database;
create  table if not exists $table (
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
)
comment '$tablecomment'
partitioned by (ds string comment '日期分区字段,yyyy-MM-dd')
row format delimited fields terminated by '|'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE; 

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
