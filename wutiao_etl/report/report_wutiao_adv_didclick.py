# !/usr/bin/env python
# -*-coding: UTF-8 -*-

# **********************程序说明*********************************#
# *模块: report_wutiao_adv_didclick -> ADL
# *功能: 区块链-五条项目-广告转化设备号统计
# *作者: zuotq
# *时间: 2018-11-21
# *备注:
# ***************************************************************#

import kingnetdc
import sys
import os
sys.path.append('/'.join(os.path.abspath(__file__).split('/')[:-2]))
from project_constant import DB_PARAMS_TEST

DB_PARAMS = DB_PARAMS_TEST


HQL = '''
set mapred.max.split.size=10000000;
set mapreduce.map.memory.mb=4096;
set mapreduce.reduce.memory.mb=4096;

with day_did_click as
(select
ds as fds,
os,
appver,
channel,
didflag,
count(distinct case when event='displayinpage' and pos='kp_1' then did else null end) as kp_display_uv,
count(distinct case when event='adclick' and pos='kp_2' then did else null end) as kp_click_uv,
count(distinct case when event='displayinpage' and pos='A#1#13' then did else null end) as info_flow_display_uv,
count(distinct case when event='adclick' and pos='A#1_13' then did else null end) as info_flow_click_uv,
count(distinct case when event='displayinpage' and pos='P#10' then did else null end) as detail_display_uv,
count(distinct case when event='adclick' and pos='P#10' then did else null end) as detail_click_uv,
count(distinct case when event='displayinpage' and pos in ('A#1#3','A#1#4','A#1#5','A#1#6','A#1#7','A#1#8','A#1#9','A#1#10','A#1#11','A#1#12') then did else null end) as hot_spot_display_uv,
count(distinct case when event='adclick' and pos in ('A#1_3','A#1_4','A#1_5','A#1_6','A#1_7','A#1_8','A#1_9','A#1_10','A#1_11','A#1_12') then did else null end) as hot_spot_click_uv,
count(distinct case when event='displayinpage' and pos like 'E#7#%%' then did else null end) as mine_display_uv,
count(distinct case when event='adclick' and pos like 'E#7#%%' then did else null end) as mine_click_uv,
count(distinct case when event='displayinpage' and pos='qb#12#8#1' then did else null end) as withdraw_display_uv,
count(distinct case when event='adclick' and pos='qb#12#8#1' then did else null end) as withdraw_click_uv,
ds
from
(
select
t1.ds as ds,
t1.os as os,
t1.appver as appver,
t1.channel as channel,
t1.did as did,
t1.event as event,
t1.pos as pos,
nvl(t2.didflag, '0') as didflag
from
(
    select ds,os,appver,channel,did,event,pos from %(sourceTable1)s
    where ds='%(ds)s' and eventtype='high' and did is not null
    and event in ('displayinpage','adclick')
    group by ds,os,appver,channel,did,event,pos
) t1
left join
(
    select  nvl(ouid,'-1') as did
            , nvl(last_os,'-1') as os
            , nvl(last_channel,'-1') as channel
            , nvl(last_appver,'-1') as appver
            , '1' as didflag
    from %(sourceTable2)s
    where ds='%(ds)s' and ut='did'
    and first_login_time between unix_timestamp('%(ds)s 00:00:00') and unix_timestamp('%(ds)s 23:59:59')
) t2
on t1.did = t2.did and t1.os=t2.os and t1.channel=t2.channel and t1.appver=t2.appver
) t3
group by ds,os,appver,channel,didflag)


INSERT overwrite TABLE %(targettab)s partition (ds)
select * from day_did_click
union all
select
fds,
os,
appver,
channel,
'alldidflag' as didflag,
sum(kp_display_uv) as kp_display_uv,
sum(kp_click_uv) as kp_click_uv,
sum(info_flow_display_uv) as info_flow_display_uv,
sum(info_flow_click_uv) as info_flow_click_uv,
sum(detail_display_uv) as detail_display_uv,
sum(detail_click_uv) as detail_click_uv,
sum(hot_spot_display_uv) as hot_spot_display_uv,
sum(hot_spot_click_uv) as hot_spot_click_uv,
sum(mine_display_uv) as mine_display_uv,
sum(mine_click_uv) as mine_click_uv,
sum(withdraw_display_uv) as withdraw_display_uv,
sum(withdraw_click_uv) as withdraw_click_uv,
ds
from day_did_click
group by fds,ds,os,appver,channel
'''


def main():
    kdc = kingnetdc.kdc
    args = dict()
    args['ds'] = kdc.workDate
    args['sourceTable1'] = 'wutiao.bdl_wutiao_event'
    args['sourceTable2'] = 'wutiao.idl_wutiao_user'
    args['targettab'] = 'wutiao.report_wutiao_adv_didclick'
    args['mysql_tb'] = 'wutiao.report_wutiao_adv_didclick'

    sql = HQL % args
    print(sql)
    # kdc.debug = True
    kdc.doHive(sql)

    sync_sql = '''
            select fds, os, appver, channel, didflag,
            kp_display_uv, kp_click_uv, info_flow_display_uv, info_flow_click_uv, detail_display_uv, detail_click_uv,
            hot_spot_display_uv, hot_spot_click_uv, mine_display_uv, mine_click_uv, withdraw_display_uv, withdraw_click_uv
            from %(targettab)s
            where ds = '%(ds)s'
        ''' % args
    ret = kingnetdc.presto_execsqlr(sync_sql)

    if ret:
        del_sql = '''delete from %(mysql_tb)s where fds = '%(ds)s' ''' % args
        kingnetdc.new_execsqlr(DB_PARAMS, del_sql)
        args['def_str'] = ','.join(['%s'] * len(ret[0]))
        insert_sql = '''insert into %(mysql_tb)s values(%(def_str)s) ''' % args
        kingnetdc.executemany_batches(DB_PARAMS, insert_sql, ret, 30000)


if __name__ == '__main__':
    main()

