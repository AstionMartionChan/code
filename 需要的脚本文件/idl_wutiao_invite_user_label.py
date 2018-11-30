# coding: utf-8

# **********************程序说明*********************************#
# *模块: idl_wutiao_invite_user_label -> IDL
# *功能: 区块链邀请事件用户标签中间表
# *作者: chenfy
# *时间: 2018-09-07
# *备注: 数据从28号开始进行计算
# ***************************************************************#

import kingnetdc

SQL = '''
set parquet.compression=SNAPPY;
set mapreduce.map.memory.mb=3000;
set mapreduce.reduce.memory.mb=3000;
set hive.merge.mapredfiles=false;
set hive.merge.mapfiles=false;
set hive.exec.dynamic.partition.mode=nonstrict;

insert overwrite table %(targettab)s partition (ds)

select 
	 tb1.appver
        ,tb1.os
        ,tb1.channel
	,tb1.invite_uid
        ,tb1.invitee_uid 
	,tb1.invite_family_id
	,tb1.invitee_family_id
	,from_unixtime(tb1.invite_last_time,'yyyy-MM-dd') as invite_last_ds
	,from_unixtime(tb1.invite_registe_time,'yyyy-MM-dd') as invite_registe_ds
	,from_unixtime(tb1.invitee_registe_time,'yyyy-MM-dd') as invitee_registe_ds	
	,tb1.invite_phone
	,tb1.invitee_phone
	,tb1.invite_type
	,if(tb1.has_family>0,'family', 'friend') as dim_type
	,if(tb1.has_family_c>0 or tb1.has_friend_c>0, 'code','h5') as dim_fun
	,if(tb1.has_family_c>0 or tb1.has_friend_c>0, 1,0) as is_code
	,tb1.status 
	,tb1.has_family_h
	,tb1.has_family_c
	,tb1.has_friend_h
	,tb1.has_friend_c
	,tb1.type_list
	,'%(run_date)s' as ds
from 
( 
	select  
  		a.inviterid as invite_uid
		, c.ouid as invitee_uid
		, a.type as invite_type
		, nvl(b.last_appver,'-1') as appver
		, nvl(b.last_os,'-1') as os
		, nvl(b.last_channel,'-1') as channel
		, b.registe_time as invite_registe_time
		, c.registe_time as invitee_registe_time
		, b.phone as invite_phone
		, a.phone as invitee_phone
		, min_ts as invite_first_time
		, max_ts as invite_last_time
		, b.familyid as invite_family_id
		, c.familyid as invitee_family_id
		, b.registe_inviter as invite_registe_inviter
		, c.registe_inviter as invitee_registe_inviter
		, a.inviterid as ainviterid
		, e.inviterid as einviterid
		, e.status as status
		, a.type as ainvite_type
		, if((e.status='1' and e.status = cast(a.type as string)) or (a.type in (1,3)) or (d.type_list='3,4') ,1,0)  as has_family
		, if((e.status='2' and e.status = cast(a.type as string)) or (a.type in (2,4)) and (d.type_list!='3,4'),1,0)  as has_friend
		, if(a.type=1,1,0) as has_family_h
		, if(a.type=3,1,0) as has_family_c
		, if(a.type=2,1,0) as has_friend_h
		, if(a.type=4,1,0) as has_friend_c
		, d.type_list as type_list
    from (
        select 
			phone
			, inviterid
			, type
			, min(ts) as min_ts
			, max(ts) as max_ts
        from %(bdl_tb)s
        where ds = '%(run_date)s'
        and event = 'invite'
        and eventtype = 'super'
        and type in (1,2,3,4)
        group by phone, inviterid, type
    ) a
    left join
    (
        select 
			ouid as riouid
			, last_appver
			, last_os
			, last_channel
			, phone
			, registe_time
			, first_login_time
			, last_login_time
			, familyid, registe_inviter
        from %(idl_tb)s
        where ut = 'uid'
        and ds = '%(run_date)s'
    ) b
    on a.inviterid = b.riouid
    left join	
    (
        select 
			ouid
			, last_appver
			, last_os
			, last_channel
			, phone
			, registe_time
			, first_login_time
			, last_login_time 
			, familyid
			, registe_inviter
        from %(idl_tb)s
        where ut = 'uid'
        and ds = '%(run_date)s'
    ) c
    on a.phone = c.phone
    left join
    (
	select 
	  phone
	  ,inviterid
	  ,concat_ws(',', collect_set(cast(tb1.type as string))) as type_list
	from 
	(
			select 
				phone
				,inviterid
				,type
			from %(bdl_tb)s
			where ds = '%(run_date)s'
			and event = 'invite'
			and eventtype = 'super'
			and type in (1,2,3,4)
			order by type asc
	) tb1
	  group by tb1.phone, tb1.inviterid
    ) d
    on a.phone = d.phone and a.inviterid = d.inviterid
    left join 
    (
		select 
			phone
			,status 
			,inviterid
	  	from %(bdl_tb)s 
	  	where ds = '%(run_date)s'
	  	and event = 'register'
	  	and eventtype = 'super'
	  	and status is not null
    ) e 
	on a.phone = e.phone and a.inviterid = e.inviterid
	) tb1;
'''


def main():
    kdc = kingnetdc.kdc
    args = {
        'run_date': kdc.workDate,
        'idl_tb': 'wutiao.idl_wutiao_user',
        'bdl_tb': 'wutiao.bdl_wutiao_event',
        'targettab': 'wutiao.idl_wutiao_invite_user_label',
        'day1': kdc.dateSub(1),
        'day7': kdc.dateSub(7)
    }
    kdc.debug = True

    sql = SQL % args
    print(sql)
    kdc.doHive(sql)


if __name__ == '__main__':
    main()
