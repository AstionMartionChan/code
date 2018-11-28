#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="report_wutiao_user_income"

hive <<EOF
use $database;
create table if not exists $table (
 fds                             string     comment    '统计日期'
,user_flag                       string     comment    '新老用户标识'
,media_type                      string     comment    '自媒体类型'
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
)comment '区块链用户收入分析表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '|'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF