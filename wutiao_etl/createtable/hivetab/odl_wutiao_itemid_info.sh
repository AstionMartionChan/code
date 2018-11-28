#! /bin/sh
 
cd `dirname $0`
source /etc/profile
 
database="wutiao"
table="odl_wutiao_itemid_info"
 
hive <<EOF
use $database;
create table if not exists $table (
item_id                    string     comment    '商品id'
,item_type                 string     comment    '商品类型'
,uid                       string     comment    '用户id'
,title                     string     comment    '商品标题'
,summary                   string     comment    '摘要'
,content                   string     comment    '内容'
,category                  string     comment    '垂直类目'
,imgs                      string     comment    '图片路径'
,videos                    string     comment    '视频路径'
,img_type                  bigint     comment    '图片类型'
,thumbnails                string     comment    '封面图'
,tag                       string     comment    '标签'
,province                  string     comment    '省份'
,status                    bigint     comment    '状态'
,create_time               string     comment    '创建时间'
,update_time               string     comment    '更新时间'
,nickname                  string     comment    '昵称'
,signature                 string     comment    '个性签名'
,sex                       string     comment    '性别'
,field                     string     comment    '擅长的领域'
)comment '商品信息表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '\001'
stored as parquet;
 
alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');
 
EOF