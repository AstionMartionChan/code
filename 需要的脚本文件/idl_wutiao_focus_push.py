# coding: utf-8

#**********************程序说明*********************************#
#*模块：REPORT
#*功能：关注推送
#*作者：xiaoj
#*时间：2018-11-13
#*备注：关注推送
#***************************************************************#
import kingnetdc
import sys
import os
sys.path.append('/'.join(os.path.abspath(__file__).split('/')[:-2]))
#from project_constant import DB_PARAMS_TEST

SQL_DAY = '''
set mapred.max.split.size=10000000;
set mapreduce.map.memory.mb=4096;
set mapreduce.reduce.memory.mb=4096;
set hive.strict.checks.cartesian.product=false;


--插入一日未活跃用户
insert OVERWRITE table wutiao_blockdata.idl_1day_inactive partition(ds='%(run_date)s')
select ouid,greatest(nvl(last_login_time,0),nvl(last_openclient_time,0),nvl(last_comment_time,0),nvl(last_vote_time,0),nvl(last_validread_time,0),nvl(last_contribution_time,0)) as last_active,last_appver as appv, last_os as os,first_channel as channel from wutiao.idl_wutiao_user
where ds='%(run_date)s' and ut='uid' and ouid>0 and last_openclient_time is not null
and from_unixtime(greatest(nvl(last_login_time,0),nvl(last_openclient_time,0),nvl(last_comment_time,0),nvl(last_vote_time,0),nvl(last_validread_time,0),nvl(last_contribution_time,0)),'yyyy-MM-dd')='%(day1)s'

;

--每天关注1\取消-1关注增量数据
insert overwrite table wutiao_blockdata.idl_wutiao_user_attention_media_delta partition(ds='%(run_date)s')
select uid,mediaid,status from (
select ouid as uid,mediaid,status, row_number() over(partition by ouid,mediaid order by ts desc) as rn
from wutiao.bdl_wutiao_event where ds='%(run_date)s' and event='attention'
) a
where rn=1
;


--用增量数据更新每日全量数据
insert OVERWRITE table  wutiao_blockdata.idl_wutiao_user_attention_media partition(ds='%(run_date)s')
select a.uid,a.mediaid
from (select * from wutiao_blockdata.idl_wutiao_user_attention_media where ds='%(day1)s')  a
--去掉取关
left join (select * from wutiao_blockdata.idl_wutiao_user_attention_media_delta where ds='%(run_date)s' and status=-1) b on a.uid=b.uid and a.mediaid=b.mediaid
where b.uid is null
union
--增加关注
select uid,mediaid from wutiao_blockdata.idl_wutiao_user_attention_media_delta where ds='%(run_date)s' and status=1
;

'''


def main():
    kdc = kingnetdc.kdc
    args = dict()
    args['run_date'] = kdc.workDate
    args['day1'] = kdc.dateSub(1)
    

    kdc.debug = True
    sql = SQL_DAY % args
    print(sql)
    kdc.doHive(sql)


if __name__ == '__main__':
    main()
 
