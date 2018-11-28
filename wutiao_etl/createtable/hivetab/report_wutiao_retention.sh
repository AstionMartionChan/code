#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="report_wutiao_retention"

tablecomment="留存用户"

hive <<EOF
use $database;
create  table if not exists $table (
fds varchar(128) COMMENT '日期'
,os varchar(128) COMMENT '终端'
,appver varchar(128) COMMENT '版本'
,channel varchar(128) COMMENT '渠道'
,newuser_cnt int COMMENT '新增帐号数'
,day1_retention int COMMENT '1日留存'
,day2_retention int COMMENT '2日留存'
,day3_retention int COMMENT '3日留存'
,day4_retention int COMMENT '4日留存'
,day5_retention int COMMENT '5日留存'
,day6_retention int COMMENT '6日留存'
,day7_retention int COMMENT '7日留存'
,day15_retention int COMMENT '15日留存'
,day30_retention int COMMENT '30日留存'
)
comment '$tablecomment'
partitioned by (ds string comment '日期分区字段,yyyy-MM-dd')
row format delimited fields terminated by '|'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE; 

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
