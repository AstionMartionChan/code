# coding: utf-8

#**********************程序说明*********************************#
#*模块: ADL
#*功能: 区块链分币领币表
#*作者:gant
#*时间:2018-05-26
#*备注:区块链分币领币按小时，按天，按历史统计，以及自媒体类型用户数据抽取
#***************************************************************#

import kingnet
import time
import os

sql_hour = '''
insert overwrite table {targettab} partition (ds,hour)
select uid,
money_gain,
money_distribute,
discover_money_gain,
nvl(t3.status,0) as media_status,
creative_money_gain,
ds,
hour
from(
select uid
,sum(if(event='gain',value,0)) as money_gain
,sum(if(event='fb',value,0)) as money_distribute
,sum(if(event='gain' and type='15',value,0)) as discover_money_gain
,sum(if(event='gain' and type='10',value,0)) as creative_money_gain
,ds
,hour
from(select nvl(uid,'-1') as uid,
     'gain' as event,
     value,
     actiontype as type,
     ds,
     hour
     from {sourcetab1}
     where ds='{last_hour_ds}' and hour='{last_hour}'
     union all
     select nvl(uid,'-1') as uid,
     'fb' as event,
     value,
     actiontype as type,
     ds,
     hour
     from {sourcetab2}
     where ds='{last_hour_ds}' and hour='{last_hour}'
)t1 
group by uid,ds,hour)t2
left join(
    select ouid,status 
    from {sourcetab3} 
    where ds='{last_hour_ds}' and ut='media'
)t3
on t2.uid=t3.ouid;
'''

sql_day = '''
set mapreduce.map.memory.mb=3000;
set mapreduce.reduce.memory.mb=3000;
set hive.merge.mapredfiles=false;
set hive.merge.mapfiles=false;

insert overwrite table {targettab} partition (ds,hour)
select uid,
money_gain,
money_distribute,
discover_money_gain,
nvl(t3.status,0) as media_status,
creative_money_gain,
ds,
hour
from(
select uid
,sum(money_gain) as money_gain
,sum(money_distribute) as money_distribute
,sum(discover_money_gain) as discover_money_gain
,sum(creative_money_gain) as creative_money_gain
,ds
,'99' as hour
from {targettab}
where ds='{ds}' and hour not in ('99','999')
group by uid,ds)t1
left join(
    select ouid,status 
    from {sourcetab3} 
    where ds='{ds}' and ut='media'
)t3
on t1.uid=t3.ouid;
'''

sql_history = '''
insert overwrite table {targettab} partition (ds,hour)
select uid,
money_gain,
money_distribute,
discover_money_gain,
t4.status,
creative_money_gain,
ds,
hour
from(
    select nvl(t1.uid,t2.uid) as uid
    ,nvl(t1.money_gain,0) + nvl(t2.money_gain,0) as money_gain
    ,nvl(t1.money_distribute,0) + nvl(t2.money_distribute,0) as money_distribute
    ,nvl(t1.discover_money_gain,0) + nvl(t2.discover_money_gain,0) as discover_money_gain
    ,nvl(t1.creative_money_gain,0) + nvl(t2.creative_money_gain,0) as creative_money_gain
    ,'{ds}' as ds
    ,'999' as hour
    from (select * from {targettab} where ds='{ds}' and hour='99') t1
    full join(select * from {targettab} where ds=date_sub('{ds}',1) and hour='999') t2
    on t1.uid=t2.uid
)t3
left join(
    select ouid,status 
    from {sourcetab3} 
    where ds='{ds}' and ut='media'
)t4
on t3.uid=t4.ouid;
'''

sql_media = '''
insert overwrite table {targettab} partition (ds='{ds}', ut='media')
select
nvl(t2.ouid,t1.ouid) as ouid,
nvl(t2.first_time,t1.first_time) as first_time,
nvl(if(t1.status=2,0,t1.status),t2.status) as status,
nvl(if(t1.status=2,null,t1.type),if(t1.status=2,null,t2.type)) as type
from
(select 
ouid
,min(if(event = 'media' and status=1,substr(\`_sst\`,1,10),null)) as first_time       
,maxbytimenotnull(if(event = 'media' and status=1,1,status),cast(substr(\`_sst\`,1,10) as bigint)) as status
,maxbytimenotnull(if(event = 'media' and status=1,type,null),cast(substr(\`_sst\`,1,10) as bigint)) as type
from {sourcetab} 
where ds = '{ds}' and ouid is not null
and event = 'media' group by ouid)t1
full outer join
(select * from {targettab} where ds = date_sub('{ds}',1) and ut = 'media'
)t2
on t1.ouid = t2.ouid;
'''

hourfile_check = '''hadoop fs -test -e /user/hive/warehouse/wutiao_db/adl_wutiao_money_realtime/ds='{last_hour_ds}'/hour='{last_hour}' '''
dayfile_check = '''hadoop fs -test -e /user/hive/warehouse/wutiao_db/adl_wutiao_money_realtime/ds='{ds}'/hour=99 '''
moneygain_yesterday = '''hive -e "select uid, money_gain from {targettab} where ds='{ds}' and hour='99' and money_gain>0"|tr "\t" "|"|grep -v 'WARN\|hadoop' > /opt/etl/wutiao_file/getmoney_{ds}.txt'''
moneygain_info = '''echo `md5sum /opt/etl/wutiao_file/getmoney_%(ds)s.txt|awk -F " " '{print $1}'`"|"`wc -l /opt/etl/wutiao_file/getmoney_%(ds)s.txt|awk -F " " '{print $1}'` > /opt/etl/wutiao_file/getmoney_%(ds)s_info.txt'''

def main():
    kdc = kingnet.kdc()
    ds = kdc.workDate
    current_minute = time.strftime('%M', time.localtime(time.time()))
    last_hour = time.strftime('%Y%m%d%H', time.localtime(time.time()-3600))
    last_hour_ds = time.strftime('%Y-%m-%d', time.localtime(time.time()-3600))
    current_hour = time.strftime('%H', time.localtime(time.time()))

    sqlhour = sql_hour.format(last_hour_ds=last_hour_ds, last_hour=last_hour,ds=ds,
                              targettab='wutiao.adl_wutiao_money_realtime',
                              sourcetab1='wutiao.odl_wutiao_money_gain',
                              sourcetab2='wutiao.odl_wutiao_wb',
                              sourcetab3='wutiao.idl_wutiao_user_status')
    sqlday = sql_day.format(ds=ds,
                            targettab='wutiao.adl_wutiao_money_realtime',
                            sourcetab3='wutiao.idl_wutiao_user_status')
    sqlhistory = sql_history.format(ds=ds,
                                    targettab='wutiao.adl_wutiao_money_realtime',
                                    sourcetab3='wutiao.idl_wutiao_user_status')
    sqlmedia_hour = sql_media.format(ds=ds,targettab='wutiao.idl_wutiao_user_status', sourcetab='wutiao.odl_event_qkl')
    sqlmedia_day = sql_media.format(ds=ds,targettab='wutiao.idl_wutiao_user_status', sourcetab='wutiao.odl_event_qkl')

    moneygain_yesterday_cmd = moneygain_yesterday.format(ds=ds,
                                                         targettab='wutiao.adl_wutiao_money_realtime')
    moneygain_info_cmd = moneygain_info % {'ds':ds}

    hourfile = hourfile_check.format(last_hour_ds=last_hour_ds, last_hour=last_hour)
    dayfile = dayfile_check.format(ds=ds)
    hourflag = kdc.doCommand(hourfile, True)
    dayflag = kdc.doCommand(dayfile, True)

    kdc.debug = True

    if int(current_minute) > 1 and current_hour != '00' and hourflag == 1:
        print('Execute the hour data!')
        res1 = kdc.doHive(sqlmedia_hour,True,True)
        res2 = kdc.doHive(sqlhour,True,True)
        if (res1 or res2) != 0:
            raise Exception('Hour money data insert error!')
    elif int(current_minute) > 1 and current_hour == '00' and dayflag == 1:
        print('Excute the hour_day_history data!')
        res0 = kdc.doHive(sqlmedia_day, True, True)
        res1 = kdc.doHive(sqlhour, True, True)
        res2 = kdc.doHive(sqlday, True, True)
        res3 = kdc.doHive(sqlhistory, True, True)
        res4 = os.system(moneygain_yesterday_cmd)
        res5 = os.system(moneygain_info_cmd)
        if(res0 or res1 or res2 or res3 or res5) != 0:
            raise Exception('day&history money data insert error!')
    else:
        print('Not the appropriate time!')


if __name__ == '__main__':
    main()
