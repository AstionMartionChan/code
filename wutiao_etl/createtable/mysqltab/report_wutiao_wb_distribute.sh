#!/bin/bash 

cd `dirname $0`
source /etc/profile
source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

month=`date +%Y_%m`
tablename="wutiao.report_wutiao_wb_distribute"
echo "set names 'utf8';
create table $tablename ( \
 fds                          varchar(100)     comment    '统计日期'
,user_flag                    varchar(100)     comment    '新老用户标识'
,media_type                   varchar(100)     comment    '自媒体类型'
,register_distribute          double     comment    '注册分币'
,inviter_distribute           double     comment    '邀请分币'
,invited_distribute           double     comment    '被邀请分币'
,content_support_distribute   double     comment    '内容提供者分币'
,content_find_distribute      double     comment    '内容发现者分币'
,share_distribute             double     comment    '分享分币'
,comment_distribute           double     comment    '评论分币'
,like_distribute              double     comment    '点赞分币'
,read_distribute              double     comment    '阅读分币'
,report_distribute            double     comment    '举报分币'
,total_distribute             double     comment    '累计发币量'
,friend_bonus_distribute      double     comment     '好友分币'
,other_distribute             double     comment     '阅读分币'
,inviter_bonus_distribute     double     comment     '邀请额外分币'
) \
comment '用户分币' \
partition by range columns (fds) (\
partition p$nowMonth values less than ( '$nextMonth-01' ), \
partition p_other values less than maxvalue\
);" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port

sh add_partition.sh $tablename $nextMonth