#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="report_wutiao_colligate_app"

tablecomment="综合数据(app)"

hive <<EOF
use $database;
create  table if not exists $table (
 fds                         date                comment     '日期分区字段,yyyy-MM-dd'
,os                          varchar(100)        comment     '终端'
,appver                      varchar(100)        comment     '版本'
,channel                     varchar(100)        comment     '渠道'
,total_newdid_cnt            bigint              comment     '累计激活数'
,activedid_cnt               bigint              comment     '活跃设备数'
,newdid_cnt                  bigint              comment     '新增激活数'
,total_newuser_cnt           bigint              comment     '累计新增账号数'
,activeuser_cnt              bigint              comment     '活跃帐号数'
,newuser_cnt                 bigint              comment     '新增帐号数'
,withdraw_success_usercnt    bigint              comment     '提现成功用户数'
,withdraw_success_rmb        double              comment     '提现成功金额'
,day1_retention              bigint              COMMENT     '1日留存'
,day3_retention              bigint              COMMENT     '3日留存'
,day7_retention              bigint              COMMENT     '7日留存'
)
comment '$tablecomment'
partitioned by (ds string comment '日期分区字段,yyyy-MM-dd')
row format delimited fields terminated by '|'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE; 

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
