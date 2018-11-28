# !/usr/bin/env python
# -*-coding: UTF-8 -*-

# **********************程序说明*********************************#
# *模块: report_activedid_qkl_danakpi -> ADL
# *功能: 区块链-五条项目-活跃设备统计
# *作者: sunyu
# *时间: 2018-05-05
# *备注: 中山机房版
# ***************************************************************#

import kylib

hql = '''
insert overwrite table %(targettab)s partition (ds)
select '%(run_date)s' as fds, last_appver, t.channel ,t.os ,newdid_cnt
    , (newdid_cnt + nvl(t2.total_newdid_cnt,0)) as total_newdid_cnt
    ,activedid_cnt ,olddid_cnt
    ,day3_activedid_cnt ,day7_activedid_cnt ,day30_activedid_cnt ,crash_did_cnt ,crash_cnt
    ,openclient_cnt
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
'''


def main():
    args = dict()
    args['run_date'] = kylib.KDC.workDate
    args['sourcetab'] = 'wutiao.idl_wutiao_user'
    args['targettab'] = 'wutiao.report_wutiao_activedid'
    args['mysql_tb'] = 'wutiao.report_wutiao_activedid'
    args['day1'] = kylib.KDC.dateSub(1)
    args['day3'] = kylib.KDC.dateSub(2)
    args['day7'] = kylib.KDC.dateSub(6)
    args['day15'] = kylib.KDC.dateSub(14)
    args['day30'] = kylib.KDC.dateSub(29)

    sql = hql % args
    print(sql)
    kylib.execute_by_task('doHive', sql)

    sync_sql = '''
        select fds, appver, channel, os, newdid_cnt, total_newdid_cnt, activedid_cnt, olddid_cnt
        , day3_activedid_cnt, day7_activedid_cnt, day30_activedid_cnt, crash_did_cnt, crash_cnt
        , if(activedid_cnt>0,1.0*openclient_cnt/activedid_cnt,0) as avg_openclient_cnt
        from %(targettab)s
        where ds = '%(run_date)s'
    ''' % args
    ret = kylib.presto_exec(sync_sql)
    print(len(ret))

    if ret:
        del_sql = '''delete from %(mysql_tb)s where fds = '%(run_date)s' ''' % args
        kylib.new_execsqlr(del_sql, kylib.DB_PARAMS_91)
        args['def_str'] = ','.join(['%s']*len(ret[0]))
        insert_sql = '''insert into %(mysql_tb)s values(%(def_str)s) ''' % args
        kylib.executemany_batches(kylib.DB_PARAMS_91, insert_sql, ret, 30000)


if __name__ == '__main__':
    main()
