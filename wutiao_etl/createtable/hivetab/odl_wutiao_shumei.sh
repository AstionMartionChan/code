#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="odl_wutiao_shumei"

hive <<EOF
use $database;
create table if not exists $table (
ouid                      string     comment    '用户id'
,\`timestamp\`            string
,\`_sst\`                 string
,did                      string     comment    '设备id'
,event                    string     comment    '事件名'
,project                  string     comment    '项目名'
,tokenid                  string
,\`ip\`                   string
,\`_ip\`                  string
,clickip                  string
,appversion               string
,\`_os\`                  string
,\`_osver\`               string
,\`_nettype\`             string
,\`_gps\`                 string
,\`_res\`                 string
,\`_appver\`              string
,\`_sdk\`                 string
,\`_sdkver\`              string
,\`_systime\`             string
,\`_channel\`             string
,\`_carrier\`             string
,\`_model\`               string
,campaign                 string
,gps_city                 string
,idfv                     string
,idfa                     string
,apputm                   string
,advertisingid            string
,clickid                  string
,phone                    string
,signupplatform           string
,nickname                 string
,risklevel                string
,withdrawamount           double
,withdrawaccountid        string
,withdrawaccounttype      string
,invitetokenid            string
,invitephone              string
,installtimestamp         string
,clicktimestamp           string
,isretargeting            string
,valid                    string
,userexist                string
,captchavalid             string
,gps_province             string       comment      '客户端获取GPS位置省份'
,gps_area                 string       comment      '客户端获取GPS位置地区'
,eventid                  string       comment       '日志上报id'
,deviceid                 string       comment       'imei或者idfa'
,kafkauniqueid            string       comment       'kafka标识'
)comment '数美数据源表'
partitioned by (ds string comment '日期分区字段, yyyy-MM-dd')
row format delimited fields terminated by '\001'
stored as parquet;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF