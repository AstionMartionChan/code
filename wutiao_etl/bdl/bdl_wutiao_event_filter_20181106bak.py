# coding: utf-8

#**********************程序说明*********************************#
#*模块: BDL
#*功能: 区块链bdl表
#*作者:gant
#*时间:2018-05-10
#*备注:区块链bdl表
#***************************************************************#

import kingnetdc

sql_day = '''
set parquet.compression=SNAPPY;
set mapreduce.map.memory.mb=24480;
set mapreduce.reduce.memory.mb=24480;
set hive.merge.mapredfiles=false;
set hive.merge.mapfiles=false;

insert overwrite table {targettab} partition (eventtype,ds)
select *
from {targettab} 
where ds = '{ds}' and eventtype='medium' and event in ('withdraw','getwb')
union all
select t1.*
from(select * from {targettab} where ds = '{ds}' and eventtype='super')t1
left join(select did from {filtertab} where ds = '{ds}' and ut='did' group by did)t2
on t1.did=t2.did
where t2.did is null
union all
select t1.*
from(select * from {targettab} where ds = '{ds}' and eventtype='high')t1
left join(select did from {filtertab} where ds = '{ds}' and ut='did' group by did)t2
on t1.did=t2.did
where t2.did is null
union all
select t1.*
from(select * from {targettab} where ds = '{ds}' and eventtype='medium' and event not in ('withdraw','getwb'))t1
left join(select did from {filtertab} where ds = '{ds}' and ut='did' group by did)t2
on t1.did=t2.did
where t2.did is null
union all
select t1.*
from(select * from {targettab} where ds = '{ds}' and eventtype='low')t1
left join(select did from {filtertab} where ds = '{ds}' and ut='did' group by did)t2
on t1.did=t2.did
where t2.did is null;

insert overwrite table {targettab} partition (eventtype,ds)
select *
from {targettab} 
where ds = '{ds}' and eventtype='medium' and event in ('withdraw','getwb')
union all
select t1.*
from(select * from {targettab} where ds = '{ds}' and eventtype='super')t1
left join(select ouid from {filtertab} where ds = '{ds}' and ut='uid' group by ouid)t2
on t1.ouid=t2.ouid
where t2.ouid is null
union all
select t1.*
from(select * from {targettab} where ds = '{ds}' and eventtype='high')t1
left join(select ouid from {filtertab} where ds = '{ds}' and ut='uid' group by ouid)t2
on t1.ouid=t2.ouid
where t2.ouid is null
union all
select t1.*
from(select * from {targettab} where ds = '{ds}' and eventtype='medium' and event not in ('withdraw','getwb'))t1
left join(select ouid from {filtertab} where ds = '{ds}' and ut='uid' group by ouid)t2
on t1.ouid=t2.ouid
where t2.ouid is null
union all
select t1.*
from(select * from {targettab} where ds = '{ds}' and eventtype='low')t1
left join(select ouid from {filtertab} where ds = '{ds}' and ut='uid' group by ouid)t2
on t1.ouid=t2.ouid
where t2.ouid is null;
'''

def main():
    kdc = kingnetdc.kdc
    ds = kdc.workDate
    sql = sql_day.format(ds=ds, targettab='wutiao.bdl_wutiao_event',filtertab='wutiao.idl_wutiao_filter')
    kdc.debug = True
    print(sql)
    kdc.doHive(sql)

if __name__ == '__main__':
    main()
