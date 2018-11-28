#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="report_wutiao_mediauser_sync"

tablecomment="自媒体入驻分析"

hive <<EOF
use $database;
create  table if not exists $table (
fds                                  varchar(128)      COMMENT '日期'
,welfaretype                         varchar(128)      COMMENT '性质'
,organtype                           varchar(128)      COMMENT '机构类型'
,mediauser_sortid                    varchar(128)      COMMENT '自媒体类目'
,his_mediauser_apply_sum             int               COMMENT '申请入驻累计数'
,day_mediauser_apply_sum             int               COMMENT '申请入驻数'
,his_mediauser_success_sum           int               COMMENT '成功入驻累计数'
,day_mediauser_success_sum           int               COMMENT '成功入驻数'
,day_uploadcontent_mediauser_sum     int               COMMENT '当日有新内容的自媒体数'
,day_uploadcontent_sum               int               COMMENT '当天新增内容数'
,new_audit_content_sum               int               COMMENT '新增审核通过内容数'
,new_insert_day_content_sum          int               COMMENT '新增抓取当日内容数'
,new_insert_his_content_sum          int               COMMENT '新增抓取往日内容数'
,new_day_content_sum                 int               COMMENT '当日总新增内容数'
)
comment '$tablecomment'
partitioned by (ds string comment '日期分区字段,yyyy-MM-dd')
row format delimited fields terminated by '|'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE; 

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
