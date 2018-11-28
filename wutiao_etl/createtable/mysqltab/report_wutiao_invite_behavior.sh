#!/bin/bash 

cd `dirname $0`
source /etc/profile
source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

month=`date +%Y_%m`
tablename="wutiao.report_wutiao_invite_behavior"
echo "set names 'utf8';
create table $tablename ( \
 fds                    varchar(20)      comment    '统计日期'
,appver                 varchar(100)     comment    '客户端版本'
,channel                varchar(100)     comment    '渠道'
,os                     varchar(100)     comment    '终端'
,dim_type               varchar(100)     comment    '邀请种类'
,dim_fun                varchar(100)     comment    '邀请方法'
,total_party_a_uv       bigint(20)      DEFAULT 0 comment    '累计邀请人'
,total_party_b_uv       bigint(20)      DEFAULT 0 comment    '累计被邀请人'
,total_reg_new_phone    bigint(20)      DEFAULT 0 comment    '累计预注册'
,total_reg_new_uv       bigint(20)      DEFAULT 0 comment    '累计预注册激活数'
,party_a_uv             bigint(20)      DEFAULT 0 comment    '邀请人'
,party_b_uv             bigint(20)      DEFAULT 0 comment    '被邀请人'
,reg_new_phone          bigint(20)      DEFAULT 0 comment    '预注册手机号'
,reg_new_uv             bigint(20)      DEFAULT 0 comment    '预注册激活数'
,red_money              decimal(10,2)   DEFAULT 0 comment    '红包金额'
,fid_uv                 bigint(20)      DEFAULT 0 comment    '累计家庭数'
,new_fid_uv             bigint(20)      DEFAULT 0 comment    '新增家庭数'
,fid_uid_uv             bigint(20)      DEFAULT 0 comment    '家庭成员数'
,UNIQUE KEY (appver,channel,os,dim_type,dim_fun,fds)
,index(fds)
,index(appver,channel,os)
) \
comment '邀请行为' \
partition by range columns (fds) (\
partition p$nowMonth values less than ( '$nextMonth-01' ), \
partition p_other values less than maxvalue\
);" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port

sh add_partition.sh $tablename $nextMonth