#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="report_wutiao_total_contribute"

hive <<EOF
use $database;
create table if not exists $table (
 fds                                   string     comment    '统计日期'
,like_contribute                       double     comment    '点赞贡献力'
,share_contribute                      double     comment    '分享贡献力'
,comment_contribute                    double     comment    '评论贡献力'
,read_contribute                       double     comment    '阅读贡献力'
,register_contribute                   double     comment    '注册贡献力'
,valid_contribute                      double     comment    '有效贡献力'
,valid_verifypoint                     double     comment    '有效审核力'
,family_contribute                     double     comment    '家庭贡献力'
,day_contribute                        double     comment    '每日贡献力'
,day_verifypoint                       double     comment    '每日审核力'
)comment '区块链贡献力分析表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '|'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF