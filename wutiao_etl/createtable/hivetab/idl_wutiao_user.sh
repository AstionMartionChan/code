#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="idl_wutiao_user"

hive <<EOF
use $database;
create table if not exists $table (
ouid                      string     comment    '设备当前用户id'
,nettype                   string     comment    '网络类型'
,model                     string     comment    '手机型号'
,first_osver               string     comment    '首次登录系统版本'
,last_osver                string     comment    '末次登录系统版本'
,first_os                  string     comment    '首次登录操作系统'
,last_os                   string     comment    '末次登录操作系统'
,res                       string     comment    '分辨率'
,carrier                   string     comment    '运营商'
,first_channel             string     comment    '首次登录渠道'
,channel_type              string     comment    '渠道类型'
,first_ip                  string     comment    '首次登陆IP地址'
,last_ip                   string     comment    '末次登陆IP地址'
,first_did_ouid            string     comment    '首次登陆设备/帐号'
,last_did_ouid             string     comment    '末次登陆设备/帐号'
,first_appver              string     comment    '首次app版本号'
,last_appver               string     comment    '末次app版本号'
,registe_time              bigint     comment    '注册时间'
,first_login_time          bigint     comment    '首次登录活跃APP时间'
,last_login_time           bigint     comment    '末次登录活跃APP时间'
,first_openclient_time     bigint     comment    '首次启动客户端时间'
,last_openclient_time      bigint     comment    '末次启动客户端时间'
,last_openclient_cnt       bigint     comment    '最后一天启动次数'
,first_comment_time        bigint     comment    '首次评论时间'
,last_comment_time         bigint     comment    '末次评论时间'
,last_comment_cnt          bigint     comment    '最后一天评论次数'
,first_vote_time           bigint     comment    '首次投票时间'
,last_vote_time            bigint     comment    '末次投票时间'
,last_vote_cnt             bigint     comment    '最后一天投票次数'
,first_search_time         bigint     comment    '首次搜索时间'
,last_search_time          bigint     comment    '末次搜索时间'
,last_search_cnt           bigint     comment    '最后一天搜索次数'
,first_favor_time          bigint     comment    '首次收藏时间'
,last_favor_time           bigint     comment    '末次收藏时间'
,last_favor_cnt            bigint     comment    '最后一天收藏次数'
,first_like_time           bigint     comment    '首次点赞时间'
,last_like_time            bigint     comment    '末次点赞时间'
,last_like_cnt             bigint     comment    '最后一天点赞次数'
,first_share_time          bigint     comment    '首次分享时间'
,last_share_time           bigint     comment    '末次分享时间'
,last_share_cnt            bigint     comment    '最后一天分享次数'
,first_attention_time      bigint     comment    '首次关注时间'
,last_attention_time       bigint     comment    '末次关注时间'
,last_attention_cnt        bigint     comment    '最后一天关注次数'
,first_reply_time         bigint     comment    '首次回复时间'
,last_reply_time          bigint     comment    '末次回复时间'
,last_reply_cnt           bigint     comment    '最后一天回复次数'
,enterbackground_cnt       bigint     comment    '当日进入后台次数'
,usetime                   bigint     comment    '当日使用时间'
,pagenum                   bigint     comment    '当日使用页面数量'
,crash_cnt                 bigint     comment    '当日崩溃次数'
,last_validread_time          bigint   comment   '最后有效阅读时间'
,last_validread_cnt           bigint   comment   '最后一天有效阅读次数'
,first_contribution_time      bigint   comment   '首次贡献力时间'
,last_contribution_time       bigint   comment   '最后贡献力世间'
,last_day_contributionpoint   double   comment   '最后一天贡献力值'
,first_verify_time            bigint   comment   '首次审核时间'
,last_verify_time             bigint   comment   '最后审核时间'
,last_day_verifypoint         double   comment   '最后一天审核力值'
,first_read_time            bigint     comment   '首次阅读时间'
,last_read_time             bigint     comment   '末次阅读时间'
,last_read_cnt              bigint     comment   '最近一天阅读次数'
,first_play_time            bigint     comment   '首次播放时间'
,last_play_time             bigint     comment   '末次播放时间'
,last_play_cnt              bigint     comment   '最近一天播放次数'
,familyid                    string     comment   '家庭id'
,first_discover_time      bigint     comment     '首次成为发现者时间'
,is_discover              string      comment    '是否发现者，是1 否0'
,inviterid                bigint      comment    '邀请者id'
,inviter_time             bigint      comment    '邀请时间'
,media_status             string      comment    '是否自媒体，是1 否0'
,first_withdraw_time   bigint     comment    '首次提现时间'
,last_withdraw_time    bigint     comment    '末次提现时间'
,last_day_withdraw_rmb     double     comment    '最后一天提现金额'
,his_withdraw_rmb          double     comment    '历史累计提现金额'
,last_day_withdraw_coin     double     comment    '最后一天提现金币'
,his_withdraw_coin          double     comment    '历史累计提现金币'
,first_money_gain_time      bigint     comment    '首次领币时间'
,last_money_gain_time      bigint     comment     '末次领币时间'
,last_day_money_gain        double    comment     '最后一天领币数'
,his_money_gain             double    comment     '历史累计领币数'
,first_report_time           bigint     comment    '首次举报时间'
,last_report_time            bigint     comment    '末次举报时间'
,last_report_cnt             bigint     comment    '最后一天举报次数'
,first_nointerest_time           bigint     comment    '首次不感兴趣时间'
,last_nointerest_time            bigint     comment    '末次不感兴趣时间'
,last_nointerest_cnt             bigint     comment    '最后一天不感兴趣次数'
,media_type                 string        comment    '自媒体类型'
,last_login_cnt             bigint      comment     '最后一天登录次数'
,last_did_ouid_time         bigint      comment     '设备id最后一次有ouid时间'
,enterfront_cnt             bigint      comment     '进入前台次数'
,gaptime                    bigint      comment     '从后台切入前台时间间隔'
,last_getwb_time            bigint      comment     '最后实际领币时间'
,last_day_getwb_wb          double      comment     '最后实际领币数'
,his_getwb_wb               double      comment     '历史实际领币数'
,last_channel               string      comment     '最近登录渠道'
,enterfront_under30_cnt     bigint      comment     '进入前台低于30秒次数'
,phone                      string      comment     '手机号'
,registe_inviter            string      comment     '注册时的邀请者'
,first_withdraw_hongbao_time      bigint   comment   '首次提现红包时间'
,last_withdraw_hongbao_time       bigint   comment   '末次提现红包时间'
,last_day_withdraw_hongbao_rmb    double   comment   '末次提现红包金额'
,his_withdraw_hongbao_rmb         double   comment   '历史累计提现红包金额'
,first_market_channel_type        string   comment   '首次市场渠道类型'
,last_market_channel_type         string   comment   '末次市场渠道类型'
)comment '区块链活跃用户中间表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd',ut string comment '用户分区')
row format delimited fields terminated by '\001'
stored as parquet;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

alter table $table add columns(
first_withdraw_duobao_time bigint comment '首次夺宝币兑换时间',
last_withdraw_duobao_time  bigint comment '末次夺宝币兑换时间',
last_day_withdraw_duobao_rmb double comment '末次夺宝币兑换金额',
his_withdraw_duobao_rmb double comment '历史累计夺宝币兑换金额',
last_day_withdraw_duobao_coin double comment '末次夺宝币兑换金币',
his_withdraw_duobao_coin double comment '历史累计夺宝币兑换金币'
);



EOF