#!/bin/bash 

cd `dirname $0`
source /etc/profile
source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

month=`date +%Y_%m`
tablename="wutiao.report_wutiao_user_fbwb"
echo "set names 'utf8';
create table $tablename ( \
fds                    varchar(30)     comment    '统计日期',
creative_fb            double     comment    '内容创作分币数',
creative_fb_usercnt    bigint     comment    '内容创作分币用户数',
comment_fb             double     comment    '评论分币数',
comment_fb_usercnt     bigint     comment    '评论分币用户数',
share_fb               double     comment    '分享分币数',
share_fb_usercnt       bigint     comment    '分享分币用户数',
vote_fb                double     comment    '投票分币数',
vote_fb_usercnt        bigint     comment    '投票分币用户数',
register_fb            double     comment    '注册分币数',
register_fb_usercnt    bigint     comment    '注册分币用户数',
like_fb                double     comment    '投票分币数',
like_fb_usercnt        bigint     comment    '投票分币用户数',
other_fb               double     comment    '其他分币',
other_fb_usercnt       bigint     comment    '其他分币用户数',
read_fb                double     comment    '阅读分币',
read_fb_usercnt        bigint     comment    '阅读分币用户数',
fb                     double     comment    '总实际分币',
fb_usercnt             bigint     comment    '总分比用户数',
complete_usercnt       bigint     comment    '完成四个行为用户数',
recycle_wb             double     comment    '当日回收币',
his_recycle_wb         double     comment    '历史累计回收币',
commonuser_fb          double     comment    '普通用户分币',
commonuser_usercnt     bigint     comment    '普通用户分币用户数',
active_usercnt         bigint     comment    '当日活跃用户'
) \
comment '用户分币领币' \
partition by range columns (fds) (\
partition p$nowMonth values less than ( '$nextMonth-01' ), \
partition p_other values less than maxvalue\
);" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port

sh add_partition.sh $tablename $nextMonth