-- 汇率指标表
CREATE TABLE report_wutiao_exchange_rate (
    ds date NOT NULL,
    stat_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '统计时间',
    exchange_rate double NOT NULL COMMENT '汇率',
    last_updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    PRIMARY KEY `pk_report_wutiao_exchange_rate` (stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='汇率指标表';


-- 汇率中间表, 用于记录当前批次计算汇率的各项指标
create table if not exists idl_wutiao_exchange_rate_realtime (
    window timestamp NOT NULL COMMENT '统计窗口 yyyy-MM-dd HH:mm:ss',
    total_register double NOT NULL COMMENT '累计注册用户数',
    total_withdraw_rmb double NOT NULL COMMENT '累计提现金额',
    total_money_gain double NOT NULL COMMENT '累计领币',
    total_withdraw_coin double NOT NULL COMMENT '累计提现金币',
    last_updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    PRIMARY KEY `pk_idl_wutiao_exchange_rate_realtime` (window)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='汇率指标计算中间表';


insert into idl_wutiao_exchange_rate_realtime (window, total_register, total_withdraw_rmb, total_money_gain, total_withdraw_coin)
values ('2018-06-13 00:00:00', 347, 115.38000000000001, 1151.1327985190626, 128.2);

insert into idl_wutiao_exchange_rate_realtime (window, total_register, total_withdraw_rmb, total_money_gain, total_withdraw_coin)
values ('2018-07-11 20:05:00', 1143441838, 176, 201867.0227206408, 176);