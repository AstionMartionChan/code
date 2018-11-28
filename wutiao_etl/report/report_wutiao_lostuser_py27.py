# !/usr/bin/env python
# -*-coding: UTF-8 -*-

# **********************程序说明*********************************#
# *模块: report_wutiao_lostuser -> ADL
# *功能: 区块链-五条项目-流失用户统计
# *作者: sunyu
# *时间: 2018-05-28
# *备注: python2.7版本
# ***************************************************************#

import kylib



# 计算总数据
HQL = '''insert overwrite table %(targettab)s partition (ds)
select '%(run_date)s' as fds, last_appver,channel,os
    , lost_activeuser_cnt
    , silent_user_cnt
    , 0 as day1_lost_user_cnt
    , 0 as day3_lost_user_cnt
    , 0 as day7_lost_user_cnt
    , 0 as day30_lost_user_cnt
    ,lost_newuser_cnt
    ,lost_olduser_cnt
    ,'%(run_date)s' as ds
from (
    select nvl(last_appver, 'allappver') as last_appver
        , nvl(channel, 'allchannel') as channel
        , nvl(os,'allos') as os
        ,count(distinct if(lost_tag=1, ouid, null)) as lost_activeuser_cnt
        ,count(distinct if(silent_tag=1,ouid,null)) as silent_user_cnt
        ,count(distinct if(lost_tag=1 and lost_new_tag=1, ouid, null)) as lost_newuser_cnt
        ,count(distinct if(lost_tag=1 and lost_new_tag=0, ouid, null)) as lost_olduser_cnt
    from (
        select nvl(last_appver,'-1') as last_appver, nvl(last_channel,'-1') as channel, nvl(ouid, '-1') as ouid
            , nvl(last_os,'-1') as os
            , if(from_unixtime(last_login_time, 'yyyy-MM-dd') = '%(run_date)s',1,0) as last_tag
            , if(from_unixtime(last_login_time, 'yyyy-MM-dd') = '%(day31)s',1,0) as lost_tag
            , if(from_unixtime(first_login_time, 'yyyy-MM-dd') = '%(day31)s',1,0) as lost_new_tag
            , if(from_unixtime(last_login_time, 'yyyy-MM-dd') >= '%(day30)s' and from_unixtime(last_login_time, 'yyyy-MM-dd') < '%(day15)s',1,0) as silent_tag
        from %(sourcetab)s
        where ds = '%(run_date)s'
        and ut = 'uid'
    ) a
    group by last_appver, channel, os with cube
) t
'''


# 流失计算及刷新
LOST_SQL = '''
set mapreduce.reduce.memory.mb=2000;
insert overwrite table %(targettab)s partition (ds)
select a.fds, a.appver, a.channel, a.os
    ,lost_activeuser_cnt
    ,silent_user_cnt
    ,day1_lost_user_cnt
    ,day3_lost_user_cnt
    ,day7_lost_user_cnt
    ,day30_lost_user_cnt
    ,lost_newuser_cnt
    ,lost_olduser_cnt
    ,a.ds
from (
    select fds, appver, channel, os, lost_activeuser_cnt, silent_user_cnt
        , lost_newuser_cnt,lost_olduser_cnt
        , ds
    from  %(targettab)s
    where ds between '%(day30)s' and '%(run_date)s'
) a
left join
(
    select new_ds as fds
        , nvl(last_appver, 'allappver') as appver
        , nvl(channel, 'allchannel') as channel
        , nvl(os, 'allos') as os
        ,count(distinct if(new_diff_day=1 and  last_diff_day=1 ,ouid,null)) as day1_lost_user_cnt
        ,count(distinct if(new_diff_day=3 and  last_diff_day=3 ,ouid,null)) as day3_lost_user_cnt
        ,count(distinct if(new_diff_day=7 and  last_diff_day=7 ,ouid,null)) as day7_lost_user_cnt
        ,count(distinct if(new_diff_day=30 and last_diff_day=30,ouid,null)) as day30_lost_user_cnt
        , new_ds as ds
    from (
        select ds, nvl(last_appver,'-1') as last_appver, nvl(last_channel,'-1') as channel, nvl(ouid, '-1') as ouid
            , nvl(last_os,'-1') as os
            ,from_unixtime(first_login_time, 'yyyy-MM-dd') as new_ds
            ,datediff(ds, from_unixtime(first_login_time, 'yyyy-MM-dd')) as new_diff_day
            ,datediff(ds, from_unixtime(last_login_time, 'yyyy-MM-dd')) as last_diff_day
        from %(sourcetab)s
        where ut = 'uid'
        and ds between '%(day30)s' and '%(run_date)s'
        and first_login_time is not null
    ) t
    group by new_ds, last_appver, channel, os
    grouping sets(
        (new_ds, last_appver, channel, os),
        (new_ds, last_appver, channel),
        (new_ds, last_appver, os),
        (new_ds, last_appver),
        (new_ds, channel, os),
        (new_ds, channel),
        (new_ds, os),
        (new_ds)
    )
) b
on a.fds = b.fds and a.appver = b.appver and a.channel = b.channel and a.os = b.os
'''



def main():
    kdc = kylib.KDC
    args = dict()
    args['run_date'] = kdc.workDate
    args['sourcetab'] = 'wutiao.idl_wutiao_user'
    args['targettab'] = 'wutiao.report_wutiao_lostuser'
    args['mysql_tb'] = 'wutiao.report_wutiao_lostuser'
    args['day1'] = kdc.dateSub(1)
    args['day3'] = kdc.dateSub(2)
    args['day7'] = kdc.dateSub(6)
    args['day15'] = kdc.dateSub(14)
    args['day30'] = kdc.dateSub(29)
    args['day31'] = kdc.dateSub(30)

    sqls = [HQL, LOST_SQL]

    for sql in sqls:
        sql = sql % args
        print(sql)
        kylib.execute_by_task('doHive', sql)

    sync_sql = '''
        select fds, appver, channel, os, lost_activeuser_cnt, silent_user_cnt
            , day1_lost_user_cnt, day3_lost_user_cnt, day7_lost_user_cnt, day30_lost_user_cnt
            , lost_newuser_cnt, lost_olduser_cnt
        from %(targettab)s
        where ds between '%(day30)s' and '%(run_date)s'
    ''' % args
    ret = kylib.presto_exec(sync_sql)
    print(len(ret))

    if ret:
        del_sql = '''delete from %(mysql_tb)s where fds between '%(day30)s' and '%(run_date)s' ''' % args
        kylib.new_execsqlr(del_sql, kylib.DB_PARAMS_91)
        args['def_str'] = ','.join(['%s']*len(ret[0]))
        insert_sql = '''insert into %(mysql_tb)s values(%(def_str)s) ''' % args
        kylib.executemany_batches(kylib.DB_PARAMS_91, insert_sql, ret, 30000)


if __name__ == '__main__':
    main()
