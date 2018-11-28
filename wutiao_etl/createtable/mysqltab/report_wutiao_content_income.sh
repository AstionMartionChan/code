#!/bin/bash 

cd `dirname $0`
source /etc/profile
source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

month=`date +%Y_%m`
tablename="wutiao.report_wutiao_content_income"
echo "set names 'utf8';
create table $tablename ( \
 fds                             varchar(100)     comment    '统计日期'
,itemtype                        varchar(100)     comment    '文章类型'
,sortid                          varchar(100)     comment    '垂直类目'
,total_content_income            double     comment    '总价值'
,valid_contribute                double     comment    '有效贡献力'
,valid_verifypoint               double     comment    '有效审核力'
) \
comment '内容收益' \
partition by range columns (fds) (\
partition p$nowMonth values less than ( '$nextMonth-01' ), \
partition p_other values less than maxvalue\
);" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port

sh add_partition.sh $tablename $nextMonth