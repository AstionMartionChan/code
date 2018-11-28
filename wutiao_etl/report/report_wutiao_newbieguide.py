# coding: utf-8

#**********************程序说明*********************************#
#*模块：REPORT
#*功能：区块链-新手引导转化率分析
#*作者：leo
#*时间：2018-09-05
#*备注：区块链-新手引导转化率分析
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
select 
	'{ds}' as fds,
	nvl(tb1.appver, 'allappver') as appver,
	nvl(tb1.channel, 'allchannel') as channel,
	nvl(tb1.os, 'allos') as os,
	nvl(tb1.type, 'alltype') as type,
	count(distinct(if(tb1.pos='XS#1',tb1.did,NULL)))  as register_uv,
	count(distinct(if(tb1.pos='XS#1_2',tb1.did,NULL)))  as register_next_uv,
	count(distinct(if(tb1.pos='XS#2',tb1.ouid,NULL)))  as login_uv,
	count(distinct(if(tb1.pos='XS#2_2',tb1.ouid,NULL)))  as login_next_uv,
	count(distinct(if(tb1.pos='XS#3',tb1.ouid,NULL)))  as read_uv,
	count(distinct(if(tb1.pos='XS#3_2',tb1.ouid,NULL)))  as read_next_uv,
	count(distinct(if(tb1.pos='XS#4',tb1.ouid,NULL)))  as vote_uv,
	count(distinct(if(tb1.pos='XS#4_2',tb1.ouid,NULL)))  as vote_next_uv,
	count(distinct(if(tb1.pos='XS#10',tb1.ouid,NULL)))  as second_vote_uv,
	count(distinct(if(tb1.pos='XS#10_2',tb1.ouid,NULL)))  as second_vote_next_uv,
	count(distinct(if(tb1.pos='XS#5',tb1.ouid,NULL)))  as comments_uv,
	count(distinct(if(tb1.pos='XS#5_2',tb1.ouid,NULL)))  as comments_next_uv,
	count(distinct(if(tb1.pos='XS#11',tb1.ouid,NULL)))  as second_comments_uv,
	count(distinct(if(tb1.pos='XS#11_2',tb1.ouid,NULL)))  as second_comments_next_uv,
	count(distinct(if(tb1.pos='XS#6',tb1.ouid,NULL)))  as withdrawal_uv,
	count(distinct(if(tb1.pos='XS#6_2',tb1.ouid,NULL)))  as withdrawal_next_uv,
	count(distinct(if(tb1.pos='XS#7',tb1.ouid,NULL)))  as share_uv,
	count(distinct(if(tb1.pos='XS#7_2',tb1.ouid,NULL)))  as share_next_uv,
	count(distinct(if(tb1.pos='XS#8',tb1.ouid,NULL)))  as share_success_uv
from 
(
  select 
	nvl(appver, '-1') as appver,
	nvl(channel, '-1') as channel,
	nvl(os, '-1') as os,
	nvl(type, '-1') as type,
	pos as pos,
	did as did,
	ouid as ouid
  from 
	{sourcetab}
  where 
	eventtype in ('high','low')
  and 
	ds = '{ds}'
  and
	event in ('click', 'show')
) tb1
group by tb1.appver, tb1.channel, tb1.os, tb1.type
with cube;
'''

delete=r'''echo "use wutiao; delete from report_wutiao_newbieguide where fds ='{ds}'; " |mysql -h {host} -u {user} -p{password} '''
sync=r'''/usr/bin/sqoop-export  --export-dir /user/hive/warehouse/wutiao.db/report_wutiao_newbieguide/ds='{ds}' --connect jdbc:mysql://{host}:{port}/wutiao --username "{user}" --password '{password}' --table "report_wutiao_newbieguide" --input-fields-terminated-by "|" --input-null-string "\\\N" --input-null-non-string "\\\N" '''

def main():
    kdc = kingnetdc.kdc
    args = {
        'ds': kdc.workDate,
        'sourcetab': 'wutiao.bdl_wutiao_event',
        'targettab': 'wutiao.report_wutiao_newbieguide',
        'mysqltab': 'wutiao.report_wutiao_newbieguide',
        'host': DB_PARAMS['host'],
        'user': DB_PARAMS['user'],
        'password': DB_PARAMS['password'],
        'port': DB_PARAMS['port']
    }
    sql = SQL_DAY.format(ds=args['ds'], targettab=args['targettab'], sourcetab=args['sourcetab'])

    kdc.debug = True
    res = kdc.doHive(sql,True,True)

    if res !=0 :
        raise Exception('report_wutiao_newbieguide hive table insert execute failure!!')

    res1 = os.system(delete.format(ds=args['ds'],host=args['host'],user=args['user'],password=args['password']))
    res2 = os.system(sync.format(ds=args['ds'],host=args['host'],user=args['user'],password=args['password'],port=args['port']))

    if (res1 or res2) !=0 :
        print(res1,res2)
        raise Exception('report_wutiao_newbieguide mysql table delete&insert execute failure!!')
    else :
        print(res1,res2)
        print('complete')



if __name__ == '__main__':
    main()
