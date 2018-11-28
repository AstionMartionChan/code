# coding: utf-8

#**********************程序说明*********************************#
#*模块：REPORT
#*功能：区块链-内容收入分析
#*作者：gant
#*时间：2018-06-25
#*备注：区块链-内容收入分析
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
t1.itemtype,
'allsortid' as sortid,
t1.total_content_income,
t2.valid_contribute,
t2.valid_verifypoint
from(
    select nvl(itemtype,'allitemtype') as itemtype,
    total_content_income
    from(
        select itemtype,
        sum(his_creative_income) as total_content_income
        from (select nvl(itemtype,'-1') as itemtype,his_creative_income from {sourcetab} where ds='{ds}')tt0
        group by itemtype
        grouping sets((itemtype),())
    )tt1
)t1
inner join(
    select nvl(itemtype,'allitemtype') as itemtype,
    valid_contribute,
    valid_verifypoint
    from(
        select itemtype,
        sum(day_contribute) as valid_contribute,
        sum(day_verifypoint) as valid_verifypoint
        from (select nvl(itemtype,'-1') as itemtype,day_contribute,day_verifypoint from {sourcetab} 
              where ds>=date_sub('{ds}',29) and ds<='{ds}')tt0
        group by itemtype
        grouping sets((itemtype),())
    )tt1
)t2
on t1.itemtype=t2.itemtype;
'''

delete=r'''echo "use wutiao; delete from report_wutiao_itemid_wb where fds ='{ds}'; " |mysql -h {host} -u {user} -p{password} '''
sync=r'''/usr/bin/sqoop-export  --export-dir /user/hive/warehouse/wutiao.db/report_wutiao_itemid_wb/ds='{ds}' --connect jdbc:mysql://{host}:{port}/wutiao --username "{user}" --password '{password}' --table "report_wutiao_itemid_wb" --input-fields-terminated-by "|" --input-null-string "\\\N" --input-null-non-string "\\\N" '''

def main():
    kdc = kingnetdc.kdc
    args = {
        'ds': kdc.workDate,
        'sourcetab': 'wutiao.idl_wutiao_itemid_wb',
        'targettab': 'wutiao.report_wutiao_itemid_wb',
        'mysqltab': 'wutiao.report_wutiao_itemid_wb',
        'host': DB_PARAMS['host'],
        'user': DB_PARAMS['user'],
        'password': DB_PARAMS['password'],
        'port': DB_PARAMS['port']
    }
    sql = SQL_DAY.format(ds=args['ds'], targettab=args['targettab'], sourcetab=args['sourcetab'])

    kdc.debug = True
    res = kdc.doHive(sql,True,True)

    if res !=0 :
        raise Exception('report_wutiao_itemid_wb hive table insert execute failure!!')

    res1 = os.system(delete.format(ds=args['ds'],host=args['host'],user=args['user'],password=args['password']))
    res2 = os.system(sync.format(ds=args['ds'],host=args['host'],user=args['user'],password=args['password'],port=args['port']))

    if (res1 or res2) !=0 :
        print(res1,res2)
        raise Exception('report_wutiao_itemid_wb mysql table delete&insert execute failure!!')
    else :
        print(res1,res2)
        print('complete')



if __name__ == '__main__':
    main()
