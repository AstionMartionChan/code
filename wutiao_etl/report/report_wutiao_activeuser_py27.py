# !/usr/bin/env python
# -*-coding: UTF-8 -*-

# **********************程序说明*********************************#
# *模块: report_activeuser_qkl_danakpi -> ADL
# *功能: 区块链-五条项目-活跃用户统计
# *作者: sunyu
# *时间: 2018-05-05
# *备注: 中山机房版
# ***************************************************************#

import kylib

HQL = '''
insert overwrite table %(targettab)s partition (ds)
select '%(run_date)s' as fds, last_appver, t.channel, t.os
    , newuser_cnt + nvl(t2.total_newuser_cnt,0) as total_newuser_cnt
    , 0 as once_user_cnt
    , callback_user_cnt
    , newuser_cnt, activeuser_cnt, olduser_cnt, day3_activeuser_cnt
    ,day7_activeuser_cnt ,day30_activeuser_cnt, lost_user_cnt, silent_user_cnt, register_user_cnt
    ,0 as visitor_user_cnt
    ,'%(run_date)s' as ds
from (
    select nvl(a.last_appver, 'allappver') as last_appver
        , nvl(a.channel, 'allchannel') as channel
        , nvl(a.os, 'allos') as os
        ,sum(if(new_tag=1,1,0)) as newuser_cnt
        ,sum(if(act_tag=1,1,0)) as activeuser_cnt
        ,sum(if(old_act_tag=1,1,0)) as olduser_cnt
        ,sum(if(day3_tag=1,1,0)) as day3_activeuser_cnt
        ,sum(if(day7_tag=1,1,0)) as day7_activeuser_cnt
        ,sum(if(day30_tag=1,1,0)) as day30_activeuser_cnt
        ,sum(if(lost_tag=1,1,0)) as lost_user_cnt
        ,sum(if(silent_tag=1,1,0)) as silent_user_cnt
        ,sum(if(reg_tag=1,1,0)) as register_user_cnt
        ,sum(if(act_tag=1 and day31_act=1,1,0)) as callback_user_cnt
    from (
        select nvl(last_appver,'-1') as last_appver, nvl(last_channel,'-1') as channel, nvl(ouid, '-1') as ouid
            , nvl(last_os,'-1') as os
            , if(from_unixtime(first_login_time, 'yyyy-MM-dd') = '%(run_date)s',1,0) as new_tag
            , if(from_unixtime(last_login_time, 'yyyy-MM-dd') = '%(run_date)s',1,0) as act_tag
            , if(from_unixtime(last_login_time, 'yyyy-MM-dd') = '%(run_date)s' and from_unixtime(first_login_time, 'yyyy-MM-dd') < '%(run_date)s',1,0) as old_act_tag
            , if(from_unixtime(last_login_time, 'yyyy-MM-dd') between '%(day3)s' and '%(run_date)s',1,0) as day3_tag
            , if(from_unixtime(last_login_time, 'yyyy-MM-dd') between '%(day7)s' and '%(run_date)s',1,0) as day7_tag
            , if(from_unixtime(last_login_time, 'yyyy-MM-dd') > '%(day30)s' and from_unixtime(last_login_time, 'yyyy-MM-dd') <= '%(run_date)s',1,0) as day30_tag
            , if(from_unixtime(last_login_time, 'yyyy-MM-dd') = '%(day31)s',1,0) as lost_tag
            , if(from_unixtime(last_login_time, 'yyyy-MM-dd') >= '%(day30)s' and from_unixtime(last_login_time, 'yyyy-MM-dd') < '%(day15)s',1,0) as silent_tag
            , if(from_unixtime(last_login_time, 'yyyy-MM-dd') = '%(run_date)s',last_openclient_cnt,0) as login_pv 
            , if(from_unixtime(registe_time, 'yyyy-MM-dd') = '%(run_date)s',1,0) as reg_tag
        from %(sourcetab)s
        where ds = '%(run_date)s'
        and ut = 'uid'
    ) a
    left join 
    (
        select nvl(last_appver,'-1') as last_appver, nvl(last_channel,'-1') as channel, nvl(ouid, '-1') as uid
            ,nvl(last_os,'-1') as os , 1 as day31_act
        from %(sourcetab)s
        where ds = '%(day1)s'
        and ut = 'uid'
        and from_unixtime(last_login_time, 'yyyy-MM-dd') = '%(day31)s'
    ) b
    on a.last_appver = b.last_appver and a.channel = b.channel and a.ouid = b.uid and a.os = b.os
    group by a.last_appver, a.channel, a.os with cube
) t
left join
(
    select appver, channel, os, total_newuser_cnt
    from %(targettab)s
    where ds = '%(day1)s'
) t2
on t.last_appver = t2.appver and t.channel = t2.channel and t.os = t2.os
'''


# 统计一次性用户
ONCE_USER_SQL = '''

set mapred.max.split.size=10000000;
SET mapreduce.map.memory.mb=4000;

insert overwrite table %(targettab)s partition (ds)
select a.fds ,a.appver,a.channel,a.os,total_newuser_cnt,nvl(b.once_user_cnt,0) as once_user_cnt
      ,callback_user_cnt,newuser_cnt,activeuser_cnt,olduser_cnt
     ,day3_activeuser_cnt ,day7_activeuser_cnt ,day30_activeuser_cnt ,lost_user_cnt ,silent_user_cnt
     ,register_user_cnt, visitor_user_cnt, ds
from (
    select fds ,appver,channel,os,total_newuser_cnt ,callback_user_cnt,newuser_cnt,activeuser_cnt,olduser_cnt
         ,day3_activeuser_cnt ,day7_activeuser_cnt ,day30_activeuser_cnt ,lost_user_cnt ,silent_user_cnt
         ,register_user_cnt ,visitor_user_cnt,ds
    from %(targettab)s
    where ds = '%(day31)s'
) a
left join
(
    select '%(day31)s' as fds
        , nvl(last_appver, 'allappver') as last_appver
        , nvl(channel, 'allchannel') as channel
        , nvl(os, 'allos') as os
        , count(1) as once_user_cnt
    from (
        select nvl(last_appver,'-1') as last_appver, nvl(last_channel,'-1') as channel, nvl(ouid, '-1') as uid
            ,nvl(last_os,'-1') as os
        from %(sourcetab)s
        where ds = '%(run_date)s'
        and ut = 'uid'
        and from_unixtime(registe_time, 'yyyy-MM-dd') = '%(day31)s'
        and first_login_time is null
    ) t
    group by last_appver, channel, os with cube
) b
on a.fds = b.fds and a.appver=b.last_appver and a.channel=b.channel and a.os = b.os
'''

# 游客统计
VISITOR_SQL = '''
set mapred.max.split.size=10000000;
SET mapreduce.map.memory.mb=4000;

insert overwrite table %(targettab)s partition (ds)
select a.fds ,a.appver,a.channel,a.os,total_newuser_cnt, once_user_cnt
      ,callback_user_cnt,newuser_cnt,activeuser_cnt,olduser_cnt
     ,day3_activeuser_cnt ,day7_activeuser_cnt ,day30_activeuser_cnt ,lost_user_cnt ,silent_user_cnt
     ,register_user_cnt, nvl(visitor_user_cnt,0) as visitor_user_cnt,ds
from (
    select fds ,appver,channel,os,total_newuser_cnt,once_user_cnt ,callback_user_cnt,newuser_cnt,activeuser_cnt,olduser_cnt
         ,day3_activeuser_cnt ,day7_activeuser_cnt ,day30_activeuser_cnt ,lost_user_cnt ,silent_user_cnt
         ,register_user_cnt ,ds
    from %(targettab)s
    where ds = '%(run_date)s'
) a
left join
(
    select nvl(last_appver, 'allappver') as last_appver
        , nvl(channel, 'allchannel') as channel
        , nvl(os, 'allos') as os
        , count(distinct uid) as visitor_user_cnt
    from (
        select nvl(last_appver,'-1') as last_appver, nvl(last_channel,'-1') as channel, nvl(ouid, '-1') as uid
            ,nvl(last_os,'-1') as os
        from %(sourcetab)s
        where ds = '%(run_date)s'
        and ut = 'did'
        and from_unixtime(last_login_time, 'yyyy-MM-dd') = ds
        and (last_did_ouid_time is null or from_unixtime(last_did_ouid_time,'yyyy-MM-dd')!=ds)
    ) t
    group by last_appver, channel, os with cube
) b
on a.appver=b.last_appver and a.channel=b.channel and a.os = b.os
'''


def main():
    args = dict()
    args['run_date'] = kylib.KDC.workDate
    args['sourcetab'] = 'wutiao.idl_wutiao_user'
    args['targettab'] = 'wutiao.report_wutiao_activeuser'
    args['mysql_tb'] = 'wutiao.report_wutiao_activeuser'
    args['day1'] = kylib.KDC.dateSub(1)
    args['day3'] = kylib.KDC.dateSub(2)
    args['day7'] = kylib.KDC.dateSub(6)
    args['day15'] = kylib.KDC.dateSub(14)
    args['day30'] = kylib.KDC.dateSub(29)
    args['day31'] = kylib.KDC.dateSub(30)

    kylib.KDC.debug = True
    sqls = [HQL, ONCE_USER_SQL, VISITOR_SQL]
    for sql in sqls:
        sql = sql % args
        print(sql)
        kylib.execute_by_task('doHive', sql)
    sync_sql = '''
        select fds, appver, channel, os, total_newuser_cnt, once_user_cnt, callback_user_cnt
        , newuser_cnt, activeuser_cnt, olduser_cnt
        , day3_activeuser_cnt, day7_activeuser_cnt, day30_activeuser_cnt, lost_user_cnt, silent_user_cnt
        , register_user_cnt
        , visitor_user_cnt
        from %(targettab)s
        where ds in ('%(run_date)s', '%(day31)s')
    ''' % args
    ret = kylib.presto_exec(sync_sql)
    print(len(ret))

    if ret:
        del_sql = '''delete from %(mysql_tb)s where fds in ('%(run_date)s', '%(day31)s') ''' % args
        kylib.new_execsqlr(del_sql, kylib.DB_PARAMS_91)
        args['def_str'] = ','.join(['%s']*len(ret[0]))
        insert_sql = '''insert into %(mysql_tb)s values(%(def_str)s) ''' % args
        kylib.executemany_batches(kylib.DB_PARAMS_91, insert_sql, ret, 30000)


if __name__ == '__main__':
    main()
