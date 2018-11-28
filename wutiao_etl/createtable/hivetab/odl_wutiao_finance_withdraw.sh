#! /bin/sh
 
cd `dirname $0`
source /etc/profile
 
database="wutiao"
table="odl_wutiao_finance_withdraw"
 
hive <<EOF
use $database;
create table if not exists $table (
    uid          string   comment '用户id'
    ,ts           bigint   comment '时间'
    ,ip           string   comment 'ip'
    ,status       int      comment '提现的状态，1：申请提现，2：提现成功，3：提现失败'
    ,coin         double   comment '金币数量'
    ,rate         double   comment '金币和rmb的比率'
    ,rmb          double   comment '提现的rmb'
    ,org_ds       string   comment '原始日志里的日期分区'
)comment '财务-提现记录快照'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '|'
stored as TEXTFILE;
 
alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');
 
EOF