# coding: utf-8

#**********************程序说明*********************************#
#*模块：REPORT
#*功能：区块链分币领币分析
#*作者：gant
#*时间：2018-07-25
#*备注：区块链分币领币分析
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
select t2.ds as fds,
t2.creative_fb,
t2.creative_fb_usercnt,
t2.comment_fb,
t2.comment_fb_usercnt,
t2.share_fb,
t2.share_fb_usercnt,
t2.vote_fb,
t2.vote_fb_usercnt,
t2.register_fb,
t2.register_fb_usercnt,
t2.like_fb,
t2.like_fb_usercnt,
t2.other_fb,
t2.other_fb_usercnt,
t2.read_fb,
t2.read_fb_usercnt,
t4.money_gain-t2.register_fb as fb,
t2.fb_usercnt,
t4.complete_usercnt,
if(t2.ds='2018-08-28',0,6848000*0.7-t4.money_gain+t2.register_fb) as recycle_wb,
if(t2.ds='2018-08-28',0,nvl(t5.his_recycle_wb,0)+6848000*0.7-t4.money_gain+t2.register_fb) as his_recycle_wb,
t2.commonuser_fb,
t2.commonuser_usercnt,
t4.active_usercnt
from(
    select ds,
        sum(creative_fb) as creative_fb,
        sum(if(creative_fb>0,1,0)) as creative_fb_usercnt,
        sum(comment_fb) as comment_fb,
        sum(if(comment_fb>0,1,0)) as comment_fb_usercnt,
        sum(share_fb) as share_fb,
        sum(if(share_fb>0,1,0)) as share_fb_usercnt,
        sum(vote_fb) as vote_fb,
        sum(if(vote_fb>0,1,0)) as vote_fb_usercnt,
        sum(register_fb) as register_fb,
        sum(if(register_fb>0,1,0)) as register_fb_usercnt,
        sum(like_fb) as like_fb,
        sum(if(like_fb>0,1,0)) as like_fb_usercnt,
        sum(other_fb) as other_fb,
        sum(if(other_fb>0,1,0)) as other_fb_usercnt,
        sum(read_fb) as read_fb,
        sum(if(read_fb>0,1,0)) as read_fb_usercnt,
        sum(fb) as fb,
        sum(if(fb>0,1,0)) as fb_usercnt,
        sum(commonuser_fb) as commonuser_fb,
        sum(if(commonuser_fb>0,1,0)) as commonuser_usercnt
    from(
            select ds,tt1.uid,
            sum(if(actiontype=10,value,0)) as creative_fb,
            sum(if(actiontype=14,value,0)) as comment_fb,
            sum(if(actiontype=13,value,0)) as share_fb,
            sum(if(actiontype=15,value,0)) as vote_fb,
            sum(if(actiontype=1 or actiontype=10001 or actiontype=10002,value,0)) as register_fb,
            sum(if(actiontype=12,value,0)) as like_fb,
            sum(if(actiontype=9,value,0)) as other_fb,
            sum(if(actiontype=11,value,0)) as read_fb,
            sum(value) as fb,
            sum(if(tt2.uid is not null,value,0)) as commonuser_fb
            from(select * from {sourcetab1} where ds = '{ds}')tt1
            left join(select uid from wutiao.idl_wutiao_mediauser_sync where ds='{ds}' and role='0' group by uid)tt2
            on cast(tt1.uid as bigint)=cast(tt2.uid as bigint)
            group by ds,tt1.uid
    )t1
    group by ds
)t2
inner join(
    select ds,
    sum(money_gain) as money_gain,
    sum(if(read_cnt>=20 and like_cnt>=20 and comment_cnt>=15 and share_cnt>=5,1,0)) as complete_usercnt,
    sum(active_usercnt) as active_usercnt
    from(
        select ds,ouid,
        sum(if(from_unixtime(last_read_time,'yyyy-MM-dd')=ds,last_read_cnt,0)) as read_cnt,
        sum(if(from_unixtime(last_comment_time,'yyyy-MM-dd')=ds,last_comment_cnt,0)) as comment_cnt,
        sum(if(from_unixtime(last_like_time,'yyyy-MM-dd')=ds,last_like_cnt,0)) as like_cnt,
        sum(if(from_unixtime(last_share_time,'yyyy-MM-dd')=ds,last_share_cnt,0)) as share_cnt,
        sum(if(from_unixtime(last_money_gain_time,'yyyy-MM-dd')=ds,last_day_money_gain,0)) as money_gain,
        sum(if(from_unixtime(last_login_time,'yyyy-MM-dd')=ds,1,0)) as active_usercnt
        from {sourcetab2}
        where ds='{ds}' and ut='uid'
        group by ds,ouid
    )t3
    group by ds
)t4
on t2.ds=t4.ds
left join(select '{ds}' as ds,if(ds='2018-08-28',0,his_recycle_wb) as his_recycle_wb from {targettab} where ds = date_sub('{ds}',1))t5
on t2.ds=t5.ds;
'''

delete=r'''echo "use wutiao; delete from report_wutiao_user_fbwb where fds ='{ds}'; " |mysql -h {host} -u {user} -p{password} '''
sync=r'''/usr/bin/sqoop-export  --export-dir /user/hive/warehouse/wutiao.db/report_wutiao_user_fbwb/ds='{ds}' --connect jdbc:mysql://{host}:{port}/wutiao --username "{user}" --password '{password}' --table "report_wutiao_user_fbwb" --input-fields-terminated-by "|" --input-null-string "\\\N" --input-null-non-string "\\\N" '''

def main():
    kdc = kingnetdc.kdc
    args = {
        'ds': kdc.workDate,
        'sourcetab1': 'wutiao.odl_wutiao_wb',
        'sourcetab2': 'wutiao.idl_wutiao_user',
        'targettab': 'wutiao.report_wutiao_user_fbwb',
        'mysqltab': 'wutiao.report_wutiao_user_fbwb',
        'host': DB_PARAMS['host'],
        'user': DB_PARAMS['user'],
        'password': DB_PARAMS['password'],
        'port': DB_PARAMS['port']
    }
    sql = SQL_DAY.format(ds=args['ds'], targettab=args['targettab'], sourcetab1=args['sourcetab1'], sourcetab2=args['sourcetab2'])

    kdc.debug = True
    res = kdc.doHive(sql,True,True)

    if res !=0 :
        raise Exception('report_wutiao_user_fbwb hive table insert execute failure!!')

    res1 = os.system(delete.format(ds=args['ds'],host=args['host'],user=args['user'],password=args['password']))
    res2 = os.system(sync.format(ds=args['ds'],host=args['host'],user=args['user'],password=args['password'],port=args['port']))

    if (res1 or res2) !=0 :
        print(res1,res2)
        raise Exception('report_wutiao_user_fbwb mysql table delete&insert execute failure!!')
    else :
        print(res1,res2)
        print('complete')

if __name__ == '__main__':
    main()
