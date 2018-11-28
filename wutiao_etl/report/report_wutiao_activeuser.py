# !/usr/bin/env python
# -*-coding: UTF-8 -*-

# **********************程序说明*********************************#
# *模块: report_activeuser_qkl_danakpi -> ADL
# *功能: 区块链-五条项目-活跃用户统计
# *作者: sunyu
# *时间: 2018-05-05
# *备注:
# ***************************************************************#
import re
import kingnetdc
import sys
import os
sys.path.append('/'.join(os.path.abspath(__file__).split('/')[:-2]))
#from project_constant import DB_PARAMS_TEST

DB_PARAMS = {'host': '172.27.0.255', 'user': 'datac', 'password': 'g3Z2zHF6uTxK6#rnlZ7', 'port': 3306, 'charset': 'utf8'}


HQL = '''
set mapred.max.split.size=10000000;
SET mapreduce.map.memory.mb=4000;
set hive.exec.dynamic.partition.mode=nonstrict;

insert overwrite table %(targettab)s partition (ds)
select '%(run_date)s' as fds, last_appver, t.channel, t.os
    , newuser_cnt + nvl(t2.total_newuser_cnt,0) as total_newuser_cnt
    , 0 as once_user_cnt
    , callback_user_cnt
    , newuser_cnt, activeuser_cnt, olduser_cnt, day3_activeuser_cnt
    ,day7_activeuser_cnt ,day30_activeuser_cnt, lost_user_cnt, silent_user_cnt, register_user_cnt
    ,0 as visitor_user_cnt
    ,0 as visitor_new_user_cnt
    ,0 as visitor_old_user_cnt
    ,t4.filter_ouid_cnt
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
left join 
(
 
select nvl(os,'allos') as os,
       nvl(appver,'allappver') as appver,
       nvl(channel,'allchannel') as channel,
       filter_ouid_cnt
from( 
select tb.os,
       tb.appver,
       tb.channel,
       count(tb.ouid) as filter_ouid_cnt
from(
  select t.ouid,
         nvl(t2.os,'-1') as os,
         nvl(t2.appver,'-1') as appver,
         t2.row1,
         nvl(t3.channel,'-1') as channel,
         t3.row2
    from wutiao.idl_wutiao_filter t 
    left join (select * from wutiao.idl_wutiao_filter  where ds = '%(day1)s' and ut = 'uid') t1
         on t.ouid = t1.ouid
   inner join (select ouid,lower(\`_os\`) as os,\`_appver\` as appver,
                      row_number() over(partition by ouid order by \`timestamp\` desc) as row1
                 from wutiao.odl_event_qkl t
                where ds = '%(run_date)s'
              ) t2
	      on t.ouid = t2.ouid
   inner join (select ouid,\`_channel\` as channel,  
                     row_number() over(partition by ouid order by \`timestamp\`) as row2
                from wutiao.odl_event_qkl t
               where ds = '%(run_date)s'
              ) t3
		  on t.ouid = t3.ouid
   where t.ds = '%(run_date)s'
     and t.ut = 'uid'
     and t1.ouid is null
    ) tb 
where tb.row1 = 1 
  and tb.row2 = 1 
group by tb.os ,tb.appver ,tb.channel
grouping sets((tb.os ,tb.appver ,tb.channel),(tb.os),(tb.appver),(tb.channel),(tb.os ,tb.appver),(tb.appver ,tb.channel),(tb.os ,tb.channel),())
) tt

) t4
on t.last_appver = t4.appver and t.channel = t4.channel and t.os = t4.os
'''


# 统计一次性用户
ONCE_USER_SQL = '''

set mapred.max.split.size=10000000;
SET mapreduce.map.memory.mb=4000;
set hive.exec.dynamic.partition.mode=nonstrict;

insert overwrite table %(targettab)s partition (ds)
select a.fds ,a.appver,a.channel,a.os,total_newuser_cnt,nvl(b.once_user_cnt,0) as once_user_cnt
      ,callback_user_cnt,newuser_cnt,activeuser_cnt,olduser_cnt
     ,day3_activeuser_cnt ,day7_activeuser_cnt ,day30_activeuser_cnt ,lost_user_cnt ,silent_user_cnt
     ,register_user_cnt
     ,visitor_user_cnt,visitor_new_user_cnt,visitor_old_user_cnt, filter_ouid_cnt
     ,ds
from (
    select fds ,appver,channel,os,total_newuser_cnt ,callback_user_cnt,newuser_cnt,activeuser_cnt,olduser_cnt
         ,day3_activeuser_cnt ,day7_activeuser_cnt ,day30_activeuser_cnt ,lost_user_cnt ,silent_user_cnt
         ,register_user_cnt ,visitor_user_cnt,visitor_new_user_cnt,visitor_old_user_cnt ,ds,filter_ouid_cnt
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
set hive.exec.dynamic.partition.mode=nonstrict;


insert overwrite table %(targettab)s partition (ds)
select a.fds ,a.appver,a.channel,a.os,total_newuser_cnt, once_user_cnt
      ,callback_user_cnt,newuser_cnt,activeuser_cnt,olduser_cnt
     ,day3_activeuser_cnt ,day7_activeuser_cnt ,day30_activeuser_cnt ,lost_user_cnt ,silent_user_cnt
     ,register_user_cnt
     , nvl(visitor_user_cnt,0) as visitor_user_cnt
     , nvl(visitor_new_user_cnt,0) as visitor_new_user_cnt
     , nvl(visitor_old_user_cnt,0) as visitor_old_user_cnt,filter_ouid_cnt
     ,ds
from (
    select fds ,appver,channel,os,total_newuser_cnt,once_user_cnt ,callback_user_cnt,newuser_cnt,activeuser_cnt,olduser_cnt
         ,day3_activeuser_cnt ,day7_activeuser_cnt ,day30_activeuser_cnt ,lost_user_cnt ,silent_user_cnt
         ,register_user_cnt ,ds,filter_ouid_cnt
    from %(targettab)s
    where ds = '%(run_date)s'
) a
left join
(
    select nvl(last_appver, 'allappver') as last_appver
        , nvl(channel, 'allchannel') as channel
        , nvl(os, 'allos') as os
        , count(distinct uid) as visitor_user_cnt
        , count(distinct new_uid) as visitor_new_user_cnt
        , count(distinct uid) - count(distinct new_uid) as visitor_old_user_cnt
    from (
        select nvl(last_appver,'-1') as last_appver, nvl(last_channel,'-1') as channel, nvl(ouid, '-1') as uid
            ,nvl(last_os,'-1') as os
            ,if(from_unixtime(first_login_time, 'yyyy-MM-dd')=ds,nvl(ouid, '-1'),null) as new_uid
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
    kdc = kingnetdc.kdc
    args = dict()
    args['run_date'] = kdc.workDate
    args['sourcetab'] = 'wutiao.idl_wutiao_user'
    args['targettab'] = 'wutiao.report_wutiao_activeuser'
    args['mysql_tb'] = 'wutiao.report_wutiao_activeuser'
    args['day1'] = kdc.dateSub(1)
    args['day3'] = kdc.dateSub(2)
    args['day7'] = kdc.dateSub(6)
    args['day15'] = kdc.dateSub(14)
    args['day30'] = kdc.dateSub(29)
    args['day31'] = kdc.dateSub(30)

    kdc.debug = True
    sqls = [
        HQL,
        ONCE_USER_SQL,  # 一次性用户
        VISITOR_SQL,  # 游客统计
    ]
    for sql in sqls:
        sql = sql % args
        print(sql)
        kdc.doHive(sql)

    sync_sql = '''
        select fds, appver, channel, os, total_newuser_cnt, once_user_cnt, callback_user_cnt
        , newuser_cnt, activeuser_cnt, olduser_cnt
        , day3_activeuser_cnt, day7_activeuser_cnt, day30_activeuser_cnt, lost_user_cnt, silent_user_cnt
        , register_user_cnt
        , visitor_user_cnt
        , visitor_new_user_cnt
        , visitor_old_user_cnt
        , filter_ouid_cnt
        from %(targettab)s
        where ds in ('%(run_date)s', '%(day31)s')
    ''' % args
    try:
        ret = kingnetdc.presto_execsqlr(sync_sql)
    except Exception as e:
        print(e)
        ret = [x.split('\t') for x in kingnetdc.do_hql_exec(sync_sql).split('\n') if bool(re.match(r'^\d{4}-\d{2}-\d{2}', x[:10]))]
    print(len(ret))

    if ret:
        del_sql = '''delete from %(mysql_tb)s where fds in ('%(run_date)s', '%(day31)s') ''' % args
        kingnetdc.new_execsqlr(DB_PARAMS, del_sql)
        args['def_str'] = ','.join(['%s']*len(ret[0]))
        insert_sql = '''insert into %(mysql_tb)s values(%(def_str)s) ''' % args
        kingnetdc.executemany_batches(DB_PARAMS, insert_sql, ret, 30000)
    ''''''

if __name__ == '__main__':
    main()