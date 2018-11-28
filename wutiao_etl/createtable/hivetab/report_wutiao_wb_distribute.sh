#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="report_wutiao_wb_distribute"

hive <<EOF
use $database;
create table if not exists $table (
 fds                          string     comment    '统计日期'
,user_flag                    string     comment    '新老用户标识'
,media_type                   string     comment    '自媒体类型'
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
)comment '区块链发币量分配分析表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '|'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF