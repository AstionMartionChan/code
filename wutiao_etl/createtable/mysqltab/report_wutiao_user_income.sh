#!/bin/bash 

cd `dirname $0`
source /etc/profile
source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

month=`date +%Y_%m`
tablename="wutiao.report_wutiao_user_income"
echo "set names 'utf8';
create table $tablename ( \
 fds                             varchar(100)     comment    '统计日期'
,user_flag                       varchar(100)     comment    '新老用户标识'
,media_type                      varchar(100)     comment    '自媒体类型'
,total_income                    double     comment    '总收益'
,total_withdraw_success_rmb      double     comment    '总提现成功金额'
,total_withdraw_success_usercnt  bigint     comment    '总提现成功用户数'
,day_withdraw_success_rmb        double     comment    '当日提现成功金额'
,day_withdraw_success_usercnt    bigint     comment    '当日提现成功用户数'
,creative_income                 double     comment    '内容创作领币数'
,vote_income                     double     comment    '投票领币数'
,comment_income                  double     comment    '评论领币数'
,share_income                    double     comment    '分享领币数'
,register_income                 double     comment    '注册领币数'
,inviter_income                  double     comment    '邀请人领币数'
,invited_income                  double     comment    '北邀请领币数'
,like_income                     double     comment    '点赞领币数'
,report_income                   double     comment    '举报领币数'
,other_income                    double     comment    '其他领币数'
,friend_income                   double     comment    '好友收益'
,read_income                     double     comment    '阅读收益'
,inviter_bonus_income            double     comment    '邀请额外收益'
) \
comment '用户收益' \
partition by range columns (fds) (\
partition p$nowMonth values less than ( '$nextMonth-01' ), \
partition p_other values less than maxvalue\
);" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port

sh add_partition.sh $tablename $nextMonth