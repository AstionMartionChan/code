#!/usr/bin/env python
# -*-coding: UTF-8 -*-

# **********************程序说明*********************************#
# *模块: adl_wutiao_new_did_etl > ADL
# *功能: 临时计算实时设备表
# *作者: sunyu
# *时间: 2018-07-19
# *备注: 在6.61上 crontab 调度
# ***************************************************************#
import time
tt1 = time.time()
import sys
import kingnetdc

# 建表备注
''' mysql -h 172.27.2.72  -udatac -pg3Z2zHF6uTxK6#rnlZ7 -P 3306
CREATE TABLE `realtime_new_device_etl` (
  `ds` varchar(64) COMMENT '日期分区字段,yyyy-MM-dd',
  `statdate` varchar(64) COMMENT '日期分区字段,yyyy-MM-dd',
  `appver` varchar(64) DEFAULT '-1' COMMENT '版本',
  `channel` varchar(64) DEFAULT '-1' COMMENT '渠道',
  `os` varchar(64) DEFAULT '-1' COMMENT '终端',
  `act_uv` bigint(20) DEFAULT 0,
  `new_uv` bigint(20) DEFAULT 0,
  `old_uv` bigint(20) DEFAULT 0,
  PRIMARY KEY (`ds`,`statdate`,`appver`,`channel`, `os`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

'''

UID_SQL = '''
select  dds, statdate
        , nvl(last_appver,'allappver') as last_appver
        , nvl(channel, 'allchannel') as channel
        , nvl(last_os, 'allos') as os
        , count(distinct if(act_tag>0,ouid,null)) as act_uv
        , count(distinct if(new_ds is null and new_tag>0,ouid,null)) as new_uv
        , count(distinct if(new_ds is not null and new_ds < dds and act_tag>0,ouid,null)) as old_uv
    from (
        select dds, statdate, a.ouid
            , nvl(b.last_appver, a.last_appver) as last_appver
            , nvl(b.channel, a.channel) as channel
            , nvl(b.last_os, a.last_os) as last_os
            , b.new_ds
            , act_tag
            , new_tag
        from (
            select '%(today)s' as dds, '%(statdate)s' as statdate, lower(did) as ouid
                ,nvl(max_by(_appver, _sst),'-1') as last_appver
                ,nvl(max_by(_channel, _sst),'-1') as channel
                ,nvl(max_by(lower(_os), _sst),'-1') as last_os
                ,sum(if(event in ('comment','favour','like','share','read','click','nointerest','report','attention','leaveread','play','search','login','active','enterfront','openclient','register'), 1,0)) as act_tag
                ,sum(if(event in ('login','active','enterfront','openclient','register'),1,0)) as new_tag
            from wutiao.odl_event_qkl
            where ds = '%(today)s'
            and nvl(did,'-1') != '-1' 
            and date_format(from_unixtime(cast(round(_sst/1000.0,0) as bigint)) AT TIME ZONE 'Asia/Shanghai','%%Y-%%m-%%d %%H:%%i:%%s') between '%(statdate)s' and '%(end_statdate)s'
            and eventtype in ('super', 'high')
            group by did
        ) a
        left join
        (
            select nvl(old.ouid,new.ouid) as ouid
                ,nvl(old.last_appver,new.last_appver) as last_appver
                ,nvl(old.channel,new.channel) as channel
                ,nvl(old.last_os,new.last_os) as last_os
                ,nvl(old.new_ds,new.new_ds) as new_ds
            from (
                select ouid, nvl(last_appver, '-1') as last_appver, nvl(last_channel, '-1') as channel, nvl(last_os,'-1') as last_os
                    , date_format(from_unixtime(first_login_time) AT TIME ZONE 'Asia/Shanghai','%%Y-%%m-%%d') as new_ds
                from wutiao.idl_wutiao_user
                where ds = '%(day1)s'
                and ut = 'did'
            ) old
            full join
            (
                select lower(did) as ouid
                    ,nvl(max_by(_appver, _sst),'-1') as last_appver
                    ,nvl(max_by(_channel, _sst),'-1') as channel
                    ,nvl(max_by(lower(_os), _sst),'-1') as last_os
                    ,min(ds) as new_ds
                from wutiao.odl_event_qkl
                where ds between '%(some_day)s' and '%(today)s'
                and nvl(did,'-1') != '-1' 
                and date_format(from_unixtime(cast(round(_sst/1000.0,0) as bigint)) AT TIME ZONE 'Asia/Shanghai','%%Y-%%m-%%d %%H:%%i:%%s') >'%(day1)s 23:59:59'
                and date_format(from_unixtime(cast(round(_sst/1000.0,0) as bigint)) AT TIME ZONE 'Asia/Shanghai','%%Y-%%m-%%d %%H:%%i:%%s') <'%(statdate)s'
                and event in ('login','active','enterfront','openclient','register')
                and eventtype in ('super', 'high')
                group by did
            ) new
            on old.ouid = new.ouid
        ) b
        on a.ouid = b.ouid
    ) t
    GROUP BY GROUPING SETS(
        (dds, statdate, last_appver, channel, last_os),
        (dds, statdate)
    )
'''


def data2table(db_params, table_name, insert_li, update_columns=[]):
    if insert_li:
        filling_str = ','.join(['%s'] * len(insert_li[0]))
        if update_columns:
            sql = """ insert into %s (%s) values(%s)
                ON DUPLICATE KEY UPDATE
            """ % (table_name, ','.join(update_columns), filling_str)
            for column_str in update_columns:
                sql += ' %s=values(%s),' % (column_str, column_str)
            sql = sql[:-1]
        else:
            sql = """insert into %s values(%s) """ % (table_name, filling_str)
        kingnetdc.executemany_batches(db_params, sql, insert_li)


def main(time_str, auto_just):
    send_just = True
    msg = ''
    kdc = kingnetdc.kdc
    args = {
        'run_date': kdc.workDate,
        'day1': kdc.workDate,
        'today': kdc.dateAdd(1),
        'uid_mysql_tb': 'wutiao.realtime_new_device_etl',
    }

    # 显示用的时间文字
    if time_str in ('23:50:00', '23:55:00') and auto_just:
        args['today'] = kdc.workDate
        args['day1'] = kdc.dateSub(1, kdc.workDate)

    try:
        args['statdate'] = '%s %s' % (args['today'], time_str)

        args['some_day'] = args['today']
        # 处理时间范围,用来给指定时间日期时使用
        if args['statdate'][-4:] == '0:00':
            args['end_statdate'] = '%s4:59' % args['statdate'][:-4]
        else:
            args['end_statdate'] = '%s9:59' % args['statdate'][:-4]

        print(args['statdate'], args['end_statdate'])

        # 如果当前时间超过3点,则获取昨日分区,3点前使用前天分区
        if time_str[:2] < '03' and auto_just:
            args['day1'] = kdc.dateSub(1, args['day1'])
            args['some_day'] = kdc.dateSub(1, args['today'])

        db_params_realtime = kingnetdc.gen_params_dict('172.27.2.72', 'datac', 'g3Z2zHF6uTxK6#rnlZ7')

        # 新增用户,去新活跃统计
        sql1 = UID_SQL % args
        print(sql1)
        new_list = kingnetdc.presto_execsqlr(sql1)
        print('sql1: ', len(new_list))
        if not new_list:
            new_list = [[args['today'], args['statdate'], 'allappver', 'allchannel', 'allos', 0, 0, 0]]
        print(new_list)
        column_list = ['ds', 'statdate', 'appver', 'channel', 'os', 'act_uv', 'new_uv', 'old_uv']
        data2table(db_params_realtime, args['uid_mysql_tb'], new_list, column_list)
        send_just = False
    except Exception as e:
        print(e)
        msg = str(e)
    finally:
        if send_just:
            kingnetdc.send_wechat('五条离线方式计算实时活跃设备脚本失败', 'sunyu@kingnet.com,zhangxj@kingnet.com,zhangfan@kingnet.com')
            kingnetdc.send_phone('五条离线方式计算实时活跃设备脚本失败: %s ' % time_str, ['13918035392', '13761810576'])
            raise Exception(msg)


if __name__ == '__main__':
    if len(sys.argv) >= 2:
        time_str = sys.argv[1]
        auto_just = False
    else:
        # 获取10分钟前时间
        befor_ten_min = kingnetdc.get_date_str_by_type('minutes', kingnetdc.get_someday(0, kingnetdc.DATE_TIME_FORMAT_STR), -10)
        print('1 ---', befor_ten_min)
        # 截取 小时和分钟
        day_str = befor_ten_min[10:]
        now_time_str = befor_ten_min[11:16]
        print('2 ---', day_str, now_time_str)
        # 边界处理
        if now_time_str[-1] < '5':
            time_str = now_time_str[:-1] + '0:00'
        else:
            time_str = now_time_str[:-1] + '5:00'
        print('3 ---', time_str)
        auto_just = True

    main(time_str, auto_just)
print('total time:', time.time() - tt1)
