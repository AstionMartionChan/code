#! /bin/sh

#cd `dirname $0`
source /etc/profile

database="wutiao"
table="adl_wutiao_comment_latest"

hive <<EOF
use $database;
create table if not exists $table (
 id	         bigint     comment   '评论id'
,uid		 bigint     comment   '用户id'
,item_id	 bigint     comment   '文章id'
,item_type	 string     comment   '文章类型'
,comment_id	 bigint     comment   '引用一级评论id'
,quote_id	 bigint     comment   '引用二级评论id'
,content	 string     comment   '评论内容'
,comment_count	 bigint     comment   '回复数'
,like_count	 bigint     comment   '点赞数'
,dislike_count	 bigint     comment   '踩数'
,status          bigint     comment   '状态'
,similarity      double     comment   '相似度'
,create_dt       string     comment   '创建时间'
,update_dt       string     comment   '修改时间'
)comment '五条评论更新表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '\001'
stored as parquet;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
