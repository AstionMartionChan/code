#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="idl_wutiao_itemid_wb"

hive <<EOF
use $database;
create table if not exists $table (
itemid                    bigint     comment '文章id',
itemtype                  int        comment '文章类型',
day_creative_income       double     comment '当日创作者分币',
day_read_income           double     comment '当日阅读分币',
day_vote_income           double     comment '当日投票分币',
day_share_income          double     comment '当日分享分币',
day_comment_income        double     comment '当日评论分币',
his_creative_income       double     comment '历史累计创作者分币',
his_read_income           double     comment '历史累计阅读分币',
his_vote_income           double     comment '历史累计投票分币',
his_share_income          double     comment '历史累计分享分币',
his_comment_income        double     comment '历史累计评论分币',
day_contribute            double     comment '当日增加贡献力',
day_verifypoint           double     comment '当日增加审核力',
his_contribute            double     comment '历史累计贡献力',
his_verifypoint           double     comment '历史累计审核力',
day_addcomment_cnt        bigint     comment '当日评论数',
his_addcomment_cnt        bigint     comment '历史评论数',
day_delcomment_cnt        bigint     comment '当日删除评论数',
his_delcomment_cnt        bigint     comment '历史删除评论数',
day_itemid_like_cnt        bigint     comment '当日文章点赞数',
his_itemid_like_cnt        bigint     comment '历史文章点赞数',
day_itemid_commentlike_cnt     bigint     comment '当日评论点赞数',
his_itemid_commentlike_cnt        bigint     comment '历史评论点赞数',
day_itemid_discover_vote_cnt        bigint     comment '当日发现者投票点赞数',
his_itemid_discover_vote_cnt        bigint     comment '历史发现者投票点赞评论数',
day_clickshare_cnt        bigint     comment '当日点击分享数',
his_clickshare_cnt        bigint     comment '历史点击分享数',
day_cancelshare_cnt        bigint     comment '当日取消分享数',
his_cancelshare_cnt        bigint     comment '历史取消分享数',
day_successshare_cnt        bigint     comment '当日成功分享数',
his_successshare_cnt        bigint     comment '历史成功分享数',
day_read_cnt        bigint     comment '当日阅读数',
his_read_cnt        bigint     comment '历史阅读数',
day_validread_cnt        bigint     comment '当日有效阅读数',
his_validread_cnt        bigint     comment '历史有效阅读数',
day_report_cnt        bigint     comment '当日举报数',
his_report_cnt        bigint     comment '历史举报数',
day_nointerest_cnt        bigint     comment '当日不感兴趣数',
his_nointerest_cnt        bigint     comment '历史不感兴趣数',
day_addfavour_cnt         bigint     comment '当日新增收藏数',
his_addfavour_cnt         bigint     comment '历史加入收藏数',
day_delfavour_cnt         bigint     comment '当日取消收藏数',
his_delfavour_cnt         bigint     comment '历史取消收藏数',
day_play_cnt            bigint       comment '当日播放次数',
his_play_cnt            bigint       comment '历史播放次数',
day_showinpage_cnt      bigint       comment '当日曝光次数',
his_showinpage_cnt      bigint       comment '历史曝光总次数',
day_income              bigint       comment '当日总收益',
his_income              bigint       comment '历史总收益',
uid                     string       comment '文章作者',
day_discover_vote_income           double     comment '当日发现者投票分币',
his_discover_vote_income           double     comment '历史累计发现者投票分币'
)comment '区块链文章价值中间表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '\001'
stored as parquet;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF