#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="odl_wutiao_audit_contents_sync"

tablecomment="五条内容审计表"

hive <<EOF
use $database;
create  table if not exists $table (
 content_id                varchar(255)   comment  '内容的编号'
,title                     varchar(255)   comment  '文章的标题'
,tags                      varchar(255)   comment  '文章的标签'
,user_name                 varchar(255)   comment  '文章的用户名'
,audit_content_type        int            comment  '文章的类型：视频/资讯'
,video_content             string         comment  '视频内容'
,audit_status              int            comment  '审核状态'
,content_type              varchar(200)   comment  '内容分类'
,target_ids                varchar(255)   comment  '作用目标'
,sensitive_groups          varchar(255)   comment  '敏感词分组'
,sensitive_levels          varchar(255)   comment  '敏感词级别'
,sensitive_categories      varchar(255)   comment  '敏感词分类'
,publish_time              string         comment  '内容发布时间'
,ip                        varchar(50)    comment  'ip地址'
,device_id                 varchar(255)   comment  '设备ID'
,media_name                varchar(255)   comment  '自媒体名称'
,media_id                  varchar(255)   comment  '自媒体ID'
,media_property            int            comment  '自媒体性质'
,match_sensitive_words     string         comment  '匹配关键词。敏感词ID列表，以逗号隔开'
,match_content_words       string         comment  '匹配到的具体词语，需要高亮显示'
,refuse_reason             int            comment  '拒绝理由'
,audit_user                varchar(255)   comment  '审核人员id'
,last_edit_time            string         comment  '最后修改时间'
,picture                   string         comment  '内容中的图片'
,pre_audit_group           varchar(255)   comment  '预审分组'
,comment_status            int            comment  '资讯评论的初始状态'
,click_step_status         int            comment  '资讯点踩状态'
,tipoff_status             int            comment  '资讯举报的初始状态'
,is_pre_audit              int            comment  '资讯是否为预审新闻'
,match_content_words_json  string         comment  '高亮的敏感词内容'
,audit_criteria            varchar(255)   comment  '审核尺度'
,content_count             int            comment  '举报次数'
,group_status              int            comment  '预审分组状态 ：1已分组 2未分组'
)
comment '$tablecomment'
partitioned by (ds string comment '日期分区字段,yyyy-MM-dd')
row format delimited fields terminated by '\t'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF
