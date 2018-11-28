#!/bin/bash 

cd `dirname $0`
source /etc/profile
source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

month=`date +%Y_%m`
tablename="wutiao.report_wutiao_newbieguide"
echo "set names 'utf8';
create table $tablename ( \
 fds                     date                comment     '日期分区字段,yyyy-MM-dd'
,appver                  varchar(50)        comment     '版本'
,channel                 varchar(50)        comment     '渠道'
,os                      varchar(50)        comment     '终端'
,type					 varchar(30)        comment     'A组测试/B组测试'
,register_uv             bigint(20)         comment      '注册引导uv'
,register_next_uv        bigint(20)    	    comment      '注册引导点击下一步uv'
,login_uv                bigint(20)     	comment      '登录成功uv'
,login_next_uv           bigint(20)     	comment      '登录成功点击下一步uv'
,read_uv                 bigint(20)     	comment      '阅读uv'
,read_next_uv            bigint(20)     	comment      '阅读点击下一步uv'
,vote_uv                 bigint(20)     	comment      '投票uv'
,vote_next_uv     		 bigint(20)     	comment      '投票点击下一步uv'
,second_vote_uv    		 bigint(20)     	comment      '二次投票uv'
,second_vote_next_uv     bigint(20)     	comment      '二次投票点击下一步uv'
,comments_uv         	 bigint(20)     	comment      '评论uv'
,comments_next_uv        bigint(20)     	comment      '评论点击下一步uv'
,second_comments_uv      bigint(20)     	comment      '二次评论uv'
,second_comments_next_uv bigint(20)     	comment      '二次评论点击下一步uv'
,withdrawal_uv           bigint(20)     	comment      '提现uv'
,withdrawal_next_uv      bigint(20)     	comment      '提现点击下一步uv'
,share_uv                bigint(20)     	comment      '分享uv'
,share_next_uv           bigint(20)     	comment      '分享点击下一步uv'
,share_success_uv        bigint(20)     	comment      '分析成功uv'
,UNIQUE KEY (fds,appver,channel,os,type)
) \
comment '区块链用户引导转化率统计表' \
partition by range columns (fds) (\
partition p$nowMonth values less than ( '$nextMonth-01' ), \
partition p_other values less than maxvalue\
);" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port

sh add_partition.sh $tablename $nextMonth