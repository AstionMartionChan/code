#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="adl_wutiao_click_statistics"

hive <<EOF
use $database;
create table if not exists $table (
fds                      string     comment    '统计日期'
,usertype                string     comment    '用户类别'
,channel                 string     comment    '渠道'
,appver                  string     comment    '客户端版本'
,model                   string     comment    '设备型号'
,res                     string     comment    '分辨率'
,os                      string     comment    '操作系统'
,nettype                 string     comment    '网络类型'
,carrier                 string     comment    '运营商'
,homepage_click_cnt                   bigint          comment       '首页点击量'
,homepage_click_usercnt               bigint          comment       '首页点击用户量'
,homepage_attention_click_cnt         bigint          comment       '首页-关注点击量'
,homepage_attention_click_usercnt     bigint          comment       '首页-关注点击用户量'
,homepage_latest_click_cnt            bigint          comment       '首页-最新点击量'
,homepage_latest_click_usercnt        bigint          comment       '首页-最新点击用户量'
,homepage_income_click_cnt            bigint          comment       '首页-收益条点击量'
,homepage_income_click_usercnt        bigint          comment       '首页-收益条点击用户量'
,search_click_cnt                     bigint          comment       '搜索点击量'
,search_click_usercnt                 bigint          comment       '搜索点击用户量'
,video_click_cnt                      bigint          comment       '视频点击量'
,video_click_usercnt                  bigint          comment       '视频点击用户量'
,find_click_cnt                       bigint          comment       '发现点击量'
,find_click_usercnt                   bigint          comment       '发现点击用户量'
,wallet_click_cnt                     bigint          comment       '钱包点击量'
,wallet_click_usercnt                 bigint          comment       '钱包点击用户量'
,personal_click_cnt                   bigint          comment       '我的点击量'
,personal_click_usercnt               bigint          comment       '我的点击用户量'
,search_result_click_cnt              bigint          comment       '搜索结果页点击量'
,search_result_click_usercnt          bigint          comment       '搜索结果页点击用户量'
,banner_click_cnt                     bigint          comment       '首页广告图点击量'
,banner_click_usercnt                 bigint          comment       '首页广告图点击用户量'
,recommend_click_cnt                  bigint          comment       '推荐页点击量'
,recommend_click_usercnt              bigint          comment       '推荐页点击用户量'
)comment '用户点击统计'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd', ut string comment '用户分区:uid|did')
row format delimited fields terminated by '\001'
stored as parquet;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF