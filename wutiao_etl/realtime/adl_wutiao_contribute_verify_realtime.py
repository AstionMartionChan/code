# coding: utf-8

#**********************程序说明*********************************#
#*模块: ADL
#*功能: 五条贡献力审核力统计表
#*作者:gant
#*时间:2018-05-25
#*备注:五条用户贡献力审核力最近30天统计表
#***************************************************************#

import kingnetdc

sql_day = '''
set mapreduce.map.memory.mb=3000;
set mapreduce.reduce.memory.mb=3000;
set hive.merge.mapredfiles=false;
set hive.merge.mapfiles=false;

insert overwrite table {targettab} partition (ds='{ds}')
select nvl(t1.ouid,t2.ouid) as uid
,nvl(t1.contributionpoint,0)+nvl(t2.contributionpoint,0) as contributionpoint
,nvl(t1.verifypoint,0)+nvl(t2.verifypoint,0) as verifypoint
,nvl(t1.registe_time,t2.registe_time) as registe_time
,nvl(t1.inviter,t2.inviterid) as inviter
,nvl(t2.active_time,t1.last_active_time) as last_active_time
,nvl(if(t1.verify_flag=0 or t1.verify_flag is null,null,1),t2.verify_flag) as verify_flag
,nvl(t1.inviter_time,t2.inviter_time) as inviter_time
,nvl(t2.contributionpoint,0) as day_contributionpoint
from(
    select ouid
    ,sum(if(from_unixtime(last_contribution_time,'yyyy-MM-dd')=ds,last_day_contributionpoint,0)) as contributionpoint
    ,sum(if(from_unixtime(last_verify_time,'yyyy-MM-dd')=ds,last_day_verifypoint,0)) as verifypoint
    ,min(registe_time) as registe_time
    ,maxbytimenotnull(inviterid,-1*inviter_time) as inviter
    ,max(last_login_time) as last_active_time
    ,max(if(first_verify_time is not null,1,0)) as verify_flag
    ,min(inviter_time) as inviter_time
    from {sourcetab1}
    where ds>=date_sub('{ds}',29) and ds<=date_sub('{ds}',1) and ut='uid'
    group by ouid
)t1
full join(
     select ouid,
     min(if(event='register',cast(substr(\`_sst\`,1,10) as bigint),null)) as registe_time,
     sum(if(event='contributioninc',contributionpoint,0)) as contributionpoint,
     sum(if(event='verifypointinc',verifypoint,0)) as verifypoint,
     max(if(event in('active','login','enterfront','openclient','register'),cast(substr(\`_sst\`,1,10) as bigint),null)) as active_time,
     max(if(event='verifypointinc',1,0)) as verify_flag,
     maxbytimenotnull(if(event='invite' and inviterid<>0 and type='5' and step='0',inviterid,null),-1*cast(substr(\`_sst\`,1,10) as bigint)) as inviterid,
     min(if(event='invite' and inviterid<>0 and type='5' and step='0',cast(substr(\`_sst\`,1,10) as bigint),null)) as inviter_time
     from {sourcetab2}
     where ds='{ds}' and eventtype in ('super','high') and event in('contributioninc','verifypointinc','register','active','login','invite','openclient','enterfront')
     group by ouid
)t2
on t1.ouid=t2.ouid;
'''

def main():
    kdc = kingnetdc.kdc
    ds = kdc.workDate
    sql = sql_day.format(ds=ds, targettab='wutiao.adl_wutiao_contribute_verify_realtime',
                         sourcetab1='wutiao.idl_wutiao_user', sourcetab2='wutiao.odl_event_qkl')
    kdc.debug = True
    print(sql)
    res = kdc.doHive(sql)
    if res != 0:
        raise Exception('wutiao.adl_wutiao_contribute_verify_realtime hive table insert error!')


if __name__ == '__main__':
    main()
