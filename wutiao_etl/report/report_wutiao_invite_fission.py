# coding: utf-8

#**********************程序说明*********************************#
#*模块：REPORT
#*功能：区块链-邀请用户裂变分析
#*作者：leo
#*时间：2018-09-17
#*备注：区块链-邀请用户裂变分析
#***************************************************************#
import kingnetdc
import sys
import os
sys.path.append('/'.join(os.path.abspath(__file__).split('/')[:-2]))

DB_PARAMS = {'host': '172.27.0.255', 'user': 'datac', 'password': 'g3Z2zHF6uTxK6#rnlZ7', 'port': 3306, 'charset': 'utf8'}

SQL_DAY = '''
set mapred.max.split.size=10000000;
set mapreduce.map.memory.mb=4096;
set mapreduce.reduce.memory.mb=4096;

INSERT overwrite TABLE {targettab} partition (ds='{ds}')
select	
	 tb1.st as fds
	,tb1.appver as appver 
	,tb1.channel as channel 
	,tb1.os as os 
	,tb1.total_invite_count as total_invite_count
	,tb1.total_invitee_count as total_invitee_count
	,tb1.invite_count as invite_count
	,tb1.invitee_count as invitee_count
	,tb1.new_invite_count as new_invite_count
	,tb1.new_invitee_count as new_invitee_count
	,tb1.old_invite_count as old_invite_count
	,tb1.old_invitee_count as old_invitee_count 
	,tb1.registe_invitee_count as registe_invitee_count
	,tb2.new_register as new_register
	,tb2.old_register as old_register
from 
(
	select 
		'{ds}' as st
		,nvl(label.appver,'allappver') as appver
		,nvl(label.os,'allos') as os
		,nvl(label.channel,'allchannel') as channel
		,count(DISTINCT if(((label.status is null and label.invite_type in (3,4)) or (label.status = cast(label.invite_type as string)) or (label.status = 2 and label.invite_type = 3 and type_list = '2,3') or (label.status in (1,2) and label.invite_type = 4 and type_list in ('3,4', '4'))) and label.invitee_registe_ds<='{ds}' ,label.invite_uid,null)) as total_invite_count
		,count(DISTINCT if(((label.status is null and label.invite_type in (3,4)) or (label.status = cast(label.invite_type as string)) or (label.status = 2 and label.invite_type = 3 and type_list = '2,3') or (label.status in (1,2) and label.invite_type = 4 and type_list in ('3,4', '4'))) and label.invitee_registe_ds<='{ds}' ,label.invitee_uid,null)) as total_invitee_count
		,count(DISTINCT if(((label.status is null and label.invite_type in (3,4)) or (label.status = cast(label.invite_type as string)) or (label.status = 2 and label.invite_type = 3 and type_list = '2,3') or (label.status in (1,2) and label.invite_type = 4 and type_list in ('3,4', '4'))) and label.invitee_registe_ds='{ds}' ,label.invite_uid,null)) as invite_count
		,count(DISTINCT if(((label.status is null and label.invite_type in (3,4)) or (label.status = cast(label.invite_type as string)) or (label.status = 2 and label.invite_type = 3 and type_list = '2,3') or (label.status in (1,2) and label.invite_type = 4 and type_list in ('3,4', '4'))) and label.invitee_registe_ds='{ds}' ,label.invitee_uid,null)) as invitee_count
		,count(DISTINCT IF(((label.status is null and label.invite_type in (3,4)) or (label.status = cast(label.invite_type as string)) or (label.status = 2 and label.invite_type = 3 and type_list = '2,3') or (label.status in (1,2) and label.invite_type = 4 and type_list in ('3,4', '4'))) and label.invitee_registe_ds='{ds}' and label.invite_registe_ds = '{ds}', label.invite_uid,null)) as new_invite_count
		,count(DISTINCT IF(((label.status is null and label.invite_type in (3,4)) or (label.status = cast(label.invite_type as string)) or (label.status = 2 and label.invite_type = 3 and type_list = '2,3') or (label.status in (1,2) and label.invite_type = 4 and type_list in ('3,4', '4'))) and label.invitee_registe_ds='{ds}' and label.invite_registe_ds = '{ds}', label.invitee_uid,null)) as new_invitee_count
		,count(DISTINCT IF(((label.status is null and label.invite_type in (3,4)) or (label.status = cast(label.invite_type as string)) or (label.status = 2 and label.invite_type = 3 and type_list = '2,3') or (label.status in (1,2) and label.invite_type = 4 and type_list in ('3,4', '4'))) and label.invitee_registe_ds='{ds}' and label.invite_registe_ds < '{ds}', label.invite_uid,null)) as old_invite_count
		,count(DISTINCT IF(((label.status is null and label.invite_type in (3,4)) or (label.status = cast(label.invite_type as string)) or (label.status = 2 and label.invite_type = 3 and type_list = '2,3') or (label.status in (1,2) and label.invite_type = 4 and type_list in ('3,4', '4'))) and label.invitee_registe_ds='{ds}' and label.invite_registe_ds < '{ds}', label.invitee_uid,null)) as old_invitee_count
		,count(DISTINCT if((label.status = cast(label.invite_type as string)) and label.invitee_registe_ds='{ds}', label.invitee_uid,null)) as registe_invitee_count
	from 
		{sourcetab} label
	group by 
		label.appver, label.channel, label.os with cube 
) tb1 
left join 
(
	select
		 nvl(tb1.appver, 'allappver') as appver 
		,nvl(tb1.channel, 'allchannel') as channel 
		,nvl(tb1.os, 'allos') as os 
		,sum(tb1.new_tag) as new_register
		,sum(tb1.old_tag) as old_register
	from 
	(
	  select 
		   nvl(last_appver, '-1') as appver
		  ,nvl(last_channel, '-1') as channel
		  ,nvl(last_os, '-1') as os
		  ,if(from_unixtime(first_login_time, 'yyyy-MM-dd') = '{ds}',1,0) as new_tag
		  ,if(from_unixtime(first_login_time, 'yyyy-MM-dd') < '{ds}' and from_unixtime(last_login_time, 'yyyy-MM-dd') = '{ds}',1,0) as old_tag
	  from 
		  {idltab}
	  where ds = '{ds}'
	  and ut = 'uid'
	) tb1
	group by tb1.appver, tb1.channel, tb1.os with cube
) tb2
on tb1.appver = tb2.appver and tb1.channel = tb2.channel and tb1.os = tb2.os;
'''

delete=r'''echo "use wutiao; delete from report_wutiao_invite_fission where fds ='{ds}'; " |mysql -h {host} -u {user} -p{password} '''
sync=r'''/usr/bin/sqoop-export  --export-dir /user/hive/warehouse/wutiao.db/report_wutiao_invite_fission/ds='{ds}' --connect jdbc:mysql://{host}:{port}/wutiao --username "{user}" --password '{password}' --table "report_wutiao_invite_fission" --input-fields-terminated-by "|" --input-null-string "\\\N" --input-null-non-string "\\\N" '''

def main():
    kdc = kingnetdc.kdc
    args = {
        'ds': kdc.workDate,
	'idltab': 'wutiao.idl_wutiao_user',
        'sourcetab': 'wutiao.idl_wutiao_invite_user_label',
        'targettab': 'wutiao.report_wutiao_invite_fission',
        'mysqltab': 'wutiao.report_wutiao_invite_fission',
        'host': DB_PARAMS['host'],
        'user': DB_PARAMS['user'],
        'password': DB_PARAMS['password'],
        'port': DB_PARAMS['port']
    }
    sql = SQL_DAY.format(ds=args['ds'], targettab=args['targettab'], sourcetab=args['sourcetab'], idltab=args['idltab'])

    kdc.debug = True
    kdc.doHive(sql,True,True)

    sync_sql = '''
    select  
         fds
        ,appver 
        ,channel 
        ,os 
        ,total_invite_count
        ,total_invitee_count
        ,invite_count
        ,invitee_count
        ,new_invite_count
        ,new_invitee_count
        ,old_invite_count
        ,old_invitee_count 
        ,registe_invitee_count
        ,new_register
        ,old_register
    from 
        %(targettab)s
    where ds = '%(ds)s'
    ''' % args

    print(sync_sql)
    ret = kingnetdc.presto_execsqlr(sync_sql)
    print(len(ret))

    if ret:
        del_sql = '''delete from %(mysqltab)s where fds = '%(ds)s' ''' % args
        kingnetdc.new_execsqlr(DB_PARAMS, del_sql)
        args['def_str'] = ','.join(['%s']*len(ret[0]))
        insert_sql = '''insert into %(mysqltab)s values(%(def_str)s) ''' % args
        kingnetdc.executemany_batches(DB_PARAMS, insert_sql, ret, 20000)




if __name__ == '__main__':
    main()
