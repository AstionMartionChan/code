#!/bin/bash

cd `dirname $0`
source /etc/profile
source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

month=`date +%Y_%m`
tablename="wutiao.report_wutiao_uid_output"
echo "set names 'utf8';
create table $tablename ( \
fds                              varchar(30)     comment    '统计日期'
,id                              varchar(50)     comment    '内容id/作者id'
,itemtype                        varchar(50)     comment    '文章类型'
,day_showinpage_cnt              bigint     comment    '当日曝光数'
,day_read_cnt                    bigint     comment    '当日阅读数'
,day_itemid_like_cnt             bigint     comment    '当日投票数'
,day_itemid_discover_vote_cnt    bigint     comment    '当日发现者投票数'
,day_addcomment_cnt              bigint     comment    '当日评论数'
,day_clickshare_cnt              bigint     comment    '当日分享数'
,day_addfavour_cnt               bigint     comment    '当日收藏数'
,day_income                      double     comment    '当日收益'
,his_showinpage_cnt              bigint     comment    '当日曝光数'
,his_read_cnt                    bigint     comment    '当日阅读数'
,his_itemid_like_cnt             bigint     comment    '当日投票数'
,his_itemid_discover_vote_cnt    bigint     comment    '当日发现者投票数'
,his_addcomment_cnt              bigint     comment    '当日评论数'
,his_clickshare_cnt              bigint     comment    '当日分享数'
,his_addfavour_cnt               bigint     comment    '当日收藏数'
,his_income                      double     comment    '当日收益'
,UNIQUE KEY (id,fds)
) \
comment '文章作者统计输出表' \
partition by range columns (fds) (\
partition p$nowMonth values less than ( '$nextMonth-01' ), \
partition p_other values less than maxvalue\
);" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port

sh add_partition.sh $tablename $nextMonth