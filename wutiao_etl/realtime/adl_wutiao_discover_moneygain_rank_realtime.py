# coding: utf-8

#**********************程序说明*********************************#
#*模块: IDL
#*功能: 五条用户操作小时表
#*作者:gant
#*时间:2018-05-26
#*备注:五条用户操作按小时按天按历史统计
#***************************************************************#

import kingnetdc
import time
import os

sql_hour = '''
set mapreduce.map.memory.mb=3000;
set mapreduce.reduce.memory.mb=3000;
set hive.merge.mapredfiles=false;
set hive.merge.mapfiles=false;

insert overwrite table {targettab} partition (ds,hour)
select t1.uid,
t1.discover_money_gain,
t2.verifypoint,
t2.vote_cnt,
null as media_status,
'{last_hour_ds}' as ds,
'{last_hour}' as hour
from(
    select uid,discover_money_gain
    from {sourcetab1}
    where ds='{last_hour_ds}' and hour='{last_hour}'
)t1
left join(
    select ouid,
    sum(if(event='verifypointinc',verifypoint,0)) as verifypoint,
    sum(if(event='like' and type=3,1,0)) as vote_cnt
    from {sourcetab2}
    where ds='{last_hour_ds}' and hour<='{last_hour}' and eventtype in ('super','high') and event in('verifypointinc','like') and ouid is not null
    group by ouid
)t2
on t1.uid=t2.ouid;
'''

sql_day = '''
set mapreduce.map.memory.mb=3000;
set mapreduce.reduce.memory.mb=3000;
set hive.merge.mapredfiles=false;
set hive.merge.mapfiles=false;

insert overwrite table {targettab} partition (ds,hour)
select t1.uid,
t1.discover_money_gain,
t2.verifypoint,
t2.vote_cnt,
null as media_status,
'{ds}' as ds,
'99' as hour
from(
    select uid,discover_money_gain
    from {sourcetab1}
    where ds='{ds}' and hour='99'
)t1
left join(
    select ouid,
    sum(if(event='verifypointinc',verifypoint,0)) as verifypoint,
    sum(if(event='like' and type=3,1,0)) as vote_cnt
    from {sourcetab2}
    where ds='{ds}' and eventtype in ('super','high') and event in('verifypointinc','like') and ouid is not null
    group by ouid
)t2
on t1.uid=t2.ouid;
'''

sql_history = '''
set mapreduce.map.memory.mb=3000;
set mapreduce.reduce.memory.mb=3000;
set hive.merge.mapredfiles=false;
set hive.merge.mapfiles=false;

insert overwrite table {targettab} partition (ds,hour)
select t3.uid,
t3.discover_money_gain,
t3.verifypoint,
t3.vote_cnt,
null as media_status,
'{ds}' as ds,
'999' as hour
from(
    select nvl(t1.uid,t2.uid) as uid,
    nvl(t1.discover_money_gain,0)+nvl(t2.discover_money_gain,0) as discover_money_gain,
    nvl(t1.verifypoint,0)+nvl(t2.verifypoint,0) as verifypoint,
    nvl(t1.vote_cnt,0)+nvl(t2.vote_cnt,0) as vote_cnt
    from (select * from {targettab} where ds='{ds}' and hour='99'
    ) t1
    full join(select * from {targettab} where ds=date_sub('{ds}',1) and hour='999') t2
    on t1.uid=t2.uid
)t3;
'''

sql_discover='''
insert overwrite table {targettab} partition (ds='{ds}', ut='discover')
select
nvl(t2.ouid,t1.ouid) as ouid,
nvl(t1.first_time,t2.first_time) as first_time,
nvl(if(t1.is_discover=2,0,t1.is_discover),t2.is_discover) as status,
null as type
from
(select 
ouid,       
min(if(event = 'discoverer' and status=1,substr(\`_sst\`,1,10),null)) as first_time,
maxbytimenotnull(if(event = 'discoverer' and status=1,1,status),cast(substr(\`_sst\`,1,10) as bigint)) as is_discover  
from {sourcetab} 
where ds = '{ds}' and eventtype='medium' and ouid is not null
and event = 'discoverer' group by ouid)t1
full outer join
(select ouid,status as is_discover,first_time from {targettab} where ds = date_sub('{ds}',1) and ut = 'discover'
)t2
on t1.ouid = t2.ouid;
'''

hourfile_check = '''hadoop fs -test -e /user/hive/warehouse/wutiao.db/adl_wutiao_discover_moneygain_rank_realtime/ds='{last_hour_ds}'/hour='{last_hour}' '''
dayfile_check = '''hadoop fs -test -e /user/hive/warehouse/wutiao.db/adl_wutiao_discover_moneygain_rank_realtime/ds='{ds}'/hour=99 '''
hourfile_dependency = '''hadoop fs -test -e /user/hive/warehouse/wutiao.db/adl_wutiao_money_realtime/ds='{last_hour_ds}'/hour='{last_hour}' '''
dayfile_dependency = '''hadoop fs -test -e /user/hive/warehouse/wutiao.db/adl_wutiao_money_realtime/ds='{ds}'/hour=99 '''
touchz_check = '''hadoop fs -touchz /user/hive/warehouse/check_point/'{last_hour_ds}'-'{last_hour_str}'_adl_wutiao_discover_moneygain_rank_realtime'''

moneygain_7day ='''hive -e "select rank() over(order by money_gain desc,first_time asc) as rank,t1.uid,money_gain,verifypoint,vote_cnt from(
select tt1.uid,first_time,sum(discover_money_gain) as money_gain,sum(verifypoint) as verifypoint,sum(vote_cnt) as vote_cnt 
from (select * from {targettab} where ds>=date_sub('{ds}',6) and ds<='{ds}' and hour=99)tt1
inner join (select ouid as uid,first_time from wutiao.idl_wutiao_user_status where ds='{ds}' and ut='discover' and status='1' 
and from_unixtime(first_time,'yyyy-MM-dd')<=date_sub('{ds}',6))tt2 on tt1.uid=tt2.uid
group by tt1.uid,first_time order by money_gain desc limit 100)t1;"|tr "\t" "|" | grep -v 'WARN\|hadoop' > /data/wwwroot/wutiao-hadooplog.kyhub.cn/getmoney_rank_{ds}.txt'''
moneygain_7day_info = '''echo `md5sum /data/wwwroot/wutiao-hadooplog.kyhub.cn/getmoney_rank_%(ds)s.txt|awk -F " " '{print $1}'`"|"`wc -l /data/wwwroot/wutiao-hadooplog.kyhub.cn/getmoney_rank_%(ds)s.txt|awk -F " " '{print $1}'` > /data/wwwroot/wutiao-hadooplog.kyhub.cn/getmoney_rank_%(ds)s_info.txt'''

moneygain_last10 = '''hive -e "select rank,uid,money_gain from(
select rank() over(order by money_gain desc,first_time asc) as rank,t1.uid,money_gain,
ntile(10) over(order by money_gain) as rn
from(select uid,sum(discover_money_gain) as money_gain
from {targettab} where ds>=date_sub('{ds}',6) and ds<='{ds}' and hour=99 group by uid)t1 
inner join (select ouid as uid,first_time
from wutiao.idl_wutiao_user_status where ds='{ds}' and ut='discover' and status='1' 
and from_unixtime(first_time,'yyyy-MM-dd')<=date_sub('{ds}',6))t2 
on t1.uid=t2.uid)t3 where rn=1;"|tr "\t" "|" | grep -v 'WARN\|hadoop' > /data/wwwroot/wutiao-hadooplog.kyhub.cn/getmoney_last_10_{ds}.txt'''
meonygain_last10_info = '''echo `md5sum /data/wwwroot/wutiao-hadooplog.kyhub.cn/getmoney_last_10_%(ds)s.txt|awk -F " " '{print $1}'`"|"`wc -l /data/wwwroot/wutiao-hadooplog.kyhub.cn/getmoney_last_10_%(ds)s.txt|awk -F " " '{print $1}'` > /data/wwwroot/wutiao-hadooplog.kyhub.cn/getmoney_last_10_%(ds)s_info.txt'''


moneygain_last15 = '''hive -e "select rank,uid,money_gain from(
select rank() over(order by money_gain desc,first_time asc) as rank,t1.uid,money_gain,
ntile(100) over(order by money_gain) as rn
from(select uid,sum(discover_money_gain) as money_gain
from {targettab} where ds>=date_sub('{ds}',6) and ds<='{ds}' and hour=99 group by uid)t1 
inner join (select ouid as uid,first_time
from wutiao.idl_wutiao_user_status where ds='{ds}' and ut='discover' and status='1' 
and from_unixtime(first_time,'yyyy-MM-dd')<=date_sub('{ds}',6))t2 
on t1.uid=t2.uid)t3 where rn<=15;"|tr "\t" "|" | grep -v 'WARN\|hadoop' > /data/wwwroot/wutiao-hadooplog.kyhub.cn/getmoney_last_15_{last_hour}.txt'''
meonygain_last15_info = '''echo `md5sum /data/wwwroot/wutiao-hadooplog.kyhub.cn/getmoney_last_15_%(last_hour)s.txt|awk -F " " '{print $1}'`"|"`wc -l /data/wwwroot/wutiao-hadooplog.kyhub.cn/getmoney_last_15_%(last_hour)s.txt|awk -F " " '{print $1}'` > /data/wwwroot/wutiao-hadooplog.kyhub.cn/getmoney_last_15_%(last_hour)s_info.txt'''


def main():
    kdc = kingnetdc.kdc
    ds = kdc.workDate
    current_minute = time.strftime('%M', time.localtime(time.time()))
    last_hour = time.strftime('%Y%m%d%H', time.localtime(time.time()-3600))
    last_hour_ds = time.strftime('%Y-%m-%d', time.localtime(time.time()-3600))
    current_hour = time.strftime('%H', time.localtime(time.time()))
    last_hour_str = time.strftime('%H', time.localtime(time.time()-3600))

    sqlhour = sql_hour.format(last_hour_ds=last_hour_ds, last_hour=last_hour,
                              targettab='wutiao.adl_wutiao_discover_moneygain_rank_realtime',
                              sourcetab1='wutiao.adl_wutiao_money_realtime',
                              sourcetab2='wutiao.odl_event_qkl',
                              sourcetab3='wutiao.idl_wutiao_user')
    sqlday = sql_day.format(ds=ds,
                            targettab='wutiao.adl_wutiao_discover_moneygain_rank_realtime',
                            sourcetab1='wutiao.adl_wutiao_money_realtime',
                            sourcetab2='wutiao.odl_event_qkl',
                            sourcetab3='wutiao.idl_wutiao_user')
    sqlhistory = sql_history.format(ds=ds,
                                    targettab='wutiao.adl_wutiao_discover_moneygain_rank_realtime')
    sqldiscover = sql_discover.format(ds=ds,
                                      sourcetab='wutiao.odl_event_qkl',
                                      targettab='wutiao.idl_wutiao_user_status')

    hourfile = hourfile_check.format(last_hour_ds=last_hour_ds, last_hour=last_hour)
    dayfile = dayfile_check.format(ds=ds)
    hourfile_d = hourfile_dependency.format(last_hour_ds=last_hour_ds,last_hour=last_hour)
    dayfile_d = dayfile_dependency.format(ds=ds)
    touchfile = touchz_check.format(last_hour_ds=last_hour_ds, last_hour_str=last_hour_str)

    moneygain_7day_cmd = moneygain_7day.format(ds=ds,targettab='wutiao.adl_wutiao_discover_moneygain_rank_realtime')
    moneygain_7day_info_cmd = moneygain_7day_info % {'ds':ds}
    moneygain_last10_cmd = moneygain_last10.format(ds=ds,targettab='wutiao.adl_wutiao_discover_moneygain_rank_realtime')
    meonygain_last10_info_cmd = meonygain_last10_info % {'ds':ds}
    moneygain_last15_cmd = moneygain_last15.format(ds=ds,last_hour=last_hour,targettab='wutiao.adl_wutiao_discover_moneygain_rank_realtime')
    meonygain_last15_info_cmd = meonygain_last15_info % {'last_hour':last_hour}

    hourflag = kdc.doCommand(hourfile, True, False)
    dayflag = kdc.doCommand(dayfile, True, False)
    hourfile_dependency_flag = kdc.doCommand(hourfile_d, True, False)
    dayfile_dependency_flag = kdc.doCommand(dayfile_d, True, False)

    kdc.debug = True

    if int(current_minute) > 1 and current_hour != '00' and hourflag == 1 and hourfile_dependency_flag == 0:
        print('Execute the discover rank hour data!')
        res = kdc.doHive(sqlhour, True, True)
        os.system(moneygain_last15_cmd)
        os.system(meonygain_last15_info_cmd)
        if res != 0:
            raise Exception('Hour discover rank data insert error!')
        else:
            kdc.doCommand(touchfile,True,False)
    elif int(current_minute) > 1 and current_hour == '00' and dayflag == 1 and dayfile_dependency_flag == 0:
        print('Excute the discover rank day data!')
        res0 = kdc.doHive(sqldiscover, True, True)
        res1 = kdc.doHive(sqlhour, True, True)
        res2 = kdc.doHive(sqlday, True, True)
        res3 = kdc.doHive(sqlhistory,True,True)
        res4 = os.system(moneygain_7day_cmd)
        res5 = os.system(moneygain_7day_info_cmd)
        res6 = os.system(moneygain_last10_cmd)
        res7 = os.system(meonygain_last10_info_cmd)
        os.system(moneygain_last15_cmd)
        os.system(meonygain_last15_info_cmd)
        if(res0 or res1 or res2 or res3 or res5 or res7) != 0:
            raise Exception('discover rank day data insert error!')
        else:
            kdc.doCommand(touchfile,True,False)
    else:
        print('Not the appropriate time!')


if __name__ == '__main__':
    main()
