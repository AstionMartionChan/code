#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="report_wutiao_invite_fission"

hive <<EOF
use $database;
create table if not exists $table (
 fds                     string     comment    '统计日期'
,appver                  string     comment    '客户端版本'
,channel                 string     comment    '渠道'
,os                      string     comment    '终端'
,total_invite_count      bigint     comment    '累计邀请人'
,total_invitee_count	 bigint	    comment    '累计被邀请人'
,invite_count		 bigint	    comment    '邀请人'
,invitee_count		 bigint	    comment    '被邀请人'
,new_invite_count	 bigint	    comment    '新用户邀请人'
,new_invitee_count	 bigint	    comment    '新用户被邀请人'
,old_invite_count	 bigint	    comment    '老用户邀请人'
,old_invitee_count	 bigint	    comment    '老用户被邀请人'
,registe_invitee_count	 bigint	    comment    '邀请注册'
,new_register		 bigint	    comment    '新增注册'
,old_register		 bigint	    comment    '去新活跃（老用户）'
)comment '区块链邀请用户裂变表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '|'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
