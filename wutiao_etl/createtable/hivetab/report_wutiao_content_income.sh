#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="report_wutiao_content_income"

hive <<EOF
use $database;
create table if not exists $table (
 fds                                   string     comment    '统计日期'
,itemtype                              string     comment    '文章类型'
,sortid                                string     comment    '垂直类目'
,total_content_income                  double     comment    '总价值'
,valid_contribute                      double     comment    '有效贡献力'
,valid_verifypoint                     double     comment    '有效审核力'
)comment '区块链内容收入表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '|'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF