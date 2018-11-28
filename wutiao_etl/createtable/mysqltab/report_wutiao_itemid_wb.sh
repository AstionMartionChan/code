#!/bin/bash 

cd `dirname $0`

source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

rid=$1
month=`date +%Y_%m`
database="wutiao"
tablename="$database.report_wutiao_interact"
echo "set names 'utf8';
create table $tablename ( \
fds                     varchar(128) COMMENT '日期'
,itemtype               varchar(128)     comment    '文章类型'
,sortid                 varchar(128)     comment    '垂直类目'
,total_content_income   double     comment    '总收益'
,valid_contribute       double     comment    '有效贡献力'
,valid_verifypoint      double     comment    '有效审核力'
) \
comment '用户互动分析' \
partition by range columns (fds) (\
partition p$nowMonth values less than ( '$nextMonth-01' ), \
partition p_other values less than maxvalue\
);" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port

sh add_partition.sh $tablename $nextMonth