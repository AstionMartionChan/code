# coding: utf-8

#**********************程序说明*********************************#
#*模块: ADL
#*功能: 五条贡献力审核力统计表
#*作者:gant
#*时间:2018-05-25
#*备注:五条用户贡献力审核力最近30天统计表
#***************************************************************#

import kingnetdc
import time

sql_hour = '''
insert overwrite table {targettab} partition (ds,hour)
select t3.familyid,
t3.ouid,
nvl(t3.contributionpoint,0)+nvl(t4.contributionpoint,0) as contributionpoint,
'{last_hour_ds}' as ds,
'{last_hour}' as hour
from(
    select case when t2.event='joinfamily' then t2.familyid
                when t2.event='leavefamily' then null
            else t1.familyid end as familyid,
    nvl(t1.ouid,t2.ouid) as ouid,
    t1.contributionpoint
    from(
        select familyid, 
        ouid,
        contributionpoint
        from {targettab}
        where ds='{last_two_hour_ds}' and hour='{last_two_hour}' 
    )t1
    full join(
        select ouid,
        maxbytime(familyid,cast(substr(\`_sst\`,1,10) as bigint)) as familyid,
        maxbytime(event,cast(substr(\`_sst\`,1,10) as bigint)) as event
        from {sourcetab}
        where ds = '{last_hour_ds}' and hour='{last_hour}' and eventtype='medium' and event in ('joinfamily','leavefamily') 
        group by ouid
    )t2
    on t1.ouid=t2.ouid
)t3
left join(
    select ouid,
    sum(contributionpoint) as contributionpoint
    from {sourcetab}
    where ds = '{last_hour_ds}' and hour='{last_hour}' and eventtype='high' and event = 'contributioninc' and familyid is not null 
    group by ouid
)t4
on t3.ouid=t4.ouid;
'''

#凌晨一点用最新的最近29天数据作为固定数据加上今天凌晨到一点的浮动数据
sql_hour_start = '''
insert overwrite table {targettab} partition (ds='{last_hour_ds}',hour='{last_hour}')
select t3.familyid,
t3.ouid,
nvl(t3.contributionpoint,0)+nvl(t4.contributionpoint,0) as contributionpoint
from(
    select case when t2.event='joinfamily' then t2.familyid
                when t2.event='leavefamily' then null
            else t1.familyid end as familyid,
    nvl(t1.ouid,t2.ouid) as ouid,
    t1.contributionpoint
    from(
        select familyid, 
        ouid,
        contributionpoint
        from {targettab}
        where ds='{ds}' and hour='29day'
    )t1
    full join(
        select ouid,
        maxbytime(familyid,cast(substr(\`_sst\`,1,10) as bigint)) as familyid,
        maxbytime(event,cast(substr(\`_sst\`,1,10) as bigint)) as event
        from {sourcetab}
        where ds = '{last_hour_ds}' and hour='{last_hour}' and eventtype='medium' and event in ('joinfamily','leavefamily')
        group by ouid
    )t2
    on t1.ouid=t2.ouid
)t3
left join(
    select ouid,
    sum(contributionpoint) as contributionpoint
    from {sourcetab}
    where ds = '{last_hour_ds}' and hour='{last_hour}' and eventtype='high' and event = 'contributioninc' and familyid is not null 
    group by ouid
)t4
on t3.ouid=t4.ouid;
'''

sql_29day = '''
insert overwrite table {targettab} partition (ds='{ds}',hour='29day')
select t2.familyid,
nvl(t2.ouid,t1.ouid) as ouid,
nvl(t1.contributionpoint,0) as contributionpoint
from(
    select nvl(tt1.ouid,tt2.ouid) as ouid,
    nvl(tt1.contributionpoint,0)+nvl(tt2.contributionpoint,0) as contributionpoint
    from(
        select ouid,
        sum(if(from_unixtime(last_contribution_time,'yyyy-MM-dd')=ds,last_day_contributionpoint,0)) as contributionpoint
        from {sourcetab1}
        where ds>=date_sub('{ds}',28) and ds<=date_sub('{ds}',1) and ut='uid' and familyid is not null
        group by ouid
    )tt1
    full join(select ouid,sum(contributionpoint) as contributionpoint from {sourcetab2} where ds='{ds}' and eventtype='high' and event='contributioninc' and familyid is not null group by ouid)tt2
    on tt1.ouid=tt2.ouid
)t1
full join(
    select case when tt2.event='joinfamily' then tt2.familyid
                when tt2.event='leavefamily' then null
            else tt1.familyid end as familyid,
    nvl(tt1.ouid,tt2.ouid) as ouid
    from(
        select ouid,
        familyid
        from {sourcetab1}
        where ds=date_sub('{ds}',1) and ut='uid' and familyid is not null)tt1
    full join(
        select ouid,
        maxbytime(familyid,cast(substr(\`_sst\`,1,10) as bigint)) as familyid,
        maxbytime(event,cast(substr(\`_sst\`,1,10) as bigint)) as event
        from {sourcetab2}
        where ds = '{ds}' and eventtype='medium' and event in ('joinfamily','leavefamily')
        group by ouid
    )tt2
    on tt1.ouid=tt2.ouid
)t2
on t1.ouid=t2.ouid
where t2.familyid is not null;
'''

hourfile_check = '''hadoop fs -test -e /user/hive/warehouse/wutiao.db/adl_wutiao_family_contribute_realtime/ds='{last_hour_ds}'/hour='{last_hour}' '''
dayfile_check = '''hadoop fs -test -e /user/hive/warehouse/wutiao.db/adl_wutiao_family_contribute_realtime/ds='{ds}'/hour=29day '''
touchz_check = '''hadoop fs -touchz /user/hive/warehouse/check_point/'{last_hour_ds}'-'{last_hour_str}'_adl_wutiao_family_contribute_realtime'''

def main():
    kdc = kingnetdc.kdc
    ds = kdc.workDate
    current_minute = time.strftime('%M', time.localtime(time.time()))
    last_hour = time.strftime('%Y%m%d%H', time.localtime(time.time()-3600))
    last_two_hour = time.strftime('%Y%m%d%H', time.localtime(time.time()-7200))
    last_hour_ds = time.strftime('%Y-%m-%d', time.localtime(time.time()-3600))
    last_two_hour_ds = time.strftime('%Y-%m-%d', time.localtime(time.time()-7200))
    current_hour = time.strftime('%H', time.localtime(time.time()))
    last_hour_str = time.strftime('%H', time.localtime(time.time()-3600))

    sqlhour = sql_hour.format(last_hour_ds=last_hour_ds, last_hour=last_hour,last_two_hour_ds=last_two_hour_ds,last_two_hour=last_two_hour,
                              targettab='wutiao.adl_wutiao_family_contribute_realtime',
                              sourcetab='wutiao.odl_event_qkl')
    sqlhour_start = sql_hour_start.format(ds=ds,last_hour_ds=last_hour_ds,last_hour=last_hour,
                                          targettab='wutiao.adl_wutiao_family_contribute_realtime',
                                          sourcetab='wutiao.odl_event_qkl')
    sql29day = sql_29day.format(ds=ds,
                                targettab='wutiao.adl_wutiao_family_contribute_realtime',
                                sourcetab1='wutiao.idl_wutiao_user',
                                sourcetab2='wutiao.odl_event_qkl')

    hourfile = hourfile_check.format(last_hour_ds=last_hour_ds, last_hour=last_hour)
    dayfile = dayfile_check.format(ds=ds)
    touchfile = touchz_check.format(last_hour_ds=last_hour_ds, last_hour_str=last_hour_str)

    hourflag = kdc.doCommand(hourfile, True, False)
    dayflag = kdc.doCommand(dayfile, True, False)

    kdc.debug = True

    if int(current_minute) > 1 and current_hour != '01' and hourflag == 1:
        print('Execute the family contribute rank hour data!')
        res = kdc.doHive(sqlhour, True, True)
        if res != 0:
            raise Exception('Hour family contribute rank data insert error!')
        else:
            kdc.doCommand(touchfile,True,False)
    elif int(current_minute) > 1 and current_hour == '01' and dayflag == 1:
        print('Excute the discover rank day data!')
        res0 = kdc.doHive(sql29day, True, True)
        res1 = kdc.doHive(sqlhour_start, True, True)
        if(res0 or res1) != 0:
            raise Exception('Daily family contribute data insert error!')
        else:
            kdc.doCommand(touchfile,True,False)
    else:
        print('Not the appropriate time!')


if __name__ == '__main__':
    main()
