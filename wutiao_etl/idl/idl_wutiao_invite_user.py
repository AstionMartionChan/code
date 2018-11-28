# coding: utf-8

# **********************程序说明*********************************#
# *模块: idl_wutiao_invite_user -> IDL
# *功能: 区块链活跃用户中间表
# *作者: sunyu
# *时间: 2018-08-29
# *备注: 数据从28号开始进行计算
# ***************************************************************#

import kingnetdc

SQL = '''
set parquet.compression=SNAPPY;
set mapreduce.map.memory.mb=3000;
set mapreduce.reduce.memory.mb=3000;
set hive.merge.mapredfiles=false;
set hive.merge.mapfiles=false;

insert overwrite table %(targettab)s partition (ds)

select nvl(b.invite_uid,a.invite_uid) as invite_uid
    ,nvl(b.invitee_uid,a.invitee_uid) as invitee_uid
    ,nvl(b.invite_type,a.invite_type) as invite_type
    ,nvl(a.appver,b.appver) as appver
    ,nvl(a.os,b.os) as os
    ,nvl(a.channel,b.channel) as channel
    ,nvl(b.invite_registe_time,a.invite_registe_time) as invite_registe_time
    ,nvl(b.invitee_registe_time,a.invitee_registe_time) as invitee_registe_time
    ,nvl(b.invite_first_login_time,a.invite_first_login_time) as invite_first_login_time
    ,nvl(a.invite_last_login_time,b.invite_last_login_time) as invite_last_login_time
    ,nvl(b.invitee_first_login_time,a.invitee_first_login_time) as invitee_first_login_time
    ,nvl(a.invitee_last_login_time,b.invitee_last_login_time) as invitee_last_login_time
    ,nvl(b.invite_phone,a.invite_phone) as invite_phone
    ,nvl(b.invitee_phone,a.invitee_phone) as invitee_phone
    ,nvl(b.invite_first_time,a.invite_first_time) as invite_first_time
    ,nvl(a.invite_last_time,b.invite_last_time) as invite_last_time
    ,nvl(b.invite_red_money,0) + nvl(a.invite_red_money,0) as invite_red_money
    ,nvl(b.invite_family_id,a.invite_family_id) as invite_family_id
    ,nvl(b.invitee_family_id,a.invitee_family_id) as invitee_family_id
    ,nvl(b.invite_registe_inviter,a.invite_registe_inviter) as invite_registe_inviter
    ,nvl(b.invitee_registe_inviter,a.invitee_registe_inviter) as invitee_registe_inviter
    , '%(run_date)s' as ds
from (
    select a.inviterid as invite_uid, c.ouid as invitee_uid, a.type as invite_type, b.last_appver as appver, b.last_os as os, b.last_channel as channel
        , b.registe_time as invite_registe_time, c.registe_time as invitee_registe_time
        , b.first_login_time as invite_first_login_time, b.last_login_time as invite_last_login_time
        , c.first_login_time as invitee_first_login_time, c.last_login_time as invitee_last_login_time
        , b.phone as invite_phone, a.phone as invitee_phone
        , min_ts as invite_first_time, max_ts as invite_last_time
        , nvl(d.money,0) as invite_red_money
        , b.familyid as invite_family_id
        , c.familyid as invitee_family_id
        , b.registe_inviter as invite_registe_inviter
        , c.registe_inviter as invitee_registe_inviter
    from (
        select phone, inviterid, type, min(ts) as min_ts, max(ts) as max_ts
        from %(bdl_tb)s
        where ds = '%(run_date)s'
        and event = 'invite'
        and eventtype = 'super'
        and type in (1,2,3,4)
        group by phone, inviterid, type
    ) a
    left join
    (
        select ouid as riouid, last_appver, last_os, last_channel, phone, registe_time, first_login_time, last_login_time
            , familyid, registe_inviter
        from %(idl_tb)s
        where ut = 'uid'
        and ds = '%(run_date)s'
    ) b
    on a.inviterid = b.riouid
    left join
    (
        select ouid, last_appver, last_os, last_channel, phone, registe_time, first_login_time, last_login_time 
            , familyid, registe_inviter
        from %(idl_tb)s
        where ut = 'uid'
        and ds = '%(run_date)s'
    ) c
    on a.phone = c.phone
    left join
    (
        select ouid, sum(nvl(rmb,0)) as money
        from %(bdl_tb)s
        where ds = '%(run_date)s'
        and event = 'hongbao'
        and type = 2
        and eventtype = 'low'
        group by ouid
    ) d
    on a.inviterid = d.ouid
) a
full join 
(
    select invite_uid ,invitee_uid ,invite_type ,appver ,os ,channel ,invite_registe_time ,invitee_registe_time
        ,invite_first_login_time ,invite_last_login_time ,invitee_first_login_time ,invitee_last_login_time 
        ,invite_phone ,invitee_phone ,invite_first_time ,invite_last_time ,invite_red_money 
        ,invite_family_id,invitee_family_id,invite_registe_inviter,invitee_registe_inviter 
    from %(targettab)s
    where ds = '%(day1)s'
) b
on a.invite_uid = b.invite_uid and a.invitee_uid = b.invitee_uid and a.invite_type = b.invite_type
'''


def main():
    kdc = kingnetdc.kdc
    args = {
        'run_date': kdc.workDate,
        'idl_tb': 'wutiao.idl_wutiao_user',
        'bdl_tb': 'wutiao.bdl_wutiao_event',
        'targettab': 'wutiao.idl_wutiao_invite_user',
        'day1': kdc.dateSub(1),
        'day7': kdc.dateSub(7)
    }
    kdc.debug = True

    sql = SQL % args
    print(sql)
    kdc.doHive(sql)
    # 清理数据
    del_sql = ''' alter table %(targettab)s drop partition(ds='%(day7)s') ; ''' % args
    print(del_sql)
    kdc.doHive(del_sql)


if __name__ == '__main__':
    main()
