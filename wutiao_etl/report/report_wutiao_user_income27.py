# coding: utf-8

#**********************程序说明*********************************#
#*模块：REPORT
#*功能：区块链-用户收益统计
#*作者：gant
#*时间：2018-06-26
#*备注：区块链-用户收益统计分析
#***************************************************************#
import kingnet
import os

SQL_DAY = '''
INSERT overwrite TABLE {targettab} partition (ds='{ds}')
select '{ds}' as fds, 
nvl(user_flag,'alluserflag') as user_flag,
nvl(media_type,'allmediatype') as media_type,
total_income,
total_withdraw_success_rmb,
total_withdraw_success_usercnt,
day_withdraw_success_rmb,
day_withdraw_success_usercnt,
creative_income,
vote_income,
comment_income,
share_income,
register_income,
inviter_income,
invited_income,
like_income,
report_income,
other_income,
friend_income,
read_income,
inviter_bonus_income
from(
    select t1.user_flag,
    t1.media_type,
    sum(t1.his_money_gain) as total_income,
    sum(t1.his_withdraw_rmb) as total_withdraw_success_rmb,
    sum(if(t1.his_withdraw_rmb>0,1,0)) as total_withdraw_success_usercnt,
    sum(if(t1.last_day_withdraw_rmb>0,last_day_withdraw_rmb,0)) as day_withdraw_success_rmb,
    sum(if(t1.last_day_withdraw_rmb>0,1,0)) as day_withdraw_success_usercnt,
    sum(creative_income) as creative_income,
    sum(vote_income) as vote_income,
    sum(comment_income) as comment_income,
    sum(share_income) as share_income,
    sum(register_income) as register_income,
    sum(inviter_income) as inviter_income,
    sum(invited_income) as invited_income,
    sum(like_income) as like_income,
    sum(report_income) as report_income,
    sum(other_income) as other_income,
    sum(friend_income) as friend_income,
    sum(read_income) as read_income,
    sum(inviter_bonus_income) as inviter_bonus_income
    from(
        select ouid,
        if(from_unixtime(first_login_time,'yyyy-MM-dd')=ds,1,0) as user_flag,
        nvl(media_type,'-1') as media_type,
        if(from_unixtime(last_withdraw_time,'yyyy-MM-dd')=ds,last_day_withdraw_rmb,0) as last_day_withdraw_rmb,
        his_withdraw_rmb,
        his_money_gain
        from {sourcetab2}
        where ds='{ds}' and ut='uid'
    )t1
    left join(
        select ds,uid,
        sum(if(actiontype=10,value,0)) as creative_income,
        sum(if(actiontype=12,value,0)) as like_income,
        sum(if(actiontype=14,value,0)) as comment_income,
        sum(if(actiontype=13,value,0)) as share_income,
        sum(if(actiontype=1,value,0)) as register_income,
        sum(if(actiontype=2,value,0)) as inviter_income,
        sum(if(actiontype=3,value,0)) as invited_income,
        sum(if(actiontype=5,value,0)) as report_income,
        sum(if(actiontype=9,value,0)) as other_income,
        sum(if(actiontype=4,value,0)) as friend_income,
        sum(if(actiontype=11,value,0)) as read_income,
        sum(if(actiontype=15,value,0)) as vote_income,
        sum(if(actiontype=16,value,0)) as inviter_bonus_income
        from {sourcetab1}
        where ds = '{ds}'
        group by ds,uid
    )t2
    on t1.ouid=t2.uid
    group by user_flag,media_type
    grouping sets((user_flag,media_type),(media_type),(user_flag),())
)t3;
'''

delete=r'''echo "use wutiao; delete from report_wutiao_user_income where fds ='{ds}'; " |mysql -h {host} -u {user} -p{password} '''
sync=r'''/usr/bin/sqoop-export  --export-dir /user/hive/warehouse/wutiao_db/report_wutiao_user_income/ds='{ds}' --connect jdbc:mysql://{host}:{port}/wutiao --username "{user}" --password '{password}' --table "report_wutiao_user_income" --input-fields-terminated-by "|" --input-null-string "\\\N" --input-null-non-string "\\\N" '''

def main():
    kdc = kingnet.kdc()
    args = {
        'ds': kdc.workDate,
        'sourcetab1': 'wutiao.odl_wutiao_money_gain',
        'sourcetab2': 'wutiao.idl_wutiao_user',
        'targettab': 'wutiao.report_wutiao_user_income',
        'mysqltab': 'wutiao.report_wutiao_user_income',
        'host': '172.17.2.91',
        'user': 'root',
        'password': '123456',
        'port': '3306'
    }
    sql = SQL_DAY.format(ds=args['ds'], targettab=args['targettab'], sourcetab1=args['sourcetab1'], sourcetab2=args['sourcetab2'])

    kdc.debug = True
    res = kdc.doHive(sql,True,True)

    if res !=0 :
        raise Exception('report_wutiao_user_income hive table insert execute failure!!')

    res1 = os.system(delete.format(ds=args['ds'],host=args['host'],user=args['user'],password=args['password']))
    res2 = os.system(sync.format(ds=args['ds'],host=args['host'],user=args['user'],password=args['password'],port=args['port']))

    if (res1 or res2) !=0 :
        print(res1,res2)
        raise Exception('report_wutiao_user_income mysql table delete&insert execute failure!!')
    else :
        print(res1,res2)
        print('complete')

if __name__ == '__main__':
    main()
