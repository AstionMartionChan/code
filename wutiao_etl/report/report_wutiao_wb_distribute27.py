# coding: utf-8

#**********************程序说明*********************************#
#*模块：REPORT
#*功能：区块链-用户发币量分配
#*作者：gant
#*时间：2018-06-26
#*备注：区块链-用户发币量分配分析
#***************************************************************#
import kingnet
import os

SQL_DAY = '''
INSERT overwrite TABLE {targettab} partition (ds='{ds}')
select '{ds}' as fds, 
nvl(t3.user_flag,t4.user_flag) as user_flag,
nvl(t3.media_type,t4.media_type) as media_type,
t3.register_distribute,
t3.inviter_distribute,
t3.invited_distribute,
t3.content_support_distribute,
t3.content_find_distribute,
t3.share_distribute,
t3.comment_distribute,
t3.like_distribute,
t3.read_distribute,
t3.report_distribute,
nvl(t3.total_distribute,0)+nvl(t4.total_distribute,0) as total_distribute,
t3.friend_bonus_distribute,
t3.other_distribute,
t3.inviter_bonus_distribute
from(
    select nvl(user_flag,'alluserflag') as user_flag,
    nvl(media_type,'allmediatype') as media_type,
    register_distribute,
    inviter_distribute,
    invited_distribute,
    content_support_distribute,
    content_find_distribute,
    share_distribute,
    comment_distribute,
    like_distribute,
    read_distribute,
    report_distribute,
    total_distribute,
    friend_bonus_distribute,
    other_distribute,
    inviter_bonus_distribute
    from(
        select user_flag,
        media_type,
        sum(register_distribute) as register_distribute,
        sum(inviter_distribute) as inviter_distribute,
        sum(invited_distribute) as invited_distribute,
        sum(content_support_distribute) as content_support_distribute,
        sum(content_find_distribute) as content_find_distribute,
        sum(share_distribute) as share_distribute,
        sum(comment_distribute) as comment_distribute,
        sum(like_distribute) as like_distribute,
        sum(read_distribute) as read_distribute,
        sum(report_distribute) as report_distribute,
        sum(total_distribute) as total_distribute,
        sum(friend_bonus_distribute) as friend_bonus_distribute,
        sum(other_distribute) as other_distribute,
        sum(inviter_bonus_distribute) as inviter_bonus_distribute
        from(
            select ds,uid,
            sum(if(actiontype=1,value,0)) as register_distribute,
            sum(if(actiontype=2,value,0)) as inviter_distribute,
            sum(if(actiontype=3,value,0)) as invited_distribute,
            sum(if(actiontype=10,value,0)) as content_support_distribute,
            sum(if(actiontype=15,value,0)) as content_find_distribute,
            sum(if(actiontype=13,value,0)) as share_distribute,
            sum(if(actiontype=14,value,0)) as comment_distribute,
            sum(if(actiontype=12,value,0)) as like_distribute,
            sum(if(actiontype=11,value,0)) as read_distribute,
            sum(if(actiontype=5,value,0)) as report_distribute,
            sum(if(actiontype=4,value,0)) as friend_bonus_distribute,
            sum(if(actiontype=9,value,0)) as other_distribute,
            sum(if(actiontype=16,value,0)) as inviter_bonus_distribute,
            sum(value) as total_distribute							  
            from {sourcetab1}
            where ds='{ds}'
            group by ds,uid
        )t1
        left join(
            select ds,
            ouid,
            nvl(media_type,'-1') as media_type,
            if(from_unixtime(first_login_time,'yyyy-MM-dd')=ds,1,0) as user_flag
            from {sourcetab2}
            where ds='{ds}' and ut='uid'
        )t2
        on t1.uid=t2.ouid
        group by user_flag,media_type
        grouping sets((user_flag,media_type),(media_type),(user_flag),())
    )t4
)t3
full join(select * from {targettab} where ds=date_sub('{ds}',1))t4
on t3.user_flag=t4.user_flag and t3.media_type=t4.media_type;
'''

delete=r'''echo "use wutiao; delete from report_wutiao_wb_distribute where fds ='{ds}'; " |mysql -h {host} -u {user} -p{password} '''
sync=r'''/usr/bin/sqoop-export  --export-dir /user/hive/warehouse/wutiao_db/report_wutiao_wb_distribute/ds='{ds}' --connect jdbc:mysql://{host}:{port}/wutiao --username "{user}" --password '{password}' --table "report_wutiao_wb_distribute" --input-fields-terminated-by "|" --input-null-string "\\\N" --input-null-non-string "\\\N" '''

def main():
    kdc = kingnet.kdc()
    args = {
        'ds': kdc.workDate,
        'sourcetab1': 'wutiao.odl_wutiao_wb',
        'sourcetab2': 'wutiao.idl_wutiao_user',
        'targettab': 'wutiao.report_wutiao_wb_distribute',
        'mysqltab': 'wutiao.report_wutiao_wb_distribute',
        'host': '172.17.2.91',
        'user': 'root',
        'password': '123456',
        'port': '3306'
    }
    sql = SQL_DAY.format(ds=args['ds'], targettab=args['targettab'], sourcetab1=args['sourcetab1'], sourcetab2=args['sourcetab2'])

    kdc.debug = True
    res = kdc.doHive(sql,True,True)

    if res !=0 :
        raise Exception('report_wutiao_wb_distribute hive table insert execute failure!!')

    res1 = os.system(delete.format(ds=args['ds'],host=args['host'],user=args['user'],password=args['password']))
    res2 = os.system(sync.format(ds=args['ds'],host=args['host'],user=args['user'],password=args['password'],port=args['port']))

    if (res1 or res2) !=0 :
        print(res1,res2)
        raise Exception('report_wutiao_wb_distribute mysql table delete&insert execute failure!!')
    else :
        print(res1,res2)
        print('complete')

if __name__ == '__main__':
    main()
