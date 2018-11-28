#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="odl_wutiao_mp_article_sync"

tablecomment="五条内容发布表"

hive <<EOF
use $database;
create  table if not exists $table (
 item_id              bigint            COMMENT '文章ID'
,item_type            varchar(255)      COMMENT '类型 article: 文章'
,uid                  bigint            COMMENT '用户ID'
,title                varchar(255)      COMMENT '文章标题'
,summary              string
,thumbnails           string            COMMENT '封面'
,img_type             int               COMMENT '封面类型 1 单张 2三张 3自动'
,category             varchar(50)       COMMENT '分类'
,imgs                 string            COMMENT '文章图片集'
,videos               string            COMMENT '视频地址'
,tag                  varchar(150)      COMMENT '文章标签'
,province             varchar(10)       COMMENT '地域'
,timing               string            COMMENT '定时发布'
,status               int               COMMENT '0 已发布 1 已经删除 2草稿箱 3待审核 4 审核失败 5回收站'
,reason               varchar(100)      COMMENT '审核拒绝的理由'
,create_time          string            COMMENT '添加时间'
,update_time          string            COMMENT '修改时间'
,insert_time          string            COMMENT '入库时间(爬虫投递时间)'
)
comment '$tablecomment'
partitioned by (ds string comment '日期分区字段,yyyy-MM-dd')
row format delimited fields terminated by '\t'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
