#!/bin/bash 

cd `dirname $0`

source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

rid=$1
month=`date +%Y_%m`
database="wutiao"
tablename="$database.report_wutiao_itemid_interact"
echo "set names 'utf8';
create table $tablename ( \
 fds                    varchar(128)     comment    '日期'
,sortid                 varchar(128)     comment    '垂直类目'
,itemtype               varchar(128)     comment    '文章类型'
,comment_cnt            int              comment    '评论'
,vote_cnt               int              comment    '投票'
,like_cnt               int              comment    '点赞'
,report_cnt             int              comment    '举报'
,nointerest_cnt         int              comment    '不感兴趣'
,UNIQUE KEY (sortid,itemtype,fds)
) \
comment '内容互动分析' \
partition by range columns (fds) (\
partition p$nowMonth values less than ( '$nextMonth-01' ), \
partition p_other values less than maxvalue\
);" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port

sh add_partition.sh $tablename $nextMonth