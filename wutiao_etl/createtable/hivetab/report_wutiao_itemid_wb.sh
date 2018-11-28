#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="report_wutiao_itemid_wb"

hive <<EOF
use $database;
create table if not exists $table (
 fds                    string     comment    '统计日期'
,itemtype               string     comment    '文章类型'
,sortid                 string     comment    '垂直类目'
,total_content_income   double     comment    '总收益'
,valid_contribute       double     comment    '有效贡献力'
,valid_verifypoint      double     comment    '有效审核力'
)comment '区块链文章收益统计表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '|'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF