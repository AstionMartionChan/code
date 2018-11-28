#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="idl_wutiao_useritemid_sync"

hive <<EOF
use $database;
create table if not exists $table (
 uid                   bigint            COMMENT '用户id'
,item_id               bigint            COMMENT '文章ID'
,item_type             int               COMMENT '1:文章 2:视频'
,content_sortid        int               COMMENT '内容垂直类目'
,audit_status          int               COMMENT '最后审核状态'
,last_audit_status     int               COMMENT '前一天审核状态'
,create_dt             string            COMMENT '创建时间'
,update_dt             string            COMMENT '更新时间'
,last_edit_time        string            COMMENT '审核更新时间'
,insert_dt             string            COMMENT '入库时间(爬虫投递时间)'
)comment '区块链用户内容中间表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '\001'
stored as parquet;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF