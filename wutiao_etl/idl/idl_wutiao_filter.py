# coding: utf-8

#**********************程序说明*********************************#
#*模块: IDL
#*功能: 区块链脏数据中间表
#*作者:gant
#*时间:2018-09-06
#*备注:根据数美数据注册的reject数据，将需要过滤的设备和手机号加入过滤中间表中
#***************************************************************#

import kingnetdc

sql_day = '''
set parquet.compression=SNAPPY;
set mapreduce.map.memory.mb=4000;
set mapreduce.reduce.memory.mb=4000;
set hive.merge.mapredfiles=false;
set hive.merge.mapfiles=false;

insert overwrite table {targettab} partition (ds='{ds}', ut='did')
select null as ouid, 
nvl(t3.did,t4.did) as did,
nvl(t3.phone,t4.phone) as phone
from(
    select t1.did,t1.phone
    from(
        select lower(trim(did)) as did,phone 
        from {sourcetab1} 
        where ds='{ds}' and eventtype='super' and event='register' and nvl(did,'-1')<>'-1'
        group by lower(trim(did)),phone
    )t1
    inner join(
        select nvl(tt2.phone,tt1.phone) as phone
        from(
            select ouid as phone
            from {sourcetab}
            where ds='{ds}' and event in ('shumei_login','shumei_register','shumei_fission','shumei_withdraw') and risklevel='REJECT' and length(ouid)=11
            group by ouid
        )tt1
        full join(
            select phone from {targettab} where ds=date_sub('{ds}',1) and ut='did' group by phone
        )tt2
        on tt1.phone=tt2.phone
    )t2
    on t1.phone=t2.phone
)t3
full join(select did,phone from {targettab} where ds=date_sub('{ds}',1) and ut='did' group by did,phone)t4
on t3.did=t4.did and t3.phone=t4.phone;

insert overwrite table {targettab} partition (ds='{ds}', ut='uid')
select nvl(t3.ouid,t4.ouid) as ouid, 
null as did,
nvl(t3.phone,t4.phone) as phone
from(
    select t1.ouid,t1.phone
    from(
        select lower(trim(ouid)) as ouid,phone 
        from {sourcetab1} 
        where ds='{ds}' and eventtype='super' and event='register' and nvl(ouid,'-1')<>'-1'
        group by lower(trim(ouid)),phone
    )t1
    inner join(
        select nvl(tt2.phone,tt1.phone) as phone
        from(
            select ouid as phone
            from {sourcetab}
            where ds='{ds}' and event in ('shumei_login','shumei_register','shumei_fission','shumei_withdraw') and risklevel='REJECT' and length(ouid)=11
            group by ouid
        )tt1
        full join(
            select phone from {targettab} where ds=date_sub('{ds}',1) and ut='uid' group by phone
        )tt2
        on tt1.phone=tt2.phone
    )t2
    on t1.phone=t2.phone
)t3
full join(select ouid,phone from {targettab} where ds=date_sub('{ds}',1) and ut='uid' group by ouid,phone)t4
on t3.ouid=t4.ouid and t3.phone=t4.phone;
'''

def main():
    kdc = kingnetdc.kdc
    ds = kdc.workDate
    sql = sql_day.format(ds=ds, targettab='wutiao.idl_wutiao_filter', sourcetab='wutiao.odl_wutiao_shumei', sourcetab1='wutiao.odl_event_qkl')
    kdc.debug = True
    res = kdc.doHive(sql,True,True)
    if res != 0:
        raise Exception("utiao.idl_wutiao_filter insert error")


if __name__ == '__main__':
    main()
