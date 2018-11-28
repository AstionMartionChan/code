#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="idl_wutiao_invite_user_label"

hive <<EOF
use $database;
create table if not exists $table (
 appver                    string     comment    '末次登录系统版本（根据邀请人的系统版本）'
,os                        string     comment    '末次登录操作系统（根据邀请人的操作系统）'
,channel                   string     comment    '末次登录渠道（根据邀请人的登录渠道）'
,invite_uid                string     comment    '邀请人uid'
,invitee_uid               string     comment    '被邀请人uid'
,invite_family_id	   string     comment    '邀请人家庭id'
,invitee_family_id	   string     comment	 '被邀请人家庭id'
,invite_last_ds		   string     comment    '邀请人最后邀请时间'
,invite_registe_ds         string     comment    '邀请人注册时间'
,invitee_registe_ds        string     comment    '被邀请人注册时间'
,invite_phone              bigint     comment    '邀请人手机号'
,invitee_phone             bigint     comment    '被邀请人手机号'
,invite_type               string     comment    '邀请种类 1：h5邀请家人 2：h5邀请好友 3：邀请码邀请家人 4：邀请码邀请好友'
,dim_type		   string     comment    '邀请分类 family, friend'
,dim_fun		   string     comment    '邀请方法 code，h5'
,is_code		   bigint     comment    '是否是邀请码邀请 0否1是'
,status		           string     comment    '注册事件时邀请类型 1：h5邀请家人 2：h5邀请好友'
,has_family_h		   bigint     comment    '是否是h5邀请家人 0否1是'
,has_family_c		   bigint     comment    '是否是邀请码邀请家人 0否1是'
,has_friend_h		   bigint     comment    '是否是h5邀请好友 0否1是'
,has_friend_c		   bigint     comment    '是否是邀请码邀请家人 0否1是'
,type_list		   string     comment	 '邀请类型集合'
)comment '区块链邀请行为用户标签中间表（统计每天的数据）'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '\001'
stored as parquet;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
