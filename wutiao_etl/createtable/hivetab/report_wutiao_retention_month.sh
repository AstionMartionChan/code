#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="report_wutiao_retention_month"

tablecomment="留存月数据"

hive <<EOF
use $database;
create  table if not exists $table (
 fds                       varchar(20)     COMMENT '日期'
,os                        varchar(50)     COMMENT '终端'
,appver                    varchar(50)     COMMENT '版本'
,channel                   varchar(50)     COMMENT '渠道'
,month_active_did_cnt      int             COMMENT '月活跃设备'
,month_active_ouid_cnt     int             COMMENT '月活跃用户'
,month_new_did_cnt         int             COMMENT '月新增设备'
,month_new_ouid_cnt        int             COMMENT '月新增注册'
,month_withdraw_ouid_cnt   int             COMMENT '月提现人数'
,month_withdraw_rmb        double          COMMENT '月提现金额'
,month1_retention          int             COMMENT '1月留存'
,month2_retention          int             COMMENT '2月留存'
,month3_retention          int             COMMENT '3月留存'
,month4_retention          int             COMMENT '4月留存'
,month5_retention          int             COMMENT '5月留存'
,month6_retention          int             COMMENT '6月留存'
)
comment '$tablecomment'
partitioned by (ds string comment '日期分区字段,yyyy-MM-dd')
row format delimited fields terminated by '|'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE; 

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
