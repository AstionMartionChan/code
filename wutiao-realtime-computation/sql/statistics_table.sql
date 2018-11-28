-- 用户指标表
create table realtime_wutiao_user_kpi (
    fds varchar(16) NOT NULL comment '日期',
    window TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP comment '事件统计时间',
    duration int NOT NULL comment '统计区间',
    appver varchar(32) NOT NULL comment '客户端版本',
    channel varchar(32) NOT NULL comment '渠道',
    os varchar(32) NOT NULL comment '终端',
    `active` bigint NOT NULL default 0 comment '活跃用户数',
    `new` bigint NOT NULL default 0 comment '新增用户数',
    `old` bigint NOT NULL default 0 comment '非新增用户数',
    last_updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    primary key (fds, window, duration, appver, channel, os) using BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户指标表';

-- 设备指标表
create table realtime_wutiao_device_kpi (
    fds varchar(16) NOT NULL comment '日期',
    window TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP comment '事件统计时间',
    duration int NOT NULL comment '统计区间',
    appver varchar(32) NOT NULL comment '客户端版本',
    channel varchar(32) NOT NULL comment '渠道',
    os varchar(32) NOT NULL comment '终端',
    `active` bigint NOT NULL default 0 comment '活跃用户数',
    `new` bigint NOT NULL default 0 comment '新增用户数',
    `old` bigint NOT NULL default 0 comment '非新增用户数',
    last_updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    primary key (fds, window, duration, appver, channel, os) using BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设备指标表';

-- 互动行为指标表
create table realtime_wutiao_interact_kpi (
    fds varchar(16) NOT NULL comment '日期',
    window TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP comment '事件统计时间',
    duration int NOT NULL comment '统计区间',
    item_type varchar(32) NOT NULL comment '1：阅读，2：视频',
    os varchar(16) NOT NULL comment '终端',
    item_category varchar(32) comment '垂直类目',
    device_type varchar(16) comment '新老设备',
    metrics_name varchar(16) comment '人数 or 次数',
    `action_read` double NOT NULL default 0 comment '阅读',
    `action_play` double NOT NULL default 0 comment '播放',
    `action_comment` double NOT NULL default 0 comment '评论',
    `action_nointerest` double NOT NULL default 0 comment '不感兴趣',
    `action_report` double NOT NULL default 0 comment '举报',
    `action_favour` double NOT NULL default 0 comment '收藏',
    `action_like` double NOT NULL default 0 comment '点赞',
    last_updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    primary key (fds, window, duration, item_type, os, item_category, device_type, metrics_name) using BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='互动行为指标表';

-- 有效阅读指标表
create table realtime_wutiao_valid_kpi (
    fds varchar(16) NOT NULL comment '日期',
    window TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP comment '事件统计时间',
    duration int NOT NULL comment '统计区间',
    item_category varchar(32) NOT NULL comment '垂直类目',
    channel varchar(32) NOT NULL comment '渠道',
    item_type varchar(32) NOT NULL comment '1：阅读，2：视频',
    metrics_name varchar(16) NOT NULL comment '指标名称',
    metrics_value double NOT NULL comment '指标值',
    last_updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    primary key (fds, window, duration, item_category, channel, item_type, metrics_name) using BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='有效阅读指标表';

-- 领币指标表
create table realtime_wutiao_moneygain_kpi (
    fds varchar(16) NOT NULL comment '日期',
    window TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP comment '事件统计时间',
    duration int NOT NULL comment '统计区间',
    item_category varchar(32) NOT NULL comment '垂直类目',
    channel varchar(32) NOT NULL comment '渠道',
    action_type varchar(32) NOT NULL comment '行为',
    media_type varchar(32) NOT NULL comment '自媒体类型',
    metrics_name varchar(16) NOT NULL comment '指标名称',
    metrics_value double NOT NULL comment '指标值',
    last_updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    primary key (fds, window, duration, item_category, channel, action_type, media_type, metrics_name) using BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='领币指标表';

-- 浏览指标表
create table realtime_wutiao_view_kpi (
    fds varchar(16) NOT NULL comment '日期',
    window TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP comment '事件统计时间',
    duration int NOT NULL comment '统计区间',
    isvisitor varchar(32) NOT NULL comment '是否游客',
    channel varchar(32) NOT NULL comment '渠道',
    metrics_name varchar(16) NOT NULL comment '指标名称',
    metrics_value double NOT NULL comment '指标值',
    last_updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    primary key (fds, window, duration, isvisitor, channel, metrics_name) using BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='浏览指标表';

-- 提现指标表
create table realtime_wutiao_withdraw_kpi (
    fds varchar(16) NOT NULL comment '日期',
    window TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP comment '事件统计时间',
    duration int NOT NULL comment '统计区间',
    appver varchar(32) NOT NULL comment '客户端版本',
    channel varchar(32) NOT NULL comment '渠道',
    os varchar(32) NOT NULL comment '终端',
    rmb_total_applied double default 0.0 NOT NULL comment '提现人民币',
    rmb_total_success double default 0.0 NOT NULL comment '成功提现人民币',
    rmb_total_failed double default 0.0 NOT NULL comment '失败提现人民币',
    ouid_total_applied bigint default 0 NOT NULL comment '提现申请用户',
    ouid_total_success bigint default 0 NOT NULL comment '提现成功用户',
    ouid_total_failed bigint default 0 NOT NULL comment '提现失败用户',
    last_updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    primary key (fds, window, duration, appver, channel, os) using BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='提现指标表';


-- 分币指标表
create table realtime_wutiao_money_distribute_kpi (
    fds varchar(16) NOT NULL comment '日期',
    window TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP comment '事件统计时间',
    duration int NOT NULL comment '统计区间',
    action_type varchar(16) NOT NULL comment '分币的来源',
    item_type varchar(16) NOT NULL comment '新闻 or 视频',
    media_type varchar(16) NOT NULL comment '自媒体类型',
    item_category varchar(16) NOT NULL comment '垂直类目',
    channel varchar(16) NOT NULL comment '渠道',
    metrics_name varchar(16) NOT NULL comment '指标名称',
    metrics_value double NOT NULL comment '指标值',
    last_updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    primary key (fds, window, duration, action_type, item_type, media_type, item_category, channel, metrics_name) using BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='分币指标表';

-- 贡献力指标表
create table realtime_wutiao_contribution_kpi (
    fds varchar(16) NOT NULL comment '日期',
    window TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP comment '事件统计时间',
    duration int NOT NULL comment '统计区间',
    incref varchar(32) NOT NULL comment '来源',
    item_category varchar(32) NOT NULL comment '垂直类目',
    media_type varchar(32) NOT NULL comment '自媒体类型',
    metrics_name varchar(16) NOT NULL comment '指标名称',
    metrics_value double NOT NULL comment '指标值',
    last_updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    primary key (fds, window, duration, incref,item_category, media_type, metrics_name) using BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='贡献力指标表';

-- 测试用户表
create table tbl_user_table (
    uid varchar(100) NOT NULL comment '用户id',
    time bigint NOT NULL comment '最新时间戳',
    primary key (uid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='测试用户表';

-- 离线汇率表(记录每日推送值)
CREATE TABLE `idl_wutiao_exchange_rate` (
  `ds` date NOT NULL,
  `exchange_rate` double NOT NULL DEFAULT 0.0 COMMENT '汇率',
  `remaining_rmb` double NOT NULL DEFAULT 0.0 COMMENT '截止ds的剩余钱',
  `remaining_coin` double NOT NULL DEFAULT 0.0 COMMENT '截止ds的剩余币',
  `last_updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  PRIMARY KEY (`ds`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='离线汇率中间表';

CREATE TABLE `idl_wutiao_exchange_rate_realtime` (
  `window` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '统计窗口 yyyy-MM-dd HH:mm:ss',
  `total_register` double NOT NULL COMMENT '累计注册用户数',
  `total_withdraw_rmb` double NOT NULL COMMENT '累计提现金额',
  `total_money_gain` double NOT NULL COMMENT '累计领币',
  `total_withdraw_coin` double NOT NULL COMMENT '累计提现金币',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  PRIMARY KEY (`window`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='实时汇率中间表';

CREATE TABLE `realtime_wutiao_exchange_rate` (
  `fds` varchar(16) NOT NULL comment '日期',
  `window` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP comment '事件统计时间',
  `exchange_rate` double NOT NULL COMMENT '汇率',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  PRIMARY KEY (`fds`, `window`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='实时汇率指标表';





