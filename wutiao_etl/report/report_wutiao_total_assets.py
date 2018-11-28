# coding: utf-8

#**********************程序说明*********************************#
#*模块：REPORT
#*功能：区块链总资产分析
#*作者：gant
#*时间：2018-07-24
#*备注：区块链总资产分析
#***************************************************************#
import kingnetdc
import sys
import os
sys.path.append('/'.join(os.path.abspath(__file__).split('/')[:-2]))
from project_constant import DB_PARAMS_TEST

DB_PARAMS = DB_PARAMS_TEST

SQL_DAY = '''
set mapred.max.split.size=10000000;
set mapreduce.map.memory.mb=4096;
set mapreduce.reduce.memory.mb=4096;

INSERT overwrite TABLE {targettab} partition (ds='{ds}')
select tz1.fds,
tz1.user_flag,
tz1.total_withdraw_success_rmb,
tz1.total_withdraw_success_usercnt,
tz1.total_withdraw_success_wb,
tz1.day_withdraw_success_rmb,
tz1.day_withdraw_success_wb,
tz1.day_withdraw_success_usercnt,
tz1.total_getwb_wb,
tz1.os,
tz1.appver,
tz1.channel,
tz1.moneytype,
tz1.active_usercnt,
tz2.apply_usercnt,
tz2.apply_coin,
tz2.apply_rmb,
null as actual_rate
from(
    select '{ds}' as fds, 
    nvl(user_flag,'alluserflag') as user_flag,
    total_withdraw_success_rmb,
    total_withdraw_success_usercnt,
    total_withdraw_success_wb,
    day_withdraw_success_rmb,
    day_withdraw_success_wb,
    day_withdraw_success_usercnt,
    total_getwb_wb,
    nvl(os,'allos') as os,
    nvl(appver,'allappver') as appver,
    nvl(channel,'allchannel') as channel,
    'wb' as moneytype,
    active_usercnt
    from(
        select t1.user_flag,
        t1.os,
        t1.appver,
        t1.channel,
        sum(t1.his_withdraw_rmb) as total_withdraw_success_rmb,
        sum(t1.his_withdraw_coin) as total_withdraw_success_wb,
        sum(if(t1.his_withdraw_rmb>0,1,0)) as total_withdraw_success_usercnt,
        sum(if(t1.last_day_withdraw_rmb>0,last_day_withdraw_rmb,0)) as day_withdraw_success_rmb,
        sum(if(t1.last_day_withdraw_wb>0,last_day_withdraw_wb,0)) as day_withdraw_success_wb,
        sum(if(t1.last_day_withdraw_rmb>0,1,0)) as day_withdraw_success_usercnt,
        sum(his_getwb_wb) as total_getwb_wb,
        sum(if(active_flag='1',1,0)) as active_usercnt
        from(
            select ouid,
            nvl(last_appver,'-1') as appver, 
            nvl(last_channel,'-1') as channel,
            nvl(last_os,'-1') as os,
            if(from_unixtime(first_login_time,'yyyy-MM-dd')=ds,1,0) as user_flag,
            if(from_unixtime(last_withdraw_time,'yyyy-MM-dd')=ds,last_day_withdraw_rmb,0) as last_day_withdraw_rmb,
            if(from_unixtime(last_withdraw_time,'yyyy-MM-dd')=ds,last_day_withdraw_coin,0) as last_day_withdraw_wb,
            if(from_unixtime(last_login_time,'yyyy-MM-dd')=ds,1,0) as active_flag,
            his_withdraw_rmb,
            his_withdraw_coin,
            his_getwb_wb
            from {sourcetab}
            where ds='{ds}' and ut='uid'
        )t1   
        group by user_flag,os,appver,channel
        grouping sets((user_flag,os,appver,channel),(os,appver,channel),(user_flag,appver,channel),(user_flag,os,channel),(user_flag,os,appver),
        (appver,channel),(user_flag,channel),(user_flag,os),(os,channel),(os,appver),(user_flag,appver),(channel),(user_flag),(os),(appver),())
    )t2
    union all
    select '{ds}' as fds, 
    nvl(user_flag,'alluserflag') as user_flag,
    total_withdraw_success_rmb,
    total_withdraw_success_usercnt,
    total_withdraw_success_wb,
    day_withdraw_success_rmb,
    day_withdraw_success_wb,
    day_withdraw_success_usercnt,
    total_getwb_wb,
    nvl(os,'allos') as os,
    nvl(appver,'allappver') as appver,
    nvl(channel,'allchannel') as channel,
    'money' as moneytype,
    active_usercnt
    from(
        select t1.user_flag,
        t1.os,
        t1.appver,
        t1.channel,
        sum(t1.his_withdraw_rmb) as total_withdraw_success_rmb,
        0 as total_withdraw_success_wb,
        sum(if(t1.his_withdraw_rmb>0,1,0)) as total_withdraw_success_usercnt,
        sum(if(t1.last_day_withdraw_rmb>0,last_day_withdraw_rmb,0)) as day_withdraw_success_rmb,
        0 as day_withdraw_success_wb,
        sum(if(t1.last_day_withdraw_rmb>0,1,0)) as day_withdraw_success_usercnt,
        0 as total_getwb_wb,
        sum(if(active_flag='1',1,0)) as active_usercnt
        from(
            select ouid,
            nvl(last_appver,'-1') as appver, 
            nvl(last_channel,'-1') as channel,
            nvl(last_os,'-1') as os,
            if(from_unixtime(first_login_time,'yyyy-MM-dd')=ds,1,0) as user_flag,
            if(from_unixtime(last_withdraw_hongbao_time,'yyyy-MM-dd')=ds,last_day_withdraw_hongbao_rmb,0) as last_day_withdraw_rmb,
            0 as last_day_withdraw_wb,
            his_withdraw_hongbao_rmb as his_withdraw_rmb,
            if(from_unixtime(last_login_time,'yyyy-MM-dd')=ds,1,0) as active_flag,
            0 as his_withdraw_coin,
            0 as his_getwb_wb
            from {sourcetab}
            where ds='{ds}' and ut='uid'
        )t1   
        group by user_flag,os,appver,channel
        grouping sets((user_flag,os,appver,channel),(os,appver,channel),(user_flag,appver,channel),(user_flag,os,channel),(user_flag,os,appver),
        (appver,channel),(user_flag,channel),(user_flag,os),(os,channel),(os,appver),(user_flag,appver),(channel),(user_flag),(os),(appver),())
    )t2
    union all
    select '{ds}' as fds,
    nvl(user_flag,'alluserflag') as user_flag,
    total_withdraw_success_rmb,
    total_withdraw_success_usercnt,
    total_withdraw_success_wb,
    day_withdraw_success_rmb,
    day_withdraw_success_wb,
    day_withdraw_success_usercnt,
    total_getwb_wb,
    nvl(os,'allos') as os,
    nvl(appver,'allappver') as appver,
    nvl(channel,'allchannel') as channel,
    'duobao' as moneytype,
    active_usercnt
    from(
        select t1.user_flag,
        t1.os,
        t1.appver,
        t1.channel,
        sum(t1.his_withdraw_rmb) as total_withdraw_success_rmb,
        sum(t1.his_withdraw_coin) as total_withdraw_success_wb,
        sum(if(t1.his_withdraw_rmb>0,1,0)) as total_withdraw_success_usercnt,
        sum(if(t1.last_day_withdraw_rmb>0,last_day_withdraw_rmb,0)) as day_withdraw_success_rmb,
        sum(if(t1.last_day_withdraw_wb>0,last_day_withdraw_wb,0)) as day_withdraw_success_wb,
        sum(if(t1.last_day_withdraw_rmb>0,1,0)) as day_withdraw_success_usercnt,
        sum(his_getwb_wb) as total_getwb_wb,
        sum(if(active_flag='1',1,0)) as active_usercnt
        from(
            select ouid,
            nvl(last_appver,'-1') as appver,
            nvl(last_channel,'-1') as channel,
            nvl(last_os,'-1') as os,
            if(from_unixtime(first_login_time,'yyyy-MM-dd')=ds,1,0) as user_flag,
            if(from_unixtime(last_withdraw_duobao_time,'yyyy-MM-dd')=ds,last_day_withdraw_duobao_rmb,0) as last_day_withdraw_rmb,
            if(from_unixtime(last_withdraw_duobao_time,'yyyy-MM-dd')=ds,last_day_withdraw_duobao_coin,0) as last_day_withdraw_wb,
            if(from_unixtime(last_login_time,'yyyy-MM-dd')=ds,1,0) as active_flag,
            his_withdraw_duobao_rmb as his_withdraw_rmb,
            his_withdraw_duobao_coin as his_withdraw_coin,
            his_getwb_wb
            from {sourcetab}
            where ds='{ds}' and ut='uid'
        )t1
        group by user_flag,os,appver,channel
        grouping sets((user_flag,os,appver,channel),(os,appver,channel),(user_flag,appver,channel),(user_flag,os,channel),(user_flag,os,appver),
        (appver,channel),(user_flag,channel),(user_flag,os),(os,channel),(os,appver),(user_flag,appver),(channel),(user_flag),(os),(appver),())
    )t3

)tz1
left join(
    select nvl(user_flag,'alluserflag') as user_flag,
    nvl(os,'allos') as os,
    nvl(appver,'allappver') as appver,
    nvl(channel,'allchannel') as channel,
    moneytype,
    apply_usercnt,
    apply_coin,
    apply_rmb
    from(
        select appver,
        channel,
        os,
        user_flag,
        moneytype,
        count(distinct t1.ouid) as apply_usercnt,
        sum(apply_coin) as apply_coin,
        sum(apply_rmb) as apply_rmb
        from(
        select ouid,
        if(type=1,'wb','money') as moneytype,
        sum(nvl(coin,0)) as apply_coin,
        sum(rmb) as apply_rmb
        from wutiao.bdl_wutiao_event
        where ds='{ds}' and eventtype='medium' and event='withdraw' and status='1'
        group by ouid,if(type=1,'wb','money'))t1
        left join(
          select ouid,
          nvl(last_appver,'-1') as appver, 
          nvl(last_channel,'-1') as channel,
          nvl(last_os,'-1') as os,
          if(from_unixtime(first_login_time,'yyyy-MM-dd')=ds,1,0) as user_flag
          from wutiao.idl_wutiao_user
          where ds='{ds}' and ut='uid'
        )t2
        on t1.ouid=t2.ouid
        group by moneytype,user_flag,os,appver,channel
        grouping sets((moneytype,user_flag,os,appver,channel),(moneytype,os,appver,channel),(moneytype,user_flag,appver,channel),(moneytype,user_flag,os,channel),(moneytype,user_flag,os,appver),
        (moneytype,appver,channel),(moneytype,user_flag,channel),(moneytype,user_flag,os),(moneytype,os,channel),(moneytype,os,appver),(moneytype,user_flag,appver),(moneytype,channel),(moneytype,user_flag),(moneytype,os),(moneytype,appver),(moneytype))
    )t3
)tz2
on tz1.appver=tz2.appver and tz1.channel=tz2.channel and tz1.os=tz2.os and tz1.user_flag=tz2.user_flag and tz1.moneytype=tz2.moneytype;

INSERT overwrite TABLE {targettab} partition (ds='{ds}')
select tz1.fds,
tz1.user_flag,
tz1.total_withdraw_success_rmb,
tz1.total_withdraw_success_usercnt,
tz1.total_withdraw_success_wb,
tz1.day_withdraw_success_rmb,
tz1.day_withdraw_success_wb,
tz1.day_withdraw_success_usercnt,
tz1.total_getwb_wb,
tz1.os,
tz1.appver,
tz1.channel,
tz1.moneytype,
tz1.active_usercnt,
tz1.apply_usercnt,
tz1.apply_coin,
tz1.apply_rmb,
tz2.actual_rate
from (select * from {targettab} where ds='{ds}')tz1
left join(select ds,actual_rate from wutiao.odl_wutiao_wutiao_rate_sync where ds='{ds}')tz2
on tz1.fds=tz2.ds;
'''

delete=r'''echo "use wutiao; delete from report_wutiao_total_assets where fds ='{ds}'; " |mysql -h {host} -u {user} -p{password} '''
sync=r'''/usr/bin/sqoop-export  --export-dir /user/hive/warehouse/wutiao.db/report_wutiao_total_assets/ds='{ds}' --connect jdbc:mysql://{host}:{port}/wutiao --username "{user}" --password '{password}' --table "report_wutiao_total_assets" --input-fields-terminated-by "|" --input-null-string "\\\N" --input-null-non-string "\\\N" '''

def main():
    kdc = kingnetdc.kdc
    args = {
        'ds': kdc.workDate,
        'sourcetab': 'wutiao.idl_wutiao_user',
        'targettab': 'wutiao.report_wutiao_total_assets',
        'mysqltab': 'wutiao.report_wutiao_total_assets',
        'host': DB_PARAMS['host'],
        'user': DB_PARAMS['user'],
        'password': DB_PARAMS['password'],
        'port': DB_PARAMS['port']
    }
    sql = SQL_DAY.format(ds=args['ds'], targettab=args['targettab'], sourcetab=args['sourcetab'])

    kdc.debug = True
    res = kdc.doHive(sql,True,True)

    if res !=0 :
        raise Exception('report_wutiao_total_assets hive table insert execute failure!!')

    res1 = os.system(delete.format(ds=args['ds'],host=args['host'],user=args['user'],password=args['password']))
    res2 = os.system(sync.format(ds=args['ds'],host=args['host'],user=args['user'],password=args['password'],port=args['port']))

    if (res1 or res2) !=0 :
        print(res1,res2)
        raise Exception('report_wutiao_total_assets mysql table delete&insert execute failure!!')
    else :
        print(res1,res2)
        print('complete')

if __name__ == '__main__':
    main()
