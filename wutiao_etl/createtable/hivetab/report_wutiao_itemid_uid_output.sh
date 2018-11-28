#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="report_wutiao_itemid_uid_output"

hive <<EOF
use $database;
create table if not exists $table (
fds                              string     comment    '统计日期'
,id                              string     comment    '内容id/作者id'
,itemtype                        string     comment    '文章类型'
,day_showinpage_cnt              bigint     comment    '当日曝光数'
,day_read_cnt                    bigint     comment    '当日阅读数'
,day_itemid_like_cnt             bigint     comment    '当日投票数'
,day_itemid_discover_vote_cnt    bigint     comment    '当日发现者投票数'
,day_addcomment_cnt              bigint     comment    '当日评论数'
,day_clickshare_cnt              bigint     comment    '当日分享数'
,day_addfavour_cnt               bigint     comment    '当日收藏数'
,day_income                      double     comment    '当日收益'
,his_showinpage_cnt              bigint     comment    '当日曝光数'
,his_read_cnt                    bigint     comment    '当日阅读数'
,his_itemid_like_cnt             bigint     comment    '当日投票数'
,his_itemid_discover_vote_cnt    bigint     comment    '当日发现者投票数'
,his_addcomment_cnt              bigint     comment    '当日评论数'
,his_clickshare_cnt              bigint     comment    '当日分享数'
,his_addfavour_cnt               bigint     comment    '当日收藏数'
,his_income                      double     comment    '当日收益'
)comment '内容和作者统计输出表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd',type string comment '数据类型')
row format delimited fields terminated by '|'
stored as textfile;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF