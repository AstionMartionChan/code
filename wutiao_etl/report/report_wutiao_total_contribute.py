# coding: utf-8

#**********************程序说明*********************************#
#*模块：REPORT
#*功能：区块链-用户贡献力分析
#*作者：gant
#*时间：2018-07-25
#*备注：区块链-用户贡献力分析
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
select tt1.ds as fds, 
tt1.like_contribute,
tt1.share_contribute,
tt1.comment_contribute,
tt1.read_contribute,
tt1.register_contribute,
nvl(tt1.day_contribute,0)+nvl(tt2.29day_contribute,0) as valid_contribute,
nvl(tt1.day_verifypoint,0)+nvl(tt2.29day_verifypoint,0) as valid_verifypoint,
tt1.family_contribute,
tt1.day_contribute,
tt1.day_verifypoint
from(
    select '{ds}' as ds,    
    sum(if(event='contributioninc' and incref='2',contributionpoint,0)) as like_contribute,
    sum(if(event='contributioninc' and incref='4',contributionpoint,0)) as share_contribute,
    sum(if(event='contributioninc' and incref='3',contributionpoint,0)) as comment_contribute,
    sum(if(event='contributioninc' and incref='1',contributionpoint,0)) as read_contribute,
    sum(if(event='contributioninc' and incref='5',contributionpoint,0)) as inviter_contribute,
    sum(if(event='contributioninc' and incref='8',contributionpoint,0)) as register_contribute,
    sum(if(event='contributioninc',contributionpoint,0)) as day_contribute,
    sum(if(event='contributioninc' and incref='7',contributionpoint,0)) as family_contribute,
    sum(if(event='verifypointinc',verifypoint,0)) as day_verifypoint
    from {sourcetab}
    where ds='{ds}' and event in('contributioninc','verifypointinc')
)tt1
left join(
    select '{ds}' as ds,
    sum(day_contribute) as 29day_contribute,
    sum(day_verifypoint) as 29day_verifypoint
    from {targettab}
    where ds>=date_sub('{ds}',28) and ds<=date_sub('{ds}',1)
)tt2
on tt1.ds=tt2.ds;
'''

delete=r'''echo "use wutiao; delete from report_wutiao_total_contribute where fds ='{ds}'; " |mysql -h {host} -u {user} -p{password} '''
sync=r'''/usr/bin/sqoop-export  --export-dir /user/hive/warehouse/wutiao.db/report_wutiao_total_contribute/ds='{ds}' --connect jdbc:mysql://{host}:{port}/wutiao --username "{user}" --password '{password}' --table "report_wutiao_total_contribute" --input-fields-terminated-by "|" --input-null-string "\\\N" --input-null-non-string "\\\N" '''

def main():
    kdc = kingnetdc.kdc
    args = {
        'ds': kdc.workDate,
        'sourcetab': 'wutiao.bdl_wutiao_event',
        'targettab': 'wutiao.report_wutiao_total_contribute',
        'mysqltab': 'wutiao.report_wutiao_total_contribute',
        'host': DB_PARAMS['host'],
        'user': DB_PARAMS['user'],
        'password': DB_PARAMS['password'],
        'port': DB_PARAMS['port']
    }
    sql = SQL_DAY.format(ds=args['ds'], targettab=args['targettab'], sourcetab=args['sourcetab'])

    kdc.debug = True
    res = kdc.doHive(sql,True,True)

    if res !=0 :
        raise Exception('report_wutiao_total_contribute hive table insert execute failure!!')

    res1 = os.system(delete.format(ds=args['ds'],host=args['host'],user=args['user'],password=args['password']))
    res2 = os.system(sync.format(ds=args['ds'],host=args['host'],user=args['user'],password=args['password'],port=args['port']))

    if (res1 or res2) !=0 :
        print(res1,res2)
        raise Exception('report_wutiao_total_contribute mysql table delete&insert execute failure!!')
    else :
        print(res1,res2)
        print('complete')



if __name__ == '__main__':
    main()
