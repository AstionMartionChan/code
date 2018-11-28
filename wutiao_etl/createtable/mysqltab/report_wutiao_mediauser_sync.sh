#!/bin/bash 

cd `dirname $0`

source ./kingnet.sh
#获取时间函数（kingnget.sh中）
getMonthDay
db_info

rid=$1
month=`date +%Y_%m`
database="wutiao"
tablename="$database.report_wutiao_mediauser_sync"
echo "set names 'utf8';
create table $tablename ( \
 fds                                 varchar(20)       COMMENT '日期'
,welfaretype                         varchar(50)       COMMENT '性质'
,organtype                           varchar(50)       COMMENT '机构类型'
,mediauser_sortid                    varchar(50)       COMMENT '自媒体类目'
,his_mediauser_apply_sum             int               COMMENT '申请入驻累计数'
,day_mediauser_apply_sum             int               COMMENT '申请入驻数'
,his_mediauser_success_sum           int               COMMENT '成功入驻累计数'
,day_mediauser_success_sum           int               COMMENT '成功入驻数'
,day_uploadcontent_mediauser_sum     int               COMMENT '当日有新内容的自媒体数'
,day_uploadcontent_sum               int               COMMENT '当天新增内容数'
,new_audit_content_sum               int               COMMENT '新增审核通过内容数'
,new_insert_day_content_sum          int               COMMENT '新增抓取当日内容数'
,new_insert_his_content_sum          int               COMMENT '新增抓取往日内容数'
,new_day_content_sum                 int               COMMENT '当日总新增内容数'
,UNIQUE KEY (welfaretype,organtype,mediauser_sortid,fds)
) \
comment '自媒体入驻分析' \
partition by range columns (fds) (\
partition p$nowMonth values less than ( '$nextMonth-01' ), \
partition p_other values less than maxvalue\
);" | mysql -h $db_ip -u $db_user -p$db_pwd -P $db_port

sh add_partition.sh $tablename $nextMonth