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

sql_hour = '''
set mapreduce.map.memory.mb=3000;
set mapreduce.reduce.memory.mb=3000;
set hive.merge.mapredfiles=false;
set hive.merge.mapfiles=false;

insert overwrite table {targettab} partition (ds,hour)
select nvl(itemid,'-1') as itemid
,sum(if(event='comment' and replyid is null,1,0)) as comment_cnt
,sum(if(event='like' and type=1,1,0)) as like_cnt
,sum(if(event='read',1,0)) as read_cnt
,nvl(itemtype,'-1') as itemtype
,ds
,hour
from {sourcetab}
where ds='{last_hour_ds}' and hour='{last_hour}' and eventtype='super' and event in('comment','like','read')
group by nvl(itemid,'-1'),nvl(itemtype,'-1'),ds,hour;
'''

sql_day = '''
set mapreduce.map.memory.mb=3000;
set mapreduce.reduce.memory.mb=3000;
set hive.merge.mapredfiles=false;
set hive.merge.mapfiles=false;

insert overwrite table {targettab} partition (ds,hour)
select itemid
,sum(comment_cnt) as comment_cnt
,sum(like_cnt) as like_cnt
,sum(read_cnt) as read_cnt
,itemtype
,ds
,'99' as hour
from {targettab}
where ds='{ds}' and hour not in ('99','999')
group by itemid,itemtype,ds;
'''

sql_history = '''
set mapreduce.map.memory.mb=3000;
set mapreduce.reduce.memory.mb=3000;
set hive.merge.mapredfiles=false;
set hive.merge.mapfiles=false;

insert overwrite table {targettab} partition (ds,hour)
select nvl(t1.itemid,t2.itemid) as itemid
,nvl(t1.comment_cnt,0) + nvl(t2.comment_cnt,0) as comment_cnt
,nvl(t1.like_cnt,0) + nvl(t2.like_cnt,0) as like_cnt
,nvl(t1.read_cnt,0) + nvl(t2.read_cnt,0) as read_cnt
,nvl(t1.itemtype,t2.itemtype) as itemtype
,'{ds}' as ds
,'999' as hour
from (select * from {targettab} where ds='{ds}' and hour='99') t1
full join(select * from {targettab} where ds=date_sub('{ds}',1) and hour='999') t2
on t1.itemid = t2.itemid;
'''

hourfile_check = '''hadoop fs -test -e /user/hive/warehouse/wutiao.db/adl_wutiao_itemid_realtime/ds='{last_hour_ds}'/hour='{last_hour}' '''
dayfile_check = '''hadoop fs -test -e /user/hive/warehouse/wutiao.db/adl_wutiao_itemid_realtime/ds='{ds}'/hour=99 '''
touchz_check = '''hadoop fs -touchz /user/hive/warehouse/check_point/'{last_hour_ds}'-'{last_hour_str}'_adl_wutiao_itemid_realtime'''

def main():
    kdc = kingnetdc.kdc
    ds = kdc.workDate
    current_minute = time.strftime('%M', time.localtime(time.time()))
    last_hour = time.strftime('%Y%m%d%H', time.localtime(time.time()-3600))
    last_hour_ds = time.strftime('%Y-%m-%d', time.localtime(time.time()-3600))
    current_hour = time.strftime('%H', time.localtime(time.time()))
    last_hour_str = time.strftime('%H', time.localtime(time.time()-3600))

    sqlhour = sql_hour.format(last_hour_ds=last_hour_ds, last_hour=last_hour, targettab='wutiao.adl_wutiao_itemid_realtime',
                              sourcetab='wutiao.odl_event_qkl')
    sqlday = sql_day.format(ds=ds, targettab='wutiao.adl_wutiao_itemid_realtime')
    sqlhistory = sql_history.format(ds=ds, targettab='wutiao.adl_wutiao_itemid_realtime')

    hourfile = hourfile_check.format(last_hour_ds=last_hour_ds, last_hour=last_hour)
    dayfile = dayfile_check.format(ds=ds)
    touchfile = touchz_check.format(last_hour_ds=last_hour_ds, last_hour_str=last_hour_str)
    hourflag = kdc.doCommand(hourfile, True, False)
    dayflag = kdc.doCommand(dayfile, True, False)

    kdc.debug = True

    if int(current_minute) > 1 and current_hour != '00' and hourflag == 1:
        print('Execute the hour data!')
        res = kdc.doHive(sqlhour, True, True)
        if res != 0:
            raise Exception('Hour itemid data insert error!')
        else:
            kdc.doCommand(touchfile,True,False)
    elif int(current_minute) > 1 and current_hour == '00' and dayflag == 1:
        print('Excute the hour_day_history data!')
        res1 = kdc.doHive(sqlhour, True, True)
        res2 = kdc.doHive(sqlday, True, True)
        res3 = kdc.doHive(sqlhistory, True, True)
        if(res1 or res2 or res3) != 0:
            raise Exception('day&history itemid data insert error!')
        else:
            kdc.doCommand(touchfile,True,False)
    else:
        print('Not the appropriate time!')


if __name__ == '__main__':
    main()
