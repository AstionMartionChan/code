#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="report_wutiao_invite_behavior"

hive <<EOF
use $database;
create table if not exists $table (
 fds                    string     comment    '统计日期'
,appver                 string     comment    '客户端版本'
,channel                string     comment    '渠道'
,os                     string     comment    '终端'
,dim_type               string     comment    '邀请种类'
,dim_fun                string     comment    '邀请方法'
,total_party_a_uv       bigint     comment    '累计邀请人'
,total_party_b_uv       bigint     comment    '累计被邀请人'
,total_reg_new_phone    bigint     comment    '累计预注册'
,total_reg_new_uv       bigint     comment    '累计预注册激活数'
,party_a_uv             bigint     comment    '邀请人'
,party_b_uv             bigint     comment    '被邀请人'
,reg_new_phone          bigint     comment    '预注册手机号'
,reg_new_uv             bigint     comment    '预注册激活数'
,red_money              double     comment    '红包金额'
,fid_uv                 bigint     comment    '累计家庭数'
,new_fid_uv             bigint     comment    '新增家庭数'
,fid_uid_uv             bigint     comment    '家庭成员数'
)comment '区块链邀请行为统计表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '|'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF