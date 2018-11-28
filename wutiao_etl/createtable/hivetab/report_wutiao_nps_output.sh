#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="report_wutiao_nps_output"

tablecomment="nps数据"

hive <<EOF
use $database;
create  table if not exists $table (
 fds                        varchar(20)       COMMENT '日期'
,active_user_cnt            int               COMMENT '活跃用户数'
,create_user_cnt            int               COMMENT '邀请者人数'
,invited_user_cnt           int               COMMENT '被邀请人数'
,recommend_user_cnt         int               COMMENT '推荐者人数'
,unactive_user_cnt          int               COMMENT '被动者人数'
,derogate_user_cnt          int               COMMENT '贬损者人数'
)
comment '$tablecomment'
partitioned by (dt string comment '周期类型',ds string comment '日期分区字段,yyyy-MM-dd')
row format delimited fields terminated by '|'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE; 

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
