#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="report_wutiao_newbieguide"

hive <<EOF
use $database;
create table if not exists $table (
 fds                     string     comment    '统计日期'
,appver                  string     comment    '客户端版本'
,channel                 string     comment    '渠道'
,os                      string     comment    '终端'
,type					 string     comment    'A组测试/B组测试'
,register_uv             bigint     comment    '注册引导uv'
,register_next_uv        bigint     comment    '注册引导点击下一步uv'
,login_uv                bigint     comment    '登录成功uv'
,login_next_uv           bigint     comment    '登录成功点击下一步uv'
,read_uv                 bigint     comment    '阅读uv'
,read_next_uv            bigint     comment    '阅读点击下一步uv'
,vote_uv                 bigint     comment    '投票uv'
,vote_next_uv     		 bigint     comment    '投票点击下一步uv'
,second_vote_uv    		 bigint     comment    '二次投票uv'
,second_vote_next_uv     bigint     comment    '二次投票点击下一步uv'
,comments_uv         	 bigint     comment    '评论uv'
,comments_next_uv        bigint     comment    '评论点击下一步uv'
,second_comments_uv      bigint     comment    '二次评论uv'
,second_comments_next_uv bigint     comment    '二次评论点击下一步uv'
,withdrawal_uv           bigint     comment    '提现uv'
,withdrawal_next_uv      bigint     comment    '提现点击下一步uv'
,share_uv                bigint     comment    '分享uv'
,share_next_uv           bigint     comment    '分享点击下一步uv'
,share_success_uv        bigint     comment    '分析成功uv'
)comment '区块链用户引导转化率统计表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '|'
COLLECTION ITEMS TERMINATED BY ','
MAP KEYS TERMINATED BY ':'
stored as  TEXTFILE;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF