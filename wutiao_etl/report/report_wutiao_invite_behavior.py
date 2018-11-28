# !/usr/bin/env python
# -*-coding: UTF-8 -*-

# **********************程序说明*********************************#
# *模块: report_wutiao_invite_behavior -> ADL
# *功能: 区块链-五条项目-邀请行为
# *作者: chenfy
# *时间: 2018-09-09
# *备注:
# ***************************************************************#
import kingnetdc
import sys
import os
sys.path.append('/'.join(os.path.abspath(__file__).split('/')[:-2]))
#from project_constant import DB_PARAMS_TEST


DB_PARAMS = {'host': '172.27.0.255', 'user': 'datac', 'password': 'g3Z2zHF6uTxK6#rnlZ7', 'port': 3306, 'charset': 'utf8'}

HQL = '''
set mapred.max.split.size=10000000;
SET mapreduce.map.memory.mb=4000;
set mapreduce.reduce.memory.mb=4000;
set hive.new.job.grouping.set.cardinality=2048;
set hive.exec.dynamic.partition.mode=nonstrict;

insert overwrite table %(targettab)s partition (ds)
select 
	tb3.st as fds
	,tb3.appver as appver
	,tb3.channel as channel
	,tb3.os as os
	,tb3.dim_type as dim_type
	,tb3.dim_fun as dim_fun
	,tb3.total_invite_count as total_party_a_uv
	,tb3.total_invitee_count as total_party_b_uv
	,0 as total_reg_new_phone
	,0 as total_reg_new_uv
	,tb3.invite_count as party_a_uv
	,tb3.invitee_count as party_b_uv
	,tb3.pre_register_count as reg_new_phone
	,tb3.register_count as reg_new_uv
	,tb5.money as red_money
	,tb4.fid_uv as fid_uv
	,tb4.new_fid as new_fid_uv
	,tb4.fid_uid_uv as fid_uid_uv
	,tb3.st as ds
from 
(
	select 
		'%(run_date)s' as st
		,nvl(label.appver,'allappver') as appver
		,nvl(label.os,'allos') as os
		,nvl(label.channel,'allchannel') as channel
		,nvl(label.dim_type,'all') as dim_type
		,nvl(label.dim_fun,'all') as dim_fun
		,count(DISTINCT if(((label.status is null and label.invite_type in (3,4)) or (label.status = cast(label.invite_type as string)) or (label.status = 2 and label.invite_type = 3 and type_list = '2,3') or (label.status in (1,2) and label.invite_type = 4 and type_list in ('3,4', '4'))) and label.invitee_registe_ds<='%(run_date)s' ,label.invite_uid,null)) as total_invite_count
		,count(DISTINCT if(((label.status is null and label.invite_type in (3,4)) or (label.status = cast(label.invite_type as string)) or (label.status = 2 and label.invite_type = 3 and type_list = '2,3') or (label.status in (1,2) and label.invite_type = 4 and type_list in ('3,4', '4'))) and label.invitee_registe_ds<='%(run_date)s' ,label.invitee_uid,null)) as total_invitee_count
		,count(DISTINCT if(((label.status is null and label.invite_type in (3,4)) or (label.status = cast(label.invite_type as string)) or (label.status = 2 and label.invite_type = 3 and type_list = '2,3') or (label.status in (1,2) and label.invite_type = 4 and type_list in ('3,4', '4'))) and label.invitee_registe_ds='%(run_date)s' ,label.invite_uid,null)) as invite_count
		,count(DISTINCT if(((label.status is null and label.invite_type in (3,4)) or (label.status = cast(label.invite_type as string)) or (label.status = 2 and label.invite_type = 3 and type_list = '2,3') or (label.status in (1,2) and label.invite_type = 4 and type_list in ('3,4', '4'))) and label.invitee_registe_ds='%(run_date)s' ,label.invitee_uid,null)) as invitee_count
		,count(DISTINCT if(label.is_code = 0 and label.invite_last_ds='%(run_date)s', label.invitee_phone,null)) as pre_register_count
		,count(DISTINCT if(label.is_code = 0 and label.invite_last_ds='%(run_date)s' and (label.status = cast(label.invite_type as string)) and label.status is not null and label.invitee_registe_ds='%(run_date)s', invitee_uid,null)) as register_count 
	from 
		%(idl_tb)s label
	group by 
		label.appver, label.channel, label.os, label.dim_type, label.dim_fun with cube 
) tb3 
left join 
(
	select 
		'%(run_date)s' as st
		,count(distinct family_id) as fid_uv
		,count(distinct uid) as fid_uid_uv
		,count(distinct new_fid) as new_fid
	from (
		select 
			distinct invite_family_id as family_id
			,invite_uid as uid
			,null as new_fid
		from 
			%(idl_ori_tb)s
		where 
			ds = '%(run_date)s'
		and 
			invite_family_id is not null
			
		union all
		
		select 
			distinct invitee_family_id as family_id
			,invitee_uid as uid
			,null as new_fid
		from 
			%(idl_ori_tb)s
		where 
			ds = '%(run_date)s'
		and 
			invitee_family_id is not null
			
		union all
		
		select 
			distinct null as family_id
			,null as uid
			,familyid as new_fid
		from 
			%(bdl_tb)s
		where 
			event = 'createfamily'
		and 
			ds = '%(run_date)s'
		and 
			eventtype = 'medium'
	) t
) tb4 
on tb3.st = tb4.st
left join 
(
	select 
		'%(run_date)s' as st
		,nvl(last_appver,'allappver') as appver
		,nvl(last_os, 'allos') as os
		,nvl(last_channel, 'allchannel') as channel
		,sum(total_money) as money
	from (
		select 
			a.ouid
			,last_appver
			,last_os
			,last_channel
			,total_money
		from (
			select 
				ouid
				,sum(rmb) as total_money
			from 
				%(bdl_tb)s
			where 
				ds = '%(run_date)s'
			and 
				event = 'hongbao'
			and
				eventtype = 'low'
			and 
				type = 2
			group by 
				ouid
		) a
		left join
		(
			select 
				ouid
				,nvl(last_appver, '-1') as last_appver
				,nvl(last_os, '-1') as last_os
				,nvl(last_channel, '-1') as last_channel
			from
				%(idl_user_tb)s
			where
				ut = 'uid'
			and
				ds = '%(run_date)s'
		) b
		on a.ouid = b.ouid
	) t
	group by last_appver, last_os, last_channel with cube
) tb5
on tb3.st = tb5.st and tb3.appver = tb5.appver and tb3.os = tb5.os and tb3.channel = tb5.channel

;

insert overwrite table %(targettab)s partition (ds)
select
	tb1.fds as fds
	,tb1.appver as appver
	,tb1.channel as channel
	,tb1.os as os
	,tb1.dim_type as dim_type
	,tb1.dim_fun as dim_fun
	,if(tb3.total_party_a_uv is null, tb1.total_party_a_uv, (tb3.total_party_a_uv + tb1.total_party_a_uv)) as total_party_a_uv
	,if(tb3.total_party_b_uv is null, tb1.total_party_b_uv, (tb3.total_party_b_uv + tb1.total_party_b_uv)) as total_party_b_uv
	,if(tb2.total_reg_new_phone is null, tb1.reg_new_phone, (tb2.total_reg_new_phone + tb1.reg_new_phone)) as total_reg_new_phone
	,if(tb2.total_reg_new_uv is null, tb1.reg_new_uv, (tb2.total_reg_new_uv + tb1.reg_new_uv)) as total_reg_new_uv
	,tb1.party_a_uv as party_a_uv
	,tb1.party_b_uv as party_b_uv
	,tb1.reg_new_phone as reg_new_phone
	,tb1.reg_new_uv as reg_new_uv
	,tb1.red_money as red_money
	,if(tb2.fid_uv is null, tb1.fid_uv,(tb2.fid_uv + tb1.new_fid_uv)) as fid_uv
	,tb1.new_fid_uv as new_fid_uv
	,tb1.fid_uid_uv as fid_uid_uv
	,tb1.ds as ds
from 
(
	select
		fds
		,appver
		,os
		,channel
		,dim_type
		,dim_fun
		,total_party_a_uv
		,total_party_b_uv
		,party_a_uv
		,party_b_uv
		,reg_new_phone
		,reg_new_uv
		,red_money
		,fid_uv
		,new_fid_uv
		,fid_uid_uv
		,ds
	from 
		%(targettab)s 
	where 
		ds = '%(run_date)s'
 ) tb1
left join 
(
	select 
  		appver
		,channel
		,os
		,dim_type
		,dim_fun
		,total_reg_new_phone
		,total_reg_new_uv
		,fid_uv
  	from 
  		%(targettab)s 
    where 
		ds = '%(day1)s'
) tb2
on tb1.appver = tb2.appver and tb1.os = tb2.os and tb1.channel = tb2.channel and tb1.dim_type = tb2.dim_type and tb1.dim_fun = tb2.dim_fun
left join
(
        select
                appver
                ,channel
                ,os
                ,dim_type
                ,dim_fun
                ,total_party_a_uv
                ,total_party_b_uv
        from 
                 %(targettab)s 
        where 
                ds = '%(originday)s'
) tb3
on tb1.appver = tb3.appver and tb1.os = tb3.os and tb1.channel = tb3.channel and tb1.dim_type = tb3.dim_type and tb1.dim_fun = tb3.dim_fun;

	

'''


def main():
    kdc = kingnetdc.kdc
    args = dict()
    args['run_date'] = kdc.workDate
    args['idl_tb'] = 'wutiao.idl_wutiao_invite_user_label'
    args['idl_ori_tb'] = 'wutiao.idl_wutiao_invite_user'
    args['idl_user_tb'] = 'wutiao.idl_wutiao_user'
    args['bdl_tb'] = 'wutiao.bdl_wutiao_event'
    args['targettab'] = 'wutiao.report_wutiao_invite_behavior'
    args['mysql_tb'] = 'wutiao.report_wutiao_invite_behavior'
    args['day1'] = kdc.dateSub(1)
    args['originday'] = '2018-09-05'

    kdc.debug = True
    sql = HQL % args
    print(sql)

    kdc.doHive(sql)
    sync_sql = '''
        select fds, appver, channel, os, dim_type,dim_fun
            ,total_party_a_uv
            ,total_party_b_uv
            ,total_reg_new_phone
            ,total_reg_new_uv
            ,party_a_uv
            ,party_b_uv
            ,reg_new_phone
            ,reg_new_uv
            ,if(dim_type in ('friend', 'all'),red_money,0) as red_money
            ,if(dim_type='friend',0,fid_uv) as fid_uv
            ,if(dim_type='friend',0,new_fid_uv) as new_fid_uv
            ,if(dim_type='friend',0,fid_uid_uv) as fid_uid_uv
        from %(targettab)s
        where ds = '%(run_date)s'
    ''' % args
    ret = kingnetdc.presto_execsqlr(sync_sql)
    print(len(ret))

    if ret:
        del_sql = '''delete from %(mysql_tb)s where fds = '%(run_date)s' ''' % args
        kingnetdc.new_execsqlr(DB_PARAMS, del_sql)
        args['def_str'] = ','.join(['%s']*len(ret[0]))
        insert_sql = '''insert into %(mysql_tb)s values(%(def_str)s) ''' % args
        kingnetdc.executemany_batches(DB_PARAMS, insert_sql, ret, 20000)


if __name__ == '__main__':
    main()
