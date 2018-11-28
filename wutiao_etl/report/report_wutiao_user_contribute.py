# coding: utf-8

#**********************程序说明*********************************#
#*模块：REPORT
#*功能：区块链-用户贡献力分析
#*作者：gant
#*时间：2018-06-25
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
select '{ds}' as fds, 
nvl(tt1.user_flag,tt2.user_flag) as user_flag,
nvl(tt1.media_type,tt2.media_type) as media_type,
tt1.like_contribute,
tt1.share_contribute,
tt1.comment_contribute,
tt1.read_contribute,
tt1.inviter_contribute,
tt1.register_contribute,
nvl(tt1.day_contribute,0)+nvl(tt2.total_contribute,0) as total_contribute,
nvl(tt1.day_contribute,0)+nvl(tt2.29day_contribute,0) as valid_contribute,
nvl(tt1.share_contribute,0)+nvl(tt2.29day_share_contribute,0) as valid_share_contribute,
nvl(tt1.comment_contribute,0)+nvl(tt2.29day_comment_contribute,0) as valid_comment_contribute,
nvl(tt1.read_contribute,0)+nvl(tt2.29day_read_contribute,0) as valid_read_contribute,
nvl(tt1.inviter_contribute,0)+nvl(tt2.29day_inviter_contribute,0) as valid_inviter_contribute,
nvl(tt1.day_verifypoint,0)+nvl(tt2.total_verifypoint,0) as total_verifypoint,
nvl(tt1.day_verifypoint,0)+nvl(tt2.29day_verifypoint,0) as valid_verifypoint,
tt1.day_contribute,
tt1.day_verifypoint,
nvl(tt1.day_contribute,0)+nvl(tt2.29day_like_contribute,0) as valid_like_contribute
from(
    select nvl(user_flag,'alluserflag') as user_flag,
    nvl(media_type,'allmediatype') as media_type,
    like_contribute,
    share_contribute,
    comment_contribute,
    read_contribute,
    inviter_contribute,
    register_contribute,
    day_contribute,
    day_verifypoint
    from(
        select user_flag,
        media_type,
        sum(like_contribute) as like_contribute,
        sum(share_contribute) as share_contribute,
        sum(comment_contribute) as comment_contribute,
        sum(read_contribute) as read_contribute,
        sum(inviter_contribute) as inviter_contribute,
        sum(register_contribute) as register_contribute,
        sum(day_contribute) as day_contribute,
        sum(day_verifypoint) as day_verifypoint
        from(
            select t1.ds,
            nvl(t2.media_type,'-1') as media_type,
            nvl(t2.user_flag,'-1') as user_flag,
            like_contribute,
            share_contribute,
            comment_contribute,
            read_contribute,
            inviter_contribute,
            register_contribute,
            day_contribute,
            day_verifypoint
            from(
                select ds,ouid,
                sum(if(event='contributioninc' and incref='2',contributionpoint,0)) as like_contribute,
                sum(if(event='contributioninc' and incref='4',contributionpoint,0)) as share_contribute,
                sum(if(event='contributioninc' and incref='3',contributionpoint,0)) as comment_contribute,
                sum(if(event='contributioninc' and incref='1',contributionpoint,0)) as read_contribute,
                sum(if(event='contributioninc' and incref='5',contributionpoint,0)) as inviter_contribute,
                sum(if(event='contributioninc' and incref='8',contributionpoint,0)) as register_contribute,
                sum(if(event='contributioninc',contributionpoint,0)) as day_contribute,
                sum(if(event='verifypointinc',verifypoint,0)) as day_verifypoint
                from {sourcetab1}
                where ds='{ds}' and event in('contributioninc','verifypointinc')
                group by ds,ouid
            )t1
            left join(
                select ds,
                ouid,
                nvl(media_type,'-1') as media_type,
                if(from_unixtime(first_login_time,'yyyy-MM-dd')=ds,1,0) as user_flag
                from {sourcetab2}
                where ds='{ds}' and ut='uid'
            )t2
            on t1.ouid=t2.ouid
        )t3
        group by user_flag,media_type
        grouping sets((user_flag,media_type),(user_flag),(media_type),())
    )t3
)tt1
full join(
    select nvl(user_flag,'alluserflag') as user_flag,
    nvl(media_type,'allmediatype') as media_type,
    29day_like_contribute,
    29day_share_contribute,
    29day_comment_contribute,
    29day_read_contribute,
    29day_inviter_contribute,
    29day_register_contribute,
    29day_contribute,
    29day_verifypoint,
    total_contribute,
    total_verifypoint
    from(
         select user_flag,
         media_type,
         sum(like_contribute) as 29day_like_contribute,
         sum(share_contribute) as 29day_share_contribute,
         sum(comment_contribute) as 29day_comment_contribute,
         sum(read_contribute) as 29day_read_contribute,
         sum(inviter_contribute) as 29day_inviter_contribute,
         sum(register_contribute) as 29day_register_contribute,
         sum(day_contribute) as 29day_contribute,
         sum(day_verifypoint) as 29day_verifypoint,
         sum(if(ds=date_sub('{ds}',1),total_contribute,0)) as total_contribute,
         sum(if(ds=date_sub('{ds}',1),total_verifypoint,0)) as total_verifypoint
         from {targettab}
         where ds>=date_sub('{ds}',28) and ds<=date_sub('{ds}',1)
         group by user_flag,media_type
    )t1
)tt2
on tt1.user_flag=tt2.user_flag and tt1.media_type=tt2.media_type;
'''

delete=r'''echo "use wutiao; delete from report_wutiao_user_contribute where fds ='{ds}'; " |mysql -h {host} -u {user} -p{password} '''
sync=r'''/usr/bin/sqoop-export  --export-dir /user/hive/warehouse/wutiao.db/report_wutiao_user_contribute/ds='{ds}' --connect jdbc:mysql://{host}:{port}/wutiao --username "{user}" --password '{password}' --table "report_wutiao_user_contribute" --input-fields-terminated-by "|" --input-null-string "\\\N" --input-null-non-string "\\\N" '''

def main():
    kdc = kingnetdc.kdc
    args = {
        'ds': kdc.workDate,
        'sourcetab1': 'wutiao.bdl_wutiao_event',
        'sourcetab2': 'wutiao.idl_wutiao_user',
        'targettab': 'wutiao.report_wutiao_user_contribute',
        'mysqltab': 'wutiao.report_wutiao_user_contribute',
        'host': DB_PARAMS['host'],
        'user': DB_PARAMS['user'],
        'password': DB_PARAMS['password'],
        'port': DB_PARAMS['port']
    }
    sql = SQL_DAY.format(ds=args['ds'], targettab=args['targettab'], sourcetab1=args['sourcetab1'], sourcetab2=args['sourcetab2'])

    kdc.debug = True
    res = kdc.doHive(sql,True,True)

    if res !=0 :
        raise Exception('report_wutiao_user_contribute hive table insert execute failure!!')

    res1 = os.system(delete.format(ds=args['ds'],host=args['host'],user=args['user'],password=args['password']))
    res2 = os.system(sync.format(ds=args['ds'],host=args['host'],user=args['user'],password=args['password'],port=args['port']))

    if (res1 or res2) !=0 :
        print(res1,res2)
        raise Exception('report_wutiao_user_contribute mysql table delete&insert execute failure!!')
    else :
        print(res1,res2)
        print('complete')



if __name__ == '__main__':
    main()
