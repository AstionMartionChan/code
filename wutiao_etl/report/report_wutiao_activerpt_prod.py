# coding: utf-8

#**********************程序说明*********************************#
#*模块：REPORT
#*功能：区块链-产品-活动报表
#*作者：jingbf
#*时间：2018-10-11
#*备注：区块链-产品-活动报表
#***************************************************************#
import kingnetdc
import sys
import os
sys.path.append('/'.join(os.path.abspath(__file__).split('/')[:-2]))
#from project_constant import DB_PARAMS_TEST
DB_PARAMS = {'host': '172.27.0.255', 'user': 'datac', 'password': 'g3Z2zHF6uTxK6#rnlZ7', 'port': 3306, 'charset': 'utf8'}

SQL_DAY = '''
set mapred.max.split.size=10000000;
set mapreduce.map.memory.mb=4096;
set mapreduce.reduce.memory.mb=4096;
set hive.exec.dynamic.partition.mode=nonstrict;
set hive.strict.checks.cartesian.product=false;


INSERT overwrite TABLE %(targettab)s partition (ds)
select 
	'%(run_date)s' as fds
	,tb2.in_exposure_did_cnt
	,tb2.download_active_did_cnt
	,tb2.download_register_ouid_cnt
	,tb2.share_success_cnt
	,tb1.invitepage_did_cnt
	,tb1.pre_registpage_did_cnt
	,tb1.pre_registclick_did_cnt
	,tb1.downloadpage_did_cnt
	,tb1.downloadclick_did_cnt
	,tb1.inviteclick_did_cnt
	,tb2.webchatshareclick_cnt
	,tb2.webchatMshareclick_cnt
	,tb2.qqshareclick_cnt
	,tb2.sinashareclick_cnt
	,tb2.webchatsharesuccess_cnt
	,tb2.webchatMsharecsuccess_cnt
	,tb2.qqsharesuccess_cnt
	,tb2.sinasharesuccess_cnt
	,'%(run_date)s' as ds
from 
(
select 
	count(DISTINCT if(event='pageview' and pos='ac2#1',did,null)) as invitepage_did_cnt,
	count(DISTINCT if(event='pageview' and pos='ac2#2',did,null)) as pre_registpage_did_cnt,
	count(DISTINCT if(event='click' and pos='ac2#2_1',did,null)) as pre_registclick_did_cnt,
	count(DISTINCT if(event='pageview' and pos='ac2#3',did,null)) as downloadpage_did_cnt,
	count(DISTINCT if(event='click' and pos='ac2#3_1',did,null)) as downloadclick_did_cnt,
	count(DISTINCT if(event='click' and pos in ('ac2#1_1','ac2#1_2','ac2#1_3','ac2#1_4','ac2#1_5','ac2#1_6','ac2#1_7'),did,null)) as inviteclick_did_cnt
from 
	%(sourcetab1)s 
where 
	event in ('pageview','click')
and 
	pos in ('ac2#1','ac2#2','ac2#2_1','ac2#3','ac2#3_1','ac2#1_1','ac2#1_2','ac2#1_3','ac2#1_4','ac2#1_5','ac2#1_6')
and 
	ds = '%(run_date)s'
) tb1
inner join 
(
select 
	 count(DISTINCT if((event = 'displayinpage' and pos = 'S_3') or (event = 'show' and pos = 'A') or (event = 'show' and pos = 'E') or (event = 'show' and pos = 'D#1#3'), did, null)) as in_exposure_did_cnt
	,count(DISTINCT if(event in ('register','login','active' ,'openclient' ,'enterfront') and ad in ('396','116'),did, null)) as download_active_did_cnt
	,count(DISTINCT if(event = 'register' and status = '2',ouid, null)) as download_register_ouid_cnt
	,sum(if(event='share' and status = '3' and itemid = '0',1,0)) as share_success_cnt
	,count(DISTINCT if(event='share' and itemid = '0' and status = '1' and shareplat = 'Wechat',did,null)) as webchatshareclick_cnt
	,count(DISTINCT if(event='share' and itemid = '0' and status = '1' and shareplat = 'WechatMoments',did,null)) as webchatMshareclick_cnt
	,count(DISTINCT if(event='share' and itemid = '0' and status = '1' and shareplat = 'QQ',did,null)) as qqshareclick_cnt
	,count(DISTINCT if(event='share' and itemid = '0' and status = '1' and shareplat = 'SinaWeibo',did,null)) as sinashareclick_cnt
	,sum(if(event='share' and itemid = '0' and status = '3' and shareplat = 'Wechat',1,0)) as webchatsharesuccess_cnt
	,sum(if(event='share' and itemid = '0' and status = '3' and shareplat = 'WechatMoments',1,0)) as webchatMsharecsuccess_cnt
	,sum(if(event='share' and itemid = '0' and status = '3' and shareplat = 'QQ',1,0)) as qqsharesuccess_cnt
	,sum(if(event='share' and itemid = '0' and status = '3' and shareplat = 'SinaWeibo',1,0)) as sinasharesuccess_cnt
from 
	%(sourcetab)s 
where 
	ds = '%(run_date)s'
and 
	eventtype in ('super', 'high', 'low')
and 
	event in ('displayinpage','show','register','login','active','openclient','enterfront','share')
) tb2
on 1 = 1

'''


def main():
    kdc = kingnetdc.kdc
    args = dict()
    args['run_date'] = kdc.workDate
    args['sourcetab'] = 'wutiao.bdl_wutiao_event'
    args['sourcetab1'] = 'wutiao.odl_wutiao_js'
    args['targettab'] = 'wutiao.report_wutiao_activerpt_prod'
    args['mysql_tb'] = 'wutiao.report_wutiao_activerpt_prod'
    

    kdc.debug = True
    sql = SQL_DAY % args
    print(sql)
    kdc.doHive(sql)

    sync_sql = '''
    select 
       fds,
       in_exposure_did_cnt,
       download_active_did_cnt,
       download_register_ouid_cnt,
       share_success_cnt,
       invitepage_did_cnt,
       pre_registpage_did_cnt,
       pre_registclick_did_cnt,
       downloadpage_did_cnt,
       downloadclick_did_cnt,
       inviteclick_did_cnt,
       webchatshareclick_cnt,
       webchatMshareclick_cnt,
       qqshareclick_cnt,
       sinashareclick_cnt,
       webchatsharesuccess_cnt,
       webchatMsharecsuccess_cnt,
       qqsharesuccess_cnt,
       sinasharesuccess_cnt
   from %(targettab)s
  where ds = '%(run_date)s'
    ''' % args
    try:
        ret = kingnetdc.presto_execsqlr(sync_sql)
    except Exception as e:
        print(e)
        ret = [x.split('\t') for x in kingnetdc.do_hql_exec(sync_sql).split('\n') if bool(re.match(r'^\d{4}-\d{2}-\d{2}', x[:10]))]
    print(len(ret))

    if ret:
        del_sql = '''delete from %(mysql_tb)s where fds = '%(run_date)s' ''' % args
        
        kingnetdc.new_execsqlr(DB_PARAMS, del_sql)
        args['def_str'] = ','.join(['%s']*len(ret[0]))
        insert_sql = '''insert into %(mysql_tb)s values(%(def_str)s) ''' % args
        kingnetdc.executemany_batches(DB_PARAMS, insert_sql, ret, 30000)
    ''''''

if __name__ == '__main__':
    main()
 
