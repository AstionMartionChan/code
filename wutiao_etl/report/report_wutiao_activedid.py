# !/usr/bin/env python
# -*-coding: UTF-8 -*-

# **********************程序说明*********************************#
# *模块: report_activedid_qkl_danakpi -> ADL
# *功能: 区块链-五条项目-活跃设备统计
# *作者: sunyu
# *时间: 2018-05-05
# *备注:
# ***************************************************************#
import re
import kingnetdc
import sys
import os
sys.path.append('/'.join(os.path.abspath(__file__).split('/')[:-2]))
from project_constant import DB_PARAMS_TEST


DB_PARAMS = DB_PARAMS_TEST

HQL = '''
set mapred.max.split.size=10000000;
SET mapreduce.map.memory.mb=4000;
insert overwrite table %(targettab)s partition (ds)
select '%(run_date)s' as fds, last_appver, t.channel ,t.os ,newdid_cnt
    , (newdid_cnt + nvl(t2.total_newdid_cnt,0)) as total_newdid_cnt
    ,activedid_cnt ,olddid_cnt
    ,day3_activedid_cnt ,day7_activedid_cnt ,day30_activedid_cnt ,crash_did_cnt ,crash_cnt
    ,openclient_cnt
    , 0 as newuser_cnt
    ,'%(run_date)s' as ds
from (
    select nvl(last_appver,'allappver') as last_appver
        , nvl(channel, 'allchannel') as channel
        , nvl(os, 'allos') as os
        ,sum(if(new_tag=1,1,0)) as newdid_cnt
        ,sum(if(act_tag=1,1,0)) as activedid_cnt
        ,sum(if(old_act_tag=1,1,0)) as olddid_cnt
        ,sum(if(day3_tag=1,1,0)) as day3_activedid_cnt
        ,sum(if(day7_tag=1,1,0)) as day7_activedid_cnt
        ,sum(if(day30_tag=1,1,0)) as day30_activedid_cnt
        ,sum(if(crash_pv>=1,1,0)) as crash_did_cnt
        ,sum(crash_pv) as crash_cnt
        ,sum(openclient_pv) as openclient_cnt
        from (
        select nvl(last_appver,'-1') as last_appver, nvl(last_channel,'-1') as channel, nvl(ouid, '-1') as ouid, nvl(last_os,'-1') as os
            , if(from_unixtime(first_login_time, 'yyyy-MM-dd') = '%(run_date)s',1,0) as new_tag
            , if(from_unixtime(last_login_time, 'yyyy-MM-dd') = '%(run_date)s',1,0) as act_tag
            , if(from_unixtime(last_login_time, 'yyyy-MM-dd') = '%(run_date)s' and from_unixtime(first_login_time, 'yyyy-MM-dd') < '%(run_date)s',1,0) as old_act_tag
            , if(from_unixtime(last_login_time, 'yyyy-MM-dd') between '%(day3)s' and '%(run_date)s',1,0) as day3_tag
            , if(from_unixtime(last_login_time, 'yyyy-MM-dd') between '%(day7)s' and '%(run_date)s',1,0) as day7_tag
            , if(from_unixtime(last_login_time, 'yyyy-MM-dd') > '%(day30)s' and from_unixtime(last_login_time, 'yyyy-MM-dd') <= '%(run_date)s',1,0) as day30_tag
            , if(from_unixtime(last_login_time, 'yyyy-MM-dd') = '%(run_date)s',crash_cnt,0) as crash_pv 
            , if(from_unixtime(last_login_time, 'yyyy-MM-dd') = '%(run_date)s',last_openclient_cnt,0) as openclient_pv 
        from %(sourcetab)s 
        where ds = '%(run_date)s'
        and ut = 'did'
    ) t
    group by last_appver, channel, os with cube
) t
left join
(
    select appver, channel, os, total_newdid_cnt
    from %(targettab)s
    where ds = '%(day1)s'
) t2
on t.last_appver = t2.appver and t.channel = t2.channel and t.os = t2.os
;


insert overwrite table %(targettab)s partition (ds)
select a.fds, a.appver, a.channel, a.os, newdid_cnt, total_newdid_cnt, activedid_cnt, olddid_cnt
, day3_activedid_cnt, day7_activedid_cnt, day30_activedid_cnt, crash_did_cnt, crash_cnt
, openclient_cnt
, nvl(b.newuser_cnt,0) as newuser_cnt
, ds
from (
    select fds, appver, channel, os, newdid_cnt, total_newdid_cnt, activedid_cnt, olddid_cnt
    , day3_activedid_cnt, day7_activedid_cnt, day30_activedid_cnt, crash_did_cnt, crash_cnt
    , openclient_cnt
    , ds
    from %(targettab)s
    where ds = '%(run_date)s'
) a
left join 
(
    select fds, appver, channel, os, newuser_cnt
    from %(user_hive_tb)s
    where ds = '%(run_date)s'
) b
on a.fds = b.fds and a.appver = b.appver and a.channel = b.channel and a.os = b.os
'''


def main():
    kdc = kingnetdc.kdc
    args = dict()
    args['run_date'] = kdc.workDate
    args['sourcetab'] = 'wutiao.idl_wutiao_user'
    args['targettab'] = 'wutiao.report_wutiao_activedid'
    args['mysql_tb'] = 'wutiao.report_wutiao_activedid'
    args['user_hive_tb'] = 'wutiao.report_wutiao_activeuser'
    args['day1'] = kdc.dateSub(1)
    args['day3'] = kdc.dateSub(2)
    args['day7'] = kdc.dateSub(6)
    args['day15'] = kdc.dateSub(14)
    args['day30'] = kdc.dateSub(29)

    sql = HQL % args
    print(sql)
    kdc.doHive(sql)
    sync_sql = '''
        select fds, appver, channel, os, newdid_cnt, total_newdid_cnt, activedid_cnt, olddid_cnt
        , day3_activedid_cnt, day7_activedid_cnt, day30_activedid_cnt, crash_did_cnt, crash_cnt
        , nvl(if(activedid_cnt>0,1.0*openclient_cnt/activedid_cnt,0),0) as avg_openclient_cnt
        , newuser_cnt
        from %(targettab)s
        where ds = '%(run_date)s'
    ''' % args
    ret = [x.split('\t') for x in kingnetdc.do_hql_exec(sync_sql).split('\n') if bool(re.match(r'^\d{4}-\d{2}-\d{2}', x[:10]))]
    print(len(ret))

    if ret:
        del_sql = '''delete from %(mysql_tb)s where fds = '%(run_date)s' ''' % args
        kingnetdc.new_execsqlr(DB_PARAMS, del_sql)
        args['def_str'] = ','.join(['%s']*len(ret[0]))
        insert_sql = '''insert into %(mysql_tb)s values(%(def_str)s) ''' % args
        kingnetdc.executemany_batches(DB_PARAMS, insert_sql, ret, 30000)


if __name__ == '__main__':
    main()
