#!/bin/bash 

cd `dirname $0`
source /etc/profile
source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

month=`date +%Y_%m`
tablename="wutiao.report_wutiao_total_contribute"
echo "set names 'utf8';
create table $tablename ( \
fds                                   varchar(30)     comment    '统计日期'
,like_contribute                      double     comment    '点赞贡献力'
,share_contribute                     double     comment    '分享贡献力'
,comment_contribute                   double     comment    '评论贡献力'
,read_contribute                      double     comment    '阅读贡献力'
,register_contribute                  double     comment    '注册贡献力'
,valid_contribute                     double     comment    '有效贡献力'
,valid_verifypoint                    double     comment    '有效审核力'
,family_contribute                    double     comment    '家庭贡献力'
,day_contribute                       double     comment    '每日贡献力'
,day_verifypoint                      double     comment    '每日审核力'
) \
comment '用户贡献力分析' \
partition by range columns (fds) (\
partition p$nowMonth values less than ( '$nextMonth-01' ), \
partition p_other values less than maxvalue\
);" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port

sh add_partition.sh $tablename $nextMonth