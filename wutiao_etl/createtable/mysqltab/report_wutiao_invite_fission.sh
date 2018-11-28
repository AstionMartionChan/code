#!/bin/bash 

cd `dirname $0`
source /etc/profile
source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

month=`date +%Y_%m`
tablename="wutiao.report_wutiao_invite_fission"
echo "set names 'utf8';
create table $tablename ( \
 fds                     date                comment     '日期分区字段,yyyy-MM-dd'
,appver                  varchar(100)        comment     '版本'
,channel                 varchar(100)        comment     '渠道'
,os                      varchar(100)        comment     '终端'
,total_invite_count      bigint(20)	     comment     '累计邀请人'
,total_invitee_count	 bigint(20)	     comment	 '累计被邀请人'
,invite_count		 bigint(20)	     comment	 '邀请人'
,invitee_count		 bigint(20)	     comment	 '被邀请人'
,new_invite_count	 bigint(20)	     comment	 '新用户邀请人'
,new_invitee_count	 bigint(20)	     comment	 '新用户被邀请人'
,old_invite_count	 bigint(20)          comment	 '老用户邀请人'
,old_invitee_count	 bigint(20)	     comment	 '老用户被邀请人'
,registe_invitee_count	 bigint(20)          comment	 '邀请注册'
,new_register	 	 bigint(20)	     comment	 '新增注册'
,old_register		 bigint(20)	     comment	 '去新活跃（老用户）'
,UNIQUE KEY (fds,appver,channel,os)
) \
comment '区块链邀请用户裂变表' \
partition by range columns (fds) (\
partition p$nowMonth values less than ( '$nextMonth-01' ), \
partition p_other values less than maxvalue\
);" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port

sh add_partition.sh $tablename $nextMonth
