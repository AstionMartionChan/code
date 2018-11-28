#! /bin/sh
 
cd `dirname $0`
source /etc/profile
 
database="wutiao"
table="odl_wutiao_finance_tx_record"
 
hive <<EOF
use $database;
create table if not exists $table (
     id           string   comment '主键'
    ,type         int      comment '交易类型 1-奖励 2-提现'
    ,tx_hash      string   comment '交易hash'
    ,from_address string   comment '转出地址,当用户提现时取该字段'
    ,to_address   string   comment '转入地址,当用户奖励时取该字段'
    ,update_time  string   comment '最近更新时间'
    ,state        int      comment '交易状态 0-交易尚未发送 1-交易成功 2-交易确认中 3-交易失败 4-合约接收成功'
    ,create_time  string   comment '创建记录时间'
    ,amount       double   comment '金额'
    ,nonce        bigint   comment '发起交易账户的nonce值'
    ,push_state   bigint   comment '将交易状态（成功/失败）推送给业务调用方，推送成功或失败'
    ,push_times   bigint   comment '交易状态推送重试次数'
    ,push_time    string   comment '最后推送时间'
    ,ts           bigint   comment '时间戳'
)comment '财务-奖励和提现记录快照'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '\t'
stored as TEXTFILE;
 
alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');
 
EOF