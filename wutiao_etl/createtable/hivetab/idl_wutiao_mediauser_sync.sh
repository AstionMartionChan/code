#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="idl_wutiao_mediauser_sync"

hive <<EOF
use $database;
create table if not exists $table (
 uid                   bigint            COMMENT '用户id'
,mediauser_type        int               COMMENT '自媒体类型'
,welfaretype           int               COMMENT '性质'
,organtype             int               COMMENT '机构类型'
,mediauser_sortid      int               COMMENT '自媒体垂直类目'
,apply_status          int               COMMENT '申请入驻'
,success_status        int               COMMENT '成功入驻'
,current_status        int               COMMENT '当前状态'
,last_status           int               COMMENT '前一天状态'
,role                  int               COMMENT '角色'
,create_dt             string            COMMENT '创建时间'
,update_dt             string            COMMENT '更新时间'
)comment '区块链自媒体用户中间表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '\001'
stored as parquet;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF