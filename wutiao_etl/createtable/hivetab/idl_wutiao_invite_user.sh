#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="idl_wutiao_invite_user"

hive <<EOF
use $database;
create table if not exists $table (
 invite_uid                string     comment    '邀请人uid'
,invitee_uid               string     comment    '被邀请人uid'
,invite_type               string     comment    '邀请种类'
,appver                    string     comment    '末次登录系统版本'
,os                        string     comment    '末次登录操作系统'
,channel                   string     comment    '末次登录渠道'
,invite_registe_time       bigint     comment    '邀请人注册时间'
,invitee_registe_time      bigint     comment    '被邀请人注册时间'
,invite_first_login_time   bigint     comment    '邀请人首次登录活跃APP时间'
,invite_last_login_time    bigint     comment    '邀请人末次登录活跃APP时间'
,invitee_first_login_time  bigint     comment    '被邀请人首次登录活跃APP时间'
,invitee_last_login_time   bigint     comment    '被邀请人末次登录活跃APP时间'
,invite_phone              bigint     comment    '邀请人手机号'
,invitee_phone             bigint     comment    '被邀请人手机号'
,invite_first_time         bigint     comment    '首次邀请时间'
,invite_last_time          bigint     comment    '末次邀请时间'
,invite_red_money          double     comment    '邀请人累计获得红包金额'
,invite_family_id          string     comment    '邀请人的家庭id'
,invitee_family_id         string     comment    '被邀请人的家庭id'
,invite_registe_inviter    string     comment    '邀请人注册时的邀请者'
,invitee_registe_inviter   string     comment    '被邀请人注册时的邀请者'
)comment '区块链邀请行为用户中间表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '\001'
stored as parquet;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF