#! /bin/sh

cd `dirname $0`
source /etc/profile

database="wutiao"
table="odl_event_qkl"

hive <<EOF
use $database;
create table if not exists $table (
ouid                      string     comment    '用户id'
,\`timestamp\`                bigint     comment    '业务行为时间'
,\`_sst\`                 bigint     comment    '服务器时间'
,did                      string     comment    '设备id'
,event                    string     comment    '事件名'
,project                  string     comment    '项目名'
,\`_ip\`                  string     comment    '客户端ip'
,\`_appver\`              string     comment    '客户端版本'
,\`_os\`                  string     comment    '客户端操作系统'
,\`_osver\`               string     comment    '客户端操作系统版本'
,\`_model\`               string     comment    '设备型号'
,\`_mfr\`                 string     comment    '设备'
,\`_res\`                 string     comment    '分辨率'
,\`_nettype\`             string     comment    '设备网络类型'
,\`_carrier\`             string     comment    '设备运营商'
,\`_channel\`             string     comment    '安装渠道'
,isvisitor                string     comment    '是否游客'
,step                     string     comment    '注册步奏'
,phone                    string     comment    '注册手机号'
,reftype                  bigint     comment    '来源类型'
,msg                      string     comment    '崩溃信息'
,type                     bigint     comment    '崩溃类别'
,trace                    string     comment    '异常信息'
,staytime                 bigint     comment    '停留时间'
,commentid                string     comment    '评论id'
,targetouid               string     comment    '被赞者id'
,mediaid                  string     comment    '自媒体id'
,itemid                   string     comment    '文章id'
,itemtype                 bigint     comment    '文章类型'
,itemlist                 string     comment    '批量文章id'
,sortid                   string     comment    '垂直类目'
,status                   string     comment    '状态'
,shareplat                string     comment    '分享到的平台'
,replyid                  string     comment    '回复id'
,target                   string     comment    '点击所需要跳转的目标页面编号'
,timemachid               string     comment    '时光机id'
,isvalid                  string     comment    '是否有效'
,subjectid                string     comment    '专题id'
,keyword                  string     comment    '搜索关键词'
,pos                      string     comment    '位置'
,userid                   string     comment    '用户id'
,inviterid                string     comment    '邀请者id'
,familyid                 string     comment    '家庭id'
,contributionpoint        double     comment    '贡献力增加值'
,verifypoint              double     comment    '审核力增加值'
,incref                   bigint     comment    '增加来源'
,\`_accemeter\`               string     comment    '加速度'
,\`_magfield\`                string     comment    '磁力'
,\`_orient\`                  string     comment    '方向'
,\`_gyros\`                   string     comment    '陀螺仪'
,\`_light\`                   string     comment    '光线感应'
,\`_press\`                   string     comment    '压力'
,\`_tempera\`                 string     comment    '温度'
,\`_prox\`                    string     comment    '距离感应'
,\`_grav\`                    string     comment    '重力'
,\`_lineacce\`                string     comment    '线性加速度'
,\`_rota\`                    string     comment    '旋转矢量'
,\`_gps\`                     string     comment    'GPS位置'
,sort                     int         comment    '对于这个文章id该用户是第几个举报的'
,usertype                 int         comment    '用户的类型，1：普通用户，2：发现者用户'
,coin                     double      comment    '金币数'
,rate                     double      comment    '汇率'
,rmb                      double      comment    '提现的rmb'
,\`_sdk\`               string       comment      'sdk'
,\`_sdkver\`            string       comment      'sdk版本'
,gps_province         string       comment      '客户端获取GPS位置省份'
,gps_area             string       comment      '客户端获取GPS位置地区'
,order_id             string       comment      '提现唯一标识'
,browser              string       comment      '浏览器'
,terminal             string       comment       '终端'
,ad                   string       comment       '广告id'
,gameid               string       comment       '游戏id'
,adid                 string       comment       '点击广告id'
,eventid              string       comment       '日志上报id'
,device_id            string       comment       'imei或者idfa'
,kafkauniqueid        string       comment       'kafka标识'
,templateid           string       comment       '素材id'
)comment '区块链事件源表'
partitioned by (eventtype string comment '日志事件等级',ds string comment '日期分区字段, yyyy-MM-dd',hour string comment '小时分区, yyyyMMddHH')
row format delimited fields terminated by '\001'
stored as parquet;

alter table $table SET SERDEPROPERTIES('serialization.null.format' = '');

EOF